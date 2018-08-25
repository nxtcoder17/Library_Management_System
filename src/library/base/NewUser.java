package library.base;

import javafx.beans.property.SimpleStringProperty;

public class NewUser {
    SimpleStringProperty username = new SimpleStringProperty();
    SimpleStringProperty email = new SimpleStringProperty();
    SimpleStringProperty password = new SimpleStringProperty();
    SimpleStringProperty security = new SimpleStringProperty();
    SimpleStringProperty answer = new SimpleStringProperty();

    public NewUser() {}

    public void setUsername(String name)    { username.set(name);  }
    public void setEmail (String eml)       { email.set(eml);      }
    public void setPassword (String passwd) { password.set(passwd);}
    public void setSecurity (String quest)  { security.set(quest); }
    public void setAnswer (String ans)      { answer.set(ans);     }

    public String getUsername() { return username.get(); }
    public String getEmail()    { return email.get();    }
    public String getPassword() { return password.get(); }
    public String getSecurity() { return security.get(); }
    public String getAnswer()   { return answer.get();   }

}
