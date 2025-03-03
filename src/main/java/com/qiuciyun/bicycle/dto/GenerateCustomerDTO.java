package com.qiuciyun.bicycle.dto;

import java.io.Serializable;
import java.util.List;

public class GenerateCustomerDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer count;
    private List<String> regions;

    public GenerateCustomerDTO() {
    }

    public GenerateCustomerDTO(Integer count, List<String> regions) {
        this.count = count;
        this.regions = regions;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    @Override
    public String toString() {
        return "GenerateCustomerDTO{" +
                "count=" + count +
                ", regions=" + regions +
                '}';
    }
} 