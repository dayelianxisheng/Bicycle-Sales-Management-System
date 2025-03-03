package com.qiuciyun.spark.service;

import com.qiuciyun.spark.config.SparkStreamingManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.*;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@DependsOn("sparkStreamingManager")
public class SparkService implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient final SparkSession sparkSession;
    private transient final SparkStreamingManager streamingManager;
    private transient final SimpMessagingTemplate messagingTemplate;
    private transient final Map<String, ProductAnalysis> currentAnalysis = new ConcurrentHashMap<>();
    private volatile boolean isRunning = true;

    // 用于存储批次数据
    private static final List<Tuple2<String, Double>> batchData = new ArrayList<>();
    private static final Map<String, Double> accumulatedPrices = new ConcurrentHashMap<>();
    private static final Map<String, Long> accumulatedCounts = new ConcurrentHashMap<>();

    @Autowired
    public SparkService(SparkSession sparkSession, SparkStreamingManager streamingManager, 
                       SimpMessagingTemplate messagingTemplate) {
        this.sparkSession = sparkSession;
        this.streamingManager = streamingManager;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            // 启动流处理
            startStreamingAnalysis();
            
            log.info("SparkService initialized with Spark version: {}", sparkSession.version());
        } catch (Exception e) {
            log.error("初始化 SparkService 时发生错误", e);
            throw e;
        }
    }

    private void startStreamingAnalysis() {
        try {
            // 创建socket流，连接到数据发送服务
            JavaDStream<String> socketStream = streamingManager.createSocketTextStream("localhost", 9998);
            
            // 处理每个RDD
            RDDProcessor processor = new RDDProcessor(messagingTemplate, currentAnalysis);
            socketStream.foreachRDD(processor);
            
            log.info("流分析服务已启动，正在监听端口 9998");
        } catch (Exception e) {
            log.error("启动流分析服务时发生错误", e);
            throw e;
        }
    }

    @PreDestroy
    public void cleanup() {
        isRunning = false;
    }

    public static class ProductAnalysis implements Serializable {
        private static final long serialVersionUID = 1L;
        private String priceCategory;
        private long productCount;
        private BigDecimal avgPrice;
        private List<String> recentProducts; // 新增：记录最近处理的产品

        public ProductAnalysis(String priceCategory, long productCount, BigDecimal avgPrice, List<String> recentProducts) {
            this.priceCategory = priceCategory;
            this.productCount = productCount;
            this.avgPrice = avgPrice;
            this.recentProducts = recentProducts;
        }

        public String getPriceCategory() { return priceCategory; }
        public long getProductCount() { return productCount; }
        public BigDecimal getAvgPrice() { return avgPrice; }
        public List<String> getRecentProducts() { return recentProducts; }

        @Override
        public String toString() {
            return String.format("类别: %s, 数量: %d, 平均价格: %s, 最近产品: %s", 
                priceCategory, productCount, avgPrice, recentProducts);
        }
    }

    private static class RDDProcessor implements VoidFunction<JavaRDD<String>>, Serializable {
        private static final long serialVersionUID = 1L;
        private final transient SimpMessagingTemplate messagingTemplate;
        private final transient Map<String, ProductAnalysis> currentAnalysis;

        public RDDProcessor(SimpMessagingTemplate messagingTemplate, Map<String, ProductAnalysis> currentAnalysis) {
            this.messagingTemplate = messagingTemplate;
            this.currentAnalysis = currentAnalysis;
        }

        private String getPriceCategory(double price) {
            if (price <= 500) return "Low";
            else if (price <= 1000) return "Medium";
            else return "High";
        }

        @Override
        public void call(JavaRDD<String> rdd) {
            if (!rdd.isEmpty()) {
                try {
                    // 收集本批次的数据
                    List<String> batchLines = rdd.collect();
                    log.info("收到新的批次数据，共 {} 条记录", batchLines.size());

                    // 处理每条数据
                    for (String line : batchLines) {
                        String[] parts = line.split(",");
                        String productId = parts[0];
                        double price = Double.parseDouble(parts[1]);
                        String category = getPriceCategory(price);
                        
                        // 更新累积数据
                        accumulatedPrices.merge(category, price, Double::sum);
                        accumulatedCounts.merge(category, 1L, Long::sum);
                        
                        // 添加到批次数据
                        batchData.add(new Tuple2<>(productId, price));

                        // 打印接收到的数据
                        log.info("接收到数据 - 产品ID: {}, 价格: {}, 类别: {}", 
                            productId, price, category);
                    }

                    // 分析结果
                    Map<String, List<String>> categoryProducts = new HashMap<>();
                    List<ProductAnalysis> analysisResults = new ArrayList<>();

                    // 处理每个价格类别
                    accumulatedCounts.forEach((category, count) -> {
                        double totalPrice = accumulatedPrices.getOrDefault(category, 0.0);
                        BigDecimal avgPrice = count > 0 
                            ? BigDecimal.valueOf(totalPrice / count).setScale(2, BigDecimal.ROUND_HALF_UP)
                            : BigDecimal.ZERO;

                        // 获取该类别最近的产品
                        List<String> recentProducts = new ArrayList<>();
                        for (Tuple2<String, Double> product : batchData) {
                            if (getPriceCategory(product._2).equals(category)) {
                                recentProducts.add(String.format("产品ID:%s,价格:%.2f", 
                                    product._1, product._2));
                            }
                        }

                        ProductAnalysis analysis = new ProductAnalysis(category, count, avgPrice, recentProducts);
                        currentAnalysis.put(category, analysis);
                        analysisResults.add(analysis);

                        // 打印分析结果
                        log.info("价格类别: {}", category);
                        log.info("  - 累计产品数: {}", count);
                        log.info("  - 平均价格: {}", avgPrice);
                        log.info("  - 本批次产品: {}", recentProducts);
                        log.info("----------------------------------------");
                    });

                    // 发送更新到WebSocket
                    if (!analysisResults.isEmpty()) {
                        messagingTemplate.convertAndSend("/topic/product-analysis", analysisResults);
                    }

                    // 保持最近的批次数据（比如最近100条）
                    if (batchData.size() > 100) {
                        batchData.subList(0, batchData.size() - 100).clear();
                    }

                } catch (Exception e) {
                    log.error("处理数据时出错: ", e);
                }
            }
        }
    }

    public List<ProductAnalysis> getCurrentAnalysis() {
        return new ArrayList<>(currentAnalysis.values());
    }
} 