-- 为每个商品生成一条初始入库记录
INSERT INTO stock_record (product_id, amount, type, operate_time, remark)
SELECT 
    id as product_id,
    stock as amount,
    'IN' as type,
    create_time as operate_time,
    '初始库存' as remark
FROM product
WHERE deleted = 0 AND stock > 0; 