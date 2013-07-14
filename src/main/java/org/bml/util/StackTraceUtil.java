
package org.bml.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**Helper for printing stack traces.
 * @author Brian M. Lima
 */
public class StackTraceUtil {

    private static Log LOG = LogFactory.getLog(StackTraceUtil.class);

    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }
}
