package utils;

public class Authentication {

    /**
     * Tries to verify whether the current user is logged in under specified domain.
     *
     * This method is called before performing the operations that are only available to
     * a certain domain of users, during which the need arises that whether the current user
     * is under that domain.
     * There are 2 'domains' in this method. 
     * <ul>
     *     <li>One is acquired by querying the current session, which is the actual domain of the current user</li>
     *     <li>The other one is passed in as parameter, which is the domain required for the operations aforementioned</li>
     * </ul>
     * 
     * @param  domain                pls refer to method description
     * @return                       An integer value that represents the current user
     * @throws CMException           Thrown when the current user isn't under the domain specified by parameter
     * @throws NumberFormatException 
     */
    public static Integer authorize(Integer domain) throws CMException,NumberFormatException{
        String CMUserString = play.mvc.Controller.session().get("cmuser");
        String domainString = play.mvc.Controller.session().get("domain");
        if(CMUserString==null || domainString==null || Integer.parseInt(domainString)!=domain){
            throw new CMException("Access denied. Please login.");
        }
        return Integer.parseInt(CMUserString);
    }
}
