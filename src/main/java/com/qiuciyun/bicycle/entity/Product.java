package com.qiuciyun.bicycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;                // 产品ID
    private String name;            // 产品名称
    private String description;     // 产品描述
    private String brand;           // 品牌
    private String model;           // 型号
    private Long categoryId;        // 分类ID
    private BigDecimal price;       // 销售价格
    private BigDecimal costPrice;   // 成本价格
    private Integer stock;          // 库存数量
    private Integer warningStock;   // 库存预警值
    private String specs;           // 规格参数(JSON格式)
    private String images;          // 商品图片(JSON数组)
    private String localImages;     // 本地图片路径(JSON数组)
    private Integer sales;          // 销量
    private Integer status;         // 状态：1-上架，0-下架
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
    private Integer deleted;        // 是否删除：0-未删除，1-已删除

    // 无参构造函数
    public Product() {}

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getWarningStock() {
        return warningStock;
    }

    public void setWarningStock(Integer warningStock) {
        this.warningStock = warningStock;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getLocalImages() {
        return localImages;
    }

    public void setLocalImages(String localImages) {
        this.localImages = localImages;
    }
}