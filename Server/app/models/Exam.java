package models;

import play.db.ebean.Model;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Exam extends Model {
    @Id
    private Integer examId;
    @ManyToOne(cascade=CascadeType.ALL)
    private Course course;
    @ManyToOne(cascade=CascadeType.ALL)
    private TimeSlot timeSlot;
    @ManyToOne(cascade=CascadeType.ALL)
    private Student student;
    @ManyToMany
    private List<Invigilator> invigilators = new ArrayList<Invigilator>();
    @OneToOne
    private Report report;

    public static Finder<Integer, Exam> find = new Finder<Integer, Exam>(
            Integer.class, Exam.class
    );

    public Exam(){ }

    public Integer getExamId(){
        return examId;
    }
    public Course getCourse(){
        return course;
    }
    public TimeSlot getTimeSlot(){
        return timeSlot;
    }
    public Student getStudent(){
        return student;
    }
    public List<Invigilator> getInvigilator(){
        return invigilators;
    }
    public Report getReport(){
        return report;
    }
}
