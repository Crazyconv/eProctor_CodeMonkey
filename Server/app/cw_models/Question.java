package cw_models;

import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A single question that may be selected to be asked in an {@link ExamSession}.
 */
@Entity
public class Question extends Model{
    /**
     * id of the Question.
     */
    @Id
    @Column(name = "ques_id")
    private Integer questionId;

    /**
     * content of the question.
     */
    @Lob   //stored in databse as text but not varchar
    private String content;
    @ManyToOne
    @JoinColumn(name="course_id")

    /**
     * the {@link Course} that this question is for.
     */
    private Course course;

    /**
     * a list of all the {@link Answer} submitted for this Question.
     */
    @OneToMany(mappedBy="question")
    private List<Answer> AnswerList = new ArrayList<Answer>();

    public static Finder<Integer, Question> find = new Finder<Integer, Question>(
            "cw", Integer.class, Question.class
    );

    /**
     * default constructor.
     */
    public Question(){ }

    /**
     * getter for questionId.
     * @return questionId
     */
    public Integer getQuestionId(){
        return questionId;
    }

    /**
     * getter for content
     * @return content.
     */
    public String getContent(){
        return content;
    }

    /**
     * getter for course.
     * @return course.
     */
    public Course getCourse(){
        return course;
    }

    /**
     * getter for answerList
     * @return answerList.
     */
    public List<Answer> getAnswerList(){
        return Answer.find.where().eq("question",this).findList();
    }

    /**
     * setter for content.
     *
     * Valid conten is considered to be a string of length 1-100000(a nominal value).
     * @param  content     content of the Question.
     * @throws CMException a generic execption indicating failure of setting content.
     */
    public void setContent(String content) throws CMException{
        if(content==""){
            throw new CMException("Content does not exist.");
        }
        if(content.length()>10000){
            throw new CMException("The question exceeds the 10000 character limit.");
        }
        this.content = content;
    }

    /**
     * setter for Course.
     * 
     * @param course course of the Question.
     */
    public void setCourse(Course course){
        this.course = course;
    }

    /**
     * Uniquely identify a Question by providing the id of it.
     * 
     * @param  questionId id of the sought-for Question.
     * @return the sought-for Question.
     */
    public static Question byId(Integer questionId){
        return Question.find.where().eq("questionId",questionId).findUnique();
    }
}
