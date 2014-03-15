package models;

import play.db.ebean.Model;
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

}
