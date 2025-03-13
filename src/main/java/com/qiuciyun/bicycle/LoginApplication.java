package com.qiuciyun.bicycle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.qiuciyun.bicycle.mapper")
@ComponentScan(basePackages = {
        "com.qiuciyun.bicycle.config",
        "com.qiuciyun.bicycle.utils",
        "com.qiuciyun.bicycle.controller",
        "com.qiuciyun.bicycle.service"
})
@EnableTransactionManagement
public class LoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
    }

}
