-- 商品テーブル
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL
);

-- 売上記録テーブル
CREATE TABLE IF NOT EXISTS sales_records (
    id BIGSERIAL PRIMARY KEY,
    sales_date DATE NOT NULL,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    total_amount INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- インデックス
CREATE INDEX IF NOT EXISTS idx_sales_date ON sales_records(sales_date);
CREATE INDEX IF NOT EXISTS idx_product_id ON sales_records(product_id);

-- サンプルデータ（開発環境用）
INSERT INTO products (name, price) VALUES
    ('Premium Lager', 580),
    ('IPA', 650),
    ('Stout', 700),
    ('Wheat Beer', 600);
