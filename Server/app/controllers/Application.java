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

    public static Result index() {
        String CMUser = session().get("cmuser");
        if(CMUser==null){
            return unauthorized(login.render());
        }
        return ok(index.render("Welcome to Code Monkey e-proctor!"));
    }

    public static Result login(){
        String CMUser = session().get("cmuser");
        if(CMUser!=null){
            return ok(index.render("Welcome to Code Monkey e-proctor!"));
        }
        return ok(login.render());
    }

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
            boolean verify = false;
            if(domain == Global.STUDENT){
                if(Student.login(username, password)){
                    verify = true;
                }
            }else if(domain == Global.INVIGILATOR){
                if(Invigilator.login(username, password)){
                    verify = true;
                }
            }else if(domain == Global.ADMIN){
                if(username.equals(Global.ADMIN_ACCOUNT) && password.equals(Global.ADMIN_PASSWORD)){
                    verify = true;
                }
            }else{
                throw new CMException("Domain error");
            }

            if(!verify){
                throw new CMException("Username does not exist or password is not correct.");
            }
            session("domain", domain.toString());
            session("cmuser",username);
            result.put("error",0);
        }catch(CMException e){
            result.put("error",e.getMessage());
        }
        return ok(result);
    }

    public static Result logout(){
        session().clear();
        return ok(login.render());
    }

}
