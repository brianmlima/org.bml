package org.bml.util;

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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.io.IOUtils;

/**
 * <p>
 * Container for commonly used compression utilities.
 * </p>
 *
 * @author Brian M. Lima
 */
public class CompressUtils {

    /**
     * Enables or disables precondition checking.
     */
    public static boolean CHECKED = true;

    /**
     * The default read buffer size used when not explicitely passed.
     */
    public static final int DEFAULT_BUFFER_SIZE = 2097152;

    /**
     * Standard Commons Logging {@link Log}
     */
    private static final Log LOG = LogFactory.getLog(CompressUtils.class);

    /**
     * Get the path separator at runtime for the OS.
     */
    private static final char PATH_SEP = System.getProperty("file.separator").charAt(0);

    /**
     * Matches zip and jar extensions. Used for surface format checking.
     */
    private static final String ZIP_MIME_PATTERN = "(.*[zZ][iI][pP])|(.*[jJ][aA][rR])|(.*[wW][aA][rR])";

    /**
     * Extracts a zip | jar | war archive to the specified directory.
     * Uses the default buffer size for read operations.
     *
     * @param zipFile
     * @param destDir
     * @throws IOException If there is an issue with the archive file or the file system.
     * @throws NullPointerException if any of the arguments are null.
     * @throws IllegalArgumentException if any of the arguments do not pass the
     * preconditions other than null tests. NOTE: This exception wil not be thrown
     * if this classes CHECKED member is set to false
     *
     * @pre zipFile!=null
     * @pre zipFile.exists()
     * @pre zipFile.canRead()
     * @pre zipFile.getName().matches("(.*[zZ][iI][pP])|(.*[jJ][aA][rR])")
     *
     * @pre destDir!=null
     * @pre destDir.exists()
     * @pre destDir.isDirectory()
     * @pre destDir.canWrite()
     */
    public static void extractZip(final File zipFile, final File destDir) throws IOException, IllegalArgumentException, NullPointerException {
        unzipFilesToPath(zipFile, destDir, CompressUtils.DEFAULT_BUFFER_SIZE);
    }

    /**
     * Extracts a zip | jar | war archive to the specified directory.
     * Uses the passed buffer size for read operations.
     *
     * @param zipFile
     * @param destDir
     * @param bufferSize
     * @throws IOException If there is an issue with the archive file or the file system.
     * @throws NullPointerException if any of the arguments are null.
     * @throws IllegalArgumentException if any of the arguments do not pass the
     * preconditions other than null tests. NOTE: This exception wil not be thrown
     * if this classes CHECKED member is set to false
     *
     * @pre zipFile!=null
     * @pre zipFile.exists()
     * @pre zipFile.canRead()
     * @pre zipFile.getName().matches("(.*[zZ][iI][pP])|(.*[jJ][aA][rR])")
     *
     * @pre destDir!=null
     * @pre destDir.exists()
     * @pre destDir.isDirectory()
     * @pre destDir.canWrite()
     *
     * @pre bufferSize > 0
     */
    public static void unzipFilesToPath(final File zipFile, final File destDir, final int bufferSize) throws IOException, IllegalArgumentException, NullPointerException {

        //zipFilePath.toLowerCase().endsWith(zipFilePath)
        if (CHECKED) {
            final String userName = User.getSystemUserName();//use cahced if possible
            //zipFile
            Preconditions.checkNotNull(zipFile, "Can not unzip null zipFile");
            Preconditions.checkArgument(zipFile.getName().matches(ZIP_MIME_PATTERN), "Zip File at %s does not match the extensions allowed by the regex %s", zipFile, ZIP_MIME_PATTERN);
            Preconditions.checkArgument(zipFile.exists(), "Can not unzip file at %s. It does not exist.", zipFile);
            Preconditions.checkArgument(zipFile.canRead(), "Can not extract archive with no read permissions. Check File permissions. USER=%s FILE=%s", System.getProperty("user.name"), zipFile);
            //destDir
            Preconditions.checkNotNull(destDir, "Can not extract zipFileName=%s to a null destination", zipFile);
            Preconditions.checkArgument(destDir.isDirectory(), "Can not extract zipFileName %s to a destination %s that is not a directory", zipFile, destDir);
            Preconditions.checkArgument(destDir.exists(), "Can not extract zipFileName %s to a non existant destination %s", zipFile, destDir);
            Preconditions.checkArgument(destDir.canWrite(), "Can not extract archive with no write permissions. Check File permissions. USER=%s FILE=%s", System.getProperty("user.name"), destDir);
            //bufferSize
            Preconditions.checkArgument(bufferSize > 0, "Can not extract archive %s to %s with a buffer size less than 1. size = %s", zipFile, destDir, bufferSize);

        }

        final FileInputStream fileInputStream = new FileInputStream(zipFile);
        final ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
        final String destBasePath = destDir.getAbsolutePath() + PATH_SEP;

        try {
            ZipEntry zipEntry;
            BufferedOutputStream destBufferedOutputStream;
            int byteCount;
            byte[] data;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                destBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(destBasePath + zipEntry.getName()), bufferSize);
                data = new byte[bufferSize];
                while ((byteCount = zipInputStream.read(data, 0, bufferSize)) != -1) {
                    destBufferedOutputStream.write(data, 0, byteCount);
                }
                destBufferedOutputStream.flush();
                destBufferedOutputStream.close();
            }
            fileInputStream.close();
            zipInputStream.close();
        } catch (IOException ioe) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(String.format("IOException caught while unziping archive %s to %s", zipFile, destDir), ioe);
            }
            throw ioe;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(zipInputStream);
        }
    }

}
