CREATE TABLE products
(
    productId   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255)  NOT NULL,
    price       INT           NOT NULL,
    imageUrl    VARCHAR(1000) NOT NULL,
    mdConfirmed BOOLEAN       NOT NULL DEFAULT FALSE
);


CREATE TABLE members
(
    memberId BIGINT PRIMARY KEY AUTO_INCREMENT,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    role     VARCHAR(50)  NOT NULL DEFAULT 'USER'
);


CREATE TABLE wishes
(
    wishId      BIGINT PRIMARY KEY AUTO_INCREMENT,
    memberId    BIGINT NOT NULL,
    productId   BIGINT NOT NULL,
    createdDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT memberFk FOREIGN KEY (memberId) REFERENCES members (memberId),
    CONSTRAINT productFk FOREIGN KEY (productId) REFERENCES products (productId),
    UNIQUE (memberId, productId)
);