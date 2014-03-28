package utils;

public class Authentication {
    public static Integer authorize(Integer domain) throws CMException,NumberFormatException{
        String CMUserString = play.mvc.Controller.session().get("cmuser");
        String domainString = play.mvc.Controller.session().get("domain");
        if(CMUserString==null || domainString==null || Integer.parseInt(domainString)!=domain){
            throw new CMException("Access denied. Please login.");
        }
        return Integer.parseInt(CMUserString);
    }
}
