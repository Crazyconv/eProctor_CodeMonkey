package models;

import play.db.ebean.Model;
import javax.persistence.*;
import java.util.Date;

@Entity
public class Chat extends Model{
    @Id
    @Column(name = "chat_id")
    private Integer chatId;
	private boolean fromStudent;
    private String message;
    private Date time;
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    public static Finder<Integer, Chat> find = new Finder<Integer, Chat>(
            Integer.class, Chat.class
    );

    public Chat(){ }

    public Integer getChatId(){
        return chatId;
    }
	public boolean isFromStudent(){
	    return fromStudent;
	}
    public String getMessage(){
        return message;
    }
    public Date getTime(){
        return time;
    }

    public void setMessage(String message){
        this.message = message;
    }
    public void setTime(Date time){
        this.time = time;
    }
    public void setReport(Report report){
        this.report = report;
    }
    public void setFromStudent(boolean fromStudent){
        this.fromStudent = fromStudent;
    }
}
