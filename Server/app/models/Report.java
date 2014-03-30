package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import utils.Global;

@Entity
public class Report extends Model{
    @Id
    @Column(name = "report_id")
    private Integer reportId;
    @OneToOne(mappedBy = "report")
    private Exam exam;
    @Lob
    private String remark;
    //0:student not signed in
    //1:student signed in
    //2:student verified
    //3:student expelled
    private Integer examStatus = Global.NOTSIGNEDIN;
    @OneToMany(mappedBy = "report")
    @OrderBy("chatId desc")
    private List<Chat> chatList = new ArrayList<Chat>();
    @OneToMany(mappedBy = "report")
    @OrderBy("imageId desc")
    private List<Image> imageList = new ArrayList<Image>();

    public static Finder<Integer, Report> find = new Finder<Integer, Report>(
            Integer.class, Report.class
    );

    public Report(){ }

    public Integer getReportId(){
        return reportId;
    }
    public Exam getExam(){
        return exam;
    }
    public String getRemark(){
        return remark;
    }
    public Integer getExamStatus(){
        return examStatus;
    }
    public List<Chat> getChatList(){
        return chatList;
    }
    public List<Image> getImageList() {
        return imageList;
    }

    public void setExamStatus(Integer examStatus){
        this.examStatus = examStatus;
    }
    public void setRemark(String remark){
        this.remark = remark;
    }
}
