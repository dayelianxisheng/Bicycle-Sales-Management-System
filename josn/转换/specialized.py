import json
from datetime import datetime


def generate_specialized_sql(json_file='D:/CODE/毕设/python爬虫/毕设/闪电/闪电产品数据.json',
                             output_file='specialized_insert.sql'):
    """生成闪电自行车的SQL语句"""
    try:
        # 读取JSON文件
        with open(json_file, 'r', encoding='utf-8') as f:
            data = json.load(f)

        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        sql_statements = []

        # 遍历所有分类
        for category in data.get('categories', []):
            category_name = category.get('name', '')

            # 处理主分类产品
            for product in category.get('products', []):
                # 构建规格参数
                specs = {
                    'category': category_name,
                    'subcategory': '',
                    'category_detail': product.get('category', ''),
                    'group': product.get('group', ''),
                    'product_family': product.get('product_family', ''),
                    'gender': product.get('gender', ''),
                    'type': product.get('type', ''),
                    'url': product.get('url', '')
                }

                # 生成SQL语句
                sql = f"""INSERT INTO product (
                    name, brand, model, category_id, price, cost_price,
                    stock, warning_stock, description, specs, status,
                    create_time, update_time, deleted
                ) VALUES (
                    '{str(product.get('name', '')).replace("'", "''")}',
                    'Specialized',
                    '{str(product.get('sku', '')).replace("'", "''")}',
                    1,
                    {float(product.get('price', 0))},
                    {float(product.get('price', 0))},
                    0,
                    10,
                    '',
                    '{json.dumps(specs, ensure_ascii=False).replace("'", "''")}',
                    1,
                    '{now}',
                    '{now}',
                    0
                );"""
                sql_statements.append(sql)

            # 遍历子分类
            for subcategory in category.get('subcategories', []):
                subcategory_name = subcategory.get('name', '')

                # 处理子分类产品
                for product in subcategory.get('products', []):
                    # 构建规格参数
                    specs = {
                        'category': category_name,
                        'subcategory': subcategory_name,
                        'category_detail': product.get('category', ''),
                        'group': product.get('group', ''),
                        'product_family': product.get('product_family', ''),
                        'gender': product.get('gender', ''),
                        'type': product.get('type', ''),
                        'url': product.get('url', '')
                    }

                    # 生成SQL语句
                    sql = f"""INSERT INTO product (
                        name, brand, model, category_id, price, cost_price,
                        stock, warning_stock, description, specs, status,
                        create_time, update_time, deleted
                    ) VALUES (
                        '{str(product.get('name', '')).replace("'", "''")}',
                        'Specialized',
                        '{str(product.get('sku', '')).replace("'", "''")}',
                        1,
                        {float(product.get('price', 0))},
                        {float(product.get('price', 0))},
                        0,
                        10,
                        '',
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

        print(f"闪电自行车SQL语句已生成到: {output_file}")
        print(f"总共生成 {len(sql_statements)} 条记录")

    except Exception as e:
        print(f"生成闪电自行车SQL失败: {str(e)}")
        import traceback
        traceback.print_exc()


if __name__ == '__main__':
    generate_specialized_sql()