package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import cw_models.ExamSession;
import cw_models.TimeSlot;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Authentication;
import utils.CMException;
import utils.Global;
import views.html.admin.*;
import views.html.invigilator.invigilateExam;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminController extends Controller {

    /**
     * Creates an invigilator account based on info of the form received.
     * 
     * @return one of the 2:
     * <ul>
     *     <li>an ok HTTP response wrapping an HTTP element rendered with error message</li>
     *     <li>an ok HTTP response wrapping an HTTP element rendered with generic success message</li>
     * </ul>
     */
    public static Result createInvigilator() {
        ObjectNode result = Json.newObject();
        DynamicForm invigilatorForm = Form.form().bindFromRequest();
        try {
            Authentication.authorize(Global.ADMIN);

            if (invigilatorForm.hasErrors()) {
                throw new CMException("Form submit error.");
            }

            String email = invigilatorForm.get("account");
            String name = invigilatorForm.get("name");
            String password = invigilatorForm.get("password");

            Invigilator invigilator = new Invigilator();
            invigilator.setAccount(email);
            invigilator.setName(name);
            invigilator.setPassword(password);
            invigilator.save();

            result.put("name",name);
            result.put("email",email);
            result.put("error",0);
        } catch (CMException e) {
            result.put("error",e.getMessage());
        } catch (NumberFormatException e) {
            result.put("error","Invalid request.");
        }

        return ok(result);
    }


    /**
     * Sets the password of an invigilator account.
     *
     * Information expected from the form received.
     * <p><ul>
     *     <li>email: used to identify the invigilator account.</li>
     *     <li>password: the new password the identified invigilator account should have.</li>
     * </ul></p>
     * 
     * @return one of the 2:
     * <ul>
     *     <li>an ok HTTP response wrapping an HTTP element rendered with error message</li>
     *     <li>an ok HTTP response wrapping an HTTP element rendered with generic success message</li>
     * </ul>
     */
    public static Result resetPassword() {
        ObjectNode result = Json.newObject();
        DynamicForm passwordForm = Form.form().bindFromRequest();

        try{
            Authentication.authorize(Global.ADMIN);

            if (passwordForm.hasErrors()) {
                throw new CMException("Form submit error.");
            }

            String account = passwordForm.get("email");
            String password = passwordForm.get("password");
            Invigilator invigilator = Invigilator.byAccount(account);
            if(invigilator==null){
                throw new CMException("Account doesn't exist.");
            }
            invigilator.setPassword(password);
            invigilator.save();

            result.put("error",0);
        } catch (CMException e){
            result.put("error",e.getMessage());
        } catch (NumberFormatException e){
            result.put("error","Invalid request.");
        }

        return ok(result);
    }


    /** 
     * Provides a full listing of all {@link TimeSlot} to be assigned to invigilators.
     * 
     * @return an ok HTTP response that could contain any of the 3
     * <ul>
     *     <li>an HTML section that provides a full listing of all TimeSlots to be
     *     assigned to invigilators.</li>
     *     <li>a message string of errors thrown from methods invoked</li>
     *     <li>a message string in the current controller</li>
     * </ul>
     */
    public static Result assignInvigilator(){
        try{
            Authentication.authorize(Global.ADMIN);

            return ok(assignInvigilator.render(TimeSlot.getAll()));
        } catch (CMException e){
            return ok(e.getMessage());
        } catch(NumberFormatException e){
            return ok("Invalid request.");
        }
    }

    /**
     * Provides a full listing of all {@link Invigilator} that are available for invigilation.
     * 
     * @return an ok HTTP response that could contain any of the 3
     * <ul>
     *     <li>an HTML section that provides a full listing of all Invigilator that are
     *     available for invigilation.</li>
     *     <li>a message string of errors thrown from methods invoked</li>
     *     <li>a message string in the current controller</li>
     * </ul>
     */
    public static Result listInvigilators(){
        try{
            Authentication.authorize(Global.ADMIN);

            List<Invigilator> invigilatorList = Invigilator.getAll();

            return ok(listInvigilators.render(invigilatorList));
        } catch (CMException e){
            return ok(e.getMessage());
        } catch(NumberFormatException e){
            return ok("Invalid request.");
        }
    }

    /**
     * Gets all the free invigilators for a TimeSlot.
     *
     * @return an selection page with available invigilators wrapped in an ok HTTP response.
     *
     * @see Invigilator
     * @see TimeSlot
     */
    public static Result toggleAssign(){
        DynamicForm assignForm = Form.form().bindFromRequest();

        try{
            Authentication.authorize(Global.ADMIN);
            if(assignForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer timeSlotId = Integer.parseInt(assignForm.get("timeSlotId"));
            TimeSlot timeSlot = TimeSlot.byId(timeSlotId);
            if(timeSlot == null){
                throw new CMException("This timeSlot does not exist.");
            }

            //get the invigilator list who is free in this time slot
            Date startTime = timeSlot.getStartTime();
            Date endTime = timeSlot.getEndTime();
            List<Invigilator> allInvigilator = Invigilator.getAll();
            List<Invigilator> invigilatorList = new ArrayList<Invigilator>();
            List<ExamRecord> examRecordList;
            boolean free;
            for(Invigilator invigilator: allInvigilator){
                free = true;
                examRecordList = invigilator.getExamRecordList();
                //check whether any examRecord overlap withthe timeslot
                for(ExamRecord examRecord: examRecordList){
                    if((examRecord.getTimeSlot().getStartTime().after(startTime) &&
                        examRecord.getTimeSlot().getStartTime().before(endTime)) ||
                       (examRecord.getTimeSlot().getEndTime().after(startTime) &&
                        examRecord.getTimeSlot().getEndTime().before(endTime))){
                        free = false;
                        break;
                    }
                }
                if(free){
                    invigilatorList.add(invigilator);
                }
            }
            return ok(assign.render(timeSlot,invigilatorList));
        }catch(CMException e){
            return ok(e.getMessage());
        }catch(NumberFormatException e){
            return ok("Invalid request");
        }
    }

    /**
     * Assign a list of {@link Invigilators} to a list of {@link examRecord}. 
     *
     * This assignment takes in a list of {@link Invigilator invigilators} and
     * a list of {@link ExamRecord ExamRecords} and do the assignment randomly.
     * 
     * @return an JSonNode wrapped in an ok HTTP response, indicating whether 
     * the assignment is successful.
     */
    public static Result performAssign(){
        ObjectNode result = Json.newObject();
        Map<String, String[]> assignMap = request().body().asFormUrlEncoded();
        try{
            Authentication.authorize(Global.ADMIN);

            //get invigilator list and exam record list
            String timeSlotId = assignMap.get("timeSlotId")[0];
            String[] invigilatorIds = assignMap.get("invigilatorIds");

            TimeSlot timeSlot = TimeSlot.byId(Integer.parseInt(timeSlotId));
            if(timeSlot == null){
                throw new CMException("Time slot does not exist");
            }
            List<ExamRecord> examRecordList = timeSlot.getExamRecordList();
            List<Invigilator> invigilatorList = new ArrayList<Invigilator>();
            Invigilator invigilator;
            for(String invigilatorId: invigilatorIds){
                invigilator = Invigilator.byId(Integer.parseInt(invigilatorId));
                if(invigilator == null){
                    throw new CMException("Invigilator does not exist.");
                }
                invigilatorList.add(invigilator);
            }

            //assign the exam records in this time slot to the invigilators
            //compute the number of exam records assigned to one invigilator
            int invigilatorsNum = invigilatorList.size();
            int examRecordsNum = examRecordList.size();
            int number = (int) Math.ceil((double)examRecordsNum/(double)invigilatorsNum);
            ExamRecord examRecord;
            Set<String> nameSet = new HashSet<String>();
            for(int i=0; i<invigilatorsNum; i++){
                for(int j=i*number; (j<(i+1)*number)&&(j<examRecordsNum); j++){
                    examRecord = examRecordList.get(j);
                    invigilator = invigilatorList.get(i);
                    examRecord.setInvigilator(invigilator);
                    examRecord.save();
                    nameSet.add(invigilator.getName());
                    System.out.println("====" + examRecord.getExamRecordId() + ":" + invigilator.getInvigilatorId() + "====" );
                }
            }

            result.put("error",0);
            result.put("names",Json.toJson(nameSet));
        }catch(CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request.");
        }
        return ok(result);
    }
}