package cw_models;

import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;

@Entity
public class Registration extends Model{
    @Id
    @Column(name="reg_id")
    private Integer registrationId;
    @ManyToOne
    @JoinColumn(name="stu_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;

    public static Finder<Integer, Registration> find = new Finder<Integer, Registration>(
            "cw", Integer.class, Registration.class
    );

    public Registration(){ }

    public Integer getRegistrationId(){
        return registrationId;
    }
    public Student getStudent(){
        return student;
    }
    public Course getCourse(){
        return course;
    }

    public void register(Student student, Course course) throws CMException{
        if(Registration.find.where().eq("student",student).eq("course",course).findRowCount()!=0){
            throw new CMException(course.getCourseCode() + " has been registered to " + student.getMatricNo());
        }
        this.student = student;
        this.course = course;
    }
}
