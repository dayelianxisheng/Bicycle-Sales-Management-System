package com.qiuciyun.bicycle.controller;

import com.qiuciyun.bicycle.common.Result;
import com.qiuciyun.bicycle.entity.User;
import com.qiuciyun.bicycle.model.LoginRequest;
import com.qiuciyun.bicycle.service.UserService;
import com.qiuciyun.bicycle.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // 配置上传文件的存储路径
    private final String uploadDir = "uploads/avatars";

    @GetMapping
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        System.out.println("Fetched users: " + users);
        return Result.success(users);
    }

    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        // 检查用户名是否已存在
        User existingUser = userService.findByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 使用 BCrypt 加密密码
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        
        userService.createUser(user);
        return Result.success(user);
    }

    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        
        // 如果提供了新密码，则使用 BCrypt 加密
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
        }
        
        userService.updateUser(user);
        return Result.success(user);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return Result.success(null);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest loginRequest) {
        // 验证用户
        User user = userService.findByUsername(loginRequest.getUsername());
        if (user == null || !BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            System.out.println("用户名或密码错误");
            return Result.error("用户名或密码错误");
        }

        // 生成 JWT
        String token = jwtUtils.generateToken(user.getUsername());
        System.out.println("生成的JWT: " + token); // 用于调试
        return Result.success(token);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        // 检查用户名是否已存在
        User existingUser = userService.findByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 加密密码
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        
        // 保存用户到数据库
        userService.createUser(user);
        return Result.success(user);
    }

    @GetMapping("/profile")
    public Result<User> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 检查 token 是否存在
            if (token == null || token.isEmpty()) {
                System.out.println("Token 不存在");
                return Result.error("未登录");
            }

            // 处理 Bearer token
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 验证 token 并获取用户名
            String username = jwtUtils.validateTokenAndGetUsername(token);
            System.out.println("Token 解析出的用户名: " + username);
            
            if (username == null) {
                System.out.println("Token 无效");
                return Result.error("Token无效");
            }

            // 获取用户信息
            User user = userService.findByUsername(username);
            if (user == null) {
                System.out.println("未找到用户: " + username);
                return Result.error("用户不存在");
            }

            // 出于安全考虑，清除密码字段
            user.setPassword(null);
            System.out.println("成功获取用户信息: " + username);
            return Result.success(user);
            
        } catch (Exception e) {
            System.out.println("获取用户信息时发生错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取用户信息失败");
        }
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestHeader("Authorization") String token, @RequestBody User updateUser) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        String username = jwtUtils.validateTokenAndGetUsername(token);
        if (username != null) {
            User currentUser = userService.findByUsername(username);
            if (currentUser != null) {
                // 更新允许的字段
                currentUser.setNickname(updateUser.getNickname());
                currentUser.setEmail(updateUser.getEmail());
                currentUser.setMobile(updateUser.getMobile());
                currentUser.setSex(updateUser.getSex());
                currentUser.setUpdateTime(new Date());
                
                userService.updateUser(currentUser);
                
                // 出于安全考虑，清除密码字段
                currentUser.setPassword(null);
                return Result.success(currentUser);
            }
        }
        return Result.error("更新用户信息失败");
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody PasswordUpdateRequest passwordRequest) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String username = jwtUtils.validateTokenAndGetUsername(token);
            if (username == null) {
                return Result.error("Token无效");
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 验证旧密码
            if (!BCrypt.checkpw(passwordRequest.getOldPassword(), user.getPassword())) {
                return Result.error("旧密码错误");
            }

            // 更新新密码
            user.setPassword(BCrypt.hashpw(passwordRequest.getNewPassword(), BCrypt.gensalt()));
            user.setUpdateTime(new Date());
            userService.updateUser(user);

            return Result.success(null);
        } catch (Exception e) {
            System.out.println("修改密码失败: " + e.getMessage());
            return Result.error("修改密码失败");
        }
    }

    @PutMapping("/avatar")
    public Result<String> updateAvatar(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String username = jwtUtils.validateTokenAndGetUsername(token);
            if (username == null) {
                return Result.error("Token无效");
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }

            // 检查文件大小（限制为2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("文件大小不能超过2MB");
            }

            // 创建上传目录
            String uploadDir = "uploads/avatars";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            // 删除旧头像文件（如果存在）
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                String oldAvatarPath = user.getAvatar().replace("/uploads/", "");
                File oldAvatar = new File(oldAvatarPath);
                if (oldAvatar.exists()) {
                    oldAvatar.delete();
                }
            }

            // 生成新的文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = Paths.get(uploadDir, newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 更新用户头像路径
            String avatarUrl = "/uploads/avatars/" + newFilename;
            user.setAvatar(avatarUrl);
            user.setUpdateTime(new Date());
            userService.updateUser(user);

            return Result.success(avatarUrl);
        } catch (IOException e) {
            System.out.println("上传头像失败: " + e.getMessage());
            return Result.error("上传头像失败: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("处理头像上传请求失败: " + e.getMessage());
            return Result.error("上传头像失败");
        }
    }

    // 添加密码更新请求类
    public static class PasswordUpdateRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}