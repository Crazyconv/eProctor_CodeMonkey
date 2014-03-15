package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question extends Model{
    @Id
    @Column(name = "ques_id")
    private Integer questionId;
    @Lob   //stored in databse as text but not varchar
    private String content;
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;
    @OneToMany
    private List<Solution> solutionList = new ArrayList<Solution>();

    public static Finder<Integer, Question> find = new Finder<Integer, Question>(
            Integer.class, Question.class
    );

    public Question(){ }

    public Integer getQuestionId(){
        return questionId;
    }
    public String getContent(){
        return content;
    }
    public Course getCourse(){
        return course;
    }
    public List<Solution> getSolutionList(){
        return solutionList;
    }
}
