package cw_models;

import models.ExamRecord;
import models.Invigilator;
import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.util.*;

/**
 * A period of time during which an exam session can take place.
 *
 * <p>Implemented as a class instead of attributes to speedup the process
 * of finding all the exam session at a certain TimeSlot, also makes it
 * easier to identify clashes in TimeSlot</p>
 */
@Entity
public class TimeSlot extends Model{
    /**
     * ID of the TimeSlot
     */
    @Id
    @Column(name = "slot_id")
    private Integer timeSlotId;

    /**
     * the point of time marking the begining of a TimeSlot.
     */
    private Date startTime;

    /**
     * the point of time marking the end of a TimeSlot.
     */
    private Date endTime;

    /**
     * a list of ExamSessions that took/take/will-take place in thi TimeSlot.
     */
    @OneToMany(mappedBy = "timeSlot")
    private List<ExamSession> examSessionList = new ArrayList<ExamSession>();

    public static Finder<Integer, TimeSlot> find = new Finder<Integer, TimeSlot>(
            "cw", Integer.class, TimeSlot.class
    );

    /**
     * default constructor.
     */
    public TimeSlot(){ }

    /**
     * getter for timeSlotId
     * @return timeSlotId
     */
    public Integer getTimeSlotId(){
        return timeSlotId;
    }

    /**
     * getter for startTime.
     * @return startTime.
     */
    public Date getStartTime(){
        return startTime;
    }

    /**
     * getter for endTime.
     * @return endTime.
     */
    public Date getEndTime(){
        return endTime;
    }

    public List<ExamRecord> getExamRecordList(){
        List<ExamRecord> examRecordList = new ArrayList<ExamRecord>();
        for(ExamSession examSession: examSessionList){
            examRecordList.addAll(examSession.getExamRecordList());
        }
        return examRecordList;
    }

    public Set<Invigilator> getInvigilatorSet(){
        Set<Invigilator> invigilatorSet = new HashSet<Invigilator>();
        for(ExamRecord examRecord: getExamRecordList()){
            if(examRecord.getInvigilator()!=null){
                invigilatorSet.add(examRecord.getInvigilator());
            }
        }
        return invigilatorSet;
    }

    /**
     * setter for startTime and EndTime.
     *
     * <p>this method will fail if:
     * <ul>
     *     <li>startTime passed in is after endTime</li>
     *     <li>a TimeSlot with the same startTime and endTime has already existed.</li>
     * </ul></p>
     * @param  startTime   startTime to be set for this TimeSlot.
     * @param  endTime     endTime to be set for this TimeSlot.
     * @throws CMException a generic exception indicating failure of operation.
     */
    public void setTime(Date startTime, Date endTime) throws CMException{
        if(endTime.before(startTime)){
            throw new CMException("Time setting error.");
        }
        if(TimeSlot.find.where().eq("startTime", startTime).eq("endTime",endTime).findRowCount()!=0){
            throw new CMException("This time slot has already existed.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * uniquely identify a TimeSlot by providing the id of it.
     * @param  timeSlotId id of the sought-for TimeSlot
     * @return the sought-for TimeSlot
     */
    public static TimeSlot byId(Integer timeSlotId){
        return TimeSlot.find.byId(timeSlotId);
    }

    public static List<TimeSlot> getAll(){
        return TimeSlot.find.all();
    }

}
