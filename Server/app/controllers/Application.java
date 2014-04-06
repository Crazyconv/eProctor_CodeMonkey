
package controllers;

import cw_models.Student;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Exam;
import models.Invigilator;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.CMException;
import utils.Global;
import views.html.admin.adminView;
import views.html.invigilator.invigilatorView;
import views.html.login;
import views.html.student.studentView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A control class that performs domain-free generic operations such as login and deny access.
 */
public class Application extends Controller {
    /**
     * Render index page based on username and domain of current session.
     * 
     * Assumption: the stored session is assumed to be a validated session,
     * as it was checked by {@link #enter()}
     * @return
     * 1 of the 2:
     *<ul>
     *  <li>unauthorized(login.render()) </li>                
     *  <li>ok(admin/student/ivigilatorView.render())</li>
     *</ul>
     */
    public static Result index() {
        // realm of the seasion is auto defined, thus no authrisation checking is needed for other addresses?
        //session is used for access control
        //CMUser is the Id of student/invigilator
        //domain is the userType: 0 for student, 1 for invigilator, 2 for domain. Refer to Global.java in utils.
        //if no session, then user hasn't login, go to the login page.
        String CMUserString = session().get("cmuser");
        String domainString = session().get("domain");
        if(CMUserString==null || domainString==null){
            return unauthorized(login.render());

        }

        try{
            Integer CMUser = Integer.parseInt(CMUserString);
            Integer domain = Integer.parseInt(domainString);
            //check the user type and go to the respective page
            if(domain==0){
                Student student = Student.byId(CMUser);
                if(student==null){
                    throw new CMException("Student does not exist.");
                }

                List<Exam> examList = student.getExamList();
                //the examMap is used in the html to get the exam time for a course.
                Map<Integer,Exam> examMap = new HashMap<Integer, Exam>();
                for(Exam exam: examList){
                    Integer courseId = exam.getCourse().getCourseId();
                    examMap.put(courseId,exam);
                }
                return ok(studentView.render(student, examMap));
            }
            if(domain==1){
                Invigilator invigilator = Invigilator.byId(CMUser);
                if(invigilator==null){
                    throw new CMException("Invigilator does not exist.");
                }
                return ok(invigilatorView.render(invigilator));
            }
            if(domain==2){
                return ok(adminView.render());
            }
            return unauthorized(login.render());
        }catch (CMException e){
            return ok(e.getMessage());
        }catch (NumberFormatException e){
            return ok("invalid request!");
        }
    }

    /**
     * Tries to login in a user and register the session when successful.
     * 
     * Logging is done by performing variaous checks and validations, which includes:
     * <ul>
     *     <li>authentication, which is done by querying the login methods in data models
     *         <ul><li>{@link Student#login(String,String)} </li>
     *         <li>{@link Invigilator#login(String,String)}</li>
     *         <li>{@link Admin#login(String,String)}</li>
     *         </ul>
     *     </li>
     *     <li>login form validity</li>
     *     <li>login form completeness</li>
     *     <li>domain existence</li>
     * </ul>
     * 
     * @return JsonNode wrapped in a ok-object. It has one filed - error, which has 2 possibilities, either a 0, or a string. 
     *     <ul>
     *         <li>If it is 0, it means loggin is successful</li>
     *         <li>if it's a string, it means loggin is unsuccessful and the string itself is the error message</li>
     *     </ul>
     */
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
                // .login is a method of Studen Model, it checks whether the 2 parameters passed in is a valid usr-pwd pair
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
        }catch (NumberFormatException e){
            result.put("error","invalid request!");
        }

        // ok is a static method of Results that returns a ok-result object that implements Result (note singular/plural/capital of result)
        // ok() is overloaded to be able to take in a parameter of JasonNode/ObjectNode
        return ok(result);
    }

    /**
     * remove session.
     * 
     * @return login page wrapped in an ok HTTP reponse
     */
    public static Result logout(){
        //clear session and go to the login page
        session().clear();
        return ok(login.render());
    }

}
