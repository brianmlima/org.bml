package org.bml.util.io;

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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.zip.ZipFile;
import static org.apache.commons.io.IOUtils.closeQuietly;
import org.apache.commons.io.LineIterator;

/**
 * @author Brian M. Lima
 */
public class IOUtils extends org.apache.commons.io.IOUtils {

  /**
   * This is just a helper method to keep all the closeQuietly methods in one
   * place
   *
   * @param theLineIterator an open or null LineIterator
   */
  public static void closeQuietly(LineIterator theLineIterator) {
    if (theLineIterator == null) {
      return;
    }
    LineIterator.closeQuietly(theLineIterator);
  }

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
