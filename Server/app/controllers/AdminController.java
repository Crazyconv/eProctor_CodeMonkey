package controllers;

import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Authentication;
import utils.CMException;
import utils.Global;
import views.html.admin.*;

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

    public static Result renderAssignInvigilator() {
        try{
            Authentication.authorize(Global.ADMIN);
        }catch(CMException e){
            return ok(adminErrorView.render(e.getMessage()));
        }

        return ok(assignInvigilator.render());
    }
}