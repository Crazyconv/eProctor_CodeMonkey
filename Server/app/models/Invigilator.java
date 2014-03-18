package models;

import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Invigilator extends Model{
    @Id
    @Column(name = "invi_id")
    private Integer invigilatorId;
    private String account;
    private String password;
    private String name;
    @OneToMany(mappedBy = "invigilator")
    private List<Exam> examList = new ArrayList<Exam>();

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
    public List<Exam> getExamList(){
        return examList;
    }

    public static boolean login(String account, String password){
        if(Invigilator.find.where().eq("account",account).eq("password",password).findRowCount()==0){
            return false;
        }
        return true;
    }

    public static Invigilator byId(Integer invigilatorId){
        return Invigilator.find.byId(invigilatorId);
    }

    public static Invigilator byAccount(String account){
        return Invigilator.find.where().eq("account",account).findUnique();
    }

}
