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


package org.bml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.bml.util.io.IOUtils;

/**
 * Container for common compression tools.
 *
 * @author Brian M. Lima
 */
public class CompressUtil {

  /**
   *
   * @param tarOutputStream
   * @param path
   * @param base
   * @throws IOException
   */
  public static void addFileToTarGz(TarArchiveOutputStream tarOutputStream, String path, String base) throws IOException {
    File f = new File(path);
    System.out.println(f.exists());
    String entryName = base + f.getName();
    TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
    tarOutputStream.putArchiveEntry(tarEntry);

    if (f.isFile()) {
      IOUtils.copy(new FileInputStream(f), tarOutputStream);
      tarOutputStream.closeArchiveEntry();
    } else {
      tarOutputStream.closeArchiveEntry();
      File[] children = f.listFiles();
      if (children != null) {
        for (File child : children) {
          System.out.println(child.getName());
          addFileToTarGz(tarOutputStream, child.getAbsolutePath(), entryName + "/");
        }
      }
    }
  }

  /**
   * Extract zip file at the specified destination path. NB:archive must consist
   * of a single root folder containing everything else
   *
   * @param archivePath path to zip file
   * @param destinationPath path to extract zip file to. Created if it doesn't
   * exist.
   */
  public static void extractZip(String archivePath, String destinationPath) {
    File archiveFile = new File(archivePath);
    File unzipDestFolder = null;

    try {
      unzipDestFolder = new File(destinationPath);
      String[] zipRootFolder = new String[]{null};
      unzipFolder(archiveFile, archiveFile.length(), unzipDestFolder, zipRootFolder);
    } catch (Exception e) {
      e.printStackTrace();
    }
  } 

  /**
   * Unzips a zip file into the given destination directory.
   *
   * The archive file MUST have a unique "root" folder. This root folder is
   * skipped when unarchiving.
   *
   * @return true if folder is unzipped correctly.
   */
  private static boolean unzipFolder(File archiveFile, long compressedSize, File zipDestinationFolder, String[] outputZipRootFolder) {

    ZipFile zipFile = null;
    Enumeration entries = null;
    ZipArchiveEntry aZipArchiveEntry = null;
    String name = null;
    byte[] aByteBuffer = new byte[65536];
    File destinationFile = null;
    File parentFolder = null;
    FileOutputStream fos = null;
    InputStream entryContent = null;

    try {
      zipFile = new ZipFile(archiveFile);

      entries = zipFile.getEntries();
      while (entries.hasMoreElements()) {
        aZipArchiveEntry = (ZipArchiveEntry) entries.nextElement();
        name = aZipArchiveEntry.getName();
        name = name.replace('\\', '/');
        int i = name.indexOf('/');
        if (i > 0) {
          outputZipRootFolder[0] = name.substring(0, i);
        } else {
          name = name.substring(i + 1);
        }

        destinationFile = new File(zipDestinationFolder, name);
        if (name.endsWith("/")) {
          if (!destinationFile.isDirectory() && !destinationFile.mkdirs()) {
            log("Error creating temp directory:" + destinationFile.getPath());
            return false;
          }
          continue;
        } else if (name.indexOf('/') != -1) {
          // Create the the parent directory if it doesn't exist
          parentFolder = destinationFile.getParentFile();
          if (!parentFolder.isDirectory()) {
            if (!parentFolder.mkdirs()) {
              log("Error creating temp directory:" + parentFolder.getPath());
              return false;
            }
          }
        }

        try {
          fos = new FileOutputStream(destinationFile);
          int n;
          entryContent = zipFile.getInputStream(aZipArchiveEntry);
          while ((n = entryContent.read(aByteBuffer)) != -1) {
            if (n > 0) {
              fos.write(aByteBuffer, 0, n);
            }
          }
        } finally {
          IOUtils.closeQuietly(fos);
        }
      }
      return true;

    } catch (IOException e) {
      log("Unzip failed:" + e.getMessage());
    } finally {
      IOUtils.closeQuietly(zipFile);
    }
    return false;
  }

  private static void log(String msg) {
    System.out.println(msg);
  }
}
