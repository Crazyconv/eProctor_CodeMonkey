package models;

import play.db.ebean.Model;
import scala.Int;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Report extends Model{
    @Id
    private Integer reportId;
    @Lob
    private String remark;
    private boolean examStatus = false;
    private List<Chat> chatList = new ArrayList<Chat>();

    public static Finder<Integer, Report> find = new Finder<Integer, Report>(
            Integer.class, Report.class
    );

    public Report(){ }

    public Integer getReportId(){
        return reportId;
    }
    public String getRemark(){
        return remark;
    }
    public boolean getExamStatus(){
        return examStatus;
    }
    public List<Chat> getChatList(){
        return chatList;
    }
}
