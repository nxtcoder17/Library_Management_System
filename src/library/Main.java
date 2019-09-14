package library;

import library.database.Database;
import library.base.BookInfo;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
// import org.controlsfx.control.Notifications;

import java.sql.SQLException;
import java.io.IOException;

public class Main extends Application {
//   Notifications notification = Notifications.create();
  @Override
  public void start(Stage primaryStage) throws SQLException,IOException {
    Database db;
    try {
      db = new Database();
    } catch(IOException e) {
      System.out.println("An IOException has been raised.");
      System.out.println(e.getMessage());
    }
//     notification.title("Success");
//     notification.text("It is working");
//     notification.darkStyle();
//     notification.show();

    Parent root = FXMLLoader.load(getClass().getResource("/library/ui/login.fxml"));
    // for(BookInfo x: db.getAllBooks()) {
      // System.out.println(x.getBookId() + ":=> " + x.getTitle());
    // Label root = new Label("Hello");

    primaryStage.setTitle("Successful Test Run");
    primaryStage.setScene(new Scene(root, 600, 400));
    primaryStage.show();
  }
}
