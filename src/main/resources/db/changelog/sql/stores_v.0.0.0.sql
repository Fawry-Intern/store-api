CREATE TABLE stores (
    store_id BIGSERIAL PRIMARY KEY,
    store_name VARCHAR(255) NOT NULL,
    store_address VARCHAR(255) NOT NULL,
    CONSTRAINT unique_store_name UNIQUE (store_name)
);
