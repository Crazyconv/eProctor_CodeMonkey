package models;

import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends Model {
    @Id
    @Column(name = "stu_id")
    private Integer studentId;
    private String matricNo;
    private String password;
    @Transient  //meaning that it won't be bound to database
    private File photo;
    private String photoPath;
    @OneToMany(mappedBy = "student")
    private List<Registration> registrationList = new ArrayList<Registration>();
    @OneToMany(mappedBy = "student")
    private List<Exam> examList = new ArrayList<Exam>();
    @OneToMany(mappedBy = "student")
    private List<Solution> solutionList = new ArrayList<Solution>();

    public static Finder<Integer, Student> find = new Finder<Integer, Student>(
            Integer.class, Student.class
    );

    public Student(){ }

    public Integer getStudentId(){
        return studentId;
    }
    public String getMatricNo(){
        return matricNo;
    }
    public String getPassword(){
        return password;
    }
    public String getPhotoPath(){
        return photoPath;
    }
    public File getPhoto(){
        if(photoPath!=null && (photo==null || !photo.getPath().equals(photoPath))){
            photo = new File(photoPath);
        }
        return photo;
    }
    public List<Registration> getRegistrationList(){
        return registrationList;
    }
    public List<Exam> getExamList(){
        return examList;
    }
    public List<Solution> getSolutionList(){
        return solutionList;
    }

    public void setPhotoPath(String photoPath){
        this.photoPath = photoPath;
    }
    public void setMatricNo(String matricNo) throws CMException{
        if(!matricNo.matches("^[a-zA-Z][0-9]{7}[a-zA-Z]$")){
            throw new CMException("matricNo error.");
        }
        if(Student.find.where("matricNo = :matricNo").setParameter("matricNo",matricNo).findRowCount()!=0){
            throw new CMException("Student " + matricNo + " has been added already!");
        }
        this.matricNo = matricNo.toUpperCase();
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setPhoto(File photo){
        this.photo = photo;
    }

    public static Student byId(Integer studentId){
        return Student.find.where().eq("studentId",studentId)
                .join("registrationList").fetch("registrationList.course")
                .fetch("registrationList.course.availableSlots").fetch("registrationList.course.questionSet")
                .fetch("examList").fetch("examList.course").fetch("examList.invigilator")
                .findUnique();
    }

    public static Student byMatricNo(String matricNo){
        return Student.find.where().eq("matricNo",matricNo).findUnique();
    }

    public static boolean login(String matricNo, String password){
        if(Student.find.where().eq("matricNo",matricNo.toUpperCase()).eq("password",password).findRowCount()==0){
            return false;
        }
        return true;
    }
}
