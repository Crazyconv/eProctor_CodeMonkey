package controllers.data;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.CMException;
import utils.StringGenerator;
import views.html.data.*;
import models.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DataController extends Controller {
    public static Result addData1(){
        List<Student> studentList = Student.find.all();
        List<Course> courseList = Course.find.all();
        return ok(stucourse.render(studentList,courseList));
    }

    public static Result addStudent(){
        //return an Json Object to the client containing error message or data to display
        ObjectNode result = Json.newObject();
        DynamicForm studentForm = Form.form().bindFromRequest();
        Http.MultipartFormData studentData = request().body().asMultipartFormData();
        try{
            //check whether the form has been uploaded correctly
            if(studentData==null || studentForm.hasErrors()){
                throw new CMException("Form submit error.");
            }
            Http.MultipartFormData.FilePart picture = studentData.getFile("photo");
            if(picture==null){
                throw new CMException("File upload error.");
            }

            //check whether matricNo matches certain format and has not been added (in setMatricNo method)
            //by now, we just set password equal to matricNo
            Student student = new Student();
            String matricNo = studentForm.get("matricNo").toUpperCase();
            student.setMatricNo(matricNo);
            student.setPassword(matricNo);

            //check the uploaded file type and store it to public/uploads
            String fileType = picture.getContentType();
            if(!(fileType.equals("image/jpeg") || fileType.equals("image/png"))){
                throw new CMException("Photo type should be jpeg/png");
            }
            String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()).toString();
            fileName += StringGenerator.generate(10);
            fileName += "." + fileType.replace("image/","");

            File dictionary = new File("public/uploads");
            if(!dictionary.exists() || !dictionary.isDirectory()){
                dictionary.mkdir();
            }

            File photo = new File("public/uploads/"+fileName);
            picture.getFile().renameTo(photo);

            student.setPhoto(photo);
            student.setPhotoPath(photo.getPath());
            student.save();

            result.put("error",0);
            result.put("matricNo",matricNo);
            result.put("studentId",student.getStudentId());
        }catch (CMException e){
            result.put("error", e.getMessage());
        }catch (Exception e){
            result.put("error",e.getMessage());
        }
        return ok(result);
    }

    public static Result getPhoto(Integer studentId){
        Student student = Student.find.byId(studentId);
        try{
            return ok(new FileInputStream(student.getPhoto()));
        }catch(FileNotFoundException e){
            return ok("Error: photo not found");
        }
    }

//    public static Result addCourse(){
//        ObjectNode result = Json.newObject();
//        DynamicForm courseForm = Form.form().bindFromRequest();
//        try{
//            if(courseForm.hasErrors()){
//                throw new CMException("Form submit error.");
//            }
//            String courseCode = courseForm.get("courseCode");
//
//
//        }catch (CMException e){
//            result.put("error", e.getMessage());
//        }
//    }
}