package utils;

import org.apache.commons.codec.binary.Base64;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Transform imageString into BufferedImage.
 */
public class ImageDecoder {
    public static BufferedImage decodeToImage(String imageString) throws IOException{
        BufferedImage image = null;
        byte[] imageByte;
        imageByte = Base64.decodeBase64(imageString.getBytes());
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(bis);
        bis.close();
        return image;
    }
}
