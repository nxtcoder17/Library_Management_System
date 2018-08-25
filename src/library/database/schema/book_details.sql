CREATE TABLE IF NOT EXISTS book_details (
    sno        int(4)       AUTO_INCREMENT PRIMARY KEY,
    bookId     varchar(10)  NOT NULL,
    title      varchar(50)  NOT NULL,
    author     varchar(50)  NOT NULL,
    branch     varchar(20)  NOT NULL,
    publisher  varchar(50)  NOT NULL,
    price      INT(4)                    DEFAULT -1,
    copies     INT(3)                    DEFAULT 1
);
