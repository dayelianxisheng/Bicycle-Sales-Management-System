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

    @Autowired
    public CustomerSparkService(SparkSession sparkSession, SparkStreamingManager streamingManager, 
                              SimpMessagingTemplate messagingTemplate) {
        this.sparkSession = sparkSession;
        this.streamingManager = streamingManager;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {
        // 加载数据表
        loadTables();
        
        // 创建一个DStream用于定期执行客户分析
        JavaDStream<String> analysisTrigger = streamingManager.getStreamingContext()
            .socketTextStream("localhost", 9999);
        analysisTrigger.foreachRDD(rdd -> {
            startCustomerAnalysis();
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

            // 创建临时视图
            customerDF.createOrReplaceTempView("customer");
            orderDF.createOrReplaceTempView("order");
            
            log.info("数据表加载完成");
        } catch (Exception e) {
            log.error("加载数据表时发生错误", e);
            throw e;
        }
    }

    private void startCustomerAnalysis() {
        try {
            // 客户消费等级分析
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

            // 客户购买频率分析
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

            // 客户地域分布
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

            // 计算汇总数据
            Row totals = sparkSession.sql(
                "WITH order_stats AS (" +
                "  SELECT customer_id, " +
                "         COUNT(*) as total_orders, " +
                "         CAST(COALESCE(SUM(total_amount), 0) AS DOUBLE) as total_spent " +
                "  FROM `order` " +
                "  WHERE status != 4 " +
                "  GROUP BY customer_id" +
                ") " +
                "SELECT " +
                "  CAST(COUNT(DISTINCT c.id) AS BIGINT) as total_customers, " +
                "  CAST(COUNT(DISTINCT CASE WHEN o2.id IS NOT NULL AND o2.created_at >= DATE_SUB(CURRENT_DATE(), 30) THEN c.id END) AS BIGINT) as active_customers, " +
                "  CAST(COALESCE(AVG(CASE WHEN os.total_orders > 0 THEN os.total_orders END), 0) AS DOUBLE) as avg_orders_per_customer, " +
                "  CAST(COALESCE(AVG(CASE WHEN os.total_spent > 0 THEN os.total_spent END), 0) AS DOUBLE) as avg_spent_per_customer " +
                "FROM customer c " +
                "LEFT JOIN order_stats os ON c.id = os.customer_id " +
                "LEFT JOIN `order` o2 ON c.id = o2.customer_id AND o2.status != 4"
            ).first();

            // 更新分析结果
            Map<String, Object> results = new HashMap<>();
            
            // 客户等级分布
            List<Map<String, Object>> levelList = new ArrayList<>();
            for (Row row : customerLevels.collectAsList()) {
                Map<String, Object> levelMap = new HashMap<>();
                levelMap.put("level", row.getString(0));      // customer_level
                levelMap.put("count", row.getLong(1));        // customer_count
                levelMap.put("avgSpent", row.getDouble(2));   // avg_spent
                levelMap.put("avgOrders", row.getDouble(3));  // avg_orders
                levelList.add(levelMap);
            }
            results.put("customerLevels", levelList);
            
            // 新增客户趋势
            List<Map<String, Object>> newCustomerList = new ArrayList<>();
            for (Row row : newCustomers.collectAsList()) {
                Map<String, Object> newCustomerMap = new HashMap<>();
                newCustomerMap.put("date", row.getDate(0).toString());  // register_date
                newCustomerMap.put("count", row.getLong(1));           // new_customers
                newCustomerList.add(newCustomerMap);
            }
            results.put("newCustomers", newCustomerList);
            
            // 购买频率分布
            List<Map<String, Object>> frequencyList = new ArrayList<>();
            for (Row row : purchaseFrequency.collectAsList()) {
                Map<String, Object> frequencyMap = new HashMap<>();
                frequencyMap.put("range", row.getString(0));  // frequency_range
                frequencyMap.put("count", row.getLong(1));    // customer_count
                frequencyList.add(frequencyMap);
            }
            results.put("purchaseFrequency", frequencyList);
            
            // 地域分布
            List<Map<String, Object>> regionList = new ArrayList<>();
            for (Row row : regionDistribution.collectAsList()) {
                Map<String, Object> regionMap = new HashMap<>();
                regionMap.put("region", row.getString(0));     // region
                regionMap.put("customerCount", row.getLong(1)); // customer_count
                regionMap.put("orderCount", row.getLong(2));    // order_count
                regionMap.put("totalAmount", row.getDouble(3)); // total_amount
                regionList.add(regionMap);
            }
            results.put("regionDistribution", regionList);

            // 汇总数据
            try {
                results.put("totalCustomers", totals.getLong(0));
            } catch (Exception e) {
                results.put("totalCustomers", 0L);
                log.warn("获取总客户数失败", e);
            }

            try {
                results.put("activeCustomers", totals.getLong(1));
            } catch (Exception e) {
                results.put("activeCustomers", 0L);
                log.warn("获取活跃客户数失败", e);
            }

            try {
                results.put("avgOrdersPerCustomer", totals.getDouble(2));
            } catch (Exception e) {
                results.put("avgOrdersPerCustomer", 0.0);
                log.warn("获取客均订单数失败", e);
            }

            try {
                results.put("avgSpentPerCustomer", totals.getDouble(3));
            } catch (Exception e) {
                results.put("avgSpentPerCustomer", 0.0);
                log.warn("获取客均消费金额失败", e);
            }

            // 发送更新
            messagingTemplate.convertAndSend("/topic/customer-analysis", results);
            currentAnalysis.clear();  // 清除旧数据
            currentAnalysis.putAll(results);
            
            log.info("客户分析完成，结果: {}", results);
        } catch (Exception e) {
            log.error("执行客户分析时发生错误", e);
        }
    }

    public Map<String, Object> getCurrentAnalysis() {
        return new HashMap<>(currentAnalysis);
    }
} 