package models;

import play.db.ebean.Model;
import javax.persistence.*;
import java.util.Date;

@Entity
public class Chat extends Model{
    @Id
    private Integer chatId;
    private String sender;
    private String message;
    private Date time;

    public static Finder<Integer, Chat> find = new Finder<Integer, Chat>(
            Integer.class, Chat.class
    );

    public Chat(){ }

    public Integer getChatId(){
        return chatId;
    }
    public String getSender(){
        return sender;
    }
    public String getMessage(){
        return message;
    }
    public Date getTime(){
        return time;
    }
}
