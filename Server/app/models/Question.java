package models;

import play.db.ebean.Model;

import javax.persistence.*;

@Entity
public class Question extends Model{
    @Id
    private Integer questionId;
    @Lob   //stored in databse as text but not varchar
    private String content;

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
}
