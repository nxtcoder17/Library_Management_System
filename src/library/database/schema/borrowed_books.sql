CREATE TABLE IF NOT EXISTS borrowed_books(
  sno           int(4)      AUTO_INCREMENT  PRIMARY KEY,
  bookId        varchar(15) NOT NULL,
  libraryId     varchar(15) NOT NULL,
  borrowerName  varchar(25) NOT NULL,
  title         varchar(50) NOT NULL,
  issueDate     DATE,
  returnDate    DATE,
  fines         int(4)
);
