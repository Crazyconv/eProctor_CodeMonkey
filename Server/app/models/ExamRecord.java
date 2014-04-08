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
     * the {@link Invigilator} assigned for this ExamRecord.
     * 
     */
    @ManyToOne
    @JoinColumn(name="invi_id")
    private Invigilator invigilator;

    /**
     * the {@link Report} that belongs to this ExamRecord
     */
    @OneToOne
    @JoinColumn(name="report_id")
    private Report report;

    public static Finder<Integer, ExamRecord> find = new Finder<Integer, ExamRecord>(
            Integer.class, ExamRecord.class
    );

    /**
     * default constructor.
     */
    public ExamRecord(){ }


    /**
     * getter for examRecordId.
     * 
     * @return examRecord.
     */
    public Integer getExamRecordId(){
        return examRecordId;
    }

    /**
     * getter for examSession.
     * 
     * @return examSession.
     */
    public ExamSession getExamSession(){
        return ExamSession.byId(examSessionId);
    }

    /**
     * finds the {@link TimeSlot} this ExamRecord falls into.
     * 
     * @return reference to a ExamSession object.
     */
    public TimeSlot getTimeSlot(){
        return ExamSession.byId(examSessionId).getTimeSlot();
    }

    /**
     * finds the {@link Course} this ExamRecord is for.
     * 
     * @return reference to a Course object
     */
    public Course getCourse(){
        return ExamSession.byId(examSessionId).getCourse();
    }

    /**
     * finds the {@link Student} owning this ExamRecord.
     * 
     * @return reference to a Student object
     */
    public Student getStudent(){
        return Student.byId(studentId);
    }

    /**
     * getter for invigilator.
     * 
     * @return invigilator.
     */
    public Invigilator getInvigilator(){
        return invigilator;
    }

    /**
     * getter for report.
     * 
     * @return report.
     */
    public Report getReport(){
        return report;
    }

    /**
     * sets the studentId according to the {@link Student} object specified.
     * 
     * @param student the Student object.
     */
    public void setStudent(Student student){
        this.studentId = student.getStudentId();
    }

    /**
     * sets the examSessionId according to the {@link ExamSession} object specified.
     * 
     * @param examSession the ExamSession object.
     */
    public void setExamSession(ExamSession examSession){
        this.examSessionId = examSession.getExamSessionId();
    }

    /**
     * sets the invigilator according to the {@link Invigilator} object specified.
     * 
     * @param invigilator the Invigilator object.
     */
    public void setInvigilator(Invigilator invigilator){
        this.invigilator = invigilator;
    }

    /**
     * sets the report according to the {@link Report} object specified.
     * 
     * @param report the Report object.
     */
    public void setReport(Report report){
        this.report = report;
    }

    /**
     * uniquely identify an ExamRecord by providing the {@link Student} and {@link Course} object.
     *
     * <p>A student can sit in for no more than 1 {@link ExamSession} for a {@link Course}, which
     * is the reason why this method can uniquely identify an ExamRecord.</p>
     *
     * <p>Returns null, if this Student hasn't booked any ExamSession for this Course yet.</p>
     * 
     * @param  student the Student object.
     * @param  course  the Course object.
     * @return the sought-for ExamRecord.
     */
    public static ExamRecord byStudentCourse(Student student, Course course){
        List<ExamRecord> examRecordList = ExamRecord.find.where().eq("studentId", student.getStudentId()).findList();
        for(ExamRecord examRecord : examRecordList){
            if(examRecord.getCourse().equals(course)){
                return examRecord;
            }
        }
        return null;
    }

    /**
     * Gets the the number of {@link Students} who has booked the {@link ExamSession}.
     *
     * <p>Used along with {@ExamSession#capacity} to compute the availability of an ExamSession.</p>
     * 
     * @param  examSession [description]
     * @return number of Students who has booked the Exam Session.
     */
    public static Integer occupied(ExamSession examSession){
        return ExamRecord.find.where().eq("examSessionId",examSession.getExamSessionId()).findRowCount();
    }

    /**
     * Uniquely identify the ExamRecord by providing the id of it.
     * 
     * @param  examRecordId id of the sought-for ExamRecord.
     * @return the sought-for ExamRecord.
     */
    public static ExamRecord byId(Integer examRecordId){
        return ExamRecord.find.where().eq("examRecordId",examRecordId).findUnique();
    }
}
