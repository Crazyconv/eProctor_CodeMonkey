package cw_models;

import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class TimeSlot extends Model{
    @Id
    @Column(name = "slot_id")
    private Integer timeSlotId;
    private Date startTime;
    private Date endTime;
    @OneToMany(mappedBy = "timeSlot")
    private List<Allocation> allocationList = new ArrayList<Allocation>();

    public static Finder<Integer, TimeSlot> find = new Finder<Integer, TimeSlot>(
            "cw", Integer.class, TimeSlot.class
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

    public void setTime(Date startTime, Date endTime) throws CMException{
        if(endTime.before(startTime)){
            throw new CMException("Time setting error.");
        }
        if(TimeSlot.find.where().eq("startTime", startTime).eq("endTime",endTime).findRowCount()!=0){
            throw new CMException("This time slot has already exist.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static TimeSlot byId(Integer timeSlotId){
        return TimeSlot.find.byId(timeSlotId);
    }

}
