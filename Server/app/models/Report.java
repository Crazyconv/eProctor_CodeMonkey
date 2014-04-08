package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import utils.Global;

/**
 * A report containing all the information captured during an {@link ExamRecord}.
 */
@Entity
public class Report extends Model{

    /**
     * id of the report.
     */
    @Id
    @Column(name = "report_id")
    private Integer reportId;

    /**
     * The ExamRecord thea this Report is about.
     */
    @OneToOne(mappedBy = "report")
    private ExamRecord examRecord;

    /**
     * Possible empty remark the invigilator left for the Student.
     */
    @Lob
    private String remark;
    /**
     * the status of the ExamRecord.
     * 
     * <p> 0:student not signed in<br/>
     *     1:student signed in<br/>
     *     2:student verified<br/>
     *     3:student expelled</p>
     */
    private Integer examStatus = Global.NOTSIGNEDIN;
    @OneToMany(mappedBy = "report")
    @OrderBy("chatId desc")

    /**
     * all chats transimitted (from both invigilator and student) during the ExamRecord owning this Report.
     */
    private List<Chat> chatList = new ArrayList<Chat>();

    /**
     * all image frames transimitted from student side during the ExamRecord owning this Report.
     */
    @OneToMany(mappedBy = "report")
    @OrderBy("imageId desc")
    private List<Image> imageList = new ArrayList<Image>();

    public static Finder<Integer, Report> find = new Finder<Integer, Report>(
            Integer.class, Report.class
    );

    /**
     * default constructor.
     */
    public Report(){ }

    /**
     * getter for reportId
     * @return reportId
     */
    public Integer getReportId(){
        return reportId;
    }

    /**
     * getter for examRecord
     * @return examRecord
     */
    public ExamRecord getExamRecord(){
        return examRecord;
    }

    /**
     * getter for remark.
     * @return remark.
     */
    public String getRemark(){
        return remark;
    }

    /**
     * getter for examStatus.
     * @return examStatus.
     */
    public Integer getExamStatus(){
        return examStatus;
    }

    /**
     * getter for chatList.
     * @return chatList.
     */
    public List<Chat> getChatList(){
        return chatList;
    }

    /**
     * getter for imageList.
     * @return imageList
     */
    public List<Image> getImageList() {
        return imageList;
    }

    /**
     * setter for examStatus.
     * @param examStatus examStatus to set to.
     */
    public void setExamStatus(Integer examStatus){
        this.examStatus = examStatus;
    }

    /**
     * setter for remark.
     * @param remark remark to set to.
     */
    public void setRemark(String remark){
        this.remark = remark;
    }
}
