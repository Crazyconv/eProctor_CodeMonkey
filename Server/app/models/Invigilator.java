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
    private List<ExamRecord> examRecordList = new ArrayList<ExamRecord>();

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
    public List<ExamRecord> getExamRecordList(){
        return examRecordList;
    }

    public void setAccount(String account) throws CMException{
        if(!account.matches("^\\s*\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")){
            throw new CMException("Invalid email format.");
        }
        if(Invigilator.find.where().eq("account",account).findRowCount()!=0){
            throw new CMException("Account " + account + " already exist.");
        }
        this.account = account.toLowerCase();
    }

    public void setPassword(String password) throws CMException{
        if(password.length()<5 || password.length()>16){
            throw new CMException("Password should contain 6-15 characters.");
        }
        this.password = password;
    }

    public void setName(String name) throws CMException{
        if(!name.matches("^[\\w\\s]{4,20}$")){
            throw new CMException(name);
        }
        this.name = name.toLowerCase();
    }

    public static boolean login(String account, String password){
        if(Invigilator.find.where().eq("account",account.toLowerCase()).eq("password",password).findRowCount()==0){
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
