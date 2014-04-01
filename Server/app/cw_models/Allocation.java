package cw_models;

import models.Exam;
import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Allocation extends Model{
    @Id
    @Column(name = "allocation_id")
    private Integer allocationId;
    private Integer capacity;
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;
    @ManyToOne
    @JoinColumn(name="slot_id")
    private TimeSlot timeSlot;
    @Transient
    private List<Exam> examList = new ArrayList<Exam>();

    public static Finder<Integer, Allocation> find = new Finder<Integer, Allocation>(
            "cw", Integer.class, Allocation.class
    );

    public Allocation(){ }

    public Integer getAllocationId(){
        return allocationId;
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
        if(Allocation.find.where().eq("course",course).eq("timeSlot",timeSlot).findRowCount()!=0){
            throw new CMException("This timeSlot has been allocated to course " + course.getCourseCode());
        }
        this.course = course;
        this.timeSlot = timeSlot;
    }

    public static Allocation byCourseSlot(Course course, TimeSlot timeSlot){
        return Allocation.find.where().eq("course",course).eq("timeSlot",timeSlot).findUnique();
    }

    public static Allocation byId(Integer allocationId){
        return Allocation.find.where().eq("allocationId",allocationId).findUnique();
    }
}
