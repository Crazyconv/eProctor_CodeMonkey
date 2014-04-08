package cw_models;

import models.ExamRecord;
import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A student account.
 */
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
    @ManyToMany
    @JoinTable(name = "registration",
            joinColumns=
            @JoinColumn(name="stu_id", referencedColumnName="stu_id"),
            inverseJoinColumns=
            @JoinColumn(name="course_id", referencedColumnName="course_id")
    )
    private List<Course> courseList = new ArrayList<Course>();
    @OneToMany(mappedBy = "student")
    private List<Answer> solutionList = new ArrayList<Answer>();
    @Transient
    private List<ExamRecord> examRecordList = new ArrayList<ExamRecord>();

    public static Finder<Integer, Student> find = new Finder<Integer, Student>(
            "cw", Integer.class, Student.class
    );

    public Student(){ }

    public List<ExamRecord> getExamRecordList(){
        return ExamRecord.find.where().eq("studentId",studentId).findList();
    }
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
    public List<Course> getCourseList(){
        return courseList;
    }
    public List<Answer> getSolutionList(){
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

    public void registerCourse(Course course) throws CMException{
        if (courseList.contains(course)){
            throw new CMException(course.getCourseCode() + " has been registered to " + matricNo);
        }
        courseList.add(course);
    }

    public static Student byId(Integer studentId){
//        return Student.find.where().eq("studentId",studentId).join("courseList")
//                .fetch("courseList.allocationList").fetch("courseList.allocationList.timeSlot")
//                .fetch("courseList.questionSet")
//                .findUnique();
        return Student.find.where().eq("studentId",studentId).findUnique();
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
