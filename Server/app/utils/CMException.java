package utils;

/**
 * A generic exception with customisable error message.
 */
public class CMException extends Exception{
    public CMException(String message){
        super(message);
    }
}
