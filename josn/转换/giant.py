import json
from datetime import datetime


def generate_giant_sql(json_file='D:/CODE/毕设/python爬虫/毕设/捷安特/giant_all_details.json',
                       output_file='giant_insert.sql'):
    """生成捷安特的SQL语句"""
    try:
        # 读取JSON文件
        with open(json_file, 'r', encoding='utf-8') as f:
            data = json.load(f)

        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        sql_statements = []

        # 遍历所有系列
        for series, products in data.items():
            if not isinstance(products, list):
                continue

            # 处理每个系列下的产品
            for product in products:
                # 提取价格
                price_str = str(product.get('price', '0'))
                price_str = price_str.replace('¥', '').replace(',', '')
                try:
                    price = float(price_str)
                except:
                    price = 0

                # 构建规格参数
                specs = {
                    'series': series,
                    'year': product.get('year', ''),
                    'categories': product.get('categories', []),
                    'specifications': product.get('specifications', {}),
                    'images': [img for img in product.get('images', []) if not img.endswith('.webp')]
                }

                # 生成SQL语句
                sql = f"""INSERT INTO product (
                    name, brand, model, category_id, price, cost_price,
                    stock, warning_stock, description, specs, status,
                    create_time, update_time, deleted
                ) VALUES (
                    '{str(product.get('model', '')).replace("'", "''")}',
                    'Giant',
                    '{series}',
                    1,
                    {price},
                    {price},
                    0,
                    10,
                    '{str(product.get('description', '')).replace("'", "''")}',
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

        print(f"捷安特SQL语句已生成到: {output_file}")
        print(f"总共生成 {len(sql_statements)} 条记录")

    except Exception as e:
        print(f"生成捷安特SQL失败: {str(e)}")
        import traceback
        traceback.print_exc()


if __name__ == '__main__':
    generate_giant_sql()