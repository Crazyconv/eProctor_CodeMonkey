package utils;

import java.util.Random;

public class StringGenerator {
    private static final String pattern = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();
    public static String generate(int length){
        String string = "";
        for(int i=0; i<length; i++){
            string += pattern.charAt(random.nextInt(pattern.length()));
        }
        return string;
    }
}
