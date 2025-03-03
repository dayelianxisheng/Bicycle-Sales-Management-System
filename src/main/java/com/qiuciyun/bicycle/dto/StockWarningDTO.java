package com.qiuciyun.bicycle.dto;

import java.math.BigDecimal;

public class StockWarningDTO {
    private Long id;                // 商品ID
    private String name;            // 商品名称
    private String brand;           // 品牌
    private String model;           // 型号
    private Integer stock;          // 当前库存
    private Integer warningStock;   // 预警阈值
    private BigDecimal price;       // 商品单价

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
} 