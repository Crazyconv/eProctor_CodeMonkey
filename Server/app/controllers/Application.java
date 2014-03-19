package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import utils.CMException;
import utils.Global;
import views.html.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application extends Controller {

    public static Result index() {
        //session is used for access control
        //CMUser is the Id of student/invigilator
        //domain is the userType: 0 for student, 1 for invigilator, 2 for domain. Refer to Global.java in utils.
        //if no session, then user hasn't login, go to the login page.
        String CMUserString = session().get("cmuser");
        String domainString = session().get("domain");
        if(CMUserString==null || domainString==null){
            return unauthorized(login.render());
        }

        Integer CMUser = Integer.parseInt(CMUserString);
        Integer domain = Integer.parseInt(domainString);
        //check the user type and go to the respective page
        if(domain==0){
            Student student = Student.byId(CMUser);
            List<Exam> examList = student.getExamList();
            //the examMap is used in the html to get the exam time for a course.
            Map<Integer,Exam> examMap = new HashMap<Integer, Exam>();
            for(Exam exam: examList){
                Integer courseId = exam.getCourse().getCourseId();
                examMap.put(courseId,exam);
            }
            return ok(studentView.render(student,examMap));
        }
        if(domain==1){
            Invigilator invigilator = Invigilator.byId(CMUser);
            return ok(invigilatorView.render(invigilator));
        }
        if(domain==2){
            return ok(adminView.render());
        }
        return unauthorized(login.render());
    }

    public static Result enter(){
        //contentType of response is a json object
        ObjectNode result = Json.newObject();
        DynamicForm loginForm = Form.form().bindFromRequest();
        try{
            //check for form error
            if(loginForm.hasErrors()){
                throw new CMException("Request failed.");
            }
            String username = loginForm.get("username");
            String password = loginForm.get("password");
            if(username=="" || password==""){
                throw new CMException("Please fill in all fields");
            }

            //verify, get the id and domain to be stored in session
            Integer domain = Integer.parseInt(loginForm.get("domain"));
            Integer CMUser = null;
            boolean verify = false;
            if(domain == Global.STUDENT){
                if(Student.login(username, password)){
                    Student student = Student.byMatricNo(username);
                    CMUser = student.getStudentId();
                    verify = true;
                }
            }else if(domain == Global.INVIGILATOR){
                if(Invigilator.login(username, password)){
                    Invigilator invigilator = Invigilator.byAccount(username);
                    CMUser = invigilator.getInvigilatorId();
                    verify = true;
                }
            }else if(domain == Global.ADMIN){
                if(username.equals(Global.ADMIN_ACCOUNT) && password.equals(Global.ADMIN_PASSWORD)){
                    verify = true;
                    CMUser = 0;
                }
            }else{
                throw new CMException("Domain error");
            }

            if(!verify){
                throw new CMException("Username does not exist or password is not correct.");
            }
            session("domain", domain.toString());
            session("cmuser",CMUser.toString());
            //error:0 is later used in javascript to indicate the request (in this case, login verification) is correctly handled.
            result.put("error",0);
        }catch(CMException e){
            //the error message is later used in javascript to display in html
            result.put("error",e.getMessage());
        }
        return ok(result);
    }

    public static Result logout(){
        //clear session and go to the login page
        session().clear();
        return ok(login.render());
    }

}
