
package org.bml.util.server;

/*
 * #%L
 * orgbml
 * %%
 * Copyright (C) 2008 - 2013 Brian M. Lima
 * %%
 * This file is part of org.bml.
 * 
 * org.bml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.bml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with org.bml.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian M, Lima
 */
public class BProxyServer extends BServer {
    
    private Log log = LogFactory.getLog(BProxyServer.class);
    
    private Map<String, Object> theObjectMap = null;
    
    /** Creates a new instance of FreshProxyServer */
    public BProxyServer() {
        super();
        theObjectMap = new HashMap<String, Object>();
    }
    
    public void runInvProxy(int aPort, int aNumThreads, int aSleepTime, int aMaxQueueSize,Log accessLog) {
        super.runInvProxy(aPort,
                aNumThreads,
                aSleepTime,
                aMaxQueueSize,accessLog);
    }
    
    public void addObjectToMap(String aKey, Object anObj) {
        theObjectMap.put(aKey, anObj);
    }
    
    public void processConnection(ObjectInputStream aIn, ObjectOutputStream aOut) {
        
        int myMethod = -1;
        try {
            myMethod = aIn.readInt();
        }catch(IOException e) {
            if(log.isWarnEnabled()){log.warn("Error reading object method: " + e);}
            return;
        }
        
        if(myMethod == 666) {
            this.stopServer();
        }else if(myMethod == 0) {
            this.callMethodByName(aIn, aOut);
        }
        
        return;
    }
    
    public void callMethodByName(ObjectInputStream aIn, ObjectOutputStream aOut) {
        
        String myObjectName = null;
        String myClassName = null;
        String myMethodName = null;
        Class[] myMethodParams = null;
        Object[] myArgList = null;
        try {
            Object myObj = aIn.readObject();
            if(myObj instanceof String) {
                myObjectName = (String)myObj;
            }
            
            myObj = aIn.readObject();
            if(myObj instanceof String) {
                myClassName = (String)myObj;
            }
            
            myObj = aIn.readObject();
            if(myObj instanceof String) {
                myMethodName = (String)myObj;
            }
            
            myObj = aIn.readObject();
            if(myObj instanceof Class[]) {
                myMethodParams = (Class[])myObj;
            }
            
            myObj = aIn.readObject();
            if(myObj instanceof Object[]) {
                myArgList = (Object[])myObj;
            }
        }catch(IOException e) {
            if(log.isWarnEnabled()){log.warn("Error reading object name: " + e);}
            return;
        }catch(ClassNotFoundException e) {
            if(log.isWarnEnabled()){log.warn("Error reading object name2: " + e);}
            return;
        }
        
        if(log.isInfoEnabled()){log.info("ObjectName: " + myObjectName + " ClassName: " + myClassName + " MethodName: " + myMethodName);}
        
        Object myObjectToCallMethodOn = (Object)this.theObjectMap.get(myObjectName);
        
        Object myReturn = null;
        if(myClassName != null && myMethodName != null) {
            try {
                Class<?> clazz = Class.forName(myClassName);
                Method myMethod = clazz.getMethod(myMethodName, myMethodParams);
                myReturn = myMethod.invoke(myObjectToCallMethodOn, myArgList);
            }catch(IllegalAccessException e) {
                System.out.println(e);
            }catch(IllegalArgumentException  e) {
                System.out.println(e);
            }catch(InvocationTargetException e) {
                e.printStackTrace();
                System.out.println(e);
            }catch(ClassNotFoundException e) {
                System.out.println(e);
            }catch(NoSuchMethodException e) {
                System.out.println(e);
            }
        }
        
        try {
            aOut.writeObject(myReturn);
        }catch(IOException e) {
            System.out.println("Error writing object return: " + e);
            return;
        }
    }
}
