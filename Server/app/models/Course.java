package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Course extends Model{
    @Id
    private Integer courseId;
    private String courseCode;
    @OneToMany(cascade=CascadeType.ALL)
    private List<Question> questionSet = new ArrayList<Question>();
    @OneToMany(cascade=CascadeType.ALL)
    private List<TimeSlot> availableSlots = new ArrayList<TimeSlot>();
    @ManyToMany(mappedBy="courseList")
    private List<Student> students = new ArrayList<Student>();

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
}
