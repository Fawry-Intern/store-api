CREATE TABLE stock (
    stock_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    stock_available_quantity INT NOT NULL CHECK (stock_available_quantity >= 0),
    stock_last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE,
    CONSTRAINT unique_product_store UNIQUE (product_id, store_id)
);