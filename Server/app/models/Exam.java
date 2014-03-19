package models;

import play.db.ebean.Model;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Exam extends Model {
    @Id
    @Column(name = "exam_id")
    private Integer examId;
    private Date startTime;
    private Date endTime;
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;
    @ManyToOne
    @JoinColumn(name="stu_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name="invi_id")
    private Invigilator invigilator;
    @OneToOne
    @JoinColumn(name="report_id")
    private Report report;

    public static Finder<Integer, Exam> find = new Finder<Integer, Exam>(
            Integer.class, Exam.class
    );

    public Exam(){ }

    public Integer getExamId(){
        return examId;
    }
    public Date getStartTime(){
        return startTime;
    }
    public Date getEndTime(){
        return endTime;
    }
    public Course getCourse(){
        return course;
    }
    public Student getStudent(){
        return student;
    }
    public Invigilator getInvigilator(){
        return invigilator;
    }
    public Report getReport(){
        return report;
    }

    public void setStudent(Student student){
        this.student = student;
    }
    public void setCourse(Course course){
        this.course = course;
    }
    public void setSlot(TimeSlot slot){
        this.startTime  = slot.getStartTime();
        this.endTime = slot.getEndTime();
    }

    public static Exam byStudentCourse(Student student, Course course){
        return Exam.find.where().eq("student",student).eq("course",course).findUnique();
    }

    public static Integer occupied(Course course, TimeSlot slot){
        return Exam.find.where().eq("course",course).eq("startTime",slot.getStartTime()).findRowCount();
    }
}
