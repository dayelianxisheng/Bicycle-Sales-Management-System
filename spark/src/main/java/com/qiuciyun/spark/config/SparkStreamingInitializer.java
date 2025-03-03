package com.qiuciyun.spark.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Configuration
@DependsOn({"sparkService", "stockSparkService", "orderSparkService", "customerSparkService"})
public class SparkStreamingInitializer {

    private final SparkStreamingManager streamingManager;

    @Autowired
    public SparkStreamingInitializer(SparkStreamingManager streamingManager) {
        this.streamingManager = streamingManager;
    }

    @PostConstruct
    public void init() {
        try {
            log.info("正在启动 Spark Streaming 服务...");
            streamingManager.start();
            log.info("Spark Streaming 服务已启动");
        } catch (Exception e) {
            log.error("启动 Spark Streaming 服务时发生错误", e);
            throw e;
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            log.info("正在停止 Spark Streaming 服务...");
            streamingManager.stop();
            log.info("Spark Streaming 服务已停止");
        } catch (Exception e) {
            log.error("停止 Spark Streaming 服务时发生错误", e);
        }
    }
} 