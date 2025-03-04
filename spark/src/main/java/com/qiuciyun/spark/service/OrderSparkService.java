package com.qiuciyun.spark.service;

import com.qiuciyun.spark.config.SparkStreamingManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OrderSparkService implements Serializable {
    private static final long serialVersionUID = 1L;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;

    private transient final SparkSession sparkSession;
    private transient final SparkStreamingManager streamingManager;
    private transient final SimpMessagingTemplate messagingTemplate;
    private transient final Map<String, Object> currentAnalysis = new ConcurrentHashMap<>();

    private int currentBatch = 0;
    private static final int BATCH_SIZE = 5;
    private final Map<String, Object> batchResults = new ConcurrentHashMap<>();
    private volatile boolean isProcessing = false;

    @Autowired
    public OrderSparkService(SparkSession sparkSession, SparkStreamingManager streamingManager, 
                           SimpMessagingTemplate messagingTemplate) {
        this.sparkSession = sparkSession;
        this.streamingManager = streamingManager;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {
        loadTables();
        
        // 创建DStream用于定期执行订单分析
        JavaDStream<String> analysisTrigger = streamingManager.getStreamingContext()
            .socketTextStream("localhost", 9999);
            
        analysisTrigger.foreachRDD(rdd -> {
            if (!isProcessing) {
                processBatch();
            }
        });
    }

    @PreDestroy
    public void cleanup() {
        // 不再需要停止 streamingContext，因为它现在是共享的
    }

    private void loadTables() {
        try {
            log.info("开始加载数据表，使用数据库URL: {}", dbUrl);
            
            // 从MySQL加载数据
            Dataset<Row> orderDF = sparkSession.read()
                .format("jdbc")
                .option("url", dbUrl)
                .option("driver", dbDriver)
                .option("dbtable", "`order`")
                .option("user", dbUsername)
                .option("password", dbPassword)
                .load();

            Dataset<Row> orderItemDF = sparkSession.read()
                .format("jdbc")
                .option("url", dbUrl)
                .option("driver", dbDriver)
                .option("dbtable", "order_item")
                .option("user", dbUsername)
                .option("password", dbPassword)
                .load();

            Dataset<Row> productDF = sparkSession.read()
                .format("jdbc")
                .option("url", dbUrl)
                .option("driver", dbDriver)
                .option("dbtable", "product")
                .option("user", dbUsername)
                .option("password", dbPassword)
                .load();

            // 创建临时视图
            orderDF.createOrReplaceTempView("order");
            orderItemDF.createOrReplaceTempView("order_item");
            productDF.createOrReplaceTempView("product");
            
            log.info("数据表加载完成");
        } catch (Exception e) {
            log.error("加载数据表时发生错误", e);
            throw e;
        }
    }

    private synchronized void processBatch() {
        try {
            isProcessing = true;
            log.info("开始处理第{}批数据...", currentBatch + 1);
            
            // 计算当前批次的偏移量
            int offset = currentBatch * BATCH_SIZE;
            
            // 订单状态分布（分批处理）
            Dataset<Row> orderStatus = sparkSession.sql(
                "WITH ordered_orders AS (" +
                "  SELECT *, ROW_NUMBER() OVER (ORDER BY created_at) as row_num " +
                "  FROM `order` " +
                ") " +
                "SELECT " +
                "CASE status " +
                "  WHEN 0 THEN '待支付' " +
                "  WHEN 1 THEN '已支付' " +
                "  WHEN 2 THEN '已发货' " +
                "  WHEN 3 THEN '已完成' " +
                "  WHEN 4 THEN '已取消' " +
                "  ELSE '未知' END as status_name, " +
                "COUNT(*) as count, " +
                "CAST(SUM(total_amount) AS DOUBLE) as total_amount " +
                "FROM ordered_orders " +
                "WHERE row_num > " + offset + " AND row_num <= " + (offset + BATCH_SIZE) + " " +
                "GROUP BY status " +
                "ORDER BY status"
            );

            // 支付方式分布（分批处理）
            Dataset<Row> paymentMethods = sparkSession.sql(
                "WITH ordered_orders AS (" +
                "  SELECT *, ROW_NUMBER() OVER (ORDER BY created_at) as row_num " +
                "  FROM `order` " +
                "  WHERE status != 4 AND payment_method IS NOT NULL" +
                ") " +
                "SELECT " +
                "CASE payment_method " +
                "  WHEN 1 THEN '微信支付' " +
                "  WHEN 2 THEN '支付宝' " +
                "  WHEN 3 THEN '银行卡' " +
                "  ELSE '其他' END as payment_name, " +
                "COUNT(*) as count " +
                "FROM ordered_orders " +
                "WHERE row_num > " + offset + " AND row_num <= " + (offset + BATCH_SIZE) + " " +
                "GROUP BY payment_method " +
                "ORDER BY payment_method"
            );

            // 热销商品分析（分批处理）
            Dataset<Row> hotProducts = sparkSession.sql(
                "WITH ordered_items AS (" +
                "  SELECT oi.*, ROW_NUMBER() OVER (ORDER BY o.created_at) as row_num " +
                "  FROM order_item oi " +
                "  JOIN `order` o ON oi.order_id = o.id " +
                "  WHERE o.status != 4" +
                ") " +
                "SELECT " +
                "p.name as product_name, " +
                "COUNT(oi.id) as order_count, " +
                "SUM(oi.quantity) as total_quantity, " +
                "CAST(SUM(oi.subtotal) AS DOUBLE) as total_amount " +
                "FROM ordered_items oi " +
                "JOIN product p ON oi.product_id = p.id " +
                "WHERE row_num > " + offset + " AND row_num <= " + (offset + BATCH_SIZE) + " " +
                "GROUP BY p.name " +
                "ORDER BY order_count DESC " +
                "LIMIT 10"
            );

            // 添加每日订单趋势分析（分批处理）
            Dataset<Row> dailyTrend = sparkSession.sql(
                "WITH ordered_orders AS (" +
                "  SELECT *, ROW_NUMBER() OVER (ORDER BY created_at) as row_num " +
                "  FROM `order` " +
                "  WHERE DATE(created_at) >= DATE_SUB(CURRENT_DATE(), 30)" +
                ") " +
                "SELECT " +
                "DATE(created_at) as order_date, " +
                "COUNT(*) as order_count, " +
                "CAST(SUM(total_amount) AS DOUBLE) as total_amount " +
                "FROM ordered_orders " +
                "WHERE row_num > " + offset + " AND row_num <= " + (offset + BATCH_SIZE) + " " +
                "GROUP BY DATE(created_at) " +
                "ORDER BY order_date"
            );

            // 合并当前批次结果
            Map<String, Object> currentResults = new HashMap<>();
            
            // 处理订单状态数据
            List<Map<String, Object>> statusList = new ArrayList<>();
            for (Row row : orderStatus.collectAsList()) {
                Map<String, Object> statusMap = new HashMap<>();
                statusMap.put("name", row.getString(0));
                statusMap.put("count", row.getLong(1));
                statusMap.put("amount", row.getDouble(2));
                statusList.add(statusMap);
            }
            currentResults.put("orderStatus", statusList);
            
            // 处理支付方式数据
            List<Map<String, Object>> paymentList = new ArrayList<>();
            for (Row row : paymentMethods.collectAsList()) {
                Map<String, Object> paymentMap = new HashMap<>();
                paymentMap.put("name", row.getString(0));
                paymentMap.put("count", row.getLong(1));
                paymentList.add(paymentMap);
            }
            currentResults.put("paymentMethods", paymentList);
            
            // 处理热销商品数据
            List<Map<String, Object>> productList = new ArrayList<>();
            for (Row row : hotProducts.collectAsList()) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("name", row.getString(0));
                productMap.put("orderCount", row.getLong(1));
                productMap.put("quantity", row.getLong(2));
                productMap.put("amount", row.getDouble(3));
                productList.add(productMap);
            }
            currentResults.put("hotProducts", productList);
            
            // 处理每日订单趋势数据
            List<Map<String, Object>> trendList = new ArrayList<>();
            for (Row row : dailyTrend.collectAsList()) {
                Map<String, Object> trendMap = new HashMap<>();
                trendMap.put("date", row.getDate(0).toString());
                trendMap.put("count", row.getLong(1));
                trendMap.put("amount", row.getDouble(2));
                trendList.add(trendMap);
            }
            currentResults.put("dailyTrend", trendList);
            
            // 更新批次结果
            batchResults.put("batch_" + currentBatch, currentResults);
            
            // 发送WebSocket通知
            Map<String, Object> message = new HashMap<>();
            message.put("batchNumber", currentBatch);
            message.put("data", currentResults);
            messagingTemplate.convertAndSend("/topic/order-analysis", message);
            
            currentBatch++;
            log.info("第{}批数据处理完成", currentBatch);
            
            // 检查是否需要继续处理下一批
            if (checkMoreData()) {
                processBatch();
            } else {
                isProcessing = false;
                currentBatch = 0;
            }
        } catch (Exception e) {
            log.error("处理批次数据时发生错误", e);
            isProcessing = false;
        }
    }

    private boolean checkMoreData() {
        try {
            long totalRecords = sparkSession.sql("SELECT COUNT(*) FROM `order`").first().getLong(0);
            return (currentBatch * BATCH_SIZE) < totalRecords;
        } catch (Exception e) {
            log.error("检查剩余数据时发生错误", e);
            return false;
        }
    }

    public Map<String, Object> getCurrentAnalysis() {
        Map<String, Object> mergedResults = new HashMap<>();
        
        // 合并所有批次的订单状态数据
        Map<String, Map<String, Object>> statusMap = new HashMap<>();
        Map<String, Map<String, Object>> paymentMap = new HashMap<>();
        Map<String, Map<String, Object>> productMap = new HashMap<>();
        
        // 合并每日订单趋势数据
        Map<String, Map<String, Object>> trendMap = new HashMap<>();
        
        batchResults.values().forEach(batchResult -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) batchResult;
            
            // 合并订单状态数据
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> orderStatusList = (List<Map<String, Object>>) resultMap.get("orderStatus");
            if (orderStatusList != null) {
                orderStatusList.forEach(status -> {
                    String name = (String) status.get("name");
                    statusMap.compute(name, (k, v) -> {
                        if (v == null) {
                            return new HashMap<>(status);
                        } else {
                            v.put("count", (Long) v.get("count") + (Long) status.get("count"));
                            v.put("amount", (Double) v.get("amount") + (Double) status.get("amount"));
                            return v;
                        }
                    });
                });
            }
            
            // 合并支付方式数据
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> paymentMethodsList = (List<Map<String, Object>>) resultMap.get("paymentMethods");
            if (paymentMethodsList != null) {
                paymentMethodsList.forEach(payment -> {
                    String name = (String) payment.get("name");
                    paymentMap.compute(name, (k, v) -> {
                        if (v == null) {
                            return new HashMap<>(payment);
                        } else {
                            v.put("count", (Long) v.get("count") + (Long) payment.get("count"));
                            return v;
                        }
                    });
                });
            }
            
            // 合并热销商品数据
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> hotProductsList = (List<Map<String, Object>>) resultMap.get("hotProducts");
            if (hotProductsList != null) {
                hotProductsList.forEach(product -> {
                    String name = (String) product.get("name");
                    productMap.compute(name, (k, v) -> {
                        if (v == null) {
                            return new HashMap<>(product);
                        } else {
                            v.put("orderCount", (Long) v.get("orderCount") + (Long) product.get("orderCount"));
                            v.put("quantity", (Long) v.get("quantity") + (Long) product.get("quantity"));
                            v.put("amount", (Double) v.get("amount") + (Double) product.get("amount"));
                            return v;
                        }
                    });
                });
            }

            // 合并每日订单趋势数据
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> dailyTrendList = (List<Map<String, Object>>) resultMap.get("dailyTrend");
            if (dailyTrendList != null) {
                dailyTrendList.forEach(trend -> {
                    String date = (String) trend.get("date");
                    trendMap.compute(date, (k, v) -> {
                        if (v == null) {
                            return new HashMap<>(trend);
                        } else {
                            v.put("count", (Long) v.get("count") + (Long) trend.get("count"));
                            v.put("amount", (Double) v.get("amount") + (Double) trend.get("amount"));
                            return v;
                        }
                    });
                });
            }
        });
        
        // 转换回列表格式
        mergedResults.put("orderStatus", new ArrayList<>(statusMap.values()));
        mergedResults.put("paymentMethods", new ArrayList<>(paymentMap.values()));
        mergedResults.put("hotProducts", new ArrayList<>(productMap.values()));
        mergedResults.put("dailyTrend", new ArrayList<>(trendMap.values()));
        
        return mergedResults;
    }

    public void resetAnalysis() {
        currentBatch = 0;
        batchResults.clear();
        isProcessing = false;
    }
} 