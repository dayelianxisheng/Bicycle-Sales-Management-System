package com.qiuciyun.spark.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class DataEmitter {
    private ServerSocket serverSocket;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicInteger currentPosition = new AtomicInteger(0);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            serverSocket = new ServerSocket(9998);
            log.info("数据发送服务启动在端口 9998");
            
            executorService.submit(() -> {
                while (running.get()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        if (running.get()) {
                            log.error("处理客户端连接时出错", e);
                        }
                    }
                }
            });
        } catch (IOException e) {
            log.error("启动数据发送服务失败", e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            log.info("客户端已连接，开始发送数据");
            
            while (running.get() && !clientSocket.isClosed()) {
                // 每次获取5条数据
                List<Map<String, Object>> products = jdbcTemplate.queryForList(
                    "SELECT id, price FROM product ORDER BY id LIMIT ?, 5",
                    currentPosition.get()
                );

                if (products.isEmpty()) {
                    log.info("没有找到数据，当前位置: {}", currentPosition.get());
                    // 如果没有更多数据，重置位置从头开始
                    currentPosition.set(0);
                    log.info("数据已读取完毕，重置位置从头开始");
                    Thread.sleep(5000); // 等待5秒后重新开始
                    continue;
                } else {
                    log.info("查询到 {} 条数据", products.size());
                    log.info("数据内容: {}", products);
                }

                log.info("正在发送第 {} 到 {} 条数据", currentPosition.get() + 1, 
                    currentPosition.get() + products.size());

                // 逐条发送数据
                for (Map<String, Object> product : products) {
                    String data = String.format("%s,%.2f", 
                        product.get("id"), 
                        ((Number) product.get("price")).doubleValue()
                    );
                    out.println(data);
                    out.flush();
                    log.info("已发送数据: {}", data);
                    Thread.sleep(2000); // 每条数据间隔2秒
                }

                // 更新位置
                currentPosition.addAndGet(products.size());
                
                // 等待一段时间后再发送下一批
                Thread.sleep(3000);
            }
        } catch (IOException | InterruptedException e) {
            if (running.get()) {
                log.error("处理客户端数据时出错", e);
            }
        } finally {
            try {
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                log.error("关闭客户端连接时出错", e);
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        running.set(false);
        executorService.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log.error("关闭数据发送服务时出错", e);
        }
    }
} 