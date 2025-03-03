import json
from datetime import datetime
import re


def clean_string(s):
    """清理字符串中的特殊字符"""
    if s is None:
        return ''
    return str(s).replace("'", "\\'").strip()


def extract_products(data):
    """从JSON数据中提取所有产品信息"""
    products = []
    # 遍历所有主分类
    for category, category_data in data.items():
        if 'subcategories' in category_data:
            # 遍历子分类
            for subcategory, subcategory_data in category_data['subcategories'].items():
                if 'products' in subcategory_data:
                    # 遍历产品
                    for product in subcategory_data['products']:
                        # 构建产品信息
                        product_info = {
                            'name': product.get('title', ''),
                            'category': f"{category}-{subcategory}",
                            'price': product.get('price', '9999'),
                            'description': '',
                            'specs': json.dumps(product.get('details', {}), ensure_ascii=False),
                            'buy_url': product.get('buy_url', ''),
                            'url': product.get('url', ''),
                            'images': json.dumps(product.get('images', []), ensure_ascii=False)  # 将图片数组转换为JSON字符串
                        }
                        products.append(product_info)
    return products


def convert_json_to_sql(json_file, output_file):
    """将JSON文件转换为SQL插入语句"""
    # 读取JSON文件
    with open(json_file, 'r', encoding='utf-8') as f:
        data = json.load(f)

    products = extract_products(data)
    sql_statements = []
    current_time = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

    for item in products:
        name = clean_string(item['name'])
        brand = '喜德盛'  # 固定品牌
        model = clean_string(name)  # 使用名称作为型号
        specs = clean_string(item['specs'])
        price = float(item['price'])
        cost_price = price * 0.7  # 成本价设为售价的70%
        description = f"商品链接: {item['url']}\n购买链接: {item['buy_url']}"
        images = clean_string(item['images'])  # 清理图片JSON字符串中的特殊字符

        # 构建SQL语句
        sql = f"""INSERT INTO `product` 
                (`name`, `brand`, `model`, `category_id`, `price`, `cost_price`, 
                `stock`, `warning_stock`, `description`, `specs`, `images`, 
                `status`, `sales`, `create_time`, `update_time`, `deleted`)
                VALUES 
                ('{name}', '{brand}', '{model}', 1, {price}, {cost_price}, 
                100, 10, '{clean_string(description)}', '{specs}', '{images}', 
                1, 0, '{current_time}', '{current_time}', 0);"""

        sql_statements.append(sql)

    # 将所有SQL语句写入文件
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(sql_statements))


if __name__ == '__main__':
    # 设置输入输出文件路径
    json_file = 'D:/CODE/毕设/python爬虫/毕设/喜德盛/xds_categories.json'
    output_file = 'insert_products.sql'

    # 执行转换
    convert_json_to_sql(json_file, output_file)
    print(f"转换完成，SQL语句已保存到 {output_file}")