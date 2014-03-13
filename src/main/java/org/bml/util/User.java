
package org.bml.util;

/**
 *This class encapsulates some simple methods that are commonly used when 
 * interacting with the system properties of a user. It is necessary because it 
 * is not clear in the java specification if the system properties associated
 * with a user IE: user.* can change at runtime. This class allows the 
 * application to chose if data should be cached or not.
 * @author Brian M. Lima
 */
public class User {

    public static boolean CACHE_USER_NAME=true;
    
    
    private static String USER_NAME_PROP_KEY="user.name";

    private static String USER_NAME=System.getProperty(USER_NAME_PROP_KEY);
    
    /**
     * <p>
     * Getter for the system property user.name. If {@link User#CACHE_USER_NAME}==true</code> the 
     * property is only read once and is cached. if {@link User#CACHE_USER_NAME}==false a call to 
     * System.getProperty("user.name") is made on every call.
     * </p>
     * @return the result of a call to System.getProperty("user.name");
     */
    public static String getSystemUserName(){
        if(!CACHE_USER_NAME){
            return System.getProperty(USER_NAME_PROP_KEY);
        }
        return USER_NAME;
    }
    
}
