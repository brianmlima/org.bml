package org.bml.util;
/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2014 Brian M. Lima
 * %%
 * This file is part of ORG.BML.
 *     ORG.BML is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     ORG.BML is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for manipulating {@link Properties} objects.
 *
 * @author Brian M. Lima
 */
public final class PropertiesUtil {

    /**
     * The standard sl4j logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * Disables the default constructor.
     *
     * @throws InstantiationException Always.
     */
    private PropertiesUtil() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden.");
    }

    /**
     * Performs a shallow copy of the keys and values in a {@link Properties}
     * Object and returns the copy.
     *
     * @param theProperties A Properties object to copy.
     * @return a shallow copy of the passed Properties object.
     * @pre theProperties!=null;
     */
    public static Properties shalowCopyProperties(final Properties theProperties) {
        checkNotNull(theProperties, "Can not copy a null Properties object");
        Properties newProperties = new Properties();
        for (Entry<Object, Object> entry : theProperties.entrySet()) {
            newProperties.put(entry.getKey(), entry.getValue());
        }
        return newProperties;
    }

    /**
     * Copies any parameters that begin with the passed prefix and returns a new
     * {@link Properties} object. The prefix is removed from the property key
     * in the returned properties object.
     *
     * @param propertyPrefix A prefix for properties
     * @param thePropertiesIn a properties object to copy from
     * @return a property object with the copied properties.
     * @pre theProperties!=null
     * @pre propertyPrefix!=null
     * @pre !propertyPrefix.isEmpty()
     *
     */
    public static Properties copyProperties(final String propertyPrefix, final Properties thePropertiesIn) {
        checkNotNull(thePropertiesIn, "Can not copy prefixed properties from a null Properties object");
        checkNotNull(propertyPrefix, "Can not copy prefixed properties from a Properties object with a null property prefix");
        checkArgument(!propertyPrefix.isEmpty(), "Can not copy prefixed properties from a Properties object with an empty property prefix");

        final Properties propertiesOut = new Properties();
        String key;
        for (Entry<Object, Object> entry : thePropertiesIn.entrySet()) {
            if ((entry.getKey() instanceof String) && (entry.getValue() instanceof String)) {
                key = (String) entry.getKey();
                if (key.startsWith(propertyPrefix)) {
                    propertiesOut.put(key.replace(propertyPrefix, ""), (String) entry.getValue());
                }
            }
        }
        return propertiesOut;
    }

    /**
     * Loads properties from a {@link File} path.
     *
     * @param thePropertiesFilePath a path to a properties file to read from.
     * @return A Properties object loaded from the passed file path or null on error.
     * @pre thePropertiesFile!=null
     * @pre thePropertiesFile.isFile()
     */
    public static Properties fromFile(final String thePropertiesFilePath) {
        checkNotNull(thePropertiesFilePath, "Can not read properties from a null thePropertiesFilePath parameter.");
        checkArgument(!thePropertiesFilePath.isEmpty(), "Can not read properties from an empty thePropertiesFilePath parameter.");
        final File thePropertiesFile = new File(thePropertiesFilePath);
        return fromFile(thePropertiesFile);
    }

    /**
     * Loads properties from a {@link File}.
     *
     * @param thePropertiesFile a properties file to read from.
     * @return A Properties object loaded from the passed file or null on error.
     * @pre thePropertiesFile!=null
     * @pre thePropertiesFile.isFile()
     * @pre thePropertiesFile.exists()
     * @pre thePropertiesFile.canRead()
     */
    public static Properties fromFile(final File thePropertiesFile) {
        checkNotNull(thePropertiesFile, "Can not read properties from a null thePropertiesFile File parameter.");
        checkArgument(thePropertiesFile.isFile(), "Can not read properties when thePropertiesFile File parameter is not a file. PATH=%s", thePropertiesFile.getAbsolutePath());
        checkArgument(thePropertiesFile.exists(), "Can not read properties when thePropertiesFile File parameter does not exist. PATH=%s", thePropertiesFile.getAbsolutePath());
        checkArgument(thePropertiesFile.canRead(), "Can not read properties when thePropertiesFile File parameter is not readable. USER=%s PATH=%s", System.getProperty("user.name"), thePropertiesFile.getAbsolutePath());
        InputStream is = null;
        final Properties props = new Properties();
        try {
            is = new FileInputStream(thePropertiesFile);
            try {
                props.load(is);
                return props;
            } catch (IOException ex) {
                LOG.error(String.format("IOException caught while attempting to read properties from %s.", thePropertiesFile.getAbsolutePath()), ex);
                return null;
            }
        } catch (FileNotFoundException ex) {
            LOG.error(String.format("FileNotFoundException caught while attempting to read properties from %s.", thePropertiesFile.getAbsolutePath()), ex);
            return null;
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
        }
    }
}
