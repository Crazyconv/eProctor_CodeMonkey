package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Course extends Model{
    @Id
    @Column(name = "course_id")
    private Integer courseId;
    private String courseCode;
    @OneToMany(mappedBy="course")
    private List<Question> questionSet = new ArrayList<Question>();
    @OneToMany(mappedBy="course")
    private List<TimeSlot> availableSlots = new ArrayList<TimeSlot>();
    @OneToMany(mappedBy="course")
    private List<Registration> registrationList = new ArrayList<Registration>();
    @OneToMany(mappedBy="course")
    private List<Exam> examList = new ArrayList<Exam>();

    public static Finder<Integer, Course> find = new Finder<Integer, Course>(
            Integer.class,Course.class
    );

    public Course(){ }

    public Integer getCourseId(){
        return courseId;
    }
    public String getCourseCode(){
        return courseCode;
    }
    public List<Question> getQuestionSet(){
        return questionSet;
    }
    public List<TimeSlot> getAvailableSlots(){
        return availableSlots;
    }
    public List<Registration> getRegistrationList(){
        return registrationList;
    }
    public List<Exam> getExamList(){
        return examList;
    }
}
