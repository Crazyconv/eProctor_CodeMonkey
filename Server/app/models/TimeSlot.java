package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TimeSlot extends Model{
    @Id
    private Integer timeSlotId;
    private Date startTime;
    private Date endTime;
    private Integer capability;

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
    public Integer getCapability(){
        return capability;
    }

}
