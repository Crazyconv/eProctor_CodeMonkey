package models;

import play.db.ebean.Model;
import utils.CMException;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An invigilator account.
 */
@Entity
public class Invigilator extends Model{
    /**
     * id of the invigilator.
     */
    @Id
    @Column(name = "invi_id")
    private Integer invigilatorId;

    /**
     * the account name of the invigilator
     */
    private String account;

    /**
     * the password of the invigilator account.
     */
    private String password;

    /**
     * name of the invigilator.
     */
    private String name;

    /**
     * all the {@link ExamRecord} that have been/ are being/ to be invigilated by this invigilator.
     */
    @OneToMany(mappedBy = "invigilator")
    private List<ExamRecord> examRecordList = new ArrayList<ExamRecord>();

    public static Finder<Integer, Invigilator> find = new Finder<Integer, Invigilator>(
            Integer.class, Invigilator.class
    );

    /**
     * default constrcutor.
     */
    public Invigilator(){ }

    /**
     * getter for invigilatorId.
     * @return invigilatorId
     */
    public Integer getInvigilatorId(){
        return invigilatorId;
    }

    /**
     * getter for account.
     * 
     * @return account.
     */
    public String getAccount(){
        return account;
    }

    /**
     * getter for password.
     * 
     * @return password
     */
    public String getPassword(){
        return password;
    }

    /**
     * getter for name.
     * 
     * @return name
     */
    public String getName(){
        return name;
    }

    /**
     * getter for examRecordList.
     * 
     * @return examRecordList
     */
    public List<ExamRecord> getExamRecordList(){
        return examRecordList;
    }

    public List<ExamRecord> getExamRecordListByDay(Date day){
        List<ExamRecord> listByDay = new ArrayList<ExamRecord>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        System.out.println(dateFormat.format(day));
        for(ExamRecord examRecord : examRecordList){
            System.out.println(dateFormat.format(examRecord.getTimeSlot().getStartTime()));
            if((dateFormat.format(examRecord.getTimeSlot().getStartTime())).equals
                    (dateFormat.format(day))){
                listByDay.add(examRecord);
            }
        }
        return listByDay;
    }

    /**
     * setter for account.
     *
     * <p>A valid account is considered to be a valid email format and no 2 accounts can have the same name.</p>
     * 
     * @param  account     account to set to.
     * @throws CMException a generic exception indicating the failure of operation.
     */
    public void setAccount(String account) throws CMException{
        if(!account.matches("^\\s*\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")){
            throw new CMException("Invalid email format.");
        }
        if(Invigilator.find.where().eq("account",account).findRowCount()!=0){
            throw new CMException("Account " + account + " already exist.");
        }
        this.account = account.toLowerCase();
    }

    /**
     * setter for password.
     *
     * <p>A valid password is considered to be of length of 6-15</p>
     * 
     * @param  password    password to set to.
     * @throws CMException a generic exception indicating the failure of operation.
     */
    public void setPassword(String password) throws CMException{
        if(password.length()<5 || password.length()>16){
            throw new CMException("Password should contain 6-15 characters.");
        }
        this.password = password;
    }


    /**
     * stter for name.
     *
     * <p>A valid name is considered to be a alphanumeric string of length of 4-20.<br/>
     * Spaces are allowed.<br/>
     * Uppercases will all be converted to lowercases.</p>
     * 
     * @param  name        name to set to.
     * 
     * @throws CMException a generic operation indicating failure of operation.
     */
    public void setName(String name) throws CMException{
        if(!name.matches("^[\\w\\s]{4,20}$")){  //
            throw new CMException(name);
        }
        this.name = name.toLowerCase();
    }

    /**
     * verify whether a account-password pair is valid.
     * 
     * @param  account  account, case-insensitive
     * @param  password password, case-sensitive.
     * @return boolean result of the verification.
     */
    public static boolean login(String account, String password){
        if(Invigilator.find.where().eq("account",account.toLowerCase()).eq("password",password).findRowCount()==0){
            return false;
        }
        return true;
    }

    /**
     * uniquely identify an invigilator by providing the id.
     * 
     * @param  invigilatorId id of the sought-for Invigilator.
     * @return the sought-for Invigilator.
     */
    public static Invigilator byId(Integer invigilatorId){
        return Invigilator.find.byId(invigilatorId);
    }

    /**
     * uniquely identify an invigilator by proding the account.
     * @param  account account of the sought-for Invigilator.
     * @return the sought-for Invigilator
     */
    public static Invigilator byAccount(String account){
        return Invigilator.find.where().eq("account",account).findUnique();
    }

    public static List<Invigilator> getAll(){
        return Invigilator.find.all();
    }

}
