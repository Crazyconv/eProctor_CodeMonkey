package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import cw_models.*;
import models.Exam;
import models.Report;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Authentication;
import utils.CMException;
import utils.Global;
import utils.NumberIterator;
import views.html.student.showSlot;
import views.html.student.takeExam;

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
            TimeSlot slot = null;
            Exam exam = Exam.byStudentCourse(student, course);
            if(exam!=null){
                slot = exam.getTimeSlot();
            }

            List<Allocation> allocationList = course.getAllocationList();

            //allocationMap is used to know the number of students selecting this slot
            //which will be displayed
            Map<Allocation,Integer> allocationMap = new HashMap<Allocation,Integer>();
            for(Allocation allocation: allocationList){
                allocationMap.put(allocation,Exam.occupied(allocation));
            }
            return ok(showSlot.render(courseId, allocationMap, slot));
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
            Integer allocationId = Integer.parseInt(slotForm.get("allocationId"));
            Course course = Course.byId(courseId);
            Student student = Student.byId(studentId);
            Allocation allocation = Allocation.byId(allocationId);
            if(student==null || course==null || allocation==null){
                throw new CMException("Necessary data missing.");
            }

            Integer capacity = allocation.getCapacity();
            Integer occupied = Exam.occupied(allocation);

            Exam exam = Exam.byStudentCourse(student, course);
            //if exam is null, namely student has not selected a slot for this course
            //need to add a new exam record
            if(exam==null){
                if(capacity<=occupied){
                    throw new CMException("The slot is full. Please select another one.");
                }
                exam = new Exam();
                exam.setStudent(student);
                exam.setAllocation(allocation);
                exam.save();
            }else{
                //if exam is not full, just update the exam record but not create a new one
                //but the updating only occurs when the slot selected is different from the original one
                if(!exam.getAllocation().equals(allocation)){
                    if(capacity<=occupied){
                        throw new CMException("The slot is full. Please select another one.");
                    }
                    exam.setAllocation(allocation);
                    exam.save();
                }
            }
            result.put("error",0);
            //info used in javascript to update the html
            result.put("examId",exam.getExamId());
            result.put("start",allocation.getTimeSlot().getStartTime().getTime());
            result.put("end",allocation.getTimeSlot().getEndTime().getTime());
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

//            Date now = new Date();
//            Date startTime = exam.getTimeSlot().getStartTime();
//            if(startTime.before(now)){
//                throw new CMException("Sorry,the exam has already started");
//            }

            Report report = exam.getReport();
            if(report==null){
                report = new Report();
                report.setExamStatus(Global.SIGNEDIN);
                report.save();
                exam.setReport(report);
                exam.save();
            }else{
                report.setExamStatus(Global.SIGNEDIN);
                report.save();
            }
            Course course = exam.getCourse();
            List<Question> questionSet = course.getQuestionSet();
            List<Question> questionList = new ArrayList<Question>();
            Integer questionNo = course.getQuestionNo();
            Iterator<Integer> randomNo = NumberIterator.generate(questionNo, questionSet.size());
            while(randomNo.hasNext()){
                questionList.add(questionSet.get(randomNo.next()));
            }

            return ok(takeExam.render(exam, questionList));
        }catch(CMException e){
            return ok(e.getMessage());
        }catch(NumberFormatException e){
            return ok("Invalid request");
        }
    }

}
