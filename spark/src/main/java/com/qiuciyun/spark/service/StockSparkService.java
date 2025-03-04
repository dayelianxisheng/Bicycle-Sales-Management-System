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

    private int currentBatch = 0;
    private static final int BATCH_SIZE = 5;
    private final Map<String, Object> batchResults = new ConcurrentHashMap<>();
    private volatile boolean isProcessing = false;

    @Autowired
    public StockSparkService(SparkSession sparkSession, SparkStreamingManager streamingManager, 
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
            
            // 检查数据是否正确加载
            long productCount = productDF.count();
            long stockRecordCount = stockRecordDF.count();
            log.info("数据表加载完成 - 商品数量: {}, 库存记录数量: {}", productCount, stockRecordCount);
            
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
            log.info("开始处理库存分析数据...");
            
            // 库存状态分布
            Dataset<Row> stockRecords = sparkSession.sql(
                "SELECT p.name, p.stock, p.warning_stock, " +
                "CASE WHEN p.stock <= p.warning_stock THEN 'WARNING' " +
                "     WHEN p.stock <= p.warning_stock * 1.5 THEN 'LOW' " +
                "     WHEN p.stock >= p.warning_stock * 3 THEN 'HIGH' " +
                "     ELSE 'NORMAL' END as stock_status " +
                "FROM product p"
            );
            log.info("商品总数: {}", stockRecords.count());

            // 库存状态分布
            Dataset<Row> stockStatus = stockRecords.groupBy("stock_status")
                .count()
                .orderBy("stock_status");
            log.info("库存状态分布: {}", stockStatus.collectAsList());

            // 库存预警商品
            Dataset<Row> warningProducts = stockRecords
                .filter("stock_status = 'WARNING'")
                .select("name", "stock", "warning_stock");
            log.info("预警商品数量: {}", warningProducts.count());

            // 库存变动分析（最近30天）
            Dataset<Row> stockChanges = sparkSession.sql(
                "SELECT DATE(operate_time) as date, type, " +
                "COUNT(*) as count, " +
                "CAST(SUM(CAST(amount AS DOUBLE)) AS DOUBLE) as total_amount " +
                "FROM stock_record " +
                "WHERE DATE(operate_time) >= DATE_SUB(CURRENT_DATE(), 30) " +
                "GROUP BY DATE(operate_time), type " +
                "ORDER BY date"
            );
            log.info("库存变动记录: {}", stockChanges.collectAsList());

            // 合并当前批次结果
            Map<String, Object> currentResults = new HashMap<>();
            
            // 处理库存状态数据
            List<Map<String, Object>> statusList = new ArrayList<>();
            for (Row row : stockStatus.collectAsList()) {
                Map<String, Object> statusMap = new HashMap<>();
                statusMap.put("status", row.getString(0));
                statusMap.put("count", row.getLong(1));
                statusList.add(statusMap);
            }
            currentResults.put("stockStatus", statusList);
            
            // 处理预警商品数据
            List<Map<String, Object>> warningList = new ArrayList<>();
            for (Row row : warningProducts.collectAsList()) {
                Map<String, Object> warningMap = new HashMap<>();
                warningMap.put("name", row.getString(0));
                warningMap.put("stock", row.getInt(1));
                warningMap.put("warningStock", row.getInt(2));
                warningList.add(warningMap);
            }
            currentResults.put("warningProducts", warningList);
            
            // 处理库存变动数据
            List<Map<String, Object>> changesList = new ArrayList<>();
            for (Row row : stockChanges.collectAsList()) {
                Map<String, Object> changeMap = new HashMap<>();
                changeMap.put("date", row.getDate(0).toString());
                changeMap.put("type", row.getString(1));
                changeMap.put("count", row.getLong(2));
                changeMap.put("totalAmount", row.getDouble(3));
                changesList.add(changeMap);
            }
            currentResults.put("stockChanges", changesList);
            
            // 汇总数据
            long totalProducts = stockRecords.count();
            long warningCount = warningProducts.count();
            currentResults.put("totalProducts", totalProducts);
            currentResults.put("warningCount", warningCount);
            currentResults.put("warningRate", totalProducts > 0 ? (double)warningCount/totalProducts : 0);

            // 更新分析结果
            currentAnalysis.clear();
            currentAnalysis.putAll(currentResults);
            
            // 发送WebSocket通知
            messagingTemplate.convertAndSend("/topic/stock-analysis", currentResults);
            
            log.info("库存分析数据处理完成: {}", currentResults);
            isProcessing = false;
        } catch (Exception e) {
            log.error("处理数据时发生错误", e);
            e.printStackTrace();
            isProcessing = false;
        }
    }

    private boolean checkMoreData() {
        try {
            long totalRecords = sparkSession.sql("SELECT COUNT(*) FROM product").first().getLong(0);
            return (currentBatch * BATCH_SIZE) < totalRecords;
        } catch (Exception e) {
            log.error("检查剩余数据时发生错误", e);
            return false;
        }
    }

    public Map<String, Object> getCurrentAnalysis() {
        try {
            // 如果没有分析结果，立即触发一次分析
            if (currentAnalysis.isEmpty()) {
                log.info("当前没有分析结果，触发新的分析...");
                processBatch();
            }
            
            Map<String, Object> result = new HashMap<>(currentAnalysis);
            log.info("返回库存分析结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("获取分析结果时发生错误", e);
            return new HashMap<>();
        }
    }

    public void resetAnalysis() {
        currentBatch = 0;
        batchResults.clear();
        isProcessing = false;
    }
} 