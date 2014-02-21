
package org.bml.util.io.net;

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
