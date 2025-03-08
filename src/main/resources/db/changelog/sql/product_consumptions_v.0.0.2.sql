CREATE TABLE product_consumptions (
    consumption_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    product_price NUMERIC(10,2) CHECK (product_price >= 0),
    consumption_quantity BIGINT NOT NULL CHECK (consumption_quantity >= 0),
    consumption_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE
);