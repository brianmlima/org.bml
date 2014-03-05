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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang.StringUtils;
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
public class CompressUtil {

    /**
     * Standard Commons Logging {@link Log}
     */
    private static final Log LOG = LogFactory.getLog(CompressUtil.class);

    /**
     * <p>
     * Adds a {@link File} or a directory to a tar archive. Recursivly adds file
     * in a directory. <b>NOTE</b>: Compression is supported by creating the
     * {@link TarArchiveOutputStream} as <code>TarArchiveOutputStream taos = new TarArchiveOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(aFile, false))));</code>
     * </p>
     * <p>
     * <b>NOTE:</b> This implementation is recursive in order to handle decent
     * into directories. Normally recursion is not allowed in No-Fault standards,
     * however in this case the file system would have to descend more levels
     * than the stack can handle which is possible but extreemly unlikely.</p>
     * <p>
     * <b>TODO: This method uses {@link File#listFiles()} which creates an array
     * of unknown size. This is an unsafe operation that should be replaced by an
     * on-demand loaded Iterator<File></b>
     * </p>
     *
     * @param theOutputStream {@link TarArchiveOutputStream} to write a file to.
     * @param theFileToArchive {@link File} to add to the archive.
     * @param theArchivePath The base path in the archive to compress the {@link File} to.
     * This can be thought of the directory the file will decompress to.
     * @throws IOException Per {@link TarArchiveOutputStream#putArchiveEntry(org.apache.commons.compress.archivers.ArchiveEntry)} , {@link TarArchiveOutputStream#closeArchiveEntry()}
     * @throws FileNotFoundException Per {@link IOUtils#copy(java.io.InputStream, java.io.OutputStream)}
     * @throws IllegalArgumentException If arguments are null or otherwise invalid to the point they should be checked before passage.
     */
    public static void addFileToTar(final TarArchiveOutputStream theOutputStream, final File theFileToArchive, final String theArchivePath) throws FileNotFoundException, IOException {
        //Sanity
        ArgumentUtils.checkFileArg(theFileToArchive, "The File to Archive", true, true);
        ArgumentUtils.checkNullArg(theOutputStream, "TarArchiveOutputStream");

        //The full path and name of the file as it will be found in the archive
        String entryName = null;
        //Allow compressing to the root of the archive.
        if (theArchivePath == null || theArchivePath.length() == 0) {
            entryName = StringUtils.EMPTY;
        }

        //Add trailing '/' if pathInArchive is not zero length and does not end in an '/'
        if (theArchivePath != null && theArchivePath.length() > 0) {
            if (theArchivePath.charAt(theArchivePath.length()) != '/') {
                entryName = theArchivePath + '/' + theFileToArchive.getName();
            } else {
                entryName = theArchivePath + theFileToArchive.getName();
            }
        }
        //The entry to be archived
        TarArchiveEntry tarEntry = new TarArchiveEntry(theFileToArchive, entryName);
        try {
            theOutputStream.putArchiveEntry(tarEntry);
        } catch (IOException ioe) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Encountered an IOException while putting a new archive entry FILE=" + theFileToArchive.getAbsolutePath() + " ARCHIVE_PATH=" + entryName, ioe);
            }
            throw ioe;
        }

        if (theFileToArchive.isFile()) {
            try {
                //copy file from input stream to output stream
                IOUtils.copy(new FileInputStream(theFileToArchive), theOutputStream);
            } catch (FileNotFoundException fnfe) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Encountered an FileNotFoundException while copying a new archive entry FILE=" + theFileToArchive.getAbsolutePath() + " ARCHIVE_PATH=" + entryName, fnfe);
                }
                throw fnfe;
            } catch (IOException ioe) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Encountered an IOException while copying a new archive entry FILE=" + theFileToArchive.getAbsolutePath() + " ARCHIVE_PATH=" + entryName, ioe);
                }
                throw ioe;
            }
            try {
                //close the archive entry.
                theOutputStream.closeArchiveEntry();
            } catch (IOException ioe) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Encountered an IOException while closing an archive entry FILE=" + theFileToArchive.getAbsolutePath() + " ARCHIVE_PATH=" + entryName, ioe);
                }
                throw ioe;
            }
        } else { //The file to archive is a directory, decend and archive.
            try {
                //Close the entry as it is not necessary.
                theOutputStream.closeArchiveEntry();
            } catch (IOException ioe) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Encountered an IOException while closing an archive entry FILE=" + theFileToArchive.getAbsolutePath() + " ARCHIVE_PATH=" + entryName, ioe);
                }
                throw ioe;
            }
            //This should be done wit an iterator that does not read an indeterminately large array.
            File[] children = theFileToArchive.listFiles();
            if (children != null) {
                for (File child : children) {
                    System.out.println(child.getName());
                    addFileToTar(theOutputStream, child, entryName + "/");
                }
            }
        }
    }

    /**
     * <p>
     * Extract zip file at the specified destination path. NB:archive must consist
     * of a single root folder containing everything else.
     * </p>
     *
     * @param theArchiveFile path to zip {}
     * @param theDestDirectory path to extract zip file to. Created if it doesn't
     * exist.
     * @throws IllegalArgumentException If arguments are null or otherwise invalid to the point they should be checked before passage. Per {@link ArgumentUtils#checkFileArg(java.io.File, java.lang.String, boolean, boolean) }
     * @pre theArchiveFile !=null
     * @pre theArchiveFile.isFile()
     * @pre theArchiveFile.exists()
     * @pre theDestDirectory!=null
     * @pre theDestDirectory.isDirectory();
     */
    public static void extractZip(final File theArchiveFile, final File theDestDirectory) {
        ArgumentUtils.checkFileArg(theArchiveFile, "The Zip Archive File", true, true);
        ArgumentUtils.checkFileArg(theDestDirectory, "The Unzip Destination directory", false, true);
        try {
            String[] zipRootFolder = new String[]{null};
            unzipFolder(theArchiveFile, theDestDirectory, theArchiveFile.length(), zipRootFolder, 1048576);
        } catch (IOException ioe) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("IOException caught while attempting to unzip File=" + theArchiveFile.getAbsolutePath(), ioe);
            }
        }
    }

    /**
     * TODO: clean up this method. It is too complex and unclear as to exactly how it accomplishes its task.
     *
     * @param archiveFile An zip archive {@link File} to expand.
     * @param compressedSize The size of the archive file IE: <code>archiveFile.length();</code>. Must meet conditions <code>compressedSize>0 && compressedSize<Long.MAX_LONG</code>. Enforced by {@link ArgumentUtils#checkLongArg(long, java.lang.String, long, long) }
     * @param zipDestDir
     * @param outputZipRootFolder
     * @return true on success, false on error.
     * @throws IOException
     * @throws IllegalArgumentException If arguments are null or otherwise invalid to the point they should be checked before passage.
     *
     */
    private static boolean unzipFolder(final File archiveFile, final File zipDestDir, final long compressedSize, final String[] outputZipRootFolder, final int byteBufferLength) throws IOException {
        ArgumentUtils.checkFileArg(archiveFile, "A Zip file to unpack.", true, true);
        ArgumentUtils.checkFileArg(zipDestDir, "A Directory to unpack a Zip file to.", false, true);
        ArgumentUtils.checkLongArg(compressedSize, "The size of the compressed File per File.length() ", 1l, Long.MAX_VALUE);

        ZipFile zipFile = null;
        Enumeration entries;
        ZipArchiveEntry aZipArchiveEntry;
        String name;
        byte[] aByteBuffer = new byte[byteBufferLength];
        File destinationFile, parentFolder;
        FileOutputStream fos = null;
        InputStream entryContent;

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

                destinationFile = new File(zipDestDir, name);
                if (name.endsWith("/")) {
                    if (!destinationFile.isDirectory() && !destinationFile.mkdirs()) {//unable to create directories    
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("Error creating temp directory:" + destinationFile.getPath());
                        }
                        return false;
                    }
                    continue;
                } else if (name.indexOf('/') != -1) {
                    // Create the the parent directory if it doesn't exist
                    parentFolder = destinationFile.getParentFile();
                    if (!parentFolder.isDirectory()) {
                        if (!parentFolder.mkdirs()) {
                            if (LOG.isWarnEnabled()) {
                                LOG.warn("Error creating temp directory:" + parentFolder.getPath());
                            }
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

        } catch (IOException ioe) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("IOException caught while Unzipping File=" + archiveFile.getAbsolutePath(), ioe);
            }
            throw ioe;
        } finally {
            IOUtils.closeQuietly(zipFile);
        }
    }

}
