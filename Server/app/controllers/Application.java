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

public class Application extends Controller {

    // authentication is assumed to be checked before index()
    // Description:
    //     get username and domain from session record
    //     render the index page based on username and domain and return it as response
    // ------------------------------------------------------------------
    public static Result index() {

        // realm of the seasion is auto defined, thus no authrisation checking is needed for other addresses?
        String CMUserString = session().get("cmuser");
        String domainString = session().get("domain");
        if(CMUserString==null || domainString==null){
            return unauthorized(login.render());

        }

        Integer CMUser = Integer.parseInt(CMUserString);
        Integer domain = Integer.parseInt(domainString);
        if(domain==0){
            Student student = Student.byId(CMUser);
            return ok(studentView.render(student));
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

    // Description:
    //      return authentication result by a JsonNode wrapped in a ok-object
    // Fileds of JasonNode:
    //      error: content;     #content==0 means no error, otherwise error
    //                         #content==msg in case content!=0, content stores an error message 
    // -------------------------------------------------------------------------------
    public static Result enter(){
        ObjectNode result = Json.newObject();
        DynamicForm loginForm = Form.form().bindFromRequest();
        try{
            if(loginForm.hasErrors()){
                throw new CMException("Request failed.");
            }

            String username = loginForm.get("username");
            String password = loginForm.get("password");
            if(username=="" || password==""){
                throw new CMException("Please fill in all fields");
            }

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
            result.put("error",0);
        }catch(CMException e){
            result.put("error",e.getMessage());
        }

        // ok is a static method of Results that returns a ok-result object that implements Result (note singular/plural/capital of result)
        // ok() is overloaded to be able to take in a parameter of JasonNode/ObjectNode
        return ok(result);
    }

    public static Result logout(){
        session().clear();
        return ok(login.render());
    }

}
