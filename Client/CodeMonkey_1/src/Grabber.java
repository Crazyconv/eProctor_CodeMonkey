import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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
                BufferedImage resize = new BufferedImage(buf.getWidth()/2, buf.getHeight()/2, buf.getType());
                Graphics2D g = resize.createGraphics();
                AffineTransform at = AffineTransform.getScaleInstance(0.5,0.5);
                g.drawRenderedImage(buf, at);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(resize, "jpg", baos);
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

