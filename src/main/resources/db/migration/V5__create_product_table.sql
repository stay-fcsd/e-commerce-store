CREATE TABLE product(
    product_id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(20) NOT NULL,
    description VARCHAR(100),
    price DOUBLE PRECISION NOT NULL,
    stock_quantity INTEGER DEFAULT 0,
    PRIMARY KEY (product_id)
)
