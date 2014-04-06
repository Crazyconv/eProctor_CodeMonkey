package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import cw_models.*;
import models.ExamRecord;
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

/**
 * Contains all methods needed for student-side application before checking in for an exam.
 */
public class StudentController extends Controller {

    /**
     * Gets the exam session the current student has registered for a course and the availability of all other sessions. 
     * 
     * <p>Information expected from the form received:
     * <ul>
     *     <li>courseId: id of the course whose exam sessions are queried on</li>
     * </ul>
     * Information expected from current session(done by a utility method provided by Authentication class):
     * <ul>
     *     <li>studenId: id of the student who is querying on the sexam session</li>
     * </ul></p>
     * 
     * @return an HTML block(not a full page) wrapped in an ok reponse, which is rendered with 3 parameters:
     * <ul>
     *     <li>courseId: id of the course whose exam sessions have been queried on</li>
     *     <li>allocationMap: a mapping from {@link cw_models.ExamSession exam session} to its availability. which shows the availability of each exam session of a course</li>
     *     <li>slot: the time slot of the exam session that the current student is registered for; null if him/she haven't done so </li>
     * </ul>
     *
     * @see Authentication#authorize(Integer)
     * @see Course
     */
    public static Result showSlot(){
        DynamicForm slotForm = Form.form().bindFromRequest();

        try{
            // get studentId from current session
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(slotForm.hasErrors()){
                throw new CMException("Form submit error.");
            }
            // get courseId from form received
            Integer courseId = Integer.parseInt(slotForm.get("courseId"));

            // locate 
            Course course = Course.byId(courseId);
            Student student = Student.byId(studentId);
            if(course==null || student==null){
                throw new CMException("Necessary data missing.");
            }

            //the original examRecord slot that the student chose, may be null
            TimeSlot slot = null;
            ExamRecord examRecord = ExamRecord.byStudentCourse(student, course);
            if(examRecord !=null){
                slot = examRecord.getTimeSlot();
            }

            // get all allocations of a course, which the current student is registered for
            List<ExamSession> examSessionList = course.getExamSessionList();

            // for every allocation, get its availability and finally produce a map consisting of allocation-availability pairs
            Map<ExamSession,Integer> examSessionMap = new HashMap<ExamSession,Integer>();
            for(ExamSession examSession: examSessionList){
                examSessionMap.put(examSession, ExamRecord.occupied(examSession));
            }

            return ok(showSlot.render(courseId, examSessionMap, slot));
        }catch(CMException e){
            return ok(e.getMessage());
        }catch(NumberFormatException e){
            return ok("Invalid request");
        }
    }

    /**
     * Tries to register the current student for a requested exam session.
     *
     * There are 3 possible execution paths:
     * <p><ul>
     *     <li>If the exam session the current student requestes to book has no more vacancy,
     *         this methods returns immediately with an error message.</li>
     *     <li>If the current student hasn't booked an exam session for a course, a new exam
     *         record will be created and gets linked up with the requested exam session.</li>
     *     <li>If the current student is found to have booked an exam session for a course,
     *         the corresponding existing exam record will be changed to link with the requested
     *         exam session, and 'detach' from the previously-booked session</li>
     * </ul></p>
     * 
     * <p>Information expected from the form received:
     * <ul>
     *     <li>courseId: id of the course whose exam sessions are queried on</li>
     *     <li>allocationId: id of the exam session the current student requestes to register for</li>
     * </ul>
     * Information expected from current session, done by a utility method provided by Authentication class:
     * <ul>
     *     <li>studenId: id of the student who is requesting to register for an exam session</li>
     * </ul></p>
     * 
     * @return A JsonNode wrapped in an ok HTTP response, which has 4 fields.
     * <ul>
     *     <li>examId: id of the exam record that the current student has for the course indicated in the request form</li>
     *     <li>start: the start time of the exam session the current students requests to register for</li>
     *     <li>end: the end time of the exam session the current students requests to register for</li>
     *     <li>error: indicates whether the registration is successful, has similar semantics as enter() in Application</li>
     * </ul>
     *
     * @see Application#enter()
     */
    public static Result selectSlot(){
        ObjectNode result = Json.newObject();
        DynamicForm slotForm = Form.form().bindFromRequest();
        try{
            // only student can perform the selectSlot operation, prevent possible unauthorized access
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(slotForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer courseId = Integer.parseInt(slotForm.get("courseId"));
            Integer examSessionId = Integer.parseInt(slotForm.get("examSessionId"));
            Course course = Course.byId(courseId);
            Student student = Student.byId(studentId);
            ExamSession examSession = ExamSession.byId(examSessionId);
            if(student==null || course==null || examSession==null){
                throw new CMException("Necessary data missing.");
            }

            Integer capacity = examSession.getCapacity();
            Integer occupied = ExamRecord.occupied(examSession);

            ExamRecord examRecord = ExamRecord.byStudentCourse(student, course);
            //if examRecord is null, namely student has not selected a slot for this course
            //need to add a new examRecord record
            if(examRecord ==null){
                if(capacity<=occupied){
                    throw new CMException("The slot is full. Please select another one.");
                }
                examRecord = new ExamRecord();
                examRecord.setStudent(student);
                examRecord.setExamSession(examSession);
                examRecord.save();
            //if examRecord is not full, just update the examRecord record but not create a new one
            }else{
                // updating only occurs when the slot selected is different from the original one
                if(!examRecord.getExamSession().equals(examSession)){
                    if(capacity<=occupied){
                        throw new CMException("The slot is full. Please select another one.");
                    }
                    examRecord.setExamSession(examSession);
                    examRecord.save();
                }
            }
            result.put("error",0);
            //info used in javascript to update the html
            result.put("examId", examRecord.getExamRecordId());
            result.put("start", examSession.getTimeSlot().getStartTime().getTime());
            result.put("end",examSession.getTimeSlot().getEndTime().getTime());
        }catch (CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }

    /**
     * It unbooks a registered exam session for the current student.
     *
     * <p>Information expected from the form received:
     * <ul>
     *     <li>courseId: id of the course whose exam session the current student requests to unbook</li>
     * </ul>
     * Information expected from session history(extracted by a utility method provided in Authentication class):
     * <ul>
     *     <li>studentId: id of the current student.</li>
     * </ul></p>
     * 
     * @return A JsonNode wrapped in a ok HTTP response carring an error field to indicate 
     *         whether the session is sucessfully unbooked, whose semantic is similar to 
     *         that of enter() in Application.
     *
     * @see Application#enter()
     */
    public static Result deleteSlot(){
        ObjectNode result = Json.newObject();
        DynamicForm slotForm = Form.form().bindFromRequest();
        try{
            //access control
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(slotForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            // locate the examRecord record with information extracted from the form received and session history
            Integer courseId = Integer.parseInt(slotForm.get("courseId"));
            Student student = Student.byId(studentId);
            Course course = Course.byId(courseId);
            if(student==null || course==null){
                throw new CMException("Necessary data missing.");
            }
            ExamRecord examRecord = ExamRecord.byStudentCourse(student, course);
            if(examRecord ==null){
                throw new CMException("No such record.");
            }

            //delete the examRecord record
            examRecord.delete();
            result.put("error",0);
        }catch (CMException e){
            result.put("error",e.getMessage());
        }catch(NumberFormatException e){
            result.put("error","Invalid request");
        }
        return ok(result);
    }


    /**
     * Signs in the current student for an exam record and displays a list of questions.
     *
     * <p>Signing in will fail if the exam session hasn't started yet. Signing in a student
     * is done by setting the flag in the {@link Report} of the {@link models.ExamRecord exam record}
     * that the student requests to sign in for.</p>
     *
     *  @return a takeExam HTML block(not a full page) wrapped in an ok response and rendered with 2 parameters:
     *  <ul>
     *      <li>exam: an {@link models.ExamRecord} object indicating the exam record during which those retrieved questions should be presented. </li>
     *      <li>questionList: A list of questions that will be presented to the current student.</li>
     *  </ul>
     */
    public static Result takeExam(){
        DynamicForm examForm = Form.form().bindFromRequest();

        try{
            Integer studentId = Authentication.authorize(Global.STUDENT);

            if(examForm.hasErrors()){
                throw new CMException("Form submission error.");
            }

            // locate the on-going examRecord record
            Integer examRecordId = Integer.parseInt(examForm.get("examRecordId"));
            ExamRecord examRecord = ExamRecord.byId(examRecordId);
            if(examRecord ==null){
                throw new CMException("ExamRecord does not exist.");
            }

//            Date now = new Date();
//            Date startTime = examRecord.getTimeSlot().getStartTime();
//            if(startTime.before(now)){
//                throw new CMException("Sorry,the examRecord has already started");
//            }

            // create an examRecord report for the current student if haven't done so
            // sign in the current student for an examRecord record (by setting the flag in the associated examRecord report)
            Report report = examRecord.getReport();
            if(report==null){
                report = new Report();
                report.setExamStatus(Global.SIGNEDIN);
                report.save();
                examRecord.setReport(report);
                examRecord.save();
            }else{
                report.setExamStatus(Global.SIGNEDIN);
                report.save();
            }

            //randomly pick k questions in the question pool of a course (k is defined by the course) 
            Course course = examRecord.getCourse();
            List<Question> questionSet = course.getQuestionSet();
            List<Question> questionList = new ArrayList<Question>();
            Integer questionNo = course.getQuestionNo();
            Iterator<Integer> randomNo = NumberIterator.generate(questionNo, questionSet.size());
            while(randomNo.hasNext()){
                questionList.add(questionSet.get(randomNo.next()));
            }

            return ok(takeExam.render(examRecord, questionList));
        }catch(CMException e){
            return ok(e.getMessage());
        }catch(NumberFormatException e){
            return ok("Invalid request");
        }
    }

}
