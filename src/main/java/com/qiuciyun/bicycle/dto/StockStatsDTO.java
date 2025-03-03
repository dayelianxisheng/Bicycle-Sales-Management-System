package com.qiuciyun.bicycle.dto;

import java.math.BigDecimal;

public class StockStatsDTO {
    private Integer totalProducts;      // 商品总数
    private Integer warningCount;       // 预警商品数量
    private Integer monthlyIn;          // 本月入库数量
    private BigDecimal totalAmount;     // 库存总金额

    public Integer getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Integer totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Integer getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount) {
        this.warningCount = warningCount;
    }

    public Integer getMonthlyIn() {
        return monthlyIn;
    }

    public void setMonthlyIn(Integer monthlyIn) {
        this.monthlyIn = monthlyIn;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
} 