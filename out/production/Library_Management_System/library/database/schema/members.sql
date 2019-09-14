CREATE TABLE IF NOT EXISTS members (
    sno             int(4)      AUTO_INCREMENT  PRIMARY KEY,
    libraryId       varchar(20) UNIQUE NOT NULL,
    firstName       varchar(50),
    lastName        varchar(50),
    semester        int(1),
    rollNo          int(13),
    branch          varchar(30),
    email           varchar(50) UNIQUE NOT NULL,
    borrowCount     int(1) 
);
