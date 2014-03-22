package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

@Entity
public class Image extends Model{
    @Id
    @Column(name = "image_id")
    private Integer imageId;
    private Date time;
    private File picture;
    private String picturePath;
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    public Image(){ }
    public static Finder<Integer, Image> find = new Finder<Integer, Image>(
            Integer.class, Image.class
    );

    public File getPicture(){
        if(picturePath!=null && (picture==null || !picture.getPath().equals(picturePath))){
            picture = new File(picturePath);
        }
        return picture;
    }
}
