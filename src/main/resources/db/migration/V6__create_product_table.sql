CREATE TABLE product(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(30) NOT NULL,
    description VARCHAR NOT NULL DEFAULT '',
    price FLOAT NOT NULL DEFAULT 0.0,
    stock_quantity  INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
)
