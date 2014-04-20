package models;

import play.db.ebean.Model;

import javax.persistence.*;

//this class is to store related info of a school's policy
//for example, when should a student/invigilator sign in
/**
 * 
 */
@Entity
public class Setting extends Model {

    /**
     * Uniquely identify a setting.
     */
    @Id
    @Column(name = "s_id")
    private Integer settingId;

    /**
     * The name of the setting.
     *
     * <p>For example, it could be "In how much advance should a studen sign in".</p>
     */
    private String sKey;

    /**
     * The value of the setting.
     */
    private String sValue;

    /**
     * Default constructor.
     * 
     * @return [description]
     */
    public Setting(){ }

    public static Finder<Integer,Setting> find = new Finder<Integer,Setting>(
            Integer.class,Setting.class
    );

    /**
     * getter for sValue.
     * 
     * @return sValue.
     */
    public String getValue(){
        return sValue;
    }

    /**
     * searches for the sValue of a setting.
     * 
     * @param  sKey sKey of the sought-for setting.
     * @return sValue of the sought-for setting.
     */
    public static String get(String sKey){
        return Setting.find.where().eq("sKey",sKey).findUnique().getValue();
    }

    /**
     * setter of sKey.
     * 
     * @param sKey sKey to set to.
     */
    public void setsKey(String sKey){
        this.sKey = sKey;
    }

    /**
     * setter of sValue.
     * 
     * @param sValue sValue to set to.
     */
    public void setsValue(String sValue){
        this.sValue = sValue;
    }

}
