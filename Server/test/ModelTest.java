import models.*;
import cw_models.*;
import org.junit.Assert;
import org.junit.Test;
import utils.CMException;

import static junit.framework.Assert.assertNotNull;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;


public class ModelTest {

    @Test
    public void testInvigilator(){
        Invigilator invig1 = new Invigilator();
        assertNotNull(invig1);
        try{
            invig1.setPassword("weinan612");
        }catch(CMException e){
            fail("Password should contain 5-16 characters.");
        }
        assertThat(invig1.getPassword()).isEqualTo("weinan612");
    }

    @Test
    public void testInvigilatorLower(){
        Invigilator invig2 = new Invigilator();
        try{
            invig2.setPassword("wwww");
            fail("Password should contain 5-15 characters.");
        }catch(CMException e){}
        
    }

    @Test
    public void testInvigilatorUpper(){
        Invigilator invig2 = new Invigilator();
        try{
            invig2.setPassword("wwwwwwwwwwwwwwwww");
            fail("Password should contain 5-16 characters.");
        }catch(CMException e){}
    }

    @Test
    public void student() {
        Student student1 = new Student();
        assertNotNull(student1);
    }

    @Test
    public void invigilator() {
        Invigilator invig = new Invigilator();
        Assert.assertNotNull(invig);
    }

    @Test
    public void exam() {
        ExamSession exam1 = new ExamSession();
        assertNotNull(exam1);
    }

    @Test
    public void course() {
        Course course1 = new Course();
        Assert.assertNotNull(course1);
    }

    @Test
    public void chat() {
        Chat chat = new Chat();
        Assert.assertNotNull(chat);
    }

    @Test
    public void question(){
        Question question = new Question();
        Assert.assertNotNull(question);
 }
}