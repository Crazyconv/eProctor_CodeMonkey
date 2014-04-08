package cw_models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Answers that a student has given in all exam sessions he/she attended.
 * 
 */
@Entity
public class Answer extends Model {
    /**
     * ID of the answer.
     */
    @Id
    @Column(name = "answer_id")
    private Integer answerId;

    /**
     * The {@link Question} that this answer is answering to.
     */
    @ManyToOne
    @JoinColumn(name="ques_id")
    private Question question;

    /**
     * The content of the answer.
     */
    @Lob
    private String answer;

    /**
     * The {@link Student} who gave the answer.
     */
    @ManyToOne
    @JoinColumn(name="stu_id")
    private Student student;

    public static Finder<Integer, Answer> find = new Finder<Integer, Answer>(
            "cw", Integer.class, Answer.class
    );

    /**
     * Default constructor.
     * 
     */
    public Answer(){ }

    /**
     * Getter for answerId.
     * 
     * @return answerId.
     */
    public Integer getAnswerId(){
        return answerId;
    }

    /**
     * Getter for question.
     * 
     * @return Question.
     */
    public Question getQuestion(){
        return question;
    }

    /**
     * Getter for answer.
     * @return answer.
     */
    public String getAnswer(){
        return answer;
    }

    /**
     * Getter for student.
     * @return student.
     */
    public Student getStudent(){
        return student;
    }

    /**
     * Setter for question.
     * @param question question.
     */
    public void setQuestion(Question question){
        this.question = question;
    }

    /**
     * Setter for answer.
     * @param answer answer.
     */
    public void setAnswer(String answer){
        this.answer = answer;
    }

    /**
     * Setter for student.
     * @param student student.
     */
    public void setStudent(Student student){
        this.student = student;
    }

    /**
     * Gets the corresponding Answer object by specifying student and question.
     * @param  student  The student who submitted the answer.
     * @param  question The question that the answer is answering to.
     * @return reference to a unique Answer object.
     */
    public static Answer byStudentQuestion(Student student, Question question){
        return Answer.find.where().eq("student",student).eq("question",question).findUnique();
    }
}
