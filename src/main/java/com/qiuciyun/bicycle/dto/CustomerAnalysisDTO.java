package com.qiuciyun.bicycle.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

public class CustomerAnalysisDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Object> stats;

    public CustomerAnalysisDTO() {
    }

    public Map<String, Object> getStats() {
        return stats;
    }

    public void setStats(Map<String, Object> stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "CustomerAnalysisDTO{" +
                "stats=" + stats +
                '}';
    }
} 