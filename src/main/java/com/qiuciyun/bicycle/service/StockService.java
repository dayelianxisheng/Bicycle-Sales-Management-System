package com.qiuciyun.bicycle.service;

import com.qiuciyun.bicycle.dto.StockStatsDTO;
import com.qiuciyun.bicycle.dto.StockWarningDTO;
import java.util.List;
import java.util.Map;

public interface StockService {
    /**
     * 获取库存统计信息
     */
    StockStatsDTO getStockStats();

    /**
     * 获取库存预警商品列表
     */
    List<StockWarningDTO> getWarningProducts();

    /**
     * 分页查询预警商品
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    Map<String, Object> getWarningProductsPage(int page, int size, String keyword);

    /**
     * 商品入库
     * @param productId 商品ID
     * @param amount 入库数量
     * @param remark 备注
     */
    void stockIn(Long productId, Integer amount, String remark);

    /**
     * 商品出库
     * @param productId 商品ID
     * @param amount 出库数量
     * @param reason 出库原因
     * @param remark 备注
     */
    void stockOut(Long productId, Integer amount, String reason, String remark);

    /**
     * 分页查询库存记录
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param type 记录类型（IN：入库，OUT：出库）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果
     */
    Map<String, Object> getStockRecords(int page, int size, String keyword, String type, String startDate, String endDate);
} 