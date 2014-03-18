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

public class StudentController extends Controller {
    public static Result displaySlot(Integer courseId){
        Course course = Course.byId(courseId);
        return ok(showSlot.render(course));
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
                throw new CMException("Necessary data missing. Please retry.");
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
        }catch (CMException e){
            result.put("error",e.getMessage());
        }
        return ok(result);
    }
}
