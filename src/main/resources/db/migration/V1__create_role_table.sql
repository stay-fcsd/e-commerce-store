CREATE TABLE user_role(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
);
