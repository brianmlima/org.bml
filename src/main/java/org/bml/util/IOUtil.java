/**
 * This file is part of org.bml.
 *
 * org.bml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.bml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with org.bml. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bml.util;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2016 Brian M. Lima
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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Locale;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FilenameUtils;

/**
 * A extension of {@link org.apache.commons.io.IOUtils} with some added utility methods.
 *
 * @author Brian M. Lima
 */
public final class IOUtil extends org.apache.commons.io.IOUtils {

    /**
     * Closes an array of {@link PrintWriter} objects quietly.
     *
     * @param thePrintWriters an array of PrintWriters;
     */
    public static void closeQuietly(final PrintWriter[] thePrintWriters) {
        if (thePrintWriters == null || thePrintWriters.length == 0) {
            return;
        }
        for (int c = 0; c < thePrintWriters.length; c++) {
            closeQuietly(thePrintWriters[c]);
        }
    }

    /**
     * Determines a files format and returns an {@link InputStream}. If the file
     * is an archive or compressed the {@link InputStream} returned is the
     * uncompressed and or un-archived stream. Note this only works for archives
     * and or compressions of single files.
     *
     * @param theFile A file to get an {@link InputStream}.
     * @return an {@link InputStream}
     * @throws ArchiveException If the file is an archive and there is an issue creating an InputStream.
     * @throws FileNotFoundException If the file can not be found when creating an InputStream.
     * @throws CompressorException If the file is compressed and there is an issue creating an InputStream.
     */
    public static InputStream inputStreamFromFile(final File theFile) throws ArchiveException, FileNotFoundException, CompressorException {
        checkNotNull(theFile, "Can not create an InputStream with a null theFile parameter.");
        checkArgument(theFile.exists(), "Can not create an InputStream with a thefile parameter that does not exist. FILE=%s", theFile.getAbsolutePath());
        checkArgument(theFile.isFile(), "Can not create an InputStream with a thefile parameter that is not a file. FILE=%s", theFile.getAbsolutePath());
        checkArgument(theFile.canRead(), "Can not create an InputStream with a thefile parameter that can not be read. FILE=%s USER=%s", theFile.getAbsolutePath(), System.getProperty("user.name"));
        final String extension = FilenameUtils.getExtension(theFile.getName());
        final InputStream is = new FileInputStream(theFile);
        if (extension == null || extension.isEmpty()) {
            return new FileInputStream(theFile);
        }
        switch (extension.toLowerCase(Locale.ROOT)) {
            case ArchiveStreamFactory.TAR:
                return new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.TAR, is);
            case ArchiveStreamFactory.ZIP:
                return new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, is);
            case ArchiveStreamFactory.JAR:
                return new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.JAR, is);
            case CompressorStreamFactory.GZIP:
                return new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.GZIP, is);
            case CompressorStreamFactory.BZIP2:
                return new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.BZIP2, is);
            default:
                return new FileInputStream(theFile);
        }
    }

}
