package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

// Image is a class is a linker to actual image file, 1 object of Image corresponds to 1 image file
@Entity
public class Image extends Model{
    @Id
    @Column(name = "image_id")
    private Integer imageId;
    private Date time;
    @Transient
    private File picture;
    private String picturePath;
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    public Image(){ }
    public static Finder<Integer, Image> find = new Finder<Integer, Image>(
            Integer.class, Image.class
    );

    public Integer getImageId(){
        return imageId;
    }

    public File getPicture(){
        if(picturePath!=null && (picture==null || !picture.getPath().equals(picturePath))){
            picture = new File(picturePath);
        }
        return picture;
    }

    public void setTime(Date time){
        this.time = time;
    }

    public void setPicturePath(String picturePath){
        this.picturePath = picturePath;
    }

    public void setReport(Report report){
        this.report = report;
    }

    public static Image byId(Integer imageId){
        return Image.find.where().eq("imageId",imageId).findUnique();
    }
}
