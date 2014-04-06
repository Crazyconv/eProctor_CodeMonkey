package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import cw_models.Course;
import cw_models.Question;
import cw_models.Answer;
import cw_models.Student;
import models.Chat;
import models.ExamRecord;
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

/**
 * This class contains a set of static methods that will handle all kinds of operations perfermed by a student in an on-going exam.
 */
public class ExamController extends Controller{

    /**
     * Checks whether the current student has checked in for an {@link models.ExamRecord exam record}.
     *
     * <p>Infomration expected from the form received:
     * <ul>
     *     <li>examId: id of the exam record, whose status is being queried on.</li>
     * </ul></p>
     * 
     * @return A JsonNode wrapped in an ok HTTP response which has 2 fields:
     * <ul>
     *     <li>error: its semantic is similar to that of enter() in Application</li>
     * </ul>
     *
     * @see Application#enter()     
     */
    public static Result checkExamStatus(){
        DynamicForm statusForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(statusForm.hasErrors()){
                throw new CMException("Form submit error.");
            }
            Integer examRecordId = Integer.parseInt(statusForm.get("examRecordId"));
            ExamRecord examRecord = ExamRecord.byId(examRecordId);
            if(examRecord ==null){
                throw new CMException("ExamRecord does not exist");
            }
            Report report = examRecord.getReport();
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

    /**
     * Validates and stores the anwser submitted as an {@link Answer object} in database.
     *
     * Expects 3 pieces of information from the caller
     * <ul>
     *     <li>the content of the answer submitted by student</li>
     *     <li>the {@link Question question} to which this answer corresponds</li>
     *     <li>the {@link models.ExamRecord exam record} during which this answer is submitted</li>
     * </ul>
     * An answer is uniquely identified by the question and the student, 
     * which are derived from the information above. If such an answer is
     * found alredy existent in the database, old answer will be overwritten
     * 
     * @see Application#enter()
     * @return A JsonNode wrapped in an ok HTTP response carring an error field to indicate whether the answer is sucessfully stored in server, whose semantic is similar to that of enter() in Application.
     */
    public static Result saveAnswer(){
        DynamicForm questionForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);
            System.out.println("======"+studentId+"=======");
            if(questionForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            // retrive info from form received
            String answer = questionForm.get("answer");
            Integer questionId = Integer.parseInt(questionForm.get("questionId"));
            System.out.println("======"+questionId+"=======");
            Integer examRecordId = Integer.parseInt(questionForm.get("examRecordId"));
            System.out.println("======"+examRecordId+"=======");

            // use info retrieved to access models
            ExamRecord examRecord = ExamRecord.byId(examRecordId);
            if(examRecord ==null){
                throw new CMException("ExamRecord does not exist.");
            }
            Report report = examRecord.getReport();
            if(report==null){
                throw new CMException("Sorry, you haven't signed in.");
            }
            if(report.getExamStatus()!=Global.VERIFIED){
                throw new CMException("Sorry, your identification is not verified.");
            }
            Question question = Question.byId(questionId);
            Student student = Student.byId(studentId);
            if(student==null){
                throw new CMException("Unauthorized access.Please login.");
            }
            if(question==null){
                throw new CMException("Question does not exist.");
            }

            // use model info to uniquely identify an Answer(aka. answer) object in database, if can not identify, construct a new one
            Answer solution = Answer.byStudentQuestion(student, question);
            if(solution==null){
                solution = new Answer();
                solution.setQuestion(question);
                solution.setStudent(student);
            }

            // store the answer to the located Answer object
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

    /**
     * Processes and stores the image-string from the form received.
     *
     * When the server receives an image string POSTed from the web, this controller is activated. It checks whether the POSTer
     * is a student. If yes, it re-assembles the string into an image and store it on students's local file system, at the same time,
     * creates in database an object of {@link Image Image model}(which is basically a pointer) to that file, and link this pointer
     * with the {@link Report Report model} of the student sending out this image.
     *
     * @return A JsonNode wrapped in a ok HTTP response carring an error field to indicate whether the image is succesfully stored in server, whose semantic is similar to that of enter() in Application.
     *
     * @see  Student
     * @see  models.ExamRecord
     * @see  Authentication#authorize(Integer)
     * @see  Application#enter()
     */
    public static Result storeImage(){
        DynamicForm imageForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            // form error checking
            if(imageForm.hasErrors()){
                throw new CMException("Form submit error.");
            }
            // extract image as a string from form received
            String imageString = imageForm.get("image");

            // extract examRecord and course info from form received
            Integer examRecordId = Integer.parseInt(imageForm.get("examRecordId"));
            ExamRecord examRecord = ExamRecord.byId(examRecordId);
            if(examRecord ==null){
                throw new CMException("ExamRecord dose not exist.");
            }
            Course course = examRecord.getCourse();
            Student student = Student.byId(studentId);
            if(course==null || student==null){
                throw new CMException("Course or student dose not exist.");
            }

            // transform the image string that is extracted and put it into a image buffer
            BufferedImage bufferedImage = ImageDecoder.decodeToImage(imageString);

            // locate model objects by info extracted just now
            String courseCode = course.getCourseCode();
            String matricNo = student.getMatricNo();

            // make a directory to contain image files(for video emulation) of all students, if not created yet
            File videoDir = new File("public/videos");
            if(!videoDir.exists() || !videoDir.isDirectory()){
                videoDir.mkdir();
            }

            // make a directory to contain all the image files(for video emulation) specific to an examRecord record
            // note:
            //      examRec-specific files are stored in public/videos/course/matric, and
            //      course + matric can uniquely identify an examRecord record, since it's on student's computer, simply a course is enought alr?
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
            // if there is no report associted with current examRecord record, create one and link them up
            Report report = examRecord.getReport();
            if(report==null){
                report = new Report();
                report.save();
                examRecord.setReport(report);
                examRecord.save();
            }

            // create a new image model object in database
            Image image = new Image();
            image.save();

            // write the image file in buffer to disk
            String picturePath = downDirName + "/" + image.getImageId() + ".jpg";
            File picture = new File(picturePath);
            ImageIO.write(bufferedImage, "jpg", picture);

            // set the image model object just created to point to that image file on disk.
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

    /**
     * Gets from database all unread messages of an {@link models.ExamRecord exam record}.
     *
     * Information expected from the form received:
     * <ul>
     *     <li>examId: id of the exam records whose chats are to be fetched</li>
     *     <li>lastChatId: id of the last chat that has been read by student, used to identify unread messages</li>
     * </ul>
     * 
     * @see  Application#enter()
     * @return A JsonNode wrapped in an ok HTTP response, it has 3 fields:
     * <ul>
     *     <li>chatList: a list of unread chat messages in Json format</li>
     *     <li>lastChatId: he latest chat among the chatList</li>
     *     <li>error: similar semantics as enter() in Application</li>
     * </ul>
     */
    public static Result pollMessage(){
        DynamicForm pollForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(pollForm.hasErrors()){
                throw new CMException("Form submission error.");
            }

            // extract info from form received
            Integer examRecordId = Integer.parseInt(pollForm.get("examRecordId"));
            Integer lastChatId = Integer.parseInt(pollForm.get("lastChatId"));

            // use info extracted to locate the Report model in database, who contains all the chats
            ExamRecord examRecord = ExamRecord.byId(examRecordId);
            if(examRecord ==null){
                throw new CMException("ExamRecord does not exist");
            }
            Report report = examRecord.getReport();
            if(report==null){
                throw new CMException("Message error");
            }

            // get all unread chats from the Report model located and the id of the latest chat and put them in the JsonNode
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

    /**
     * Puts into database a message sent by student.
     * 
     * <p>Information expected from the form received:
     * <ul>
     *     <li>examId: The {@link models.ExamRecord exam record} during which the message is sent.</li>
     *     <li>message: The content of the message</li>
     * </ul></p>
     * 
     * @return A JsonNode wrapped in a ok HTTP response carring an error field, whose semantic is similar to that of enter() in Application.
     *
     * @see Report
     * @see Chat
     * @see Application#enter()
     */
    public static Result sendMessage(){
        DynamicForm messageForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(messageForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            // extract examId from form received and locate the Report it concerns
            Integer examRecordId = Integer.parseInt(messageForm.get("examRecordId"));
            ExamRecord examRecord = ExamRecord.byId(examRecordId);
            if(examRecord ==null){
                throw new CMException("ExamRecord does not exist");
            }
            Report report = examRecord.getReport();
            if(report==null){
                throw new CMException("Message error");
            }

            // extract the message from form received
            String message = messageForm.get("message");
            // initialise a Chat in database to hold the message and link with the Report it concerns
            Chat chat = new Chat();
            chat.setMessage(message);
            chat.setReport(report);
            chat.setTime(new Date());
            chat.setFromStudent(true);
            chat.save();

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
