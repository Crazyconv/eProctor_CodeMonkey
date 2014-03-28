package controllers;

import cw_models.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import utils.*;
import views.html.*;
import play.mvc.Controller;
import play.mvc.Result;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class StudentController extends Controller {

    public static Result showSlot(){
        DynamicForm slotForm = Form.form().bindFromRequest();

        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(slotForm.hasErrors()){
                throw new CMException("Form submit error.");
            }
            Integer courseId = Integer.parseInt(slotForm.get("courseId"));
            Course course = Course.byId(courseId);
            Student student = Student.byId(studentId);
            if(course==null || student==null){
                throw new CMException("Necessary data missing.");
            }
            //the original exam slot that the student chose, may be null
            Exam exam = Exam.byStudentCourse(student,course);
            List<TimeSlot> availableSlots = course.getAvailableSlots();
            //slotMap is used to know the number of students selecting this slot
            //which will be displayed
            Map<TimeSlot,Integer> slotMap = new HashMap<TimeSlot,Integer>();
            for(TimeSlot slot: availableSlots){
                slotMap.put(slot,Exam.occupied(course,slot));
            }
            return ok(showSlot.render(courseId,slotMap,exam));
        }catch(CMException e){
            return ok(e.getMessage());
        }catch(NumberFormatException e){
            return ok("Invalid request");
        }
    }

    public static Result selectSlot(){
        ObjectNode result = Json.newObject();
        DynamicForm slotForm = Form.form().bindFromRequest();
        try{
            //only student can perform the selectSlot operation, prevent possible unauthorized access
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(slotForm.hasErrors()){
                throw new CMException("Form submit error.");
            }
            Integer courseId = Integer.parseInt(slotForm.get("courseId"));
            Integer slotId = Integer.parseInt(slotForm.get("slotId"));
            Student student = Student.byId(studentId);
            Course course = Course.byId(courseId);
            TimeSlot slot = TimeSlot.byId(slotId);
            if(student==null || course==null || slot==null){
                throw new CMException("Necessary data missing.");
            }

            Integer capacity = slot.getCapacity();
            Integer occupied = Exam.occupied(course,slot);

            Exam exam = Exam.byStudentCourse(student, course);
            //if exam is null, namely student has not selected a slot for this course
            //need to add a new exam record
            if(exam==null){
                if(capacity<=occupied){
                    throw new CMException("The slot is full. Please select another one.");
                }
                exam = new Exam();
                exam.setStudent(student);
                exam.setCourse(course);
                exam.setSlot(slot);
                exam.save();
            }else{
                //if exam is not full, just update the exam record but not create a new one
                //but the updating only occurs when the slot selected is different from the original one
                if(!exam.getStartTime().equals(slot.getStartTime())){
                    if(capacity<=occupied){
                        throw new CMException("The slot is full. Please select another one.");
                    }
                    exam.setSlot(slot);
                    exam.save();
                }
            }
            result.put("error",0);
            //info used in javascript to update the html
            result.put("examId",exam.getExamId());
            result.put("start",slot.getStartTime().getTime());
            result.put("end",slot.getEndTime().getTime());
        }catch (CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }

    public static Result deleteSlot(){
        ObjectNode result = Json.newObject();
        DynamicForm slotForm = Form.form().bindFromRequest();
        try{
            //access control
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(slotForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer courseId = Integer.parseInt(slotForm.get("courseId"));
            Student student = Student.byId(studentId);
            Course course = Course.byId(courseId);
            if(student==null || course==null){
                throw new CMException("Necessary data missing.");
            }
            Exam exam = Exam.byStudentCourse(student,course);
            if(exam==null){
                throw new CMException("No such record.");
            }
            //delete the exam slot
            exam.delete();
            result.put("error",0);
        }catch (CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }

    public static Result takeExam(){
        DynamicForm examForm = Form.form().bindFromRequest();

        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(examForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer examId = Integer.parseInt(examForm.get("examId"));
            Exam exam = Exam.byId(examId);
            if(exam==null){
                throw new CMException("Exam does not exist.");
            }

            Date now = new Date();
            Date startTime = exam.getStartTime();
            if(startTime.before(now)){
                throw new CMException("Sorry,the exam has already started");
            }

            Course course = exam.getCourse();
            List<Question> questionSet = course.getQuestionSet();
            List<Question> questionList = new ArrayList<Question>();
            Integer questionNo = course.getQuestionNo();
            Iterator<Integer> randomNo = NumberIterator.generate(questionNo,questionSet.size());
            while(randomNo.hasNext()){
                questionList.add(questionSet.get(randomNo.next()));
            }

            return ok(takeExam.render(exam,questionList));
        }catch(CMException e){
            return ok(e.getMessage());
        }catch(NumberFormatException e){
            return ok("Invalid request");
        }
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
}
