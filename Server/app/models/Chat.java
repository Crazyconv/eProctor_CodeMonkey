package models;

import play.db.ebean.Model;
import javax.persistence.*;
import java.util.Date;

/**
 * A chat message comminucated during an {@link ExamRecord}.
 */
@Entity
public class Chat extends Model{

    /**
     * ID of this chat.
     */
    @Id
    @Column(name = "chat_id")
    private Integer chatId;

    /**
     * if it's 0, this chat is sent from {@link Student}; otherwise, {@link Invigilator}.
     */
	private boolean fromStudent;

    /**
     * content of the chat message.
     */
    private String message;

    /**
     * the time when this chat was sent.
     */
    private Date time;

    /**
     * the report that this Chat belongs to
     */
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    public static Finder<Integer, Chat> find = new Finder<Integer, Chat>(
            Integer.class, Chat.class
    );

    /**
     * defautl constructor.
     */
    public Chat(){ }

    /**
     * getter for chatId.
     * 
     * @return chatId.
     */
    public Integer getChatId(){
        return chatId;
    }

    /**
     * getter for fromStudent.
     * 
     * @return fromStudent.
     */
	public boolean isFromStudent(){
	    return fromStudent;
	}

    /**
     * getter for message.
     * 
     * @return message.
     */
    public String getMessage(){
        return message;
    }

    /**
     * getter for time.
     * @return time.
     */
    public Date getTime(){
        return time;
    }

    /**
     * setter for message.
     * 
     * @param message message.
     */
    public void setMessage(String message){
        this.message = message;
    }

    /**
     * setter for time.
     * 
     * @param time time.
     */
    public void setTime(Date time){
        this.time = time;
    }

    /**
     * setter for report.
     * 
     * @param report report.
     */
    public void setReport(Report report){
        this.report = report;
    }

    /**
     * setter for fromStudent.
     * 
     * @param fromStudent fromStudent.
     */
    public void setFromStudent(boolean fromStudent){
        this.fromStudent = fromStudent;
    }
}
