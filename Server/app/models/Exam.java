package models;

import cw_models.Allocation;
import cw_models.Course;
import cw_models.Student;
import cw_models.TimeSlot;
import play.db.ebean.Model;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Exam extends Model {
    @Id
    @Column(name = "exam_id")
    private Integer examId;
    private Integer allocationId;
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
    public Allocation getAllocation(){
        return Allocation.byId(allocationId);
    }
    public TimeSlot getTimeSlot(){
        return Allocation.byId(allocationId).getTimeSlot();
    }
    public Course getCourse(){
        return Allocation.byId(allocationId).getCourse();
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
    public void setAllocation(Allocation allocation){
        this.allocationId = allocation.getAllocationId();
    }

    public void setInvigilator(Invigilator invigilator){
        this.invigilator = invigilator;
    }
    public void setReport(Report report){
        this.report = report;
    }

    public static Exam byStudentCourse(Student student, Course course){
        List<Exam> examList = Exam.find.where().eq("studentId", student.getStudentId()).findList();
        for(Exam exam: examList){
            if(exam.getCourse().equals(course)){
                return exam;
            }
        }
        return null;
    }

    public static Integer occupied(Allocation allocation){
        return Exam.find.where().eq("allocationId",allocation.getAllocationId()).findRowCount();
    }

    public static Exam byId(Integer examId){
        return Exam.find.where().eq("examId",examId).findUnique();
    }
}
