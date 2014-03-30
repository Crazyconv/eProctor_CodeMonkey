package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import cw_models.Course;
import cw_models.Question;
import cw_models.Solution;
import cw_models.Student;
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
import utils.ImageDecoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamController extends Controller{
    public static Result checkExamStatus(){
        DynamicForm statusForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(statusForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer examId = Integer.parseInt(statusForm.get("examId"));
            Exam exam = Exam.byId(examId);
            if(exam==null){
                throw new CMException("Exam does not exist");
            }
            Report report = exam.getReport();
            if(report==null){
                throw new CMException("Status error");
            }

            Integer examStatus = report.getExamStatus();
            result.put("examStatus",examStatus);

            result.put("error",0);
        }catch(CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }

    public static Result saveAnswer(){
        DynamicForm questionForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(questionForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            String answer = questionForm.get("answer");
            Integer questionId = Integer.parseInt(questionForm.get("questionId"));
            Integer examId = Integer.parseInt(questionForm.get("examId"));

            Exam exam = Exam.byId(examId);
            if(exam==null){
                throw new CMException("Exam does not exist.");
            }
            Report report = exam.getReport();
            if(report==null){
                throw new CMException("Sorry, you haven't signed in.");
            }
            if(report.getExamStatus()!=Global.VERIFIED){
                throw new CMException("Sorry, you identification is not verified.");
            }

            Question question = Question.byId(questionId);
            Student student = Student.byId(studentId);
            if(student==null){
                throw new CMException("Unauthorized access.Please login.");
            }
            if(question==null){
                throw new CMException("Question does not exist.");
            }

            Solution solution = Solution.byStudentQuestion(student,question);
            if(solution==null){
                solution = new Solution();
                solution.setQuestion(question);
                solution.setStudent(student);
            }
            solution.setAnswer(answer);
            solution.save("cw");


            result.put("error",0);
        }catch(CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }

    public static Result storeImage(){
        DynamicForm imageForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(imageForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            String imageString = imageForm.get("image");
            Integer examId = Integer.parseInt(imageForm.get("examId"));
            Exam exam = Exam.byId(examId);
            if(exam==null){
                throw new CMException("Exam dose not exist.");
            }
            Course course = exam.getCourse();
            Student student = Student.byId(studentId);
            if(course==null || student==null){
                throw new CMException("Course or student dose not exist.");
            }

            //get the image ready
            BufferedImage bufferedImage = ImageDecoder.decodeToImage(imageString);

            //image path
            String courseCode = course.getCourseCode();
            String matricNo = student.getMatricNo();
            File videoDir = new File("public/videos");
            if(!videoDir.exists() || !videoDir.isDirectory()){
                videoDir.mkdir();
            }
            String upDirName = "public/videos/"+courseCode;
            String downDirName = upDirName+"/"+matricNo;
            File upDir = new File(upDirName);
            if(!upDir.exists() || !upDir.isDirectory()){
                upDir.mkdir();
            }
            File downDir = new File(downDirName);
            if(!downDir.exists() || !downDir.isDirectory()){
                downDir.mkdir();
            }

            Report report = exam.getReport();
            if(report==null){
                report = new Report();
                report.save();
                exam.setReport(report);
                exam.save();
            }
            Image image = new Image();
            image.save();

            String picturePath = downDirName + "/" + image.getImageId() + ".jpg";
            File picture = new File(picturePath);
            ImageIO.write(bufferedImage, "jpg", picture);

            image.setPicturePath(picturePath);
            image.setReport(report);
            image.setTime(new Date());
            image.save();

            result.put("error", 0);
        }catch(CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }catch(IOException e){
            result.put("error","Video process error");
        }
        return ok(result);
    }

    public static Result pollMessage(){
        DynamicForm pollForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(pollForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer examId = Integer.parseInt(pollForm.get("examId"));
            Integer lastChatId = Integer.parseInt(pollForm.get("lastChatId"));
            System.out.println(examId+"----"+lastChatId);
            Exam exam = Exam.byId(examId);
            if(exam==null){
                throw new CMException("Exam does not exist");
            }
            Report report = exam.getReport();
            if(report==null){
                throw new CMException("Message error");
            }

            List<Chat> chatList = report.getChatList();
            List<Chat> newChatList = new ArrayList<Chat>();
            Integer newChatId = lastChatId;
            for(Chat chat: chatList){
                if(chat.getChatId()>lastChatId){
                    newChatList.add(chat);
                    if(chat.getChatId()>newChatId){
                        newChatId = chat.getChatId();
                    }
                }
            }
            result.put("chatList",Json.toJson(newChatList));
            result.put("lastChatId",newChatId);

            result.put("error",0);
        }catch(CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }

    public static Result sendMessage(){
        DynamicForm messageForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

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
                throw new CMException("Message error");
            }

            String message = messageForm.get("message");
            Chat chat = new Chat();
            chat.setMessage(message);
            chat.setReport(report);
            chat.setTime(new Date());
            chat.setFromStudent(true);
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

    public static Result getPhoto(Integer studentId){
        Student student = Student.byId(studentId);
        try{
            return ok(new FileInputStream(student.getPhoto()));
        }catch(Exception e){
            return ok("Error: photo not found");
        }
    }

    public static Result getImage(Integer imageId){
        Image image = Image.byId(imageId);
        try{
            return ok(new FileInputStream(image.getPicture()));
        }catch(Exception e){
            return ok("Error: photo not found");
        }
    }
}
