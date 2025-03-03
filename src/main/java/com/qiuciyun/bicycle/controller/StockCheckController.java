package com.qiuciyun.bicycle.controller;

import com.qiuciyun.bicycle.common.Result;
import com.qiuciyun.bicycle.entity.StockCheck;
import com.qiuciyun.bicycle.entity.StockCheckItem;
import com.qiuciyun.bicycle.service.StockCheckService;
import com.qiuciyun.bicycle.dto.StockCheckPrepareDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock/check")
public class StockCheckController {

    @Autowired
    private StockCheckService stockCheckService;

    @GetMapping("/list")
    public Result<Map<String, Object>> getStockCheckList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return Result.success(stockCheckService.getStockCheckList(page, size, status, startTime, endTime));
    }

    @GetMapping("/{id}")
    public Result<StockCheck> getStockCheckDetail(@PathVariable Long id) {
        return Result.success(stockCheckService.getStockCheckDetail(id));
    }

    @GetMapping("/{checkId}/items")
    public Result<List<StockCheckItem>> getStockCheckItems(@PathVariable Long checkId) {
        return Result.success(stockCheckService.getStockCheckItems(checkId));
    }

    @GetMapping("/prepare")
    public Result<StockCheckPrepareDTO> prepareStockCheck() {
        return Result.success(stockCheckService.prepareStockCheck());
    }

    @PostMapping
    public Result<StockCheck> createStockCheck(@RequestParam String operator) {
        return Result.success(stockCheckService.createStockCheck(operator));
    }

    @PutMapping("/{checkId}/items")
    public Result<Void> updateStockCheckItem(
            @PathVariable Long checkId,
            @RequestParam Long productId,
            @RequestParam Integer actualStock,
            @RequestParam(required = false) String remark) {
        stockCheckService.updateStockCheckItem(checkId, productId, actualStock, remark);
        return Result.success(null);
    }

    @PostMapping("/{checkId}/complete")
    public Result<Void> completeStockCheck(@PathVariable Long checkId) {
        stockCheckService.completeStockCheck(checkId);
        return Result.success(null);
    }
} 