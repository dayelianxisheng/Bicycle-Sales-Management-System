package com.qiuciyun.bicycle.mapper;

import com.qiuciyun.bicycle.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM system_users WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM system_users WHERE deleted = 0 and role!='role_admin'")
    List<User> findAll();

    @Insert("INSERT INTO system_users (username, password, nickname, email, mobile, sex, avatar, status, " +
            "remark, dept_id, post_ids, creator, create_time, updater, update_time, deleted, tenant_id, role) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{email}, #{mobile}, #{sex}, #{avatar}, #{status}, " +
            "#{remark}, #{deptId}, #{postIds}, #{creator}, NOW(), #{updater}, NOW(), 0, 1, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE system_users SET " +
            "username = #{username}, password = #{password}, nickname = #{nickname}, " +
            "email = #{email}, mobile = #{mobile}, sex = #{sex}, avatar = #{avatar}, " +
            "status = #{status}, remark = #{remark}, dept_id = #{deptId}, " +
            "post_ids = #{postIds}, updater = #{updater}, update_time = NOW(), " +
            "role = #{role} " +
            "WHERE id = #{id}")
    void update(User user);

    @Update("UPDATE system_users SET deleted = 1, update_time = NOW() WHERE id = #{id}")
    void delete(@Param("id") Integer id);

    @Update("UPDATE system_users SET login_ip = #{loginIp}, login_date = NOW(), " +
            "update_time = NOW() WHERE id = #{id}")
    void updateLoginInfo(@Param("id") Long id, @Param("loginIp") String loginIp);
}