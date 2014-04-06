package cw_models;

import play.db.ebean.Model;

import javax.persistence.*;

@Entity
public class Answer extends Model {
    @Id
    @Column(name = "answer_id")
    private Integer answerId;
    @ManyToOne
    @JoinColumn(name="ques_id")
    private Question question;
    @Lob
    private String answer;
    @ManyToOne
    @JoinColumn(name="stu_id")
    private Student student;

    public static Finder<Integer, Answer> find = new Finder<Integer, Answer>(
            "cw", Integer.class, Answer.class
    );

    public Answer(){ }

    public Integer getAnswerId(){
        return answerId;
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

    public void setQuestion(Question question){
        this.question = question;
    }
    public void setAnswer(String answer){
        this.answer = answer;
    }
    public void setStudent(Student student){
        this.student = student;
    }

    public static Answer byStudentQuestion(Student student, Question question){
        return Answer.find.where().eq("student",student).eq("question",question).findUnique();
    }
}
