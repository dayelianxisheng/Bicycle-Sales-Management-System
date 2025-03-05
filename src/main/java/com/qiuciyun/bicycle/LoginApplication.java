package com.qiuciyun.bicycle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.qiuciyun.bicycle.mapper")
@ComponentScan(basePackages = {
        "com.qiuciyun.bicycle.config",
        "com.qiuciyun.bicycle.utils",
        "com.qiuciyun.bicycle.controller",
        "com.qiuciyun.bicycle.service"
})
public class LoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
    }

}
