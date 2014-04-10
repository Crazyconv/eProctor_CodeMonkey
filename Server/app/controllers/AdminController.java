package controllers;

import cw_models.ExamSession;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Authentication;
import utils.CMException;
import utils.Global;
import views.html.admin.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminController extends Controller {

    public static Result createInvigilator() {
        DynamicForm invigilatorForm = Form.form().bindFromRequest();
        try {
            Authentication.authorize(Global.ADMIN);

            if (invigilatorForm.hasErrors()) {
                throw new CMException("Form submit error.");
            }

            String email = invigilatorForm.get("email");
            String name = invigilatorForm.get("name");
            String password = invigilatorForm.get("password");

            Invigilator invigilator = new Invigilator();
            invigilator.setAccount(email);
            invigilator.setName(name);
            invigilator.setPassword(password);
            invigilator.save();

        } catch (CMException e) {
            return ok(adminErrorView.render(e.getMessage()));
        } catch (NumberFormatException e) {
            return ok(adminErrorView.render("Invalid request!"));
        }

        return ok(adminSuccessPage.render());
    }

    public static Result renderCreateInvigilator() {
        try{
            Authentication.authorize(Global.ADMIN);
        }catch(CMException e){
            return ok(adminErrorView.render(e.getMessage()));
        }

        return ok(createInvigilator.render());
    }

    public static Result renderEditInvigilator() {

        try{
            Authentication.authorize(Global.ADMIN);
        }catch(CMException e){
            return ok(adminErrorView.render(e.getMessage()));
        }

        return ok(editInvigilator.render());
    }

    public static Result resetPassword() {
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

        } catch (CMException e){
            return ok(adminErrorView.render(e.getMessage()));
        }

        return ok(adminSuccessPage.render());
    }

    static List<ExamSession> alList;

    public static Result renderAssignInvigilator(){
        try{
            Authentication.authorize(Global.ADMIN);

            alList = ExamSession.find.all();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            List<String> stringList = new ArrayList<String>();


            for(ExamSession examSession : alList){
                Date startTime = examSession.getTimeSlot().getStartTime();
                String startTimeStr = sdf.format(startTime);

                Date endTime = examSession.getTimeSlot().getEndTime();
                String endTimeStr = sdf.format(endTime);

                //generate the Time String, such as 2010-09-24 18:30---2010-09-24 21:30
                stringList.add(startTimeStr+"---"+endTimeStr);

            }
            return ok(assignInvigilator.render(stringList));
        } catch (CMException e){
            return ok(adminErrorView.render(e.getMessage()));
        } catch(NumberFormatException e){
            return ok(adminErrorView.render(e.getMessage()));
        }
    }

    public static Result assignInvigilator(){

        DynamicForm form = Form.form().bindFromRequest();
        try{
            Authentication.authorize(Global.ADMIN);

            if(form.hasErrors()){
                throw new CMException("Form submission error.");
            }

            String invigilatorStr = form.get("invigilator email");
            String numStudentStr = form.get("numStudent");//get the number of exam the invigilator wants to invigilate
            String selectionStr = form.get("selection");// choose the index of the slot


            int selectionIndex = Integer.parseInt(selectionStr);

            int numStudent = Integer.parseInt(numStudentStr);

            ExamSession examSession = alList.get(selectionIndex);
            System.out.println(examSession.getExamSessionId());
            List<ExamRecord> examRecordList = examSession.getExamRecordList();
            Invigilator invigilator = Invigilator.byAccount(invigilatorStr);

            if(examRecordList.size()==0){
                throw new CMException("No exam records for this slot.");
            }
            int count = 0;
            ExamRecord examRecord = examRecordList.remove(0);
            while(count<numStudent && examRecord!=null){
                //assign the exam in this slot, until the count is satified the invigilators' Number Of Students
                //Or until the slot of students in this list are assigned up.
                if(examRecord.getInvigilator()==null){
                    examRecord.setInvigilator(invigilator);
                    examRecord.save();
                    count++;
                }
                if(examRecordList.size()>0){
                    examRecord = examRecordList.remove(0);
                }else{
                    examRecord = null;
                }
            }

            String outputStr;
            if(count==numStudent){
                outputStr = "This invigilator has been assigned with "+count+" students.";
            }else{
                //if the count is lower than Number of Students of Invigilator. It will happen when the list of exams in this slot is lower than Number of Invigilator.
                outputStr = "This invigilator has been assigned with "+count+" students. It is lower than your expect because this is all students available in this time slots.";
            }

            return ok(adminErrorView.render(outputStr));
        } catch(CMException e) {
            return ok(adminErrorView.render(e.getMessage()));
        } catch(NumberFormatException e){
            return ok(adminErrorView.render(e.getMessage()));
        }
    }
}