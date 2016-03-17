package org.bml.util.log;

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

import static com.google.common.base.Preconditions.checkNotNull;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

/**
 * A utility for changing and reseting log levels using the Log4j2 core.
 *
 * @author Brian M. Lima
 */
public class LogLevelUtil {

    /**
     * Holder for the classes simple name to avoid calls to <code>LogLevelUtil.class.getSimpleName();</code>.
     */
    private static final String CLASS_SIMPLE_NAME = LogLevelUtil.class.getSimpleName();

    /**
     * The standard sl4j logger for this class.
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LogLevelUtil.class);

    /**
     * The Class whose Log should be configured.
     */
    private final Class theClass;
    /**
     * The classes original log Level. Used to support reset functionality.
     */
    private final Level originalLogLevel;

    /**
     * The Logger that allows us to control levels.
     */
    //private final Logger theLogger;
    private final LoggerContext theLoggerContext = ((ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory());

    /**
     * The {@link Logger}for the {@link Class}.
     *
     * @return a Logger
     */
    public Logger getLogger() {
        return this.theLoggerContext.getLogger(theClass);
    }

    /**
     * Creates a new instance of LogLevelUtil.
     *
     * @param theClass The class to operate on.
     * @pre theClass!=null
     */
    public LogLevelUtil(final Class theClass) {
        checkNotNull(theClass, "Can not set create a new instance of %s with a null theClass parameter.", CLASS_SIMPLE_NAME);
        this.theClass = theClass;
        final Level tmpLevel = theLoggerContext.getLogger(theClass).getLevel();
        if (tmpLevel == null) {
            LOG.trace("{} for class {} modifying original log level of null to {}", CLASS_SIMPLE_NAME, theClass, Level.OFF);
            theLoggerContext.getLogger(theClass).setLevel(Level.OFF);
        }
        this.originalLogLevel = (tmpLevel != null) ? tmpLevel : Level.OFF;
        LOG.trace("Created instance of {} for class {} with original log level {}", CLASS_SIMPLE_NAME, theClass, originalLogLevel);
    }

    /**
     * Helper function that configures a classes logger to the passed level.
     *
     * @param theLevel The level you wish to set the classes log to.
     * @pre theLevel!=null
     */
    public void setLogLevel(final Level theLevel) {
        checkNotNull(theLevel, "Can not set log level with a null theLogLevel.");
        Logger logger = getLogger();
        final Level currentLevel = logger.getLevel();
        if (currentLevel.levelInt != theLevel.toInt()) {
            LOG.trace("{} for class {} with original log level {} Changing Log level from {} to {}", CLASS_SIMPLE_NAME, theClass, originalLogLevel, currentLevel, theLevel);
            logger.setLevel(theLevel);
        } else {
            LOG.trace("{} for class {} with original log level {} Not changing Log level from {} to {}", CLASS_SIMPLE_NAME, theClass, originalLogLevel, currentLevel, theLevel);
        }
    }

    /**
     * Get the current log {@link Level} from the {@link LoggerConfig}.
     *
     * @return the current log {@link Level}
     */
    public Level getLogLevel() {
        return getLogger().getLevel();
    }

    /**
     * Resets the log {@link Level} to the value it had before any changes were made.
     */
    public void resetLogLevel() {
        LOG.trace("{} for class {} with original log level {} Resetting to original Log level from {} to {}", CLASS_SIMPLE_NAME, theClass, originalLogLevel, getLogLevel(), originalLogLevel);
        this.setLogLevel(originalLogLevel);
    }

    /**
     * Get the original log {@link Level}.
     *
     * @return the original log Level.
     */
    public Level getOriginalLogLevel() {
        return originalLogLevel;
    }

}
