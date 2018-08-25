CREATE TABLE IF NOT EXISTS authorized (
    username varchar(20) NOT NULL,
    password varchar(50) NOT NULL,
    email   varchar(100) UNIQUE NOT NULL,
    security varchar(100) NOT NULL,
    answer   varchar(50) NOT NULL
);
