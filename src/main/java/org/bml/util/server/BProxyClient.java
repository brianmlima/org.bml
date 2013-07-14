

package org.bml.util.server;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian M. Lima
 */
public class BProxyClient extends BClient {
    
    private static Log LOG = LogFactory.getLog(BProxyClient.class);
    
    private String theHost = "localhost";
    private int thePort = 1234;
    
    /** Creates a new instance of FreshProxyClient */
    public BProxyClient(String aHost, int aPort) {
        super();
        theHost = aHost;
        thePort = aPort;
        //theHost = myPs.getValue(PDef.FS_HOST, "localhost");
        //thePort = myPs.getValue(PDef.FS_PORT, 1234);
    }
    
    public void shutDownServer() {
        try {
            if(this.connect(theHost, thePort)) {
            
                this.theObjectOut.writeInt(666);
                this.theObjectOut.flush();
            }
        }catch(IOException e) {
            if(LOG.isWarnEnabled()){LOG.warn(e);}
        }
    }
    
    /**
     *Method: callMethodByName
     *Description: This method allows you to call a method on a server by sending the
     *    server a signature of the method.  This works for both static and non-static
     *    methods.
     *Param: anObjectName - null for static methods.  If you wish to call a non static method
     *    this is the key in the server side map that points to the object you wish to use.
     *Param: aClassName - The full class name of the class that defines the method.
     *    Examples: HelloWorld.class.getName() or "com.freshnotes.test.HelloWorld"
     *Param: aMethodName - The name of the method Example: "myMethod"
     *Param: aMethodParamTypes - A list of classes (in order) defining the types of parameters
     *    passed to the method.  Example: new Class[]{int.class, String.class, ....} (can be
     *    null if no params. However, aMethodParams has to be null as well)
     *Param: aMethodParams - A list of the values to be passed to the method (in order)
     *    Example: new Object[]{5, "a string param", ....} (can be null if no params. however,
     *    aMethodParamTypes has to be null as well)
     *Return: Object - An object returned from the method.
     */
    public synchronized Object callMethodByName(String anObjectName, String aClassName, String aMethodName,
            Class[] aMethodParamTypes, Object[] aMethodParams) {
        
        Object myReturn = null;
        try {
            if(this.connect(theHost, thePort)) {
                this.theObjectOut.writeInt(0);
                this.theObjectOut.writeObject(anObjectName);
                this.theObjectOut.writeObject(aClassName);
                this.theObjectOut.writeObject(aMethodName);
                this.theObjectOut.writeObject(aMethodParamTypes);
                this.theObjectOut.writeObject(aMethodParams);
                
                myReturn = (Object)this.theObjectIn.readObject();
                
                this.close();
            }else {
                if(LOG.isWarnEnabled()){LOG.warn("Unable to connect with server");}
            }
        }catch(IOException e) {
            if(LOG.isWarnEnabled()){LOG.warn(e);}
        }catch(ClassNotFoundException e) {
            if(LOG.isWarnEnabled()){LOG.warn(e);}
        }
        
        return myReturn;
    }
    
}
