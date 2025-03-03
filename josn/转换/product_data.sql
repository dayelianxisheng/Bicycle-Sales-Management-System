-- 商品数据导入脚本
-- 生成时间: 2025-01-12 10:43:49

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- E-Bike 类商品
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('FRENETICA-CITY电助力', 'JAVA', 'FRENETICA-CITY电助力', 1, 6999, 4899.3,
     100, 10, 'JAVA FRENETICA-CITY电助力', '{"类别": "E-Bike", "品牌": "JAVA", "型号": "FRENETICA-CITY电助力", "产品链接": "https://www.javabikes.com/ProductsDetail/24.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/8d73394c-86c8-48af-9556-66029bb92a8e.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('GRAVEL电助力', 'JAVA', 'GRAVEL电助力', 1, 7999, 5599.3,
     100, 10, 'JAVA GRAVEL电助力', '{"类别": "E-Bike", "品牌": "JAVA", "型号": "GRAVEL电助力", "产品链接": "https://www.javabikes.com/ProductsDetail/25.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/af1e7150-b1a4-4bbc-971b-c62299a72828.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('软尾电助力E-SUS01A', 'JAVA', '软尾电助力E-SUS01A', 1, 9999, 6999.3,
     100, 10, 'JAVA 软尾电助力E-SUS01A', '{"类别": "E-Bike", "品牌": "JAVA", "型号": "软尾电助力E-SUS01A", "产品链接": "https://www.javabikes.com/ProductsDetail/28.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/e42a4606-4365-4db7-a67c-095ec7174704.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('E-SUS06A', 'JAVA', 'E-SUS06A', 1, 13000, 9100.0,
     100, 10, 'JAVA E-SUS06A', '{"类别": "E-Bike", "品牌": "JAVA", "型号": "E-SUS06A", "产品链接": "https://www.javabikes.com/ProductsDetail/169.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/0404c26c-fab5-4094-b26e-409ef461011d.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('E-SUS03A', 'JAVA', 'E-SUS03A', 1, 13999, 9799.3,
     100, 10, 'JAVA E-SUS03A', '{"类别": "E-Bike", "品牌": "JAVA", "型号": "E-SUS03A", "产品链接": "https://www.javabikes.com/ProductsDetail/168.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/4eb168bd-13d8-49f1-90f5-eda1cbe5d436.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('公路电助力-SCATTO', 'JAVA', '公路电助力-SCATTO', 1, 30000, 21000.0,
     100, 10, 'JAVA 公路电助力-SCATTO', '{"类别": "E-Bike", "品牌": "JAVA", "型号": "公路电助力-SCATTO", "产品链接": "https://www.javabikes.com/ProductsDetail/27.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/7ddbe00b-5ea2-41af-b30b-0a3678a384b4.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('三轮车电助力', 'JAVA', '三轮车电助力', 1, 30000, 21000.0,
     100, 10, 'JAVA 三轮车电助力', '{"类别": "E-Bike", "品牌": "JAVA", "型号": "三轮车电助力", "产品链接": "https://www.javabikes.com/ProductsDetail/29.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/efd42c7e-245c-456f-80e1-eefaa26dd61e.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('GRAVEL电助力-FANGO', 'JAVA', 'GRAVEL电助力-FANGO', 1, 33000, 23100.0,
     100, 10, 'JAVA GRAVEL电助力-FANGO', '{"类别": "E-Bike", "品牌": "JAVA", "型号": "GRAVEL电助力-FANGO", "产品链接": "https://www.javabikes.com/ProductsDetail/26.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/2bbc4527-d196-4332-a181-e7911a7a1ccf.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);

-- 公路车 类商品
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('沃小白-平把', 'JAVA', '沃小白-平把', 2, 999, 699.3,
     100, 10, 'JAVA 沃小白-平把', '{"类别": "公路车", "品牌": "JAVA", "型号": "沃小白-平把", "产品链接": "https://www.javabikes.com/ProductsDetail/4830.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/7eeaf3d5-1ac6-4c2f-8fbd-bea9dbe1f79e.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('沃小黑', 'JAVA', '沃小黑', 2, 1499, 1049.3,
     100, 10, 'JAVA 沃小黑', '{"类别": "公路车", "品牌": "JAVA", "型号": "沃小黑", "产品链接": "https://www.javabikes.com/ProductsDetail/4807.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/e2d62708-4697-45f2-9fe8-847b2a9f7f43.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('沃小白', 'JAVA', '沃小白', 2, 1499, 1049.3,
     100, 10, 'JAVA 沃小白', '{"类别": "公路车", "品牌": "JAVA", "型号": "沃小白", "产品链接": "https://www.javabikes.com/ProductsDetail/3862.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/8e342e27-a125-4a33-9b97-9372fa122436.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('VELOCE-平把', 'JAVA', 'VELOCE-平把', 2, 1599, 1119.3,
     100, 10, 'JAVA VELOCE-平把', '{"类别": "公路车", "品牌": "JAVA", "型号": "VELOCE-平把", "产品链接": "https://www.javabikes.com/ProductsDetail/4819.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/3f9898a6-d1c1-40d0-9b9b-2675557a1733.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('RAPIDA', 'JAVA', 'RAPIDA', 2, 1899, 1329.3,
     100, 10, 'JAVA RAPIDA', '{"类别": "公路车", "品牌": "JAVA", "型号": "RAPIDA", "产品链接": "https://www.javabikes.com/ProductsDetail/3875.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/7d135ec0-9520-457f-893c-39a2a029376e.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('VELOCE', 'JAVA', 'VELOCE', 2, 2299, 1609.3,
     100, 10, 'JAVA VELOCE', '{"类别": "公路车", "品牌": "JAVA", "型号": "VELOCE", "产品链接": "https://www.javabikes.com/ProductsDetail/171.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/dccd4828-1a79-4409-975e-abcd97f19e88.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('SEQUOIA（红杉）', 'JAVA', 'SEQUOIA（红杉）', 2, 2399, 1679.3,
     100, 10, 'JAVA SEQUOIA（红杉）', '{"类别": "公路车", "品牌": "JAVA", "型号": "SEQUOIA（红杉）", "产品链接": "https://www.javabikes.com/ProductsDetail/3893.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/e19ac4dc-032c-421e-aba5-de7299629472.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('RONDA', 'JAVA', 'RONDA', 2, 2560, 1792.0,
     100, 10, 'JAVA RONDA', '{"类别": "公路车", "品牌": "JAVA", "型号": "RONDA", "产品链接": "https://www.javabikes.com/ProductsDetail/170.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/96ff2a31-663a-4ad6-bfe8-570b20d22112.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('SENTIERO', 'JAVA', 'SENTIERO', 2, 2600, 1820.0,
     100, 10, 'JAVA SENTIERO', '{"类别": "公路车", "品牌": "JAVA", "型号": "SENTIERO", "产品链接": "https://www.javabikes.com/ProductsDetail/183.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/a356e2ce-ae94-4aaa-9e5b-ce749f46d54a.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('AURIGA-22s', 'JAVA', 'AURIGA-22s', 2, 2999, 2099.3,
     100, 10, 'JAVA AURIGA-22s', '{"类别": "公路车", "品牌": "JAVA", "型号": "AURIGA-22s", "产品链接": "https://www.javabikes.com/ProductsDetail/3906.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/fe4ea924-4ab0-43ea-a1bb-8a589ed4d2dc.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('AURIGA-18s', 'JAVA', 'AURIGA-18s', 2, 2699, 1889.3,
     100, 10, 'JAVA AURIGA-18s', '{"类别": "公路车", "品牌": "JAVA", "型号": "AURIGA-18s", "产品链接": "https://www.javabikes.com/ProductsDetail/30.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/c4135523-07f2-45af-83b5-e3a5da32123a.JPG"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('IDRA', 'JAVA', 'IDRA', 2, 3690, 2583.0,
     100, 10, 'JAVA IDRA', '{"类别": "公路车", "品牌": "JAVA", "型号": "IDRA", "产品链接": "https://www.javabikes.com/ProductsDetail/32.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/781c2936-bdb2-4369-9b4e-c6d440cc3fad.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('SILURO6-TOP', 'JAVA', 'SILURO6-TOP', 2, 3999, 2799.3,
     100, 10, 'JAVA SILURO6-TOP', '{"类别": "公路车", "品牌": "JAVA", "型号": "SILURO6-TOP", "产品链接": "https://www.javabikes.com/ProductsDetail/74.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/2d051813-47be-4e64-a28e-726d6d6077ed.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('FUOCO TOP 铝轮版', 'JAVA', 'FUOCO TOP 铝轮版', 2, 5999, 4199.3,
     100, 10, 'JAVA FUOCO TOP 铝轮版', '{"类别": "公路车", "品牌": "JAVA", "型号": "FUOCO TOP 铝轮版", "产品链接": "https://www.javabikes.com/ProductsDetail/3930.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/52204a5d-2754-447c-872a-866fa4702a07.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('FUOCO TOP-24S碳轮版', 'JAVA', 'FUOCO TOP-24S碳轮版', 2, 6999, 4899.3,
     100, 10, 'JAVA FUOCO TOP-24S碳轮版', '{"类别": "公路车", "品牌": "JAVA", "型号": "FUOCO TOP-24S碳轮版", "产品链接": "https://www.javabikes.com/ProductsDetail/31.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/6a8eca9b-a69b-4991-b06b-439ab0ef8488.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('VESUVIO-铝轮电变版', 'JAVA', 'VESUVIO-铝轮电变版', 2, 7999, 5599.3,
     100, 10, 'JAVA VESUVIO-铝轮电变版', '{"类别": "公路车", "品牌": "JAVA", "型号": "VESUVIO-铝轮电变版", "产品链接": "https://www.javabikes.com/ProductsDetail/3941.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/2fcaafe4-2a72-4243-9349-1cbbec9c1eef.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('VESUVIO', 'JAVA', 'VESUVIO', 2, 8999, 6299.3,
     100, 10, 'JAVA VESUVIO', '{"类别": "公路车", "品牌": "JAVA", "型号": "VESUVIO", "产品链接": "https://www.javabikes.com/ProductsDetail/173.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/82ac6363-45a0-4e76-ba9d-d4d78682a5e0.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('VOLATA-铝轮电变版', 'JAVA', 'VOLATA-铝轮电变版', 2, 9999, 6999.3,
     100, 10, 'JAVA VOLATA-铝轮电变版', '{"类别": "公路车", "品牌": "JAVA", "型号": "VOLATA-铝轮电变版", "产品链接": "https://www.javabikes.com/ProductsDetail/3954.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/505f2b34-397f-4f63-978c-428bcc57c69a.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('VOLATA碳轮', 'JAVA', 'VOLATA碳轮', 2, 12999, 9099.3,
     100, 10, 'JAVA VOLATA碳轮', '{"类别": "公路车", "品牌": "JAVA", "型号": "VOLATA碳轮", "产品链接": "https://www.javabikes.com/ProductsDetail/172.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/cdbc8b76-dc13-420b-85cc-3cb8e9d9d7d4.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('SUPREMA', 'JAVA', 'SUPREMA', 2, 9999, 6999.3,
     100, 10, 'JAVA SUPREMA', '{"类别": "公路车", "品牌": "JAVA", "型号": "SUPREMA", "产品链接": "https://www.javabikes.com/ProductsDetail/174.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/899e968e-502a-4ea6-be6e-f8a652a8c74c.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('TT SCIA', 'JAVA', 'TT SCIA', 2, 12999, 9099.3,
     100, 10, 'JAVA TT SCIA', '{"类别": "公路车", "品牌": "JAVA", "型号": "TT SCIA", "产品链接": "https://www.javabikes.com/ProductsDetail/182.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/608c58a7-87bd-44c3-8f81-34b455205318.JPG"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('LANCIA', 'JAVA', 'LANCIA', 2, 14999, 10499.3,
     100, 10, 'JAVA LANCIA', '{"类别": "公路车", "品牌": "JAVA", "型号": "LANCIA", "产品链接": "https://www.javabikes.com/ProductsDetail/181.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/cbdd20bb-51b7-4573-9faa-24871748af03.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);

-- 山地车 类商品
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('SABBIA铝合金避震车', 'JAVA', 'SABBIA铝合金避震车', 3, 3099, 2169.3,
     100, 10, 'JAVA SABBIA铝合金避震车', '{"类别": "山地车", "品牌": "JAVA", "型号": "SABBIA铝合金避震车", "产品链接": "https://www.javabikes.com/ProductsDetail/36.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/6313940f-5a88-4bff-b062-d4f19ad5b46a.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('DOLOMIA', 'JAVA', 'DOLOMIA', 3, 2999, 2099.3,
     100, 10, 'JAVA DOLOMIA', '{"类别": "山地车", "品牌": "JAVA", "型号": "DOLOMIA", "产品链接": "https://www.javabikes.com/ProductsDetail/34.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/aef932a6-4547-4847-84fc-94bc2cea4455.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('SUOH', 'JAVA', 'SUOH', 3, 4900, 3430.0,
     100, 10, 'JAVA SUOH', '{"类别": "山地车", "品牌": "JAVA", "型号": "SUOH", "产品链接": "https://www.javabikes.com/ProductsDetail/2589.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/ecbad499-fd5b-4a14-acd0-c63d1c0952c2.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('VETTA', 'JAVA', 'VETTA', 3, 3999, 2799.3,
     100, 10, 'JAVA VETTA', '{"类别": "山地车", "品牌": "JAVA", "型号": "VETTA", "产品链接": "https://www.javabikes.com/ProductsDetail/184.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/4495e376-37d1-4710-8fc2-d684ab4c8215.JPG"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('SALTAFOSSI-SUS', 'JAVA', 'SALTAFOSSI-SUS', 3, 5999, 4199.3,
     100, 10, 'JAVA SALTAFOSSI-SUS', '{"类别": "山地车", "品牌": "JAVA", "型号": "SALTAFOSSI-SUS", "产品链接": "https://www.javabikes.com/ProductsDetail/37.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/941d17e2-ac62-45a4-bd7d-e4c4b819bb40.JPG"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);

-- 童车-KSR 类商品
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('童车-KSR', 'JAVA', '童车-KSR', 4, 1600, 1120.0,
     100, 10, 'JAVA 童车-KSR', '{"类别": "童车-KSR", "品牌": "JAVA", "型号": "童车-KSR", "产品链接": "https://www.javabikes.com/ProductsDetail/38.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/d741653c-7f2b-4821-8345-3a394a952941.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);

-- 小轮车 类商品
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('CL2', 'JAVA', 'CL2', 5, 2600, 1820.0,
     100, 10, 'JAVA CL2', '{"类别": "小轮车", "品牌": "JAVA", "型号": "CL2", "产品链接": "https://www.javabikes.com/ProductsDetail/39.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/f76ccdc8-2e7b-43f6-bb66-0aac122e757b.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('CL-CB-451-18S-直把', 'JAVA', 'CL-CB-451-18S-直把', 5, 4399, 3079.3,
     100, 10, 'JAVA CL-CB-451-18S-直把', '{"类别": "小轮车", "品牌": "JAVA", "型号": "CL-CB-451-18S-直把", "产品链接": "https://www.javabikes.com/ProductsDetail/40.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/cd4edd7a-f17a-4618-9a66-efa27f41706e.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('CL-CB-ROAD-22S-弯把', 'JAVA', 'CL-CB-ROAD-22S-弯把', 5, 5999, 4199.3,
     100, 10, 'JAVA CL-CB-ROAD-22S-弯把', '{"类别": "小轮车", "品牌": "JAVA", "型号": "CL-CB-ROAD-22S-弯把", "产品链接": "https://www.javabikes.com/ProductsDetail/41.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/c769a4b5-c3d9-41c4-9f44-9dee76ae4e1b.png"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);

-- 折叠车 类商品
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('X2', 'JAVA', 'X2', 6, 1999, 1399.3,
     100, 10, 'JAVA X2', '{"类别": "折叠车", "品牌": "JAVA", "型号": "X2", "产品链接": "https://www.javabikes.com/ProductsDetail/187.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/a4ba241c-e232-4d5e-8b21-cce1271f300a.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('X3', 'JAVA', 'X3', 6, 3900, 2730.0,
     100, 10, 'JAVA X3', '{"类别": "折叠车", "品牌": "JAVA", "型号": "X3", "产品链接": "https://www.javabikes.com/ProductsDetail/2588.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/40a4a662-0174-487f-b667-7a4dc2aec88e.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('X1', 'JAVA', 'X1', 6, 5300, 3710.0,
     100, 10, 'JAVA X1', '{"类别": "折叠车", "品牌": "JAVA", "型号": "X1", "产品链接": "https://www.javabikes.com/ProductsDetail/2584.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/831e965e-f05f-4a0f-832d-0cdcb1157c35.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('NEO2', 'JAVA', 'NEO2', 6, 2999, 2099.3,
     100, 10, 'JAVA NEO2', '{"类别": "折叠车", "品牌": "JAVA", "型号": "NEO2", "产品链接": "https://www.javabikes.com/ProductsDetail/189.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/795bd77e-6f25-484e-b033-467555fbb013.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);
INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('ARIA', 'JAVA', 'ARIA', 6, 5300, 3710.0,
     100, 10, 'JAVA ARIA', '{"类别": "折叠车", "品牌": "JAVA", "型号": "ARIA", "产品链接": "https://www.javabikes.com/ProductsDetail/185.html"}', '["https://www.javabikes.com/UserFiles/Article/Products/9a737a68-b8a7-4d31-9480-6afcaf8c76bc.jpg"]',
     1, 0, '2025-01-12 10:43:49', '2025-01-12 10:43:49', 0);

SET FOREIGN_KEY_CHECKS = 1;
