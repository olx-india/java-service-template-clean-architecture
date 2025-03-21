CREATE TABLE IF NOT EXISTS tbl_user
(
    id    INT AUTO_INCREMENT,
    name  VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tbl_user
(
    id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product  VARCHAR(64) NOT NULL,
    quantity INT         NOT NULL,
    price    DOUBLE      NOT NULL
);
