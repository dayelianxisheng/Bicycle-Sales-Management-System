package com.qiuciyun.bicycle.controller;

import com.qiuciyun.bicycle.common.Result;
import com.qiuciyun.bicycle.dto.StockStatsDTO;
import com.qiuciyun.bicycle.dto.StockWarningDTO;
import com.qiuciyun.bicycle.dto.StockInDTO;
import com.qiuciyun.bicycle.dto.StockOutDTO;
import com.qiuciyun.bicycle.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/stats")
    public Result<StockStatsDTO> getStockStats() {
        return Result.success(stockService.getStockStats());
    }

    @GetMapping("/warning")
    public Result<List<StockWarningDTO>> getWarningProducts() {
        return Result.success(stockService.getWarningProducts());
    }

    @GetMapping("/warning/page")
    public Result<Map<String, Object>> getWarningProductsPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(stockService.getWarningProductsPage(page, size, keyword));
    }

    @PostMapping("/in")
    public Result<Void> stockIn(@RequestBody StockInDTO stockInDTO) {
        stockService.stockIn(stockInDTO.getProductId(), stockInDTO.getAmount(), stockInDTO.getRemark());
        return Result.success(null);
    }

    @PostMapping("/out")
    public Result<Void> stockOut(@RequestBody StockOutDTO stockOutDTO) {
        stockService.stockOut(stockOutDTO.getProductId(), stockOutDTO.getAmount(), stockOutDTO.getReason(), stockOutDTO.getRemark());
        return Result.success(null);
    }

    @GetMapping("/records")
    public Result<Map<String, Object>> getStockRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(stockService.getStockRecords(page, size, keyword, type, startDate, endDate));
    }
} 