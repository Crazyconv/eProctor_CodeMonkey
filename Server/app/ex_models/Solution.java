package ex_models;

import play.db.ebean.Model;

import javax.persistence.*;

@Entity
public class Solution extends Model {
    @Id
    private Integer solutionId;
    @ManyToOne(cascade=CascadeType.ALL)
    private Question question;
    private String answer;
    @ManyToOne(cascade=CascadeType.ALL)
    private Student student;

    public static Finder<Integer, Solution> find = new Finder<Integer, Solution>(
            Integer.class, Solution.class
    );

    public Solution(){ }

    public Integer getSolutionId(){
        return solutionId;
    }
    public Question getQuestion(){
        return question;
    }
    public String getAnswer(){
        return answer;
    }
    public Student getStudent(){
        return student;
    }
}
