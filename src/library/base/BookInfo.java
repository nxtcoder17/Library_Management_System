package library.base;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BookInfo {

    SimpleIntegerProperty sno     = new SimpleIntegerProperty();
    SimpleStringProperty bookId   = new SimpleStringProperty();
    SimpleStringProperty branch   = new SimpleStringProperty();
    SimpleStringProperty title    = new SimpleStringProperty();
    SimpleStringProperty author   = new SimpleStringProperty();
    SimpleStringProperty publisher = new SimpleStringProperty();
    SimpleIntegerProperty price   = new SimpleIntegerProperty();
    SimpleIntegerProperty copies  = new SimpleIntegerProperty();

    public BookInfo() {}

    public void setSno(int sno)               { this.sno.set(sno);            }
    public void setBookId(String bookId)      { this.bookId.set(bookId);      }
    public void setTitle(String title)        { this.title.set(title);        }
    public void setAuthor(String author)      { this.author.set(author);      }
    public void setPublisher(String publisher){ this.publisher.set(publisher);}
    public void setPrice(int price)           { this.price.set(price);        }
    public void setCopies(int copies)         { this.copies.set(copies);      }
    public void setBranch(String branch)      { this.branch.set(branch);      }

    public int    getSno()      { return sno.get();       }
    public String getBookId()   { return bookId.get();    }
    public String getTitle()    { return title.get();     }
    public String getAuthor()   { return author.get();    }
    public String getPublisher(){ return publisher.get(); }
    public int    getPrice()    { return price.get();     }
    public int    getCopies()   { return copies.get();    }
    public String getBranch()   { return branch.get();    }
}


