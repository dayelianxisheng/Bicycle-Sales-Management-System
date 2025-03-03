package com.qiuciyun.spark.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AnalysisTriggerService {

    private ServerSocket serverSocket;
    private PrintWriter out;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        try {
            // 启动Socket服务器
            serverSocket = new ServerSocket(9999);
            log.info("分析触发服务器启动在端口9999");

            // 在后台线程中接受连接
            new Thread(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        out = new PrintWriter(clientSocket.getOutputStream(), true);
                        log.info("Spark Streaming客户端已连接");
                    } catch (IOException e) {
                        if (!serverSocket.isClosed()) {
                            log.error("接受连接时发生错误", e);
                        }
                    }
                }
            }).start();

            // 每5秒发送一次触发信号
            scheduler.scheduleAtFixedRate(() -> {
                if (out != null) {
                    out.println("trigger");
                    log.debug("已发送分析触发信号");
                }
            }, 0, 5, TimeUnit.SECONDS);

        } catch (IOException e) {
            log.error("启动分析触发服务器时发生错误", e);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error("关闭服务器时发生错误", e);
            }
        }
    }
} 