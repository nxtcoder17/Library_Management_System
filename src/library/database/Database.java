package library.database;

import library.base.BookInfo;
import library.base.BorrowerInfo;
import library.base.NewUser;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/* File Handling Imports */
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/* Imports for returngin Lists for TableView */
import javafx.collections.ObservableList;
import java.util.LinkedList;
import javafx.collections.FXCollections;

public class Database {
	private static final String DB_NAME = "library";
	private static final String DB_URL = "jdbc:mariadb://localhost/" + DB_NAME;

	private static final String TABLE_NAME = "book_details";
	private static final String BORROWER_TABLE = "borrowed_books";
	private static final String AUTHORIZED = "authorized";

    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement prepStmt;

	public Database() throws SQLException,IOException {
	  connection = DriverManager.getConnection(DB_URL, "root", "");
	  // System.out.println("Connection Established with MariaDB");

    // Preparing the Table to store data
    final String BOOK_DETAILS_SCHEMA = readFromSqlFile("book_details.sql");
    final String AUTHORIZED_SCHEMA = readFromSqlFile("signup.sql");
    final String BORROWER_TABLE_SCHEMA = readFromSqlFile("borrowed_books.sql");

    // System.out.println(BOOK_DETAILS_SCHEMA);
    stmt = connection.createStatement();
    stmt.executeQuery(AUTHORIZED_SCHEMA);
    stmt.executeQuery(BOOK_DETAILS_SCHEMA);
    stmt.executeQuery(BORROWER_TABLE_SCHEMA);
    // System.out.println("Successfully Created Table [or it exists]");

	}

  private String readFromSqlFile(String fileName) throws IOException {
    InputStream in = Database.class.getResourceAsStream("/library/database/schema/" + fileName);
    StringBuilder text = new StringBuilder();

    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

    String line;
    while ((line = reader.readLine()) != null) {
      text.append(line);
    }
    return text.toString();
  }

  public void addUser(NewUser user) throws SQLException {
      final String ADD_USER = "INSERT INTO " + AUTHORIZED
                        + "(username,password,email,security,answer) "
                        + "VALUES(?,?,?,?,?)";
      prepStmt = connection.prepareStatement(ADD_USER);
      prepStmt.setString(1, user.getUsername());
      prepStmt.setString(2, user.getPassword());
      prepStmt.setString(3, user.getEmail());
      prepStmt.setString(4, user.getSecurity());
      prepStmt.setString(5, user.getAnswer());

      prepStmt.executeUpdate();
  }

  public void changePassword(String username, String passwd) 
                        throws SQLException {

      final String PASS_CHANGE="UPDATE " + AUTHORIZED
                                 + " set password=? "
                                 + " WHERE username=?";
      prepStmt = connection.prepareStatement(PASS_CHANGE);
      prepStmt.setString(1, passwd);
      prepStmt.setString(2, username);

      prepStmt.executeUpdate();
  }

  public void addBook(BookInfo book) throws SQLException {
      final String ADD_BOOK = "INSERT INTO " + TABLE_NAME
                      + "(bookId,title,author,publisher,price,copies,branch) "
                      + "VALUES(?,?,?,?,?,?,?)";
      prepStmt = connection.prepareStatement(ADD_BOOK); 
      prepStmt.setString (1, book.getBookId());
      prepStmt.setString (2, book.getTitle());
      prepStmt.setString (3, book.getAuthor());
      prepStmt.setString (4, book.getPublisher());
      prepStmt.setInt    (5, book.getPrice());
      prepStmt.setInt    (6, book.getCopies());
      prepStmt.setString (7, book.getBranch());

      prepStmt.executeUpdate();
  }

  public void removeBook(BookInfo book) throws SQLException {
    /* Removing a Book from DB involves 4 steps here: 
       # Search the to be deleted book in DB 
       # Remove that Book
       # Update the Serial Nos to val - 1 for all books below
       # Adjust AUTO_INCREMENT value to 1 again
   */

    final String SEARCH_BOOKID = "SELECT sno,bookId from " + TABLE_NAME
                                    + " WHERE bookId=?";
    prepStmt = connection.prepareStatement(SEARCH_BOOKID);
    prepStmt.setString(1, book.getBookId());
    ResultSet r = prepStmt.executeQuery();
    r.next();
    int sno = r.getInt("sno");
    System.out.println("Serial No: " + sno);

    final String REMOVE_BOOK = "DELETE FROM " + TABLE_NAME
                              + " WHERE bookId=?";

    prepStmt = connection.prepareStatement(REMOVE_BOOK);
    prepStmt.setString(1, book.getBookId());
    prepStmt.executeUpdate();

    final String DECREMENT_SNO = "UPDATE " + TABLE_NAME
                                    + " SET sno=sno-1 where sno > " + sno;
    prepStmt = connection.prepareStatement(DECREMENT_SNO);
    prepStmt.executeUpdate();

    final String FIX_AUTO_INCREMENT = "ALTER TABLE " + TABLE_NAME
                                        + " AUTO_INCREMENT=1";
    prepStmt = connection.prepareStatement(FIX_AUTO_INCREMENT);
    prepStmt.executeUpdate();

    System.out.println("Successfully Removed book: " + book.getBookId() + " from the DB");
  }


  public ObservableList<BookInfo> getAllBooks() throws SQLException {
    final String FETCH_ALL = "SELECT * from " + TABLE_NAME;

    LinkedList<BookInfo> list = new LinkedList<>();

    stmt = connection.createStatement();
    ResultSet results = stmt.executeQuery(FETCH_ALL);
    
    while (results.next()) {
      BookInfo book = new BookInfo();
      book.setSno       (results.getInt("sno"));
      book.setBookId    (results.getString("bookId"));
      book.setTitle     (results.getString("title"));
      book.setAuthor    (results.getString("author"));
      book.setPublisher (results.getString("publisher"));
      book.setPrice     (results.getInt("price"));
      book.setCopies    (results.getInt("copies"));
      book.setBranch    (results.getString("branch"));

      list.add(book);
    }

    return FXCollections.observableList(list);
  }
  

  public void issueBook(BorrowerInfo  borrower) throws SQLException {
    // First, I need to check whether my book exists in the repo or not
    final String IS_PRESENT = "SELECT * from " + TABLE_NAME + " where bookId=?";
    prepStmt = connection.prepareStatement(IS_PRESENT);
    prepStmt.setString(1, borrower.getBookId());

    ResultSet r = prepStmt.executeQuery();
    if (r.next() == false) {
      System.out.println("Book with BookID: " + borrower.getBookId() + " is not in our repos. ");
      return;
    }


    final String ISSUE_BOOK = "INSERT INTO " + BORROWER_TABLE
                                    + "(bookId,libraryId,borrowerName,title,issueDate,returnDate)"
                                    + " VALUES(?,?,?,?,?,?)";
    prepStmt = connection.prepareStatement(ISSUE_BOOK);

    prepStmt.setString(1, borrower.getBookId());
    prepStmt.setString(2, borrower.getLibraryId());
    prepStmt.setString(3, borrower.getBorrowerName());
    prepStmt.setString(4, borrower.getTitle());       // getTitle() method from BookInfo
    prepStmt.setString(5, borrower.getIssueDate());
    prepStmt.setString(6, borrower.getReturnDate());

    prepStmt.executeUpdate();
  }

  public ObservableList<BorrowerInfo> getAllIssuedBooks() throws SQLException {
    LinkedList<BorrowerInfo> list = new LinkedList<>();

    final String FETCH_ALL = "SELECT * from " + BORROWER_TABLE;
    stmt = connection.createStatement();
    ResultSet results = stmt.executeQuery(FETCH_ALL);

    while (results.next()) {
      BorrowerInfo borrower = new BorrowerInfo();
      borrower.setSno         ( results.getInt("sno"));
      borrower.setBookId      ( results.getString("bookId"));
      borrower.setLibraryId   ( results.getString("libraryId"));
      borrower.setBorrowerName( results.getString("borrowerName"));
      borrower.setTitle       ( results.getString("title"));    // setTitle() method from BookInfo
      borrower.setIssueDate   ( results.getString("issueDate"));
      borrower.setReturnDate  ( results.getString("returnDate"));
      borrower.setFines       ( results.getInt("fines"));

      list.add(borrower);
    }

    return FXCollections.observableList(list);
  }

  public boolean doesUserExist(String username, String email) 
                                            throws SQLException {
      /*
       Queries the DB for credentials of user with given username 
        and password.
            Then, if the r.next() i.e. any info exist in the returned
        ResultSet object, means user exist and r.text() returns True
            Otherwise, does not exist
        and, r.next() returns False

        */
      final String QUERY = "SELECT * from " + AUTHORIZED
                    + " where username=? and email=?";
      prepStmt = connection.prepareStatement(QUERY);
      prepStmt.setString(1, username);
      prepStmt.setString(2, email);

      ResultSet r = prepStmt.executeQuery();
      return r.next();
  }

  public boolean isAuthorized(String username, String passwd) 
    throws SQLException {
      /*
       Queries the DB for credentials of user with given username 
        and password.
            Then, if the r.next() i.e. any info exist in the returned
        ResultSet object, means user exist and r.next() returns True
            Otherwise, does not exist
        and, r.next() returns False

        */

    final String QUERY = "SELECT * from " + AUTHORIZED
                    + " WHERE username=? and password=?";
    prepStmt = connection.prepareStatement(QUERY);
    prepStmt.setString(1, username);
    prepStmt.setString(2, passwd);

    ResultSet r = prepStmt.executeQuery();
    return r.next();
  }

  public String getSecurityQuestion(String username, String email)
  throws SQLException {
      final String QUERY = "SELECT security from " + AUTHORIZED
                    + " WHERE username=? and email=?";
      prepStmt = connection.prepareStatement(QUERY);
      prepStmt.setString(1, username);
      prepStmt.setString(2, email);

      ResultSet r = prepStmt.executeQuery();
      r.next();
      return r.getString("security");
  }

  public boolean checkSecurityAnswer(String username, String answer) 
      throws SQLException {

      final String QUERY = "SELECT answer from " + AUTHORIZED
                    + " WHERE username=?";
      prepStmt = connection.prepareStatement(QUERY);
      prepStmt.setString(1, username);
      ResultSet r = prepStmt.executeQuery();
      r.next();
      return r.getString("answer").equalsIgnoreCase(answer);
  }
}
