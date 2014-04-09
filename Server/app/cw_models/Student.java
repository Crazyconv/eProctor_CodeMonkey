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
    /**
     * 
     * id of the student
     */
    @Id
    @Column(name = "stu_id")
    private Integer studentId;

    /**
     * matric number of the student, used as username of the student account.
     */
    private String matricNo;

    /**
     * password of the student account.
     */
    private String password;

    /**
     * photo of the student to identify/verify a student visually。
     *
     */
    @Transient  //meaning that it won't be bound to database
    private File photo;

    /**
     * path of the {@link #photo} on local disk.
     */
    private String photoPath;

    /**
     * Courses that this student is registered under.
     */
    @ManyToMany
    @JoinTable(name = "registration",
            joinColumns=
            @JoinColumn(name="stu_id", referencedColumnName="stu_id"),
            inverseJoinColumns=
            @JoinColumn(name="course_id", referencedColumnName="course_id")
    )
    private List<Course> courseList = new ArrayList<Course>();

    /**
     * Answers that a student has given in all exam sessions he/she attended.
     */
    @OneToMany(mappedBy = "student")
    private List<Answer> solutionList = new ArrayList<Answer>();

    /**
     * All {@link ExamRecord} created by this Student.
     */
    @Transient
    private List<ExamRecord> examRecordList = new ArrayList<ExamRecord>();

    public static Finder<Integer, Student> find = new Finder<Integer, Student>(
            "cw", Integer.class, Student.class
    );

    /**
     * default constructor
     */
    public Student(){ }

    /**
     * getter for all {@link ExamRecord} created by this Student.
     * @return [description]
     */
    public List<ExamRecord> getExamRecordList(){
        return ExamRecord.find.where().eq("studentId",studentId).findList();
    }

    /**
     * getter for studentId.
     * @return studentId.
     */
    public Integer getStudentId(){
        return studentId;
    }

    /**
     * getter for matricNo
     * @return matricNo
     */
    public String getMatricNo(){
        return matricNo;
    }

    /**
     * getter for password.
     * @return password
     */
    public String getPassword(){
        return password;
    }

    /**
     * getter  for photoPath
     * @return photoPath
     */
    public String getPhotoPath(){
        return photoPath;
    }

    /**
     * getter for photo.
     * @return photo
     */
    public File getPhoto(){
        if(photoPath!=null && (photo==null || !photo.getPath().equals(photoPath))){
            photo = new File(photoPath);
        }
        return photo;
    }

    /**
     * getter for courseList
     * @return courseList
     */
    public List<Course> getCourseList(){
        return courseList;
    }

    /**
     * getter for solutionList
     * @return solutionList
     */
    public List<Answer> getSolutionList(){
        return solutionList;
    }

    /**
     * setter for photoPath
     * @param photoPath photoPath to set to
     */
    public void setPhotoPath(String photoPath){
        this.photoPath = photoPath;
    }

    /**
     * stter for matric No
     *
     * <p>a valid matric number is considered to start by an alphabet
     * followed by 7 digits and end by an alphabet, all alphabetic
     * inputs will be converted to uppercase.</p>
     * @param  matricNo    matricNo to set to.
     * @throws CMException [description]
     */
    public void setMatricNo(String matricNo) throws CMException{
        if(!matricNo.matches("^[a-zA-Z][0-9]{7}[a-zA-Z]$")){
            throw new CMException("matricNo error.");
        }
        if(Student.find.where("matricNo = :matricNo").setParameter("matricNo",matricNo).findRowCount()!=0){
            throw new CMException("Student " + matricNo + " has been added already!");
        }
        this.matricNo = matricNo.toUpperCase();
    }

    /**
     * setter for password.
     * 
     * @param password password to set to.
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * setter for photo.
     * 
     * @param photo 
     */
    public void setPhoto(File photo){
        this.photo = photo;
    }

    /**
     * register a this student for a course.
     * @param  course      [description]
     * @throws CMException [description]
     */
    public void registerCourse(Course course) throws CMException{
        if (courseList.contains(course)){
            throw new CMException(course.getCourseCode() + " has been registered to " + matricNo);
        }
        courseList.add(course);
    }

    /**
     * uniquely identify a student by id.
     * @param  studentId id of the sought-for student.
     * @return the sought-for student.
     */
    public static Student byId(Integer studentId){
//        return Student.find.where().eq("studentId",studentId).join("courseList")
//                .fetch("courseList.allocationList").fetch("courseList.allocationList.timeSlot")
//                .fetch("courseList.questionSet")
//                .findUnique();
        return Student.find.where().eq("studentId",studentId).findUnique();
    }

    /**
     * uniquely identify a student by matric number
     * @param  matricNo matricNo if the sought-for student.
     * @return the sought-for student.
     */
    public static Student byMatricNo(String matricNo){
        return Student.find.where().eq("matricNo",matricNo).findUnique();
    }

    /**
     * verify whether a matricNo-password pair is a valid one.
     * 
     * @param  matricNo matricNo of the student to be verified
     * @param  password password of te student to be verified
     * @return return true if the passed in matricNo-password pair is a valid one; otherwise return false.
     */
    public static boolean login(String matricNo, String password){
        if(Student.find.where().eq("matricNo",matricNo.toUpperCase()).eq("password",password).findRowCount()==0){
            return false;
        }
        return true;
    }
}
