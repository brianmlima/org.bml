
package org.bml.util;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2014 Brian M. Lima
 * %%
 * This file is part of ORG.BML.
 * 
 *     ORG.BML is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     ORG.BML is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
