package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import utils.CMException;
import views.html.*;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.SimpleDateFormat;

public class StudentController extends Controller {

    public static Result showSlot(){
        DynamicForm slotForm = Form.form().bindFromRequest();

        try{
            if(slotForm.hasErrors()){
                throw new CMException("Form submit error.");
            }
            Integer courseId = Integer.parseInt(slotForm.get("courseId"));
            Integer studentId = Integer.parseInt(slotForm.get("studentId"));
            Course course = Course.byId(courseId);
            Student student = Student.byId(studentId);
            if(course==null || student==null){
                throw new CMException("Necessary data missing.");
            }
            Exam exam = Exam.byStudentCourse(student,course);
            return ok(showSlot.render(course,exam));
        }catch(CMException e){
            return ok(e.getMessage());
        }
    }

    public static Result selectSlot(){
        ObjectNode result = Json.newObject();
        DynamicForm slotForm = Form.form().bindFromRequest();
        try{
            String CMUserString = session().get("cmuser");
            String domainString = session().get("domain");
            if(CMUserString==null || domainString==null || Integer.parseInt(domainString)!=0){
                throw new CMException("Access denied. Please login.");
            }

            if(slotForm.hasErrors()){
                throw new CMException("Form submit error.");
            }
            Integer courseId = Integer.parseInt(slotForm.get("courseId"));
            Integer slotId = Integer.parseInt(slotForm.get("slotId"));
            Integer studentId = Integer.parseInt(CMUserString);
            Student student = Student.byId(studentId);
            Course course = Course.byId(courseId);
            TimeSlot slot = TimeSlot.byId(slotId);
            if(student==null || course==null || slot==null){
                throw new CMException("Necessary data missing.");
            }

            Exam exam = Exam.byStudentCourse(student, course);
            if(exam==null){
                exam = new Exam();
                exam.setStudent(student);
                exam.setCourse(course);
            }
            exam.setSlot(slot);
            exam.save();
            result.put("error",0);
            result.put("date",new SimpleDateFormat("dd/MM/yyyy").format(slot.getStartTime()));
            result.put("start",new SimpleDateFormat("kk:mm").format(slot.getStartTime()));
            result.put("end",new SimpleDateFormat("kk:mm").format(slot.getEndTime()));
        }catch (CMException e){
            result.put("error",e.getMessage());
        }
        return ok(result);
    }

    public static Result deleteSlot(){
        ObjectNode result = Json.newObject();
        DynamicForm slotForm = Form.form().bindFromRequest();
        try{
            String CMUserString = session().get("cmuser");
            String domainString = session().get("domain");
            if(CMUserString==null || domainString==null || Integer.parseInt(domainString)!=0){
                throw new CMException("Access denied. Please login.");
            }

            if(slotForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer courseId = Integer.parseInt(slotForm.get("courseId"));
            Integer studentId = Integer.parseInt(CMUserString);
            Student student = Student.byId(studentId);
            Course course = Course.byId(courseId);
            if(student==null || course==null){
                throw new CMException("Necessary data missing.");
            }
            Exam exam = Exam.byStudentCourse(student,course);
            if(exam==null){
                throw new CMException("No such record.");
            }
            exam.delete();
            result.put("error",0);
        }catch (CMException e){
            result.put("error",e.getMessage());
        }
        return ok(result);
    }
}
