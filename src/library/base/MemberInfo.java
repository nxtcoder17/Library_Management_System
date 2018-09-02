package library.base;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class MemberInfo {
    private SimpleStringProperty libraryId;

    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;

    private SimpleStringProperty branch;
    private SimpleStringProperty email;

    private SimpleIntegerProperty rollNo;
    private SimpleIntegerProperty semester;

    public MemberInfo() {
        libraryId = new SimpleStringProperty();
        firstName = new SimpleStringProperty();
        lastName = new SimpleStringProperty();
        branch = new SimpleStringProperty();
        rollNo = new SimpleIntegerProperty();
        semester = new SimpleIntegerProperty();
        email  = new SimpleStringProperty();
    }

    // Setters 
    public void setLibraryId(String libraryId)      { this.libraryId.set(libraryId); }
    public void setFirstName(String firstName)      { this.firstName.set(firstName); }
    public void setLastName(String lastName)        { this.lastName.set(lastName);   }
    public void setBranch(String branch)            { this.branch.set(branch);       }
    public void setRollNo(int rollNo)               { this.rollNo.set(rollNo);       }
    public void setSemester(int semester)           { this.semester.set(semester);   }
    public void setEmail(String email)              { this.email.set(email);         }

    // Getters
    public String getLibraryId()         { return libraryId.get();   }
    public String getFirstName()        { return firstName.get();   }
    public String getLastName()         { return lastName.get();    }
    public String getBranch()           { return branch.get();      }
    public int getRollNo()              { return rollNo.get();      }
    public int getSemester()            { return semester.get();    }
    public String getEmail()            { return email.get();       }
}
