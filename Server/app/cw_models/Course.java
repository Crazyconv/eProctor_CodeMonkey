package cw_models;

import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A course for which exams are created and need proctoring services.
 */
@Entity
public class Course extends Model{
    /**
     * ID of the Course object.
     */
    @Id
    @Column(name = "course_id")
    private Integer courseId;

    /**
     * course code.
     */
    private String courseCode;

    /**
     * title of the course.
     */
    private String title;

    /**
     * the number of question that should appear in any exam session of this course.
     * 
     */
    private Integer questionNo;

    /**
     * possible instruction given to the students taking the exam of this course.
     */
    @Lob
    private String instruction;

    /**
     * the list of students who have registered for a course and eligible to book an exam.
     */
    @ManyToMany(mappedBy = "courseList")
    private List<Student> studentList = new ArrayList<Student>();
    
    
    /**
     * the question pool from where the questions for a particular exam session are selected.
     */
    @OneToMany(mappedBy="course")
    private List<Question> questionSet = new ArrayList<Question>();

    /**
     * all exam sessions that has been set up fot this course.
     */
    @OneToMany(mappedBy = "course")
    private List<ExamSession> examSessionList = new ArrayList<ExamSession>();

    public static Finder<Integer, Course> find = new Finder<Integer, Course>(
            "cw", Integer.class,Course.class
    );

    /**
     * default constructor
     */
    public Course(){ }

    /**
     * getter for courseId
     * @return courseId
     */
    public Integer getCourseId(){
        return courseId;
    }

    /**
     * getter for courseCode
     * @return courseCode
     */
    public String getCourseCode(){
        return courseCode;
    }

    /**
     * getter for course title
     * @return title
     */
    public String getTitle(){
        return title;
    }

    /**
     * getter for questionNo
     * @return questionNo
     */
    public Integer getQuestionNo(){return questionNo;}

    /**
     * getter for instrcution.
     * @return instruction
     */
    public String getInstruction(){
        return instruction;
    }

    /**
     * getter for question set.
     * @return questionSet.
     */
    public List<Question> getQuestionSet(){
        return questionSet;
    }

    /**
     * getter for examSessionList
     * @return examSessionList.
     */
    public List<ExamSession> getExamSessionList(){
        return examSessionList;
    }

    /**
     * setter for courseCode.
     * 
     * Valid courseCode is considered to be of 5-7 alphanumerics
     * @param  courseCode  the courseCode this course will be set to.
     * @throws CMException a generic Exception containing reasons for failure of operation.
     */
    public void setCourseCode(String courseCode) throws CMException{
        if(!courseCode.matches("^[a-zA-Z0-9]{5,7}$")){
            throw new CMException("CourseCode should be 5-7 alphanumeric");
        }
        if(Course.find.where("courseCode = :courseCode").setParameter("courseCode",courseCode).findRowCount()!=0){
            throw new CMException("Course " + courseCode + " has been added already.");
        }
        this.courseCode = courseCode.toUpperCase();
    }

    /**
     * setter for title.
     *
     * Valid course title is considered to be non-empty and of less than 30 characters.
     * @param  title       the title this course will be set to.
     * @throws CMException a generic Exception containing reasons for failure of operation.
     */
    public void setTitle(String title) throws CMException{
        if(title == ""){
            throw new CMException("Please enter the course title.");
        }
        if(title.length()>30){
            throw new CMException("Title length should be less than 30");
        }
        this.title = title;
    }

    /**
     * setter for questionNo.
     *
     * Valid questionNo is considered to be a positive value.
     * @param  questionNo  the questionNo this course will be set to have.
     * @throws CMException a generic Exception containing reasons for failure of operation.
     */
    public void setQuestionNo(Integer questionNo) throws CMException{
        if(questionNo <= 0){
            throw new CMException("Please enter a positive question number.");
        }
        this.questionNo = questionNo;
    }

    /**
     * setter for instruction.
     * 
     * @param instruction the instruction this course will be set to have.
     */
    public void setInstruction(String instruction){
        this.instruction = instruction;
    }

    /**
     * a search method to locate a Course by its id.
     * 
     * @param  courseId id of the target Course.
     * @return reference to the target Course
     */
    public static Course byId(Integer courseId){
        return Course.find.where().eq("courseId",courseId).findUnique();
    }
}
