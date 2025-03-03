/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : login

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 28/02/2025 16:40:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态：0待支付，1已支付，2已发货，3已完成，4已取消',
  `payment_method` tinyint NULL DEFAULT NULL COMMENT '支付方式：1微信，2支付宝，3银行卡',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `shipping_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货地址',
  `shipping_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `shipping_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  CONSTRAINT `fk_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, 'ORD202403150001', 1, 29997.00, 2, NULL, '2025-02-28 14:49:39', '北京市朝阳区xxx街道', '张三', '13800138000', '2025-02-28 13:40:11', '2025-02-28 13:40:11');
INSERT INTO `order` VALUES (3, 'ORD17407272046486258', 3, 220798.00, 2, 3, '2025-02-28 15:20:20', '广州市天河区xxx大道', '王五', '13700137000', '2025-02-26 11:38:24', '2025-02-26 11:38:24');
INSERT INTO `order` VALUES (4, 'ORD17407273697330002', 3, 308198.00, 4, 1, '2025-03-22 19:18:47', '广州市天河区xxx大道', '王五', '13700137000', '2025-03-22 19:06:47', '2025-03-22 19:06:47');
INSERT INTO `order` VALUES (5, 'ORD17407273697660042', 3, 199998.00, 3, 3, '2025-03-03 01:18:22', '广州市天河区xxx大道', '王五', '13700137000', '2025-03-03 00:22:22', '2025-03-03 00:22:22');
INSERT INTO `order` VALUES (6, 'ORD17407273697776198', 2, 84596.00, 1, 3, '2025-03-08 18:04:48', '上海市浦东新区xxx路', '李四', '13900139000', '2025-03-08 17:57:48', '2025-03-08 17:57:48');
INSERT INTO `order` VALUES (7, 'ORD17407273698002538', 2, 45592.00, 3, 2, '2025-02-28 15:24:16', '上海市浦东新区xxx路', '李四', '13900139000', '2025-03-17 19:59:39', '2025-03-17 19:59:39');
INSERT INTO `order` VALUES (8, 'ORD17407273698200968', 3, 1798.00, 4, 2, '2025-03-20 09:03:03', '广州市天河区xxx大道', '王五', '13700137000', '2025-03-20 08:29:03', '2025-03-20 08:29:03');
INSERT INTO `order` VALUES (9, 'ORD17407273698319686', 2, 9778.00, 2, 1, '2025-03-07 20:07:33', '上海市浦东新区xxx路', '李四', '13900139000', '2025-03-07 19:27:33', '2025-03-07 19:27:33');
INSERT INTO `order` VALUES (10, 'ORD17407273698421113', 3, 65600.00, 2, 2, '2025-03-05 17:10:23', '广州市天河区xxx大道', '王五', '13700137000', '2025-03-05 16:28:23', '2025-03-05 16:28:23');
INSERT INTO `order` VALUES (11, 'ORD17407273698519094', 3, 27979.00, 3, 2, '2025-03-19 04:32:43', '广州市天河区xxx大道', '王五', '13700137000', '2025-03-19 04:31:43', '2025-03-19 04:31:43');
INSERT INTO `order` VALUES (12, 'ORD17407273698617059', 3, 103199.00, 4, 1, '2025-03-17 23:14:30', '广州市天河区xxx大道', '王五', '13700137000', '2025-03-17 22:19:30', '2025-03-17 22:19:30');
INSERT INTO `order` VALUES (13, 'ORD17407273698781266', 1, 40989.00, 3, 3, '2025-02-28 15:24:12', '北京市朝阳区xxx街道', '张三', '13800138000', '2025-03-22 02:18:27', '2025-03-22 02:18:27');
INSERT INTO `order` VALUES (14, 'ORD17407293990377012', 2, 2998.00, 1, 2, '2025-03-07 20:03:56', '上海市浦东新区xxx路', '李四', '13900139000', '2025-03-07 19:51:56', '2025-03-07 19:51:56');
INSERT INTO `order` VALUES (15, 'ORD17407293990560998', 1, 6796.00, 3, 3, '2025-03-25 04:43:09', '北京市朝阳区xxx街道', '张三', '13800138000', '2025-03-25 04:04:09', '2025-03-25 04:04:09');
INSERT INTO `order` VALUES (16, 'ORD17407293990665722', 3, 6597.00, 3, 2, '2025-03-06 08:14:19', '广州市天河区xxx大道', '王五', '13700137000', '2025-03-06 08:08:19', '2025-03-06 08:08:19');
INSERT INTO `order` VALUES (17, 'ORD17407293990758188', 1, 10797.00, 1, 1, '2025-03-17 10:14:03', '北京市朝阳区xxx街道', '张三', '13800138000', '2025-03-17 09:22:03', '2025-03-17 09:22:03');
INSERT INTO `order` VALUES (18, 'ORD17407293990929441', 2, 119193.00, 3, 3, '2025-03-25 03:01:34', '上海市浦东新区xxx路', '李四', '13900139000', '2025-03-25 03:00:34', '2025-03-25 03:00:34');
INSERT INTO `order` VALUES (19, 'ORD17407293991156502', 1, 119200.00, 2, 1, '2025-02-28 16:16:43', '北京市朝阳区xxx街道', '张三', '13800138000', '2025-03-27 05:43:48', '2025-03-27 05:43:48');
INSERT INTO `order` VALUES (20, 'ORD17407293991291009', 2, 8997.00, 1, 3, '2025-03-20 08:03:42', '上海市浦东新区xxx路', '李四', '13900139000', '2025-03-20 08:03:42', '2025-03-20 08:03:42');
INSERT INTO `order` VALUES (21, 'ORD17407293991379162', 2, 6796.00, 2, 3, '2025-03-19 05:40:58', '上海市浦东新区xxx路', '李四', '13900139000', '2025-03-19 04:43:58', '2025-03-19 04:43:58');
INSERT INTO `order` VALUES (22, 'ORD17407293991513007', 2, 3998.00, 2, 1, '2025-03-11 14:36:12', '上海市浦东新区xxx路', '李四', '13900139000', '2025-03-11 14:26:12', '2025-03-11 14:26:12');
INSERT INTO `order` VALUES (23, 'ORD17407293991676916', 3, 9379.00, 3, 2, '2025-03-13 23:56:47', '广州市天河区xxx大道', '王五', '13700137000', '2025-03-13 23:21:47', '2025-03-13 23:21:47');

SET FOREIGN_KEY_CHECKS = 1;
