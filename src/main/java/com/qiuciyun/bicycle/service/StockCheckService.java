package com.qiuciyun.bicycle.service;

import com.qiuciyun.bicycle.entity.StockCheck;
import com.qiuciyun.bicycle.entity.StockCheckItem;
import com.qiuciyun.bicycle.dto.StockCheckPrepareDTO;
import java.util.List;
import java.util.Map;

public interface StockCheckService {
    /**
     * 分页查询盘点记录
     */
    Map<String, Object> getStockCheckList(int page, int size, String status, String startTime, String endTime);

    /**
     * 获取盘点详情
     */
    StockCheck getStockCheckDetail(Long id);

    /**
     * 获取盘点明细
     */
    List<StockCheckItem> getStockCheckItems(Long checkId);

    /**
     * 创建新的盘点单
     */
    StockCheck createStockCheck(String operator);

    /**
     * 更新盘点明细
     */
    void updateStockCheckItem(Long checkId, Long productId, Integer actualStock, String remark);

    /**
     * 完成盘点
     */
    void completeStockCheck(Long checkId);

    /**
     * 准备盘点数据
     */
    StockCheckPrepareDTO prepareStockCheck();
} 