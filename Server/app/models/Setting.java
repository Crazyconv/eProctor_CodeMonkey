package models;

import play.db.ebean.Model;

import javax.persistence.*;

//this class is to store related info of a school's policy
//for example, when should a student/invigilator sign in
@Entity
public class Setting extends Model {

    @Id
    @Column(name = "s_id")
    private Integer settingId;
    private String sKey;
    private String sValue;

    public Setting(){ }

    public static Finder<Integer,Setting> find = new Finder<Integer,Setting>(
            Integer.class,Setting.class
    );

    public String getValue(){
        return sValue;
    }

    public static String get(String sKey){
        return Setting.find.where().eq("sKey",sKey).findUnique().getValue();
    }

    public void setsKey(String sKey){
        this.sKey = sKey;
    }

    public void setsValue(String sValue){
        this.sValue = sValue;
    }

}
