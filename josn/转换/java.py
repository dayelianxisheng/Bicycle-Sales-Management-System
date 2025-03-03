import json
import datetime
from decimal import Decimal


def clean_price(price_str):
    """清理价格字符串,返回数值"""
    return Decimal(price_str.replace('￥', '').replace('¥', '').replace(',', ''))


def generate_sql_file():
    # 读取JSON文件
    with open('../JAVA/java_bikes_data.json', 'r', encoding='utf-8') as f:
        data = json.load(f)

    # 创建SQL文件
    with open('product_data.sql', 'w', encoding='utf-8') as f:
        # 写入SQL文件头部
        f.write("-- 商品数据导入脚本\n")
        f.write("-- 生成时间: " + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S') + "\n\n")
        f.write("SET NAMES utf8mb4;\n")
        f.write("SET FOREIGN_KEY_CHECKS = 0;\n\n")

        # SQL语句模板
        sql_template = """INSERT INTO `product` 
    (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
     `stock`, `warning_stock`, `description`, `specs`, `images`, 
     `status`, `sales`, `create_time`, `update_time`, `deleted`)
VALUES
    ('{name}', 'JAVA', '{model}', {category_id}, {price}, {cost_price},
     {stock}, {warning_stock}, '{description}', '{specs}', '{images}',
     {status}, {sales}, '{create_time}', '{update_time}', {deleted});\n"""

        current_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')

        # 类别ID映射
        category_mapping = {
            'E-Bike': 1,
            '公路车': 2,
            '山地车': 3,
            '童车-KSR': 4,
            '小轮车': 5,
            '折叠车': 6
        }

        for category in data['categories']:
            category_name = category['name']
            category_id = category_mapping.get(category_name, 1)

            # 写入类别注释
            f.write(f"\n-- {category_name} 类商品\n")

            for product in category['products']:
                # 处理图片
                images = [product['image']] if product.get('image') else []

                # 构建基本规格信息
                specs = {
                    "类别": category_name,
                    "品牌": "JAVA",
                    "型号": product['title'],
                    "产品链接": product['url']
                }

                # 构建INSERT语句
                sql = sql_template.format(
                    name=product['title'].replace("'", "''"),
                    model=product['title'].replace("'", "''"),
                    category_id=category_id,
                    price=clean_price(product['price']),
                    cost_price=clean_price(product['price']) * Decimal('0.7'),
                    stock=100,
                    warning_stock=10,
                    description=f"JAVA {product['title']}".replace("'", "''"),
                    specs=json.dumps(specs, ensure_ascii=False).replace("'", "''"),
                    images=json.dumps(images, ensure_ascii=False).replace("'", "''"),
                    status=1,
                    sales=0,
                    create_time=current_time,
                    update_time=current_time,
                    deleted=0
                )

                f.write(sql)

        # 写入SQL文件尾部
        f.write("\nSET FOREIGN_KEY_CHECKS = 1;\n")


# 运行生成器
generate_sql_file()
print("SQL文件已生成: product_data.sql")