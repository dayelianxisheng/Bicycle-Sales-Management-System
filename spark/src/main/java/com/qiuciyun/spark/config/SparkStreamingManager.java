package com.qiuciyun.spark.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.StreamingContextState;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SparkStreamingManager {
    private static JavaStreamingContext streamingContext;
    private final JavaSparkContext sparkContext;
    private boolean isInitialized = false;

    public SparkStreamingManager(JavaSparkContext sparkContext) {
        this.sparkContext = sparkContext;
    }

    public synchronized void initialize() {
        if (!isInitialized) {
            log.info("初始化 Spark Streaming 上下文");
            streamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(5));
            isInitialized = true;
        }
    }

    public JavaStreamingContext getStreamingContext() {
        if (!isInitialized) {
            initialize();
        }
        return streamingContext;
    }

    public JavaDStream<String> createSocketTextStream(String host, int port) {
        if (!isInitialized) {
            initialize();
        }
        return streamingContext.socketTextStream(host, port);
    }

    public synchronized void start() {
        try {
            if (!isInitialized) {
                initialize();
            }
            if (streamingContext != null && streamingContext.getState() != StreamingContextState.ACTIVE) {
                log.info("启动 Spark Streaming 上下文");
                streamingContext.start();
            } else {
                log.warn("Spark Streaming 上下文已经在运行中或未初始化");
            }
        } catch (IllegalStateException e) {
            log.warn("Spark Streaming 上下文已经在运行中: {}", e.getMessage());
        }
    }

    public synchronized void stop() {
        if (streamingContext != null) {
            log.info("停止 Spark Streaming 上下文");
            streamingContext.stop(true, true);
            isInitialized = false;
        }
    }

    public synchronized void restart() {
        stop();
        initialize();
    }
} 