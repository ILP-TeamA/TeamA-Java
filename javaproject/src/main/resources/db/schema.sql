-- 商品テーブル
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL
);

-- 売上記録テーブル
CREATE TABLE IF NOT EXISTS sales_records (
    id SERIAL PRIMARY KEY,
    sales_date DATE NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    total_amount INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- インデックス
CREATE INDEX IF NOT EXISTS idx_sales_date ON sales_records(sales_date);
CREATE INDEX IF NOT EXISTS idx_product_id ON sales_records(product_id);
