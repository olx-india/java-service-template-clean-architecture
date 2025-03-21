CREATE USER 'testDBUser'@'%' IDENTIFIED BY 'testDBPassword';
GRANT ALL PRIVILEGES ON *.* TO 'testDBUser'@'%';
FLUSH PRIVILEGES;

CREATE DATABASE public;
USE public;

CREATE TABLE tbl_user
(
    id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(64)  NOT NULL,
    email VARCHAR(256) NOT NULL
);

INSERT INTO tbl_user (id, name, email)
VALUES (998, 'Test User 1', 'testEmail');

INSERT INTO tbl_user (id, name, email)
VALUES (999, 'Test User 2', 'testEmail');

CREATE TABLE tbl_order
(
    id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product  VARCHAR(64) NOT NULL,
    quantity INT         NOT NULL,
    price    DOUBLE      NOT NULL
);

INSERT INTO tbl_order (id, product, quantity, price)
VALUES (998, 'Test Order 1', 1, 120.0);

INSERT INTO tbl_order (id, product, quantity, price)
VALUES (999, 'Test Order 2', 2, 140.0);
