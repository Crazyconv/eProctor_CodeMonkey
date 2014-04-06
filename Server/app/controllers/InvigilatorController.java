package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
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
import views.html.invigilator.invigilateExam;

import java.util.*;


public class InvigilatorController extends Controller {
    /**
     * Signs in the current invigilator for exams specified in the form received and bring him to a new page.
     *
     * Information expected from the form received.
     *<ul>
     *      <li>examIds: a list of examIds, whose corresponding {@link models.ExamRecord exam records} the invigilator requestes to sign in for</li>
     *</ul>
     * 
     * @return An on-going invigilation page rendered with a list of ExamRecord as parameter.
     */
    public static Result signIn(){
        Map<String, String[]> signInMap = request().body().asFormUrlEncoded();
        try{
            Integer InvigilatorId = Authentication.authorize(Global.INVIGILATOR);

            String[] examRecordIds = signInMap.get("examRecordIds");
            List<ExamRecord> examRecordList = new ArrayList<ExamRecord>();
            ExamRecord examRecord;
            // add all exams addressable by examIds into a new list and 
            for(String examRecordId: examRecordIds){
                examRecord = ExamRecord.byId(Integer.parseInt(examRecordId));
                if(examRecord ==null){
                    throw new CMException("ExamRecord does not exist.");
                }
                examRecordList.add(examRecord);
            }
            // use this list to render the page returned
            return ok(invigilateExam.render(examRecordList));
        }catch(CMException e){
            return ok(e.getMessage());
        }catch(NumberFormatException e){
            return ok("Invalid request");
        }
    }

    /**
     * Gets the latest unseen image and all unread images for all {@link models.ExamRecord exam record} currently being invigilated.
     *
     * <p>Executing this method once will give: 
     * <ul>
     *     <li> 1 image for 1 student currently being invigilated by the current invigilator. Each image 
     *          will be the latest one at the time of execution for the corresponding student. This means
     *          not all unseen images will get to be presented to invigilator. For example, if they are 2
     *          unseen images of a student, only the latest one will be fetched by this method, and the
     *          other one will simply be skipped</li>
     *     <li> 1 set of chats for 1 student currently being invigilated by the current invigilator. Each set
     *          of chats will contain all chats that are sent by the corresponding student but haven't been read
     *          by the current invigilator</li>
     *     
     * </ul></p>
     * 
     * <p>Expected information from form received:
     * <ul>
     *     <li>lastChatId: the id of the latest chats among </li>
     *     <li>lastImageId</li>
     *     <li><span id='examIds'>examIds</span>: A list of exams organized in a string and separated by comma.</li>
     * </ul></p>
     * 
     * @return A JsonNode wrapped in an ok HTTP reponse, and there are at least 3 fields in this JsonNode (* means there are 0 to many number of that field):
     * <ul>
     *     <li>{&lt;examId&gt;}*: a JsonNode with 2 fields (every entry in <a href='#examIds'>examIds</a> will result in one such field with its own name)<ul>
     *         <li>chatList: a list of all <b>unread</b> chat messages in Json format, in ascending order of id</li>
     *         <li>imageId: id of the latest image (whether it is unread is not guaranteed)</li>
     *     </ul></li>
     *     <li>newChatId: id of the latest message among all exam records that are being invigilated by current invigilator
     *         (whether it is unread is not guaranteed)</li>
     *     <li>newImageId id of the latest image among all exam records that are being invigilated by current invigilator 
     *         (whether it is unread is not guaranteed)</li>
     *     <li>error: similar semantics as enter() in Application</li>
     * </ul>
     *
     * @see  Application#enter()
     */
    public static Result pollMessageImage(){
        DynamicForm pollForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer invigilatorId = Authentication.authorize(Global.INVIGILATOR);

            if(pollForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            // extract id of the latest read chat and image from form received
            Integer lastChatId = Integer.parseInt(pollForm.get("lastChatId"));
            Integer lastImageId = Integer.parseInt(pollForm.get("lastImageId"));

            // extract and put into a List, IDs of all exam records that are being invigilated by an invigilator
            String examRecordIdString = pollForm.get("examRecordIds");
            String[] examRecordIdArray = examRecordIdString.split(",");
            List<Integer> examRecordIds = new ArrayList<Integer>();
            for(String ei : examRecordIdArray){
                examRecordIds.add(Integer.parseInt(ei));
                System.out.println("========="+ei+"========");
            }

            Integer newChatId = lastChatId;
            Integer newImageId = lastImageId;

            // iterate through all exam records that are being invigilated by the current invigilator
            //      1. get the id of the lastest chat(with the largest id) and the id of the latest image
            //      2. create and populate <examId> field of the returned JsonNode for each exam records
            for(Integer examRecordId : examRecordIds){
                ObjectNode update = checkUpDate(examRecordId, lastChatId, lastImageId);
                if(update!=null){
                    if(update.get("newChatId").intValue()>newChatId){
                        newChatId = update.get("newChatId").intValue();
                    }
                    if(update.get("newImageId").intValue()>newImageId){
                        newImageId = update.get("newImageId").intValue();
                    }
                    result.put(examRecordId.toString(),update.get("current"));
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

    /**
     * Gets the latest unseen image and all unread images for a {@link models.ExamRecord exam record}.
     * 
     * @param  examRecordId                the id of exam record whose chat and image will be queried on.
     * @param  lastChatId            the id of the latest read chat message(a reference point). all chats newer than this are unread
     * @param  lastImageId           the id of the latest read image(a reference point). All images newer than this are unread
     * 
     * @return A JsonNode with 3 fields
     * <ul>
     *     <li>current: reference to another JsonNode with 2 fields</li><ul>
     *         <li>chatList: a list of all <b>unread</b> chat messages in Json format, in ascending order of id</li>
     *         <li>imageId: id of the latest image(whether it is unread is not guaranteed)</li>
     *     </ul>
     *     <li>newChatId: id of the latest message(whether it is unread is not guaranteed)</li>
     *     <li>newImageId id of the latest image(whether it is unread is not guaranteed)</li>
     * </ul>
     * 
     * @throws CMException           [description]
     * @throws NumberFormatException [description]
     */
    public static ObjectNode checkUpDate(Integer examRecordId, Integer lastChatId, Integer lastImageId)
            throws CMException,NumberFormatException{
        ObjectNode current = Json.newObject();
        ObjectNode update = Json.newObject();

        ExamRecord examRecord = ExamRecord.byId(examRecordId);
        if(examRecord ==null){
            throw new CMException("ExamRecord does not exist");
        }
        Report report = examRecord.getReport();

        if(report!=null){
            // get all chats and images from database, both are in descendent order in Report, aka the newest is at index 0
            List<Chat> chatList = report.getChatList();
            List<Image> imageList = report.getImageList();
            Integer newChatId = lastChatId;
            Integer newImageId = lastImageId;
            
            // put all unread chats into a temporary list (unread => id is greater than the latest read chat id)
            List<Chat> newChatList = new ArrayList<Chat>();
            for(Chat chat: chatList){
                if(chat.getChatId()>lastChatId){
                    newChatList.add(chat);
                }else{
                    break;
                }
            }

            // get the id of latest unread image
            if(imageList.size()>0){
                if(imageList.get(0).getImageId()>newImageId){
                    newImageId = imageList.get(0).getImageId();
                }
            }

            if(newChatList.size()>0 || newImageId!=lastImageId){
                // put value under "current" field in returned node 
                if(newChatList.size()>0){
                    // get the chatId with the biggest id in the list of all unread chats, chat at index 0 has the largest id by the definition of database
                    newChatId = newChatList.get(0).getChatId();
                    // reverse the order of chats, so the chat with smallest id(oldest unread chat) is at index 0
                    Collections.reverse(newChatList);
                    current.put("chatList",Json.toJson(newChatList));
                }
                // put value under "current" field in returned node 
                if(newImageId!=lastImageId){
                    current.put("imageId",newImageId);
                }

                // put value into the 3 top-level fields in returned node
                update.put("current",current);
                update.put("newChatId",newChatId);
                update.put("newImageId",newImageId);
                return update;
            //if there is not any new messages or image, return null
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    /**
     * Stores in database the message a invigilator wants to send to a student.
     *
     * Information(fields) expected from the form submitted by Ajax
     * <ul>
     *     <li>examRecordId: {@link models.ExamRecord#examRecordId ID of the exam record} that this message belongs to</li>
     *     <li>message: the chat message that the invigilator wants to send to a student</li>
     * </ul>
     *
     * @see Chat
     * @see Report  
     * @see Application#enter()
     * @return A JsonNode wrapped in a ok HTTP response carring an error field, whose semantic is similar to that of enter() in Application
     */
    public static Result sendMessage(){
        DynamicForm messageForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer InvigilatorId = Authentication.authorize(Global.INVIGILATOR);

            if(messageForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            // extract examRecord info from form received, locate the corresponding examRecord report
            Integer examRecordId = Integer.parseInt(messageForm.get("examRecordId"));
            ExamRecord examRecord = ExamRecord.byId(examRecordId);
            if(examRecord ==null){
                throw new CMException("ExamRecord does not exist");
            }
            Report report = examRecord.getReport();
            if(report==null){
                throw new CMException("Student has not signed in.");
            }

            // extract the message from form received, create a new Chat and link it up with the report
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

    /**
     * Set the status of exam record to be either verified or expelled.
     *
     * <p>It receives the judgement from invigilator on whether the person signed in for the exam (appearing on the camera)
     * matches with the information of the student account. <p/>
     * The judgment can be either 
     * <ul>
     *     <li>verified - the person present on camera is the student account holder</li>
     *     <li>expelled - the person present on camera isn't the student account holder</li>
     * </ul>
     * and based on it, the status of {@link models.ExamRecord exam record} (which is actually stored in the {@link Report} object
     * associated with the exam record) will be set to either verified or expelled
     * 
     * @see Application#enter()
     * @return A JsonNode wrapped in a ok HTTP response carring an error field, whose semantic is similar to that of enter() in Application
     */
    public static Result verifyStudent(){       // name is misleading, should better be called, set status of exam record
        DynamicForm verifyForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer InvigilatorId = Authentication.authorize(Global.INVIGILATOR);

            if(verifyForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            Integer examRecordId = Integer.parseInt(verifyForm.get("examRecordId"));
            Integer verified = Integer.parseInt(verifyForm.get("status"));
            ExamRecord examRecord = ExamRecord.byId(examRecordId);
            if(examRecord ==null){
                throw new CMException("ExamRecord does not exist");
            }
            Report report = examRecord.getReport();
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

    /**
     * Store into database the remark from form received.
     *
     * Information expected from form received.
     * <ul>
     *     <li>remark: the content of the remark</li>
     *     <li>examId: used to locate the {@link models.ExamRecord exam record} that the remark is supposed to be made on</li>
     * </ul>
     * Newer remark will overwrite the old one, if any.
     *
     * @see Report
     * @see Application#enter()
     * @return A JsonNode wrapped in a ok HTTP response carring an error field, whose semantic is similar to that of enter() in Application
     */
    public static Result submitRemark(){
        DynamicForm remarkForm = Form.form().bindFromRequest();
        ObjectNode result = Json.newObject();
        try{
            Integer InvigilatorId = Authentication.authorize(Global.INVIGILATOR);

            if(remarkForm.hasErrors()){
                throw new CMException("Form submit error.");
            }

            // locate the the object of Report model and some checkings
            Integer examRecordId = Integer.parseInt(remarkForm.get("examRecordId"));
            String remark = remarkForm.get("remark");
            ExamRecord examRecord = ExamRecord.byId(examRecordId);
            if(examRecord ==null){
                throw new CMException("ExamRecord does not exist");
            }
            Report report = examRecord.getReport();
            if(report==null){
                throw new CMException("Student has not signed in.");
            }

            // store the remark into the located Report model
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
