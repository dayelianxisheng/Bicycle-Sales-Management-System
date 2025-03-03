package com.qiuciyun.bicycle.service;

import com.qiuciyun.bicycle.entity.User;

import java.util.List;

public interface UserService {
    User login(String username, String password);
    List<User> getAllUsers(); // 获取所有用户
    void createUser(User user);
    void updateUser(User user);
    void deleteUser(Integer id);
    User findByUsername(String username);
}