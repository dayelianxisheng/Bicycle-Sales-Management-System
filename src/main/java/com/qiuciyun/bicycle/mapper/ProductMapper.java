package com.qiuciyun.bicycle.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiuciyun.bicycle.entity.Product;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    @Select({
        "<script>",
        "SELECT * FROM product",
        "WHERE deleted = 0",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (name LIKE CONCAT('%', #{keyword}, '%')",
        "  OR brand LIKE CONCAT('%', #{keyword}, '%')",
        "  OR model LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "<if test='categoryId != null'>",
        "  AND category_id = #{categoryId}",
        "</if>",
        "<if test='status != null'>",
        "  AND status = #{status}",
        "</if>",
        "ORDER BY create_time DESC",
        "</script>"
    })
    List<Product> findAll();

    @Select({
        "<script>",
        "SELECT COUNT(*) FROM product",
        "WHERE deleted = 0",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (name LIKE CONCAT('%', #{keyword}, '%')",
        "  OR brand LIKE CONCAT('%', #{keyword}, '%')",
        "  OR model LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "<if test='categoryId != null'>",
        "  AND category_id = #{categoryId}",
        "</if>",
        "<if test='status != null'>",
        "  AND status = #{status}",
        "</if>",
        "</script>"
    })
    long count(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("status") Integer status);

    @Select({
        "<script>",
        "SELECT * FROM product",
        "WHERE deleted = 0",
        "<if test='keyword != null and keyword != \"\"'>",
        "  AND (name LIKE CONCAT('%', #{keyword}, '%')",
        "  OR brand LIKE CONCAT('%', #{keyword}, '%')",
        "  OR model LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "<if test='categoryId != null'>",
        "  AND category_id = #{categoryId}",
        "</if>",
        "<if test='status != null'>",
        "  AND status = #{status}",
        "</if>",
        "ORDER BY create_time DESC",
        "LIMIT #{offset}, #{size}",
        "</script>"
    })
    List<Product> findPage(@Param("offset") int offset, @Param("size") int size, 
                          @Param("keyword") String keyword, @Param("categoryId") Long categoryId, 
                          @Param("status") Integer status);
    
    @Select("SELECT * FROM product WHERE id = #{id} AND deleted = 0")
    Product findById(Long id);
    
    @Insert("INSERT INTO product (name, brand, model, category_id, price, cost_price, " +
            "stock, warning_stock, description, specs, images, status, sales, " +
            "create_time, update_time, deleted) " +
            "VALUES (#{name}, #{brand}, #{model}, #{categoryId}, #{price}, #{costPrice}, " +
            "#{stock}, #{warningStock}, #{description}, #{specs}, #{images}, " +
            "#{status}, #{sales}, #{createTime}, #{updateTime}, #{deleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);
    
    @Update("UPDATE product SET name = #{name}, brand = #{brand}, model = #{model}, " +
            "category_id = #{categoryId}, price = #{price}, cost_price = #{costPrice}, " +
            "stock = #{stock}, warning_stock = #{warningStock}, description = #{description}, " +
            "specs = #{specs}, images = #{images}, status = #{status}, sales = #{sales}, " +
            "update_time = #{updateTime} WHERE id = #{id} AND deleted = 0")
    int update(Product product);
    
    @Update("UPDATE product SET deleted = 1, update_time = #{updateTime} WHERE id = #{id}")
    int delete(@Param("id") Long id, @Param("updateTime") LocalDateTime updateTime);

    @Select("SELECT COUNT(*) FROM product WHERE deleted = 0")
    Integer selectCount(@Param("wrapper") QueryWrapper<Product> wrapper);

    @Select("SELECT COUNT(*) FROM product WHERE stock <= warning_stock AND deleted = 0")
    Integer countWarningProducts();
    
    @Select("SELECT * FROM product WHERE stock <= warning_stock AND deleted = 0")
    List<Product> selectWarningProducts();
    
    @Select("SELECT SUM(stock * price) FROM product WHERE deleted = 0")
    BigDecimal calculateTotalStockAmount();

    @Select("SELECT * FROM product WHERE " +
            "(#{keyword} IS NULL OR (name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR brand LIKE CONCAT('%', #{keyword}, '%') " +
            "OR model LIKE CONCAT('%', #{keyword}, '%'))) " +
            "AND deleted = 0 " +
            "AND stock <= warning_stock " +
            "ORDER BY stock ASC " +
            "LIMIT #{offset}, #{size}")
    List<Product> findWarningProductsPage(@Param("keyword") String keyword, 
                                        @Param("offset") Integer offset, 
                                        @Param("size") Integer size);

    @Select("SELECT COUNT(*) FROM product WHERE " +
            "(#{keyword} IS NULL OR (name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR brand LIKE CONCAT('%', #{keyword}, '%') " +
            "OR model LIKE CONCAT('%', #{keyword}, '%'))) " +
            "AND deleted = 0 " +
            "AND stock <= warning_stock")
    Long countWarningProductsWithKeyword(@Param("keyword") String keyword);
} 