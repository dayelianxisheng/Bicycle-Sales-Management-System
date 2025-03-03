import json
from datetime import datetime


def generate_merida_sql(json_file='D:/CODE/毕设/python爬虫/毕设/美利达/merida_full_data.json',
                        output_file='merida_insert.sql'):
    """生成美利达的SQL语句"""
    try:
        # 读取JSON文件
        with open(json_file, 'r', encoding='utf-8') as f:
            data = json.load(f)

        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        sql_statements = []

        # 遍历所有分类
        for category in data.get('categories', []):
            category_name = category.get('name', '')

            # 遍历子分类
            for subcategory in category.get('subcategories', []):
                subcategory_name = subcategory.get('name', '')

                # 处理每个产品
                for product in subcategory.get('products', []):
                    # 提取价格
                    price_str = str(product.get('price', '0'))
                    price_str = price_str.replace('¥', '').replace(',', '')
                    try:
                        price = float(price_str)
                    except:
                        price = 0

                    # 构建规格参数
                    specs = {
                        'category': category_name,
                        'subcategory': subcategory_name,
                        'url': product.get('url', ''),
                        'specifications': product.get('specifications', {}),
                        'images': product.get('images', [])
                    }

                    # 从规格中提取颜色
                    colors = product.get('specifications', {}).get('颜色', '')
                    description = product.get('description', '')
                    if colors:
                        description = f"{description}\n颜色：{colors}" if description else f"颜色：{colors}"

                    # 生成SQL语句
                    sql = f"""INSERT INTO product (
                        name, brand, model, category_id, price, cost_price,
                        stock, warning_stock, description, specs, status,
                        create_time, update_time, deleted
                    ) VALUES (
                        '{str(product.get('model', '')).replace("'", "''")}',
                        'Merida',
                        '{subcategory_name.replace("'", "''")}',
                        1,
                        {price},
                        {price},
                        0,
                        10,
                        '{description.replace("'", "''")}',
                        '{json.dumps(specs, ensure_ascii=False).replace("'", "''")}',
                        1,
                        '{now}',
                        '{now}',
                        0
                    );"""
                    sql_statements.append(sql)

        # 写入SQL文件
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write('\n'.join(sql_statements))

        print(f"美利达SQL语句已生成到: {output_file}")
        print(f"总共生成 {len(sql_statements)} 条记录")

    except Exception as e:
        print(f"生成美利达SQL失败: {str(e)}")
        import traceback
        traceback.print_exc()


if __name__ == '__main__':
    generate_merida_sql()