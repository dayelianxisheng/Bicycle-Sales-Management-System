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
public class CustomerSparkService implements Serializable {
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
    
    // 批处理相关变量
    private int currentBatch = 0;
    private static final int BATCH_SIZE = 1000;
    private final Map<String, Object> batchResults = new ConcurrentHashMap<>();
    private volatile boolean isProcessing = false;

    @Autowired
    public CustomerSparkService(SparkSession sparkSession, SparkStreamingManager streamingManager, 
                              SimpMessagingTemplate messagingTemplate) {
        this.sparkSession = sparkSession;
        this.streamingManager = streamingManager;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {
        loadTables();
        
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
    }

    private void loadTables() {
        try {
            log.info("开始加载数据表，使用数据库URL: {}", dbUrl);
            
            Dataset<Row> customerDF = sparkSession.read()
                .format("jdbc")
                .option("url", dbUrl)
                .option("driver", dbDriver)
                .option("dbtable", "customer")
                .option("user", dbUsername)
                .option("password", dbPassword)
                .load();

            Dataset<Row> orderDF = sparkSession.read()
                .format("jdbc")
                .option("url", dbUrl)
                .option("driver", dbDriver)
                .option("dbtable", "`order`")
                .option("user", dbUsername)
                .option("password", dbPassword)
                .load();

            customerDF.createOrReplaceTempView("customer");
            orderDF.createOrReplaceTempView("order");
            
            long customerCount = customerDF.count();
            long orderCount = orderDF.count();
            log.info("数据表加载完成 - 客户数: {}, 订单数: {}", customerCount, orderCount);
            
            // 初始化时立即触发一次分析
            processBatch();
        } catch (Exception e) {
            log.error("加载数据表时发生错误", e);
            throw e;
        }
    }

    private synchronized void processBatch() {
        try {
            isProcessing = true;
            int offset = currentBatch * BATCH_SIZE;
            
            log.info("开始处理第 {} 批次客户数据，偏移量: {}", currentBatch + 1, offset);

            // 获取总数据量
            long totalCustomers = sparkSession.sql("SELECT COUNT(*) FROM customer").first().getLong(0);
            log.info("客户总数: {}", totalCustomers);

            if (totalCustomers == 0) {
                Map<String, Object> emptyResults = new HashMap<>();
                emptyResults.put("customerLevels", new ArrayList<>());
                emptyResults.put("newCustomers", new ArrayList<>());
                emptyResults.put("purchaseFrequency", new ArrayList<>());
                emptyResults.put("regionDistribution", new ArrayList<>());
                emptyResults.put("batchNumber", 0);
                emptyResults.put("hasMore", false);
                emptyResults.put("progress", 100);
                emptyResults.put("totalCustomers", 0);
                messagingTemplate.convertAndSend("/topic/customer-analysis", emptyResults);
                isProcessing = false;
                return;
            }

            // 处理当前批次结果
            Map<String, Object> results = new HashMap<>();
            results.put("totalCustomers", totalCustomers);
            
            // 客户等级分析（分批）
            Dataset<Row> customerLevels = sparkSession.sql(
                "WITH customer_stats AS (" +
                "  SELECT c.id, c.name, " +
                "         CAST(COUNT(DISTINCT o.id) AS BIGINT) as order_count, " +
                "         CAST(COALESCE(SUM(o.total_amount), 0) AS DOUBLE) as total_spent " +
                "  FROM customer c " +
                "  LEFT JOIN `order` o ON c.id = o.customer_id AND o.status != 4 " +
                "  GROUP BY c.id, c.name" +
                ") " +
                "SELECT " +
                "  CASE " +
                "    WHEN total_spent >= 100000 THEN 'VIP客户' " +
                "    WHEN total_spent >= 50000 THEN '金牌客户' " +
                "    WHEN total_spent >= 10000 THEN '银牌客户' " +
                "    ELSE '普通客户' " +
                "  END as customer_level, " +
                "  CAST(COUNT(*) AS BIGINT) as customer_count, " +
                "  CAST(COALESCE(AVG(CASE WHEN total_spent > 0 THEN total_spent END), 0) AS DOUBLE) as avg_spent, " +
                "  CAST(COALESCE(AVG(CASE WHEN order_count > 0 THEN order_count END), 0) AS DOUBLE) as avg_orders " +
                "FROM customer_stats " +
                "GROUP BY " +
                "  CASE " +
                "    WHEN total_spent >= 100000 THEN 'VIP客户' " +
                "    WHEN total_spent >= 50000 THEN '金牌客户' " +
                "    WHEN total_spent >= 10000 THEN '银牌客户' " +
                "    ELSE '普通客户' " +
                "  END"
            );

            // 处理客户等级分布数据
            List<Map<String, Object>> levelList = new ArrayList<>();
            for (Row row : customerLevels.collectAsList()) {
                Map<String, Object> levelMap = new HashMap<>();
                levelMap.put("level", row.getString(0));
                levelMap.put("count", row.getLong(1));
                levelMap.put("avgSpent", row.getDouble(2));
                levelMap.put("avgOrders", row.getDouble(3));
                levelList.add(levelMap);
            }
            results.put("customerLevels", levelList);

            // 新增客户趋势
            Dataset<Row> newCustomers = sparkSession.sql(
                "SELECT " +
                "  DATE(created_at) as register_date, " +
                "  CAST(COUNT(*) AS BIGINT) as new_customers " +
                "FROM customer " +
                "WHERE DATE(created_at) >= DATE_SUB(CURRENT_DATE(), 30) " +
                "GROUP BY DATE(created_at) " +
                "ORDER BY register_date"
            );

            // 处理新增客户趋势数据
            List<Map<String, Object>> newCustomerList = new ArrayList<>();
            for (Row row : newCustomers.collectAsList()) {
                Map<String, Object> newCustomerMap = new HashMap<>();
                newCustomerMap.put("date", row.getDate(0).toString());
                newCustomerMap.put("count", row.getLong(1));
                newCustomerList.add(newCustomerMap);
            }
            results.put("newCustomers", newCustomerList);

            // 购买频率分析
            Dataset<Row> purchaseFrequency = sparkSession.sql(
                "WITH customer_orders AS (" +
                "  SELECT c.id, " +
                "         CAST(COUNT(DISTINCT o.id) AS BIGINT) as order_count " +
                "  FROM customer c " +
                "  LEFT JOIN `order` o ON c.id = o.customer_id AND o.status != 4 " +
                "  GROUP BY c.id" +
                ") " +
                "SELECT " +
                "  CASE " +
                "    WHEN order_count > 10 THEN '高频客户(>10单)' " +
                "    WHEN order_count > 5 THEN '中频客户(6-10单)' " +
                "    WHEN order_count > 0 THEN '低频客户(1-5单)' " +
                "    ELSE '未购买客户' " +
                "  END as frequency_range, " +
                "  CAST(COUNT(*) AS BIGINT) as customer_count " +
                "FROM customer_orders " +
                "GROUP BY " +
                "  CASE " +
                "    WHEN order_count > 10 THEN '高频客户(>10单)' " +
                "    WHEN order_count > 5 THEN '中频客户(6-10单)' " +
                "    WHEN order_count > 0 THEN '低频客户(1-5单)' " +
                "    ELSE '未购买客户' " +
                "  END " +
                "ORDER BY " +
                "  CASE frequency_range " +
                "    WHEN '高频客户(>10单)' THEN 1 " +
                "    WHEN '中频客户(6-10单)' THEN 2 " +
                "    WHEN '低频客户(1-5单)' THEN 3 " +
                "    ELSE 4 " +
                "  END"
            );

            // 处理购买频率分布数据
            List<Map<String, Object>> frequencyList = new ArrayList<>();
            for (Row row : purchaseFrequency.collectAsList()) {
                Map<String, Object> frequencyMap = new HashMap<>();
                frequencyMap.put("range", row.getString(0));
                frequencyMap.put("count", row.getLong(1));
                frequencyList.add(frequencyMap);
            }
            results.put("purchaseFrequency", frequencyList);

            // 地域分布
            Dataset<Row> regionDistribution = sparkSession.sql(
                "WITH customer_orders AS (" +
                "  SELECT c.id, " +
                "         COALESCE(SUBSTRING_INDEX(c.address, ' ', 1), '未知地区') as region, " +
                "         CAST(COUNT(DISTINCT o.id) AS BIGINT) as order_count, " +
                "         CAST(COALESCE(SUM(o.total_amount), 0) AS DOUBLE) as total_amount " +
                "  FROM customer c " +
                "  LEFT JOIN `order` o ON c.id = o.customer_id AND o.status != 4 " +
                "  GROUP BY c.id, COALESCE(SUBSTRING_INDEX(c.address, ' ', 1), '未知地区')" +
                ") " +
                "SELECT " +
                "  region, " +
                "  CAST(COUNT(DISTINCT id) AS BIGINT) as customer_count, " +
                "  CAST(SUM(order_count) AS BIGINT) as order_count, " +
                "  CAST(SUM(total_amount) AS DOUBLE) as total_amount " +
                "FROM customer_orders " +
                "GROUP BY region " +
                "ORDER BY customer_count DESC"
            );

            // 处理地域分布数据
            List<Map<String, Object>> regionList = new ArrayList<>();
            for (Row row : regionDistribution.collectAsList()) {
                Map<String, Object> regionMap = new HashMap<>();
                regionMap.put("region", row.getString(0));
                regionMap.put("customerCount", row.getLong(1));
                regionMap.put("orderCount", row.getLong(2));
                regionMap.put("totalAmount", row.getDouble(3));
                regionList.add(regionMap);
            }
            results.put("regionDistribution", regionList);

            // 更新批次信息
            results.put("batchNumber", currentBatch);
            results.put("hasMore", false);
            results.put("progress", 100);
            
            // 更新当前分析结果
            currentAnalysis.clear();
            currentAnalysis.putAll(results);
            
            // 发送WebSocket通知
            messagingTemplate.convertAndSend("/topic/customer-analysis", results);
            
            log.info("客户分析完成，结果: {}", results);
            isProcessing = false;
            
        } catch (Exception e) {
            log.error("处理客户数据时发生错误", e);
            e.printStackTrace();
            isProcessing = false;
        }
    }

    public Map<String, Object> getCurrentAnalysis() {
        return new HashMap<>(currentAnalysis);
    }

    public void resetAnalysis() {
        currentBatch = 0;
        batchResults.clear();
        currentAnalysis.clear();
        isProcessing = false;
    }
} 