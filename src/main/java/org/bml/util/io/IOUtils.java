
package org.bml.util.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.zip.ZipFile;

/**
 * @author Brian M. Lima
 */
public class IOUtils extends org.apache.commons.io.IOUtils {

  public static void closeQuietly(PrintWriter thePrintWriters[]) {
    if (thePrintWriters == null || thePrintWriters.length == 0) {
      return;
    }
    for (int c = 0; c < thePrintWriters.length; c++) {
      closeQuietly(thePrintWriters[c]);
    }
  }

  public static void closeQuietly(ZipFile zipFile) {
    if (zipFile == null) {
      return;
    }
    try {
      zipFile.close();
    } catch (IOException ex) {
      Logger.getLogger(IOUtils.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
