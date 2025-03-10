package com.qiuciyun.spark.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
            .setAppName("bicycle-spark")
            .setMaster("local[*]");
    }

    @Bean
    public SparkSession sparkSession(SparkConf sparkConf) {
        return SparkSession.builder()
            .config(sparkConf)
            .getOrCreate();
    }

    @Bean
    public JavaSparkContext javaSparkContext(SparkSession sparkSession) {
        return JavaSparkContext.fromSparkContext(sparkSession.sparkContext());
    }
} 