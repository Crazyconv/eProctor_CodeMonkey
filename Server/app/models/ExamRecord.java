package models;

import cw_models.ExamSession;
import cw_models.Course;
import cw_models.Student;
import cw_models.TimeSlot;
import play.db.ebean.Model;
import javax.persistence.*;
import java.util.List;

/**
 * An exam record.
 * 
 * <p>An exam record is not confined to a "record" only. It in fact refers to a specific activity that a
 * particular student sits in for a particular exam session.</p>
 * 
 * <p>Thus it makes sense to design the database in a way that an exam session contains multiple exam records,
 * as there are multiple test-taking activities happening concurrently in that exam session.<br/>
 * And it also makes sense to say "studentA checks in for exam_recordX", which means studentA starts
 * that particular test-taking activity.</br>
 * However, one can also say "studentA checks in for exam session", it's just this way of putting it isn't
 * as specific as the fommer one.</br></p>
 *
 * <p>Similarly, it is logical to say "InvigilatorA is invigilating exam recordX", which simply means InvigilatorA
 * is invigilating an specific avtivity. And in fact this way of puting it is more precise than saying "InvigilatorA
 * is invilating studentB", which miss out the information of exam session (simply put, "on which subject and at what
 * time is this InviglatorA invigilating studentB?").
 * </p>
 * 
 * 
 */
@Entity
public class ExamRecord extends Model {

    /**
     * ID of the exam, used to uniquely identify an exam record.
     */
    @Id
    @Column(name = "exam_record_id")
    private Integer examRecordId;

    /**
     * ID of the {@link cw_models.ExamSession exam session} this exam record refers to.
     */
    private Integer examSessionId;

    /**
     * ID of the {@link Student} this exam record refers to.
     */
    private Integer studentId;

    /**
     *
     * 
     */
    @ManyToOne
    @JoinColumn(name="invi_id")
    private Invigilator invigilator;
    @OneToOne
    @JoinColumn(name="report_id")
    private Report report;

    public static Finder<Integer, ExamRecord> find = new Finder<Integer, ExamRecord>(
            Integer.class, ExamRecord.class
    );

    public ExamRecord(){ }

    public Integer getExamRecordId(){
        return examRecordId;
    }
    public ExamSession getExamSession(){
        return ExamSession.byId(examSessionId);
    }
    public TimeSlot getTimeSlot(){
        return ExamSession.byId(examSessionId).getTimeSlot();
    }
    public Course getCourse(){
        return ExamSession.byId(examSessionId).getCourse();
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
    public void setExamSession(ExamSession examSession){
        this.examSessionId = examSession.getExamSessionId();
    }

    public void setInvigilator(Invigilator invigilator){
        this.invigilator = invigilator;
    }
    public void setReport(Report report){
        this.report = report;
    }

    public static ExamRecord byStudentCourse(Student student, Course course){
        List<ExamRecord> examRecordList = ExamRecord.find.where().eq("studentId", student.getStudentId()).findList();
        for(ExamRecord examRecord : examRecordList){
            if(examRecord.getCourse().equals(course)){
                return examRecord;
            }
        }
        return null;
    }

    public static Integer occupied(ExamSession examSession){
        return ExamRecord.find.where().eq("examSessionId",examSession.getExamSessionId()).findRowCount();
    }

    public static ExamRecord byId(Integer examRecordId){
        return ExamRecord.find.where().eq("examRecordId",examRecordId).findUnique();
    }
}
