package models;

import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TimeSlot extends Model{
    @Id
    @Column(name = "time_id")
    private Integer timeSlotId;
    private Date startTime;
    private Date endTime;
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;
    private Integer capacity;

    public static Finder<Integer, TimeSlot> find = new Finder<Integer, TimeSlot>(
            Integer.class, TimeSlot.class
    );

    public TimeSlot(){ }

    public Integer getTimeSlotId(){
        return timeSlotId;
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
    public Integer getCapacity(){
        return capacity;
    }

    public void setCapacity(Integer capacity){
        this.capacity = capacity;
    }
    public void setSlot(Course course, Date startTime, Date endTime) throws CMException{
        if(endTime.before(startTime)){
            throw new CMException("Time setting error.");
        }
        if(TimeSlot.find.where().eq("startTime", startTime).eq("course",course).findRowCount()!=0){
            throw new CMException("This timeslot has been added to course " + course.getCourseCode() + " or has overlapping.");
        }
        this.course = course;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static TimeSlot byId(Integer timeSlotId){
        return TimeSlot.find.byId(timeSlotId);
    }

}
