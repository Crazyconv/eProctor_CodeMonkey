package cw_models;

import models.ExamRecord;
import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ExamSession extends Model{
    @Id
    @Column(name = "exam_session_id")
    private Integer examSessionId;
    private Integer capacity;
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;
    @ManyToOne
    @JoinColumn(name="slot_id")
    private TimeSlot timeSlot;
    @Transient
    private List<ExamRecord> examRecordList = new ArrayList<ExamRecord>();

    public static Finder<Integer, ExamSession> find = new Finder<Integer, ExamSession>(
            "cw", Integer.class, ExamSession.class
    );

    public ExamSession(){ }

    public Integer getExamSessionId(){
        return examSessionId;
    }
    public Integer getCapacity(){
       return capacity;
    }
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }
    public Course getCourse(){
        return course;
    }

    public void setCapacity(Integer capacity){
        this.capacity = capacity;
    }
    public void allocate(Course course, TimeSlot timeSlot) throws CMException{
        if(ExamSession.find.where().eq("course",course).eq("timeSlot",timeSlot).findRowCount()!=0){
            throw new CMException("This timeSlot has been allocated to course " + course.getCourseCode());
        }
        this.course = course;
        this.timeSlot = timeSlot;
    }

    public static ExamSession byCourseSlot(Course course, TimeSlot timeSlot){
        return ExamSession.find.where().eq("course",course).eq("timeSlot",timeSlot).findUnique();
    }

    public static ExamSession byId(Integer examSessionId){
        return ExamSession.find.where().eq("examSessionId",examSessionId).findUnique();
    }
}
