package cw_models;

import models.ExamRecord;
import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An exam session is an on-line virtual "examination hall".
 * 
 * <p>It holds all the students who take the same test for a particular course
 * at a particular time slot.<br/>
 * In other words, an exam session is uniquely identified by a {@link Course} and
 * a {@link TimeSlot}.<br/>
 * Also it contains a number of {@link ExamRecord}, which is thought to be simply
 * a test taking activity, as there will be a number of test-taking activities 
 * of various students going on concurrently in an "examination hall".</p>
 */
@Entity
public class ExamSession extends Model{
    /**
     * id of the exam session.
     */
    @Id
    @Column(name = "exam_session_id")
    private Integer examSessionId;

    /**
     * the maximum number of students who can book for this exam session.
     */
    private Integer capacity;
    @ManyToOne
    @JoinColumn(name="course_id")

    /**
     * the {@link Course} that this exam session is for.
     */
    private Course course;

    /**
     * the {@link TimeSlot} during which this exam session is carried on.
     */
    @ManyToOne
    @JoinColumn(name="slot_id")
    private TimeSlot timeSlot;
    
    /**
     * the {@link ExamRecord} that this exam session holds.
     *
     */
    @Transient
    private List<ExamRecord> examRecordList = new ArrayList<ExamRecord>();

    public static Finder<Integer, ExamSession> find = new Finder<Integer, ExamSession>(
            "cw", Integer.class, ExamSession.class
    );

    /**
     * default constructor.
     */
    public ExamSession(){ }

    /**
     * getter for examSessionId.
     * @return examSessionId
     */
    public Integer getExamSessionId(){
        return examSessionId;
    }

    /**
     * getter for examCapacity.
     * @return examCapacity.
     */
    public Integer getCapacity(){
       return capacity;
    }

    /**
     * getter for timeSlot
     * @return timeSlot
     */
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /**
     * getter for course
     * @return course
     */
    public Course getCourse(){
        return course;
    }

    /**
     * setter for capacity.
     * @param capacity capacity
     */
    public void setCapacity(Integer capacity){
        this.capacity = capacity;
    }

    /**
     * treis to create an ExamSession for a specified Course at a specified TimeSlot.
     *
     * @param  course      the {@link Course} that this exam session is to be created for.
     * @param  timeSlot    the {@link TimeSlot} during which this exam session is carried on.
     * @throws CMException a generic error indicating there is already an exam session for
     *         the specified Course at a specified TimeSlot
     */
    public void allocate(Course course, TimeSlot timeSlot) throws CMException{
        if(ExamSession.find.where().eq("course",course).eq("timeSlot",timeSlot).findRowCount()!=0){
            throw new CMException("This timeSlot has been allocated to course " + course.getCourseCode());
        }
        this.course = course;
        this.timeSlot = timeSlot;
    }

    /**
     * uniquely identify an ExamSession by providing its {@link Course} and {@link TimeSlot}
     * @param  course   the {@link Course} of this sought-for ExamSession.
     * @param  timeSlot the {@link TimeSlot} of the sought-for ExamSession.
     * @return the sought-for ExamSession.
     */
    public static ExamSession byCourseSlot(Course course, TimeSlot timeSlot){
        return ExamSession.find.where().eq("course",course).eq("timeSlot",timeSlot).findUnique();
    }

    /**
     * uniquely identify an ExamSession by providing its id.
     * @param  examSessionId the id of this sought-for ExamSession
     * @return the sought-for ExamSession.
     */
    public static ExamSession byId(Integer examSessionId){
        return ExamSession.find.where().eq("examSessionId",examSessionId).findUnique();
    }
}
