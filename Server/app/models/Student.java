package models;

import play.db.ebean.Model;
import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends Model {
    @Id
    private Integer studentId;
    private String matricNo;
    private String password;
    @Transient  //meaning that it won't be bound to database
    private File photo;
    private String photoPath;
    @ManyToMany
    private List<Course> courseList = new ArrayList<Course>();

    public static Finder<Integer, Student> find = new Finder<Integer, Student>(
            Integer.class, Student.class
    );

    public Student(){ }

    public Integer getStudentId(){
        return studentId;
    }
    public String getMatricNo(){
        return matricNo;
    }
    public String getPassword(){
        return password;
    }
    public String getPhotoPath(){
        return photoPath;
    }
    public File getPhoto(){
        if(photoPath!=null && (photo==null || !photo.getPath().equals(photoPath))){
            photo = new File(photoPath);
        }
        return photo;
    }
    public List<Course> getCourseList(){
        return courseList;
    }

    public void setPhotoPath(String photoPath){
        this.photoPath = photoPath;
    }
}
