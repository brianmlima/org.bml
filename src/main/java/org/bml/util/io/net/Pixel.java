
package org.bml.util.io.net;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.util.Base64;

/**
 * @author Brian M. Lima
 */
public class Pixel {

    /**
     * A transparent GIF String
     */
    public static final String PIXEL_B64 = "R0lGODlhAQABAPAAAAAAAAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==".intern();
    /**
     * A transparent GIF byte array.
     */
    public static final byte[] PIXEL_BYTES = Base64.decodeBase64(PIXEL_B64.getBytes());
    
    /**
     * @return A byte array representing a transparent 1X1 GIF pixel.
     */
    public static byte[] getTransparentGif(){
        return PIXEL_BYTES;
    }


    public static void writeTransparentGif(OutputStream out) throws IOException{
        out.write(PIXEL_BYTES);
    }



}
