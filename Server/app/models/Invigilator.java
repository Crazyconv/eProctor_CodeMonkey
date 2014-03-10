package models;

import play.db.ebean.Model;
import javax.persistence.*;

@Entity
public class Invigilator extends Model{
    @Id
    private Integer invigilatorId;
    private String account;
    private String password;
    private String name;

    public static Finder<Integer, Invigilator> find = new Finder<Integer, Invigilator>(
            Integer.class, Invigilator.class
    );

    public Invigilator(){ }

    public Integer getInvigilatorId(){
        return invigilatorId;
    }
    public String getAccount(){
        return account;
    }
    public String getPassword(){
        return password;
    }
    public String getName(){
        return name;
    }

}
