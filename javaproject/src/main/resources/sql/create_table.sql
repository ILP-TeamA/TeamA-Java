-- 商品テーブル
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price INTEGER NOT NULL
);

-- 売上記録テーブル
CREATE TABLE sales_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_date DATE NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    total_amount INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE KEY unique_sales_date_product (sales_date, product_id)
);

-- 初期データ投入
INSERT INTO products (name, price) VALUES
('ホワイトビール', 900),
('ラガー', 800),
('ペールエール', 1000),
('フルーツビール', 1000),
('黒ビール', 1200),
('IPA', 900);

-- サンプル売上データ
INSERT INTO sales_records (sales_date, product_id, quantity, total_amount) VALUES
('2025-06-17', 1, 15, 13500),
('2025-06-17', 2, 20, 16000),
('2025-06-17', 3, 12, 12000),
('2025-06-17', 4, 8, 8000),
('2025-06-17', 5, 5, 6000),
('2025-06-17', 6, 10, 9000),
('2025-06-16', 1, 12, 10800),
('2025-06-16', 2, 18, 14400),
('2025-06-16', 3, 10, 10000),
('2025-06-16', 4, 6, 6000),
('2025-06-16', 5, 4, 4800),
('2025-06-16', 6, 8, 7200);