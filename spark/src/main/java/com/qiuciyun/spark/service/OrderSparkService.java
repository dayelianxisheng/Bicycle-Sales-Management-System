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

    @Autowired
    public OrderSparkService(SparkSession sparkSession, SparkStreamingManager streamingManager, 
                           SimpMessagingTemplate messagingTemplate) {
        this.sparkSession = sparkSession;
        this.streamingManager = streamingManager;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {
        // 加载数据表
        loadTables();
        
        // 创建一个DStream用于定期执行订单分析
        JavaDStream<String> analysisTrigger = streamingManager.getStreamingContext()
            .socketTextStream("localhost", 9999);
        analysisTrigger.foreachRDD(rdd -> {
            startOrderAnalysis();
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

    private void startOrderAnalysis() {
        try {
            log.info("开始执行订单分析...");
            
            // 订单状态分布
            Dataset<Row> orderStatus = sparkSession.sql(
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
                "FROM `order` " +
                "GROUP BY status, status_name " +
                "ORDER BY status"
            );
            log.info("订单状态分布: {}", orderStatus.collectAsList());

            // 支付方式分布
            Dataset<Row> paymentMethods = sparkSession.sql(
                "SELECT " +
                "CASE payment_method " +
                "  WHEN 1 THEN '微信支付' " +
                "  WHEN 2 THEN '支付宝' " +
                "  WHEN 3 THEN '银行卡' " +
                "  ELSE '其他' END as payment_name, " +
                "COUNT(*) as count " +
                "FROM `order` " +
                "WHERE status != 4 " +  // 排除已取消的订单
                "AND payment_method IS NOT NULL " +  // 排除未支付的订单
                "GROUP BY payment_method " +
                "ORDER BY payment_method"
            );
            log.info("支付方式分布: {}", paymentMethods.collectAsList());

            // 热销商品分析
            Dataset<Row> hotProducts = sparkSession.sql(
                "SELECT " +
                "p.name as product_name, " +
                "COUNT(oi.id) as order_count, " +
                "SUM(oi.quantity) as total_quantity, " +
                "CAST(SUM(oi.subtotal) AS DOUBLE) as total_amount " +
                "FROM order_item oi " +
                "JOIN product p ON oi.product_id = p.id " +
                "JOIN `order` o ON oi.order_id = o.id " +
                "WHERE o.status != 4 " +  // 排除已取消的订单
                "GROUP BY p.name " +
                "ORDER BY order_count DESC " +
                "LIMIT 10"
            );

            // 每日订单趋势
            Dataset<Row> dailyTrend = sparkSession.sql(
                "SELECT " +
                "DATE(created_at) as order_date, " +
                "COUNT(*) as order_count, " +
                "CAST(SUM(total_amount) AS DOUBLE) as total_amount " +
                "FROM `order` " +
                "WHERE DATE(created_at) >= DATE_SUB(CURRENT_DATE(), 30) " +
                "AND status != 4 " +
                "GROUP BY DATE(created_at) " +
                "ORDER BY order_date"
            );

            // 更新分析结果
            Map<String, Object> results = new HashMap<>();
            
            // 订单状态分布
            List<Map<String, Object>> statusList = new ArrayList<>();
            for (Row row : orderStatus.collectAsList()) {
                Map<String, Object> statusMap = new HashMap<>();
                statusMap.put("name", row.getString(0));  // status_name
                statusMap.put("count", row.getLong(1));   // count
                statusMap.put("amount", row.getDouble(2)); // total_amount
                statusList.add(statusMap);
            }
            results.put("orderStatus", statusList);
            
            // 支付方式分布
            List<Map<String, Object>> paymentList = new ArrayList<>();
            for (Row row : paymentMethods.collectAsList()) {
                Map<String, Object> paymentMap = new HashMap<>();
                paymentMap.put("name", row.getString(0));  // payment_name
                paymentMap.put("count", row.getLong(1));   // count
                paymentList.add(paymentMap);
            }
            results.put("paymentMethods", paymentList);
            
            // 热销商品分析
            List<Map<String, Object>> productList = new ArrayList<>();
            for (Row row : hotProducts.collectAsList()) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("name", row.getString(0));     // product_name
                productMap.put("orderCount", row.getLong(1)); // order_count
                productMap.put("quantity", row.getLong(2));   // total_quantity
                productMap.put("amount", row.getDouble(3));   // total_amount
                productList.add(productMap);
            }
            results.put("hotProducts", productList);
            
            // 每日订单趋势
            List<Map<String, Object>> trendList = new ArrayList<>();
            for (Row row : dailyTrend.collectAsList()) {
                Map<String, Object> trendMap = new HashMap<>();
                trendMap.put("date", row.getDate(0).toString());  // order_date
                trendMap.put("count", row.getLong(1));           // order_count
                trendMap.put("amount", row.getDouble(2));        // total_amount
                trendList.add(trendMap);
            }
            results.put("dailyTrend", trendList);

            // 计算汇总数据
            Row totals = sparkSession.sql(
                "SELECT COUNT(*) as total_orders, " +
                "CAST(COALESCE(SUM(total_amount), 0) AS DOUBLE) as total_amount, " +
                "CAST(COALESCE(AVG(total_amount), 0) AS DOUBLE) as avg_amount, " +
                "COUNT(DISTINCT customer_id) as customer_count " +
                "FROM `order` " +
                "WHERE status != 4"
            ).first();

            results.put("totalOrders", totals.getLong(0));
            results.put("totalAmount", totals.getDouble(1));
            results.put("avgAmount", totals.getDouble(2));
            results.put("customerCount", totals.getLong(3));

            // 发送更新
            messagingTemplate.convertAndSend("/topic/order-analysis", results);
            currentAnalysis.clear();  // 清除旧数据
            currentAnalysis.putAll(results);

            log.info("订单分析完成，结果: {}", results);
        } catch (Exception e) {
            log.error("订单分析过程中发生错误", e);
        }
    }

    public Map<String, Object> getCurrentAnalysis() {
        return new HashMap<>(currentAnalysis);
    }
} 