//package com.qiuciyun.login.utils;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class Result<T> {
//    private int code;          // 状态码
//    private String message;    // 消息
//    private T data;           // 数据
//
//    public static <T> Result<T> success(T data) {
//        return new Result<>(200, "成功", data);
//    }
//
//    public static <T> Result<T> error(String message) {
//        return new Result<>(500, message, null);
//    }
//
//    public static Result<String> success(String message) {
//        return new Result<>(200, message, null);
//    }
//}