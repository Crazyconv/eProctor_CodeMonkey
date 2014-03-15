package models;

import play.db.ebean.Model;

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
            Integer.class, Registration.class
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
}
