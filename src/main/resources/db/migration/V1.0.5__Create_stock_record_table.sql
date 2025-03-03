CREATE TABLE stock_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '商品ID',
    amount INT NOT NULL COMMENT '数量',
    type VARCHAR(10) NOT NULL COMMENT '类型：IN-入库，OUT-出库',
    operate_time DATETIME NOT NULL COMMENT '操作时间',
    remark VARCHAR(255) COMMENT '备注',
    FOREIGN KEY (product_id) REFERENCES product(id)
) COMMENT '库存记录表'; 