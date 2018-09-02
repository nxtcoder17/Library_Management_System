package library.ui;

/* ========[ imports ] ============ */ //{{{
import library.base.BookInfo;
import library.base.BorrowerInfo;
import library.base.NewUser;
import library.base.MemberInfo;

import library.database.Database;
import library.regex.RegexMatcher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ComboBox;

import javafx.scene.control.DatePicker;
import javafx.geometry.Pos;

import java.io.IOException;
import java.sql.SQLException;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import org.controlsfx.control.Notifications;
import javafx.util.Duration;

// For TableView and Table
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;


/* For Regular Expressions */
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/* End of ====[ imports ]===========*///}}}

public class Controller {

    // // Static Fields
    // private static Database db2 = null;
    private static Database db2;
    private static String username = null;
    private static final String NOTIFY_ICONS = "/library/images/notifications/";

    public Controller() 
        throws IOException, SQLException {
        if (db2 == null)
            db2 = new Database();
        System.out.println("Controller Constructor Invoked");
    }
    
    //==============[ Login System: login.fxml ]==============={{{

    @FXML private TextField login_field_user;
    @FXML private PasswordField login_field_passwd;

    @FXML protected void forgot_password_click() throws IOException {
        // System.out.println("Forget Password Click");
        sceneSwitcher("forgotPassword.fxml", login_field_user,
                "Reset Password", 600, 300);
    }

    @FXML private ToggleButton login_button_submit;

    @FXML protected void login_button_submit_Clicked()
                        throws IOException, SQLException {
        String user = login_field_user.getText();
        String passwd = login_field_passwd.getText();

        if (user.length() == 0 || passwd.length() == 0) {

            Notifications notification = throwNotification("Username & Password Required",
                                                "You need to provide a valid set of Username & Password");
            // notification.showError();
            notification.show();
        }
        else {
            Database db = new Database();       // throws IOException, SQLException
            // Check if the credentials are valid
            
            if (db.isAuthorized(user, passwd)) {
                System.out.println("Login Successfull");
                homePage(login_field_user);
            }
            else{
                Notifications notification = throwNotification("Access Denied",
                                                "You must provide a valid set of Username & Password");
                notification.graphic(new ImageView(NOTIFY_ICONS + "invalid-64px.png"));
                notification.show();
            }
        }
    }

    @FXML 
    protected void logout_method() throws IOException {
        System.out.println("Logout Protocol Instantiated");
        sceneSwitcher("login.fxml", homepage_button_addBook,
                            "Login Screen", 600, 300);
        System.out.println("LogOut Successfull");
    }

    @FXML private void handle_EnterKey_press() throws IOException, SQLException {
        // throws Both exceptions as login_button_submit_Clicked throws them
        login_button_submit.setSelected(true);
        login_button_submit_Clicked();
    }

    //==============[ End of: login.fxml ]===============}}}

    //===============[ Utility Methods ]================={{{
    
    private void homePage(Parent controlElement) throws IOException {
        Stage stage; Parent root; stage = (Stage) controlElement.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/library/ui/homepage.fxml"));

        stage.hide();
        stage.setScene(new Scene(root, 600, 400));
        stage.centerOnScreen();
        stage.show();
    }

    private void sceneSwitcher(String fxmlFileName,Parent controlElement, 
                    String title, double width, double height) throws IOException {
        Stage stage;
        Parent newRoot;

        stage = (Stage) controlElement.getScene().getWindow();
        newRoot = FXMLLoader.load(
                    getClass().getResource("/library/ui/" + fxmlFileName));

        stage.hide();
        stage.setTitle(title);
        stage.setScene(new Scene(newRoot, width, height));
        stage.show();
    }

    private Notifications throwNotification(String title, String text) {
        Notifications notification = Notifications.create();
        notification.darkStyle();
        notification.position(Pos.TOP_RIGHT);
        notification.hideAfter(Duration.seconds(5));

        notification.title(title);
        notification.text(text);

        return notification;
    }

    private void goToLoginScreen(Parent controlElement) throws IOException {
        System.out.print("Going to Login Screen...");
        sceneSwitcher("login.fxml", controlElement,
                            "Login Screen", 600, 300 );
        System.out.println("done");
    }

    @FXML protected void createAccount_Clicked() throws IOException {
        sceneSwitcher("signup.fxml", login_button_submit,
                        "Sign Up Page", 600, 300);
        System.out.println("Create Account [Sign Up] thing invoked");
    }

    //===============[ Utility Methods ]=================}}}
  
    //=============[ Sign Up: signup.fxml ]==================== {{{

    // FXML props 
    @FXML private TextField signup_field_username;
    @FXML private PasswordField signup_field_password;
    @FXML private PasswordField signup_field_confirmPassword;
    @FXML private TextField signup_field_email;

    @FXML private ComboBox<String> signup_combo_securityQuestion;
    @FXML private TextField signup_field_securityAnswer;
    @FXML private Button signup_button_submit;
    @FXML private Button signup_button_cancel;


    RegexMatcher regex = new RegexMatcher();
    @FXML
    void signup_button_submit_Clicked() throws IOException,SQLException {
        System.out.println("Submitted Successfully");
        /* Validation Email: 
        *        Matches emails with [a-z] , underscores, hyphens and periods 
        *        Only a [a-z] or [0-9] could be just before @. No special chars
        */
        String user = signup_field_username.getText();
        String email = signup_field_email.getText();
        String password = signup_field_password.getText();
        String conf_password = signup_field_confirmPassword.getText();
        String security = signup_combo_securityQuestion.getValue();
        String answer = signup_field_securityAnswer.getText();
        
        // Checks that each info is in correct format
        boolean integFlag = false;

        // Regex Check : username
        if (regex.checkUsername(user) && user.length() < 16) {
            System.out.println("UserName is in Correct Format");
            signup_field_username.getStyleClass().remove("error");
            integFlag = true;
        } else {
            System.out.println("Username is in invalid Format");
            signup_field_username.getStyleClass().add("error");
            integFlag = false;
        }

        // Regex Check: email
        if (regex.checkEmail(email)) {
            System.out.println("Email is in correct format");
            signup_field_email.getStyleClass().remove("error");
            integFlag = true;
        } else {
            System.out.println("Email is in Incorrect Format");
            signup_field_email.getStyleClass().add("error");
            integFlag = false;
        }

        // Matching the passwords
        if (!password.equals(conf_password)) {
            System.out.println("Password does not match");
            signup_field_password.getStyleClass().add("error");
            signup_field_confirmPassword.getStyleClass().add("error");
            integFlag = false;
        } else {
            signup_field_password.getStyleClass().remove("error");
            signup_field_confirmPassword.getStyleClass().remove("error");

            integFlag = true;
        }

        // Finally Add the New User to the AUTHORIZED Table in DB
        if (integFlag) {
            NewUser newUser= new NewUser();
            newUser.setUsername(user);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setSecurity(security);
            newUser.setAnswer(answer);

                // Throws IOException and SQLException 
            Database db = new Database();  
            db.addUser(newUser);
            System.out.println("A new User has been created");
        } else {
            System.out.println("Wrong Info has been submited");
        }
    }

 
    @FXML
    void signup_button_cancel_Clicked() throws IOException { 
        System.out.println("Sign up page -> Login Page");
        goToLoginScreen(signup_button_cancel);
    }
    // End of =========[ signup.fxml ]============}}}
 
    //========[ Forgot Password: forgotPassword.fxml ]============{{{
    @FXML private TextField forgotPassword_field_username;
    @FXML private TextField forgotPassword_field_email;
    @FXML private TextField forgotPassword_field_question;
    @FXML private TextField forgotPassword_field_answer;

    @FXML private Button forgotPassword_button_submit;
    @FXML private Button forgotPassword_button_cancel;

    @FXML protected void forgotPassword_fetch_securityQuestion() 
        throws IOException, SQLException {

        String user = forgotPassword_field_username.getText();
        String email = forgotPassword_field_email.getText();

        Database db = new Database();
        // String question;
        if (db.doesUserExist(user, email)) {
            Controller.username = user;

            String question = db.getSecurityQuestion(user, email);
            forgotPassword_field_question.setText(question);
            forgotPassword_field_question.setEditable(false);

        } else {
            System.out.println("User Details are not correct");
            // Throw a notification abt it
            Notifications notification = throwNotification("Invalid User Details",
                                                "Enter a valid set of Username & Email");
            notification.title("Invalid User Details");
            notification.text("Enter a valid set of Username & Email");
            notification.showError();
        }
    }

  @FXML protected void forgotPassword_button_submit_Clicked() 
                                    throws IOException, SQLException {
    String user = forgotPassword_field_username.getText();
    String email = forgotPassword_field_email.getText();
    String question = forgotPassword_field_question.getText();

    if (user.length() == 0 || email.length() == 0 || question.length() == 0) {
        System.out.println("Invalid User Identity");
        // Throw a notification abt it
        Notifications notification = throwNotification(
                                        "User Identity Unknown",
                                        "You need to provide a valid set of Username & Email");
        notification.showError();
    } else {
        Database db = new Database();

        // Parent parent = forgotPassword_field_email.getScene().getRoot();
        // System.out.println(parent.getId());
        // if (db.doesUserExist(forgottenUsername, forgottenEmail)) {
            // System.out.println("Valid User Identity");

            // question = db.getSecurityQuestion(forgottenUsername, forgottenEmail);
            // System.out.println(question);

        String answer = forgotPassword_field_answer.getText();
        if (db.checkSecurityAnswer(user, answer)) {
            sceneSwitcher("resetPassword.fxml", forgotPassword_field_answer,
                            "Reset Password", 600, 300);
            System.out.println("Security Answer Matched");
        }
    }
  }

  @FXML protected void forgotPassword_button_cancel_Clicked()
                                    throws IOException {
    sceneSwitcher("login.fxml", forgotPassword_button_cancel,
                      "Login Screen", 600, 300);
  }
  //===========================================}}}

    //===========[ Reset Password: resetPassword.fxml ]================{{{
    @FXML private TextField resetPassword_field_password;
    @FXML private TextField resetPassword_field_confirmPassword;

    @FXML protected void resetPassword_button_submit_Clicked()
                                                throws SQLException {
        String passwd = resetPassword_field_password.getText();
        String confPasswd = resetPassword_field_confirmPassword.getText();

        System.out.println("Reset: " + Controller.username);
        if (passwd.equals(confPasswd)) {
            db2.changePassword(Controller.username, passwd);
            System.out.println("Password Successfully Changed");
            // Throwing a notification about password changed
            Notifications notification = throwNotification(
                                            "Password Changed",
                                            "Password changed for [user] " + Controller.username);
            notification.graphic(new ImageView("/library/images/notifications/confirmation-64px.png"));
            notification.show(); // Notification with a [tick] sign
        }
        
    }

    @FXML protected void resetPassword_button_cancel_Clicked() {
    }
    //===========[ End of: Reset Password ]========}}}
  
    //=============[ HomePage: homepage.fxml ]=================={{{
    @FXML private Button homepage_button_addBook;
    @FXML private Button homepage_button_removeBook;
    @FXML private Button homepage_button_displayBook;
    @FXML private Button homepage_button_issueBook;
    @FXML private Button homepage_button_listOfIssuedBooks;


    @FXML protected void homepage_button_addBook_Clicked() throws IOException {

        sceneSwitcher("addBook.fxml", homepage_button_addBook,
                        "Library: Add Book", 800, 400);

    }

    @FXML protected void homepage_button_removeBook_Clicked() throws IOException{

        sceneSwitcher("removeBook.fxml", homepage_button_removeBook,
                        "Library: Remove Book", 800, 400);

    }


    @FXML protected void homepage_button_displayBook_Clicked() throws IOException {
        
        Stage stage = (Stage) homepage_button_displayBook.getScene().getWindow();
        VBox newRoot = (VBox) FXMLLoader.load(getClass().getResource("/library/ui/displayBook.fxml"));

        /* Creating a table */
        TableView<BookInfo> table = new TableView<>();

        TableColumn<BookInfo, String> col_sno = new TableColumn<>("S.No.");
        col_sno.setCellValueFactory(
                        new PropertyValueFactory("sno"));

        TableColumn<BookInfo, String> col_bookId = new TableColumn<>("Book's ID");
        col_bookId.setCellValueFactory(
                        new PropertyValueFactory("bookId"));

        TableColumn<BookInfo, String> col_title = new TableColumn<>("Title");
        col_title.setCellValueFactory(
                        new PropertyValueFactory("title"));

        TableColumn<BookInfo, String> col_author = new TableColumn<>("Author");
        col_author.setCellValueFactory(
                        new PropertyValueFactory("author"));


        TableColumn<BookInfo, String> col_branch = new TableColumn<>("Branch");
        col_branch.setCellValueFactory(
                        new PropertyValueFactory("branch"));

        TableColumn<BookInfo, String> col_publisher = new TableColumn<>("Publisher");
        col_publisher.setCellValueFactory(
                        new PropertyValueFactory("publisher"));


        TableColumn<BookInfo, String> col_price = new TableColumn<>("Price");
        col_price.setCellValueFactory(
                        new PropertyValueFactory("price"));

        TableColumn<BookInfo, String> col_copies = new TableColumn<>("Copies");
        col_copies.setCellValueFactory(
                        new PropertyValueFactory("copies"));

        // Adding all columns to the Table
        table.getColumns().add(col_sno);
        table.getColumns().add(col_bookId);
        table.getColumns().add(col_branch);
        table.getColumns().add(col_title);
        table.getColumns().add(col_author);
        table.getColumns().add(col_publisher);
        table.getColumns().add(col_price);
        table.getColumns().add(col_copies);

        ObservableList<BookInfo> data = null;

        try {
        Database db = new Database();
        data = db.getAllBooks();
        } catch(SQLException e) {
        System.out.println("[Database Exception]: " + e.getMessage());
        e.printStackTrace();
        } catch(IOException z) {
        System.out.println("[IOException]: " + z.getMessage());
        }

        table.setItems(data);

        HBox hbox = new HBox();
        hbox.setSpacing(30);

        Button displayBook_button_submit = new Button("submit");
        // displayBook_button_submit.setStyleClass("submitButton");
        displayBook_button_submit.setOnAction(
                        (ActionEvent e) -> {
                        try {
                            homePage(displayBook_button_submit);
                        } catch(IOException io) {
                            System.out.println("Can't switch to Home Screen");
                            io.printStackTrace();
                        }
                        });


        Button displayBook_button_cancel = new Button("cancel");
        // displayBook_button_cancel.setStyleClass("submitButton");
        displayBook_button_cancel.setOnAction(
                        (ActionEvent e) ->  {
                        try {
                            homePage(displayBook_button_cancel);
                        } catch(IOException io) {
                            System.out.println("Can't switch to Home Screen");
                            io.printStackTrace();
                        }
                        });
        
        hbox.getChildren().add(displayBook_button_submit);
        hbox.getChildren().add(displayBook_button_cancel);
        

        /* Adding Stuffs to the new Scene */
        stage.hide();
        newRoot.getChildren().add(table);
        newRoot.getChildren().add(hbox);
        stage.setScene(new Scene(newRoot, 1000, 500));
        stage.show();
    }


    @FXML protected void homepage_button_issueBook_Clicked() throws IOException {

        sceneSwitcher("issueBook.fxml", homepage_button_issueBook,
                        "Library: Issue Book", 800, 400);

    }

    @FXML protected void homepage_button_listOfIssuedBooks_Clicked() throws IOException {

        Stage stage = (Stage) homepage_button_listOfIssuedBooks.getScene().getWindow();
        VBox newRoot = (VBox) FXMLLoader.load(getClass().getResource("/library/ui/listOfIssuedBooks.fxml"));

        TableView <BorrowerInfo> table = new TableView<>();

        TableColumn<BorrowerInfo, String> col_sno = new TableColumn<>("S.No.");
        col_sno.setCellValueFactory(
                    new PropertyValueFactory("sno"));

        TableColumn<BorrowerInfo, String> col_libraryId = new TableColumn<>("Library ID");
        col_libraryId.setCellValueFactory(
                    new PropertyValueFactory("libraryId"));

        TableColumn<BorrowerInfo, String> col_borrowerName = new TableColumn<>("Student Name");
        col_borrowerName.setCellValueFactory(
                    new PropertyValueFactory("borrowerName"));

        TableColumn<BorrowerInfo, String> col_bookId = new TableColumn<>("Book Id");
        col_bookId.setCellValueFactory(
                    new PropertyValueFactory("bookId"));

        TableColumn<BorrowerInfo, String> col_title = new TableColumn<>("Title");
        col_title.setCellValueFactory(
                    new PropertyValueFactory("title"));

        TableColumn<BorrowerInfo, String> col_issueDate = new TableColumn<>("Issue Date");
        col_issueDate.setCellValueFactory(
                    new PropertyValueFactory("issueDate"));

        TableColumn<BorrowerInfo, String> col_returnDate = new TableColumn<>("Issue Date");
        col_returnDate.setCellValueFactory(
                    new PropertyValueFactory("returnDate"));

        table.getColumns().add(col_sno);
        table.getColumns().add(col_libraryId);
        table.getColumns().add(col_borrowerName);
        table.getColumns().add(col_bookId);
        table.getColumns().add(col_title);
        table.getColumns().add(col_issueDate);
        table.getColumns().add(col_returnDate);

        ObservableList<BorrowerInfo> data = null;

        try {
            Database db = new Database();
            data = db.getAllIssuedBooks();
        } catch(SQLException e) {
            System.out.println("[Database Exception]: " + e.getMessage());
            e.printStackTrace();
        } catch(IOException z) {
            System.out.println("[IOException]: " + z.getMessage());
            z.printStackTrace();
        }

        table.setItems(data);

        Button cancel = new Button("cancel");
        cancel.setOnAction(
            (ActionEvent ae) -> {
            try {
                homePage(cancel);
            } catch(IOException e) {
                System.out.println("Can't go back from Issued Books to HomePage");
                e.printStackTrace();
            }
            });
        newRoot.getChildren().add(table);
        newRoot. getChildren().add(cancel);
        stage.hide();
        stage.setScene(new Scene(newRoot, 800, 600));
        stage.show();
    }

    @FXML private Button homepage_button_addMembers;
    @FXML protected void homepage_button_addMembers_Clicked() 
                                                throws IOException {
        sceneSwitcher("addMembers.fxml", homepage_button_addMembers,
                        "Add Library Member", 800, 400);
    }
    /* End of ========[homepage.fxml ]======= */ //}}}

    //============[ Add Books: addBook.fxml ]================== {{{

    @FXML private TextField addBook_field_bookId;
    @FXML private TextField addBook_field_title;
    @FXML private TextField addBook_field_author;
    @FXML private TextField addBook_field_branch;
    @FXML private TextField addBook_field_publisher;
    @FXML private TextField addBook_field_price;
    @FXML private TextField addBook_field_copies;

    @FXML private Button addBooks_button_submit;
    @FXML private Button addBooks_button_cancel;
    @FXML private Label addBook_label_message;

    @FXML protected void addBooks_button_submit_Clicked() throws SQLException,IOException {
        Database db = new Database();

        BookInfo book = new BookInfo();
        book.setBookId    (addBook_field_bookId.getText());
        book.setTitle     (addBook_field_title.getText());
        book.setAuthor    (addBook_field_bookId.getText());
        book.setBranch    (addBook_field_branch.getText());
        book.setPublisher (addBook_field_publisher.getText());
        book.setPrice     (Integer.parseInt(addBook_field_price.getText()));
        book.setCopies    (Integer.parseInt(addBook_field_copies.getText()));

        try {
        db.addBook(book);
        } catch (SQLException e) {
        System.out.println("[SQL Error]: Book can't be added to the DB");
        System.out.println(e.getMessage());
        }
        addBook_label_message.setText("Book Successfully Added into the DB");
    }

    @FXML protected void addBooks_button_cancel_Clicked() throws IOException {
        homePage(addBooks_button_cancel);
    }
    //===========[ End of addBooks.fxml ]================== }}}

    //===============[ Remove Book: removeBook.fxml ]================== {{{
    @FXML private TextField removeBook_field_bookId;
    @FXML private Button removeBook_button_submit;
    @FXML private Button removeBook_button_cancel;

    @FXML protected void removeBook_button_submit_Clicked() throws IOException {
        try {
        Database db = new Database();
        BookInfo book = new BookInfo();
        book.setBookId(removeBook_field_bookId.getText());
        db.removeBook(book);
        } 
        catch(SQLException e) {
        System.out.println("[Error]: " + e.getMessage());
        }
    }

    @FXML protected void removeBook_button_cancel_Clicked() throws IOException {
        homePage(removeBook_button_cancel);
    }

    // ==============[End of removeBook.fxml ]================== }}}

    //=============[ Issue Books : issueBooks.fxml ]============{{{

    @FXML private TextField issueBook_field_libraryId;
    @FXML private TextField issueBook_field_bookId;
    @FXML private TextField issueBook_field_borrowerName;
    @FXML private TextField issueBook_field_bookName;

    @FXML private DatePicker issueBook_date_issue;
    @FXML private DatePicker issueBook_date_return;

    @FXML private Button  issueBook_button_cancel;
    @FXML private Button  issueBook_button_submit;
    @FXML private Label issueBook_label;

    @FXML protected void issueBook_button_submit_Clicked() throws IOException {
        BorrowerInfo borrower = new BorrowerInfo();
        borrower.setLibraryId   (issueBook_field_libraryId.getText());
        borrower.setBookId      (issueBook_field_bookId.getText());
        borrower.setBorrowerName(issueBook_field_borrowerName.getText());
        borrower.setTitle       (issueBook_field_bookName.getText());
        borrower.setIssueDate   (datePickerToString(issueBook_date_issue, "yyyy-MM-dd"));
        borrower.setReturnDate  (datePickerToString(issueBook_date_return, "yyyy-MM-dd"));
        // String dateString = datePickerToString(issueBook_date_issue, "dd-MM-yyyy");
        // issueBook_label.setText(dateString);
        // issueBook_label.setText(df.format(issueBook_date_issue.getValue());
        try {
            Database db = new Database();
            if (db.issueBook(borrower)) {
                Notifications notification = throwNotification( "Successfully Issued",
                                                "Book [" + borrower.getBookId() + "] successfully Issued");
                notification.graphic(new ImageView(NOTIFY_ICONS + "confirmation-64px.png"));
                notification.show();
            }
        } catch(SQLException e) {
        System.out.println("Book can't be issued ");
        e.printStackTrace();
        }
    }

    private static String datePickerToString(DatePicker obj, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = obj.getValue(); 

        return formatter.format(date);
    }

    @FXML protected void issueBook_button_cancel_Clicked() throws IOException {
        homePage(issueBook_button_cancel);
    }

    //=============[ End of: issueBooks.fxml ]============}}}

    //============[ Add New Library Member: addMembers.fxml ]=========={{{
    
    @FXML protected TextField addMembers_field_firstName;
    @FXML protected TextField addMembers_field_lastName;
    @FXML protected TextField addMembers_field_rollNo;
    @FXML protected TextField addMembers_field_email;

    @FXML protected ComboBox<String> addMembers_combo_semester;
    @FXML protected ComboBox<String> addMembers_combo_branch;

    @FXML private Button addMembers_button_submit;
    @FXML protected void addMembers_button_submit_Clicked() 
                                throws SQLException, IOException {

        String firstName = addMembers_field_firstName.getText();
        String lastName  = addMembers_field_lastName.getText();
        String rollNo    = addMembers_field_rollNo.getText();
        String email     = addMembers_field_email.getText();

        String semester  = addMembers_combo_semester.getValue();
        String branch = addMembers_combo_branch.getValue();

        RegexMatcher regex = new RegexMatcher();
        boolean integFlag = false;
        if (regex.checkEmail(email)) {
            addMembers_field_email.getStyleClass().remove("error");
            integFlag = true;
        } else {
            addMembers_field_email.getStyleClass().add("error");
            integFlag=false;
        }

        if (integFlag) {
            MemberInfo member = new MemberInfo();
            member.setFirstName(firstName);
            member.setLastName(lastName);
            member.setBranch(branch);
            member.setRollNo(Integer.parseInt(rollNo));
            member.setSemester(Integer.parseInt(semester));
            member.setEmail(email);

            db2.addMember(member);

            System.out.println("Member successfully added");

            Notifications notification = throwNotification("Library Membership",
                                            "New Library Member [" + firstName + "] successfully added");
            notification.graphic(new ImageView(NOTIFY_ICONS + "confirmation-64px.png"));
            notification.show();
        } 
        else {
            System.out.println("Details don't pass the Regex Test in Add Library Member");
            Notifications notification = throwNotification("Library Membership",
                                            "Fill The Details Carefully, There are Errors to fix");
            notification.graphic(new ImageView(NOTIFY_ICONS + "invalid-64px.png"));
            notification.show();
        }
        return;
    }

    @FXML protected Button addMembers_button_cancel;
    @FXML protected void addMembers_button_cancel_Clicked() 
                                throws SQLException, IOException {
        homePage(addMembers_button_cancel);
    }

    //=================[ End : addMembers.fxml ]====================}}}

}
