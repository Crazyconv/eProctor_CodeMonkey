package models;

import play.db.ebean.Model;
import cw_models.*;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

/**
 * an abstract representation of 1 frame of the video stream of a particular {@link Student}.
 *
 * <p>A {@link Report} has a collection of images, all of which are frames
 * transimitted during an {@link ExamRecord}.</br>
 * Objects of this class do not contain the physical image file on disk, but
 * only information that can be used to locate/link to it, thus providing an
 * interface for the rest of the system to access thoses images.</p>
 */
@Entity
public class Image extends Model{

    /**
     * id of the image.
     */
    @Id
    @Column(name = "image_id")
    private Integer imageId;

    /**
     * time when the image is received at server side
     */
    private Date time;

    /**
     * An abstract representation of an Image on disk. (It is used to
     * retreive the actual image file upon user request)
     */
    @Transient
    private File picture;

    /**
     * the path on disk to this Image. 
     */
    private String picturePath;

    /**
     * the {@link Report} that this image belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    public Image(){ }
    public static Finder<Integer, Image> find = new Finder<Integer, Image>(
            Integer.class, Image.class
    );

    /**
     * getter for imageid
     */
    public Integer getImageId(){
        return imageId;
    }

    /**
     * getter for picture.
     * @return picture.
     */
    public File getPicture(){
        if(picturePath!=null && (picture==null || !picture.getPath().equals(picturePath))){
            picture = new File(picturePath);
        }
        return picture;
    }

    /**
     * setter for time
     * @param time time to be set to
     */
    public void setTime(Date time){
        this.time = time;
    }

    /**
     * setter for picturePath
     * 
     * @param picturePath picturePath to be set to
     */
    public void setPicturePath(String picturePath){
        this.picturePath = picturePath;
    }

    /**
     * stter for report
     * @param report Report to be set to link to
     */
    public void setReport(Report report){
        this.report = report;
    }

    /**
     * uniquely identify an Image object by providing the id of it.
     * 
     * @param  imageId id of the sought-for image
     * @return the sought-for image
     */
    public static Image byId(Integer imageId){
        return Image.find.where().eq("imageId",imageId).findUnique();
    }
}
