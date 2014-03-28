import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;

public class Grabber{
    private FrameGrabber grabber;
    public Grabber(){
        grabber = null;
    }
    public boolean start(){
        System.out.println("starting...");
        boolean success = false;
        try{
            grabber = FrameGrabber.createDefault(0);
            grabber.start();
            success = true;
        }catch(FrameGrabber.Exception e){
            System.out.println(e.getMessage());
        }
        return success;
    }
    public String grab(){
        String imageString = "";
        try{
            opencv_core.IplImage img = grabber.grab();
            if(img != null){
                BufferedImage buf = img.getBufferedImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(buf, "jpg", baos);
                baos.flush();
                byte[] imageBytes = baos.toByteArray();
                baos.close();
                imageString = Base64.encodeBase64String(imageBytes);
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }catch(FrameGrabber.Exception e){
            System.out.println(e.getMessage());
        }
        return imageString;
    }
    public void stop(){
        try{
            grabber.stop();
            grabber.release();
            grabber = null;
        }catch(FrameGrabber.Exception e){
            System.out.println(e.getMessage());
        }
    }
}

