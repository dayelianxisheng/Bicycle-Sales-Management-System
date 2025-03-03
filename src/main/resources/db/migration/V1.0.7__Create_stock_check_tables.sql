-- 创建库存盘点主表
CREATE TABLE stock_check (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    check_no VARCHAR(32) NOT NULL COMMENT '盘点单号',
    check_time DATETIME NOT NULL COMMENT '盘点时间',
    operator VARCHAR(50) NOT NULL COMMENT '盘点人',
    total_products INT NOT NULL DEFAULT 0 COMMENT '商品总数',
    diff_count INT NOT NULL DEFAULT 0 COMMENT '差异数',
    status VARCHAR(20) NOT NULL COMMENT '状态：PENDING-待盘点，IN_PROGRESS-盘点中，COMPLETED-已完成',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_check_no (check_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点主表';

-- 创建库存盘点明细表
CREATE TABLE stock_check_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    check_id BIGINT NOT NULL COMMENT '盘点ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    system_stock INT NOT NULL COMMENT '系统库存',
    actual_stock INT NOT NULL COMMENT '实际库存',
    diff_quantity INT NOT NULL COMMENT '差异数量',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_check_id (check_id),
    KEY idx_product_id (product_id),
    FOREIGN KEY (check_id) REFERENCES stock_check(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点明细表'; 