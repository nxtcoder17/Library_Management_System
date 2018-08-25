package library.base;

import library.base.BookInfo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BorrowerInfo extends BookInfo {
  SimpleStringProperty libraryId = new SimpleStringProperty();
  SimpleStringProperty borrowerName = new SimpleStringProperty();
  SimpleStringProperty issueDate = new SimpleStringProperty();
  SimpleStringProperty returnDate = new SimpleStringProperty();

  SimpleIntegerProperty sno = new SimpleIntegerProperty();
  SimpleIntegerProperty fines = new SimpleIntegerProperty();

  public BorrowerInfo() {}

  public void setLibraryId(String libraryId) { this.libraryId.set(libraryId); }
  public void setBorrowerName (String bName) { this.borrowerName.set(bName);  }
  public void setIssueDate (String issue)    { this.issueDate.set(issue);     }
  public void setReturnDate (String rDate)  { this.returnDate.set(rDate);   }
  public void setFines(int fine)             { this.fines.set(fine);          }
  public void setSno(int sno)                { this.sno.set(sno);             }

  public String getLibraryId()              { return libraryId.get(); }
  public String getBorrowerName()           { return borrowerName.get(); }
  public String getIssueDate()              { return issueDate.get(); }
  public String getReturnDate()             { return returnDate.get(); }
  public int getFines()                     { return fines.get(); }
  public int getSno()                       { return sno.get(); } 
}
