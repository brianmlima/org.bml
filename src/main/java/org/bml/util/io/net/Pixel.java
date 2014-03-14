
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
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.util.Base64;

/**
 * <p>
 * Holds a static Transparent GIF and a method for easily writing to an 
 * OutputStream
 * </p>
 * @author Brian M. Lima
 */
public class Pixel {

    /**
     * Enables or disables precondition checking.
     */
    public static boolean CHECKED = true;

    /**
     * <p>
     * Standard Commons Logging {@link Log}
     * </p>
     */
    private static final Log LOG = LogFactory.getLog(Pixel.class);

    /**
     * <p>
     * A transparent GIF String
     * </p>
     */
    public static final String PIXEL_B64 = "R0lGODlhAQABAPAAAAAAAAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==".intern();
    /**
     * <p>
     * A transparent GIF byte array.
     * </p>
     */
    public static final byte[] PIXEL_BYTES = Base64.decodeBase64(PIXEL_B64.getBytes());

    /**
     * @return A byte array representing a transparent 1X1 GIF pixel.
     */
    public static byte[] getTransparentGif() {
        return PIXEL_BYTES;
    }

    /**
     * Writes a single pixel square transparent gif to the passed OutputStream.
     * Very handy for implementing empty call backs in web API's
     *
     * @param out The
     * @throws IOException OutputStream to write a pixel to.
     * @throws NullPointerException if passed OutputStream is null.
     */
    public static void writeTransparentGif(final OutputStream out) throws IOException {
        if (CHECKED) {
            Preconditions.checkNotNull(out, "Can not write a transparent gif to a null OutputStream.");
        }
        out.write(PIXEL_BYTES);
    }
}
