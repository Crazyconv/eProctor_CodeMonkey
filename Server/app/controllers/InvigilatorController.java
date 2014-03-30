package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Chat;
import models.Exam;
import models.Image;
import models.Report;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Authentication;
import utils.CMException;
import utils.Global;
import views.html.invigilator.invigilateExam;

import java.util.*;

public class InvigilatorController extends Controller {
    public static Result signIn(){
        Map<String, String[]> signInMap = request().body().asFormUrlEncoded();
        try{
            Integer InvigilatorId = Authentication.authorize(Global.INVIGILATOR);

            String[] examIds = signInMap.get("examIds");
            List<Exam> examList = new ArrayList<Exam>();
            Exam exam;
            for(String examId: examIds){
                exam = Exam.byId(Integer.parseInt(examId));
                if(exam==null){
                    throw new CMException("Exam does not exist.");
                }
                examList.add(exam);
            }
            return ok(invigilateExam.render(examList));
        }catch(CMException e){
            return ok(e.getMessage());
        }catch(NumberFormatException e){
            return ok("Invalid request");
        }
    }

    public static Result pollMessageImage(){
        DynamicForm pollForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer invigilatorId = Authentication.authorize(Global.INVIGILATOR);

            if(pollForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer lastChatId = Integer.parseInt(pollForm.get("lastChatId"));
            Integer lastImageId = Integer.parseInt(pollForm.get("lastImageId"));
            String examIdString = pollForm.get("examIds");
            String[] examIdArray = examIdString.split(",");
            List<Integer> examIds = new ArrayList<Integer>();
            for(String ei : examIdArray){
                examIds.add(Integer.parseInt(ei));
            }

            Integer newChatId = lastChatId;
            Integer newImageId = lastImageId;
            for(Integer examId : examIds){
                ObjectNode update = checkUpDate(examId, lastChatId, lastImageId);
                if(update!=null){
                    if(update.get("newChatId").intValue()>newChatId){
                        newChatId = update.get("newChatId").intValue();
                    }
                    if(update.get("newImageId").intValue()>newImageId){
                        newImageId = update.get("newImageId").intValue();
                    }
                    result.put(examId.toString(),update.get("current"));
                }
            }

            result.put("lastChatId", newChatId);
            result.put("lastImageId", newImageId);
            result.put("error",0);
        }catch(CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }

    public static ObjectNode checkUpDate(Integer examId, Integer lastChatId, Integer lastImageId)
            throws CMException,NumberFormatException{
        ObjectNode current = Json.newObject();
        ObjectNode update = Json.newObject();

        Exam exam = Exam.byId(examId);
        if(exam==null){
            throw new CMException("Exam does not exist");
        }
        Report report = exam.getReport();
        if(report!=null){
            List<Chat> chatList = report.getChatList();
            List<Image> imageList = report.getImageList();
            Integer newChatId = lastChatId;
            Integer newImageId = lastImageId;
            //new message
            List<Chat> newChatList = new ArrayList<Chat>();
            for(Chat chat: chatList){
                if(chat.getChatId()>lastChatId){
                    newChatList.add(chat);
                }else{
                    break;
                }
            }

            if(imageList.size()>0){
                if(imageList.get(0).getImageId()>newImageId){
                    newImageId = imageList.get(0).getImageId();
                }
            }

            //if there is not any new messages or image, return null
            if(newChatList.size()>0 || newImageId!=lastImageId){
                if(newChatList.size()>0){
                    newChatId = newChatList.get(0).getChatId();
                    Collections.reverse(newChatList);
                    current.put("chatList",Json.toJson(newChatList));
                }
                if(newImageId!=lastImageId){
                    current.put("imageId",newImageId);
                }
                update.put("current",current);
                update.put("newChatId",newChatId);
                update.put("newImageId",newImageId);
                return update;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    public static Result sendMessage(){
        DynamicForm messageForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer InvigilatorId = Authentication.authorize(Global.INVIGILATOR);

            if(messageForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer examId = Integer.parseInt(messageForm.get("examId"));
            Exam exam = Exam.byId(examId);
            if(exam==null){
                throw new CMException("Exam does not exist");
            }
            Report report = exam.getReport();
            if(report==null){
                throw new CMException("Student has not signed in.");
            }

            String message = messageForm.get("message");
            Chat chat = new Chat();
            chat.setMessage(message);
            chat.setReport(report);
            chat.setTime(new Date());
            chat.setFromStudent(false);
            chat.save();
            //no need for sender id?

            result.put("error",0);
        }catch(CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }

    public static Result verifyStudent(){
        DynamicForm verifyForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer InvigilatorId = Authentication.authorize(Global.INVIGILATOR);

            if(verifyForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer examId = Integer.parseInt(verifyForm.get("examId"));
            Integer verified = Integer.parseInt(verifyForm.get("status"));
            Exam exam = Exam.byId(examId);
            if(exam==null){
                throw new CMException("Exam does not exist");
            }
            Report report = exam.getReport();
            if(report==null){
                throw new CMException("Student has not signed in.");
            }

            if(verified==1){
                report.setExamStatus(Global.VERIFIED);
            }else{
                report.setExamStatus(Global.EXPELLED);
            }
            report.save();

            result.put("error",0);
        }catch(CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }

    public static Result submitRemark(){
        DynamicForm remarkForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer InvigilatorId = Authentication.authorize(Global.INVIGILATOR);

            if(remarkForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer examId = Integer.parseInt(remarkForm.get("examId"));
            String remark = remarkForm.get("remark");
            Exam exam = Exam.byId(examId);
            if(exam==null){
                throw new CMException("Exam does not exist");
            }
            Report report = exam.getReport();
            if(report==null){
                throw new CMException("Student has not signed in.");
            }

            report.setRemark(remark);
            report.save();

            result.put("error",0);
        }catch(CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }

}
