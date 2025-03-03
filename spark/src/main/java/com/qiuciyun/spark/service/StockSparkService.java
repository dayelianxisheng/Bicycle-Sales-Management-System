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
public class StockSparkService implements Serializable {
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
    public StockSparkService(SparkSession sparkSession, SparkStreamingManager streamingManager, 
                           SimpMessagingTemplate messagingTemplate) {
        this.sparkSession = sparkSession;
        this.streamingManager = streamingManager;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {
        // 加载数据表
        loadTables();
        
        // 创建一个DStream用于定期执行库存分析
        JavaDStream<String> analysisTrigger = streamingManager.getStreamingContext()
            .socketTextStream("localhost", 9999);
        analysisTrigger.foreachRDD(rdd -> {
            startStockAnalysis();
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
            Dataset<Row> productDF = sparkSession.read()
                .format("jdbc")
                .option("url", dbUrl)
                .option("driver", dbDriver)
                .option("dbtable", "product")
                .option("user", dbUsername)
                .option("password", dbPassword)
                .load();

            Dataset<Row> stockRecordDF = sparkSession.read()
                .format("jdbc")
                .option("url", dbUrl)
                .option("driver", dbDriver)
                .option("dbtable", "stock_record")
                .option("user", dbUsername)
                .option("password", dbPassword)
                .load();

            // 创建临时视图
            productDF.createOrReplaceTempView("product");
            stockRecordDF.createOrReplaceTempView("stock_record");
            
            log.info("数据表加载完成");
        } catch (Exception e) {
            log.error("加载数据表时发生错误", e);
            throw e;
        }
    }

    private void startStockAnalysis() {
        try {
            Dataset<Row> stockRecords = sparkSession.sql(
                "SELECT p.name, p.stock, p.warning_stock, " +
                "CASE WHEN p.stock <= p.warning_stock THEN 'WARNING' " +
                "     WHEN p.stock <= p.warning_stock * 1.5 THEN 'LOW' " +
                "     WHEN p.stock >= p.warning_stock * 3 THEN 'HIGH' " +
                "     ELSE 'NORMAL' END as stock_status " +
                "FROM product p"
            );

            // 库存状态分布
            Dataset<Row> stockStatus = stockRecords.groupBy("stock_status")
                .count()
                .orderBy("stock_status");

            // 库存预警商品
            Dataset<Row> warningProducts = stockRecords
                .filter("stock_status = 'WARNING'")
                .select("name", "stock", "warning_stock");

            // 库存变动分析
            Dataset<Row> stockChanges = sparkSession.sql(
                "SELECT sr.type, COUNT(*) as count, SUM(sr.amount) as total_amount " +
                "FROM stock_record sr " +
                "GROUP BY sr.type"
            );

            // 更新分析结果
            Map<String, Object> results = new HashMap<>();
            
            // 库存状态分布
            List<Map<String, Object>> statusList = new ArrayList<>();
            for (Row row : stockStatus.collectAsList()) {
                Map<String, Object> statusMap = new HashMap<>();
                statusMap.put("status", row.getString(0));  // stock_status
                statusMap.put("count", row.getLong(1));     // count
                statusList.add(statusMap);
            }
            results.put("stockStatus", statusList);
            
            // 库存预警商品
            List<Map<String, Object>> warningList = new ArrayList<>();
            for (Row row : warningProducts.collectAsList()) {
                Map<String, Object> warningMap = new HashMap<>();
                warningMap.put("name", row.getString(0));     // name
                warningMap.put("stock", row.getInt(1));       // stock
                warningMap.put("warningStock", row.getInt(2)); // warning_stock
                warningList.add(warningMap);
            }
            results.put("warningProducts", warningList);
            
            // 库存变动分析
            List<Map<String, Object>> changesList = new ArrayList<>();
            for (Row row : stockChanges.collectAsList()) {
                Map<String, Object> changeMap = new HashMap<>();
                changeMap.put("type", row.getString(0));       // type
                changeMap.put("count", row.getLong(1));        // count
                changeMap.put("totalAmount", row.getLong(2));  // total_amount
                changesList.add(changeMap);
            }
            results.put("stockChanges", changesList);
            
            // 汇总数据
            long totalProducts = stockRecords.count();
            long warningCount = warningProducts.count();
            
            results.put("totalProducts", totalProducts);
            results.put("warningCount", warningCount);
            results.put("warningRate", totalProducts > 0 ? (double)warningCount/totalProducts : 0);

            // 发送更新
            messagingTemplate.convertAndSend("/topic/stock-analysis", results);
            currentAnalysis.clear();  // 清除旧数据
            currentAnalysis.putAll(results);
            
            log.info("库存分析完成，结果: {}", results);
        } catch (Exception e) {
            log.error("执行库存分析时发生错误", e);
        }
    }

    public Map<String, Object> getCurrentAnalysis() {
        return new HashMap<>(currentAnalysis);
    }
} 