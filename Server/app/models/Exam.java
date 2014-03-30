package models;

import cw_models.Course;
import cw_models.Student;
import cw_models.TimeSlot;
import play.db.ebean.Model;
import javax.persistence.*;
import java.util.Date;

@Entity
public class Exam extends Model {
    @Id
    @Column(name = "exam_id")
    private Integer examId;
    private Date startTime;
    private Date endTime;
    private Integer courseId;
    private Integer studentId;
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
        return Course.byId(courseId);
    }
    public Student getStudent(){
        return Student.byId(studentId);
    }
    public Invigilator getInvigilator(){
        return invigilator;
    }
    public Report getReport(){
        return report;
    }

    public void setStudent(Student student){
        this.studentId = student.getStudentId();
    }
    public void setCourse(Course course){
        this.courseId = course.getCourseId();
    }
    public void setSlot(TimeSlot slot){
        this.startTime  = slot.getStartTime();
        this.endTime = slot.getEndTime();
    }
    public void setInvigilator(Invigilator invigilator){
        this.invigilator = invigilator;
    }
    public void setReport(Report report){
        this.report = report;
    }

    public static Exam byStudentCourse(Student student, Course course){
        return Exam.find.where().eq("studentId",student.getStudentId()).eq("courseId",course.getCourseId()).findUnique();
    }

    public static Integer occupied(Course course, TimeSlot slot){
        return Exam.find.where().eq("courseId",course.getCourseId()).eq("startTime",slot.getStartTime()).findRowCount();
    }

    public static Exam byId(Integer examId){
        return Exam.find.where().eq("examId",examId).findUnique();
    }
}
