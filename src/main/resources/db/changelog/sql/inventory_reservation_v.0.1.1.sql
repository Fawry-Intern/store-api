CREATE TABLE inventory_reservation (
   id SERIAL PRIMARY KEY,
   product_id INT NOT NULL,
   order_id INT NOT NULL,
   reserved_quantity INT NOT NULL CHECK (reserved_quantity >= 0),
   status VARCHAR(10) NOT NULL
);
