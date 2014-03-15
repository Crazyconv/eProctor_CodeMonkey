package models;

import play.db.ebean.Model;

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

}
