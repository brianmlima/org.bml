/**
 *   This file is part of org.bml.
 *
 *   org.bml is free software: you can redistribute it and/or modify it under the
 *   terms of the GNU General Public License as published by the Free Software
 *   Foundation, either version 3 of the License, or (at your option) any later
 *   version.
 *
 *   org.bml is distributed in the hope that it will be useful, but WITHOUT ANY
 *   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 *   A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License along with
 *   org.bml. If not, see <http://www.gnu.org/licenses/>.
 */


package org.bml.util.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian M. Lima Based on open source code By Singaram Subramanian
 */
public class LinuxInteractor {

    /**
     * Standard Logging. All logging should be funneled through this log so we
     * can use adaptors for ELB and in house with the same results.
     */
    private static final Log LOG = LogFactory.getLog(LinuxInteractor.class);
    /**
     * Use to avoid calling .class.getName() in high throughput situations
     */
    private static final String CLASS_NAME = LinuxInteractor.class.getName();
    /**
     * Use to avoid calling .class.getSimpleName() in high throughput situations
     */
    private static final String SIMPLE_CLASS_NAME = LinuxInteractor.class.getSimpleName();

    /**
     *
     * @param command The shell command to execute
     * @param waitForResponse Block until command execution has finish
     * @return
     */
    public static String executeCommand(String command, boolean waitForResponse) {
        String response="";
        ProcessBuilder pb;
        Process shell;
        InputStream shellIn;
        int shellExitStatus;

        pb = new ProcessBuilder("bash", "-c", command);
        pb.redirectErrorStream(true);
        try {
            shell = pb.start();
            if (waitForResponse) {
                // To capture output from the shell
                shellIn = shell.getInputStream();
                // Wait for the shell to finish and get the return code
                shellExitStatus = shell.waitFor();
                response = convertStreamToStr(shellIn);
                shellIn.close();
            }

        } catch (IOException e) {
        } catch (InterruptedException e) {
        }
        return response;
    }

    /*
     * To convert the InputStream to String we use the Reader.read(char[]
     * buffer) method. We iterate until the Reader return -1 which means
     * there's no more data to read. We use the StringWriter class to
     * produce the string.
     */
    public static String convertStreamToStr(InputStream is) throws IOException {

        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
