CREATE TABLE order_status(
    id SMALLINT GENERATED ALWAYS AS IDENTITY,
    status VARCHAR(20) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);
