package com.qiuciyun.bicycle.dto;

import java.util.List;

public class StockCheckPrepareDTO {
    private Integer totalProducts;
    private List<ProductStockDTO> products;

    public Integer getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Integer totalProducts) {
        this.totalProducts = totalProducts;
    }

    public List<ProductStockDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductStockDTO> products) {
        this.products = products;
    }

    public static class ProductStockDTO {
        private Long id;
        private String name;
        private String brand;
        private String model;
        private Integer stock;

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
    }
} 