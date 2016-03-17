package org.bml.util.cmd;
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

import ch.qos.logback.classic.Level;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.bml.util.log.LogLevelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper utility class for running checks on files passed to an application via {@link org.apache.commons.cli}.
 *
 * @author Brian M. Lima
 */
public class CMDLineUtil {

    /**
     * Holder for the classes simple name to avoid calls to <code>CMDLineUtil.class.getSimpleName();</code>.
     */
    private static final String CLASS_NAME = CMDLineUtil.class.getName();

    /**
     * Holder for the classes simple name to avoid calls to CMDLineUtil.class.getSimpleName().
     */
    private static final String CLASS_SIMPLE_NAME = CMDLineUtil.class.getSimpleName();

    /**
     * The standard sl4j logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CMDLineUtil.class);

    /**
     * A {@link HelpFormatter} that can be shared with all classes.
     */
    private static final HelpFormatter HELP_FORMATTER = new HelpFormatter();

    /**
     * The exit code to be used when command line utilities exit due to a parameter error.
     */
    public static final int PARAM_ERROR_CODE = 1;

    /**
     * The print name of the utility to be used when printing the help message.
     */
    private final String theUtilityName;
    /**
     * The Logger to log to if appropriate.
     */
    private final Logger theLogger;
    /**
     * The CommandLineObject used to get properties.
     */
    private final CommandLine theCommandLine;
    /**
     * The Options object for help formatting.
     */
    private final Options theOptions;

    /**
     * Prints the standard help using a {@link HelpFormatter}.
     */
    public void printHelp() {
        HELP_FORMATTER.printHelp(theUtilityName, theOptions);
    }

    /**
     * Handles the creation of a {@link CommandLine} object. Logs any errors.
     *
     * @param theOptions The commons cli Options object.
     * @param args the command line arguments as they are passed to main.
     * @param theParser A cli parser.
     * @param theUtilityName The name of the utility for help messages.
     * @return a CommandLine object.
     */
    public static CommandLine makeCommandLine(final Options theOptions, final String[] args, final Parser theParser, final String theUtilityName) {
        CommandLine theCommandLine = null;
        try {
            theCommandLine = theParser.parse(theOptions, args);
        } catch (MissingOptionException ex) {
            LOG.error("Missing Required Option. {}", ex.getMissingOptions().get(0));
            HELP_FORMATTER.printHelp(theUtilityName, theOptions);
        } catch (MissingArgumentException ex) {
            LOG.error("Missing argument value for option {}", CMDLineUtil.getOptionName(ex.getOption()));
            HELP_FORMATTER.printHelp(theUtilityName, theOptions);
        } catch (org.apache.commons.cli.ParseException ex) {
            LOG.error(String.format("Unable to build command line parser for class %s using %s", CLASS_NAME, PosixParser.class.getName()), ex);
            HELP_FORMATTER.printHelp(theUtilityName, theOptions);
        }
        return theCommandLine;
    }

    /**
     * Creates a new Instance of CMDLineFileCheckUtil.
     *
     * @param theUtilityName The print name of the utility to be used when printing the help message.
     * @param theLogger The Logger to log to if appropriate.
     * @param theCommandLine The CommandLine Object.
     * @param theOptions The Options object for help formatting.
     * @pre theUtilityName!=null
     * @pre theLogger!=null
     * @pre theOptions!=null
     */
    public CMDLineUtil(final String theUtilityName, final Logger theLogger, final CommandLine theCommandLine, final Options theOptions) {
        checkNotNull(theUtilityName, "Can not create a new instance of %s with a null %s parameter", CLASS_SIMPLE_NAME, "theUtilityName");
        checkNotNull(theLogger, "Can not create a new instance of %s with a null %s parameter", CLASS_SIMPLE_NAME, "theLogger");
        checkNotNull(theCommandLine, "Can not create a new instance of %s with a null %s parameter", CLASS_SIMPLE_NAME, "theCommandLine");
        checkNotNull(theOptions, "Can not create a new instance of %s with a null %s parameter", CLASS_SIMPLE_NAME, "theOptions");
        this.theUtilityName = theUtilityName;
        this.theLogger = theLogger;
        this.theCommandLine = theCommandLine;
        this.theOptions = theOptions;
    }

    /**
     * Creates a new Instance of CMDLineFileCheckUtil.
     * This constructor assumes
     * <ol>
     * <li>You have set the UTIL_NAME system property for logging messages or are ok with the utility name defaulting to this class name</li>
     * <li>You want to use the PosixParser parser class.</li>
     * </ol>
     *
     * @param theLogger The Logger to log to if appropriate.
     * @param args The main methods argument array.
     * @param theOptions The Options object for help formatting.
     * @pre theLogger!=null
     * @pre theOptions!=null
     */
    public CMDLineUtil(final Logger theLogger, final String[] args, final Options theOptions) {
        checkNotNull(theLogger, "Can not create a new instance of %s with a null %s parameter", CLASS_SIMPLE_NAME, "theLogger");
        checkNotNull(args, "Can not create a new instance of %s with a null %s parameter", CLASS_SIMPLE_NAME, "args");
        checkNotNull(theOptions, "Can not create a new instance of %s with a null %s parameter", CLASS_SIMPLE_NAME, "theOptions");
        this.theUtilityName = System.getProperty("UTIL_NAME", CLASS_NAME);
        this.theLogger = theLogger;
        this.theCommandLine = makeCommandLine(theOptions, args, new PosixParser(), theUtilityName);
        this.theOptions = theOptions;
    }

    /**
     * Helper method for getting the string value of an option.
     *
     * @param theOption The Option whose value is to be retrieved.
     * @return The value of the option or null if not set.
     */
    public String getStringOption(final Option theOption) {
        checkNotNull(theOption, "Can not get string value from an option %s if parameter is null.", "theOption");
        checkArgument(theOption.hasArg(), "Can not get string value from an Option parameter that does not pass Option.hasArg()");
        final String optionName = getOptionName(theOption);
        if (!theCommandLine.hasOption(optionName)) {
            LOG.debug("The Option was not set. Option={}", optionName);
            return null;
        }
        return theCommandLine.getOptionValue(optionName);
    }

    /**
     * Helper method for getting the string values of an option.
     *
     * @param theOption The Option whose values is to be retrieved.
     * @return The values of the option or null if not set.
     */
    @SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
    public String[] getStringOptionValues(final Option theOption) {
        checkNotNull(theOption, "Can not get string values from an option %s if parameter is null.", "theOption");
        checkArgument(theOption.hasArg(), "Can not get string values from an Option parameter that does not pass Option.hasArg()");
        final String optionName = getOptionName(theOption);
        if (!theCommandLine.hasOption(optionName)) {
            LOG.debug("The Option was not set. Option={}", optionName);
            return null;
        }
        return theCommandLine.getOptionValues(optionName);
    }

    /**
     * Helper method for getting the File value of an option.
     *
     * @param theOption The Option whose File value is to be retrieved.
     * @return The File value of the option or null if not set.
     */
    public File getFileOption(final Option theOption) {
        checkNotNull(theOption, "Can not get File value from an option %s if parameter is null.", "theOption");
        checkArgument(theOption.hasArg(), "Can not get File value from an Option parameter that does not pass Option.hasArg()");
        final String optionName = getOptionName(theOption);
        if (!theCommandLine.hasOption(optionName)) {
            LOG.debug("The Option was not set. Option={}", optionName);
            return null;
        }
        final String optionValue = this.getStringOption(theOption);
        if (optionValue == null) {
            LOG.debug("The Option value was null. Option={}", optionName);
            return null;
        }
        if (optionValue.isEmpty()) {
            LOG.debug("The Option value was empty. Option={}", optionName);
            return null;
        }
        return new File(optionValue);
    }

    /**
     * Gets an option value that is expected to be a {@link Pattern} and compiles it.
     *
     * @param theOption an option whose value is a regex pattern
     * @return a pattern
     * @throws PatternSyntaxException if the option value is an invalid pattern.
     */
    public Pattern getPatternOption(final Option theOption) throws PatternSyntaxException {
        checkNotNull(theOption, "Can not get Pattern value from an option %s if parameter is null.", "theOption");
        checkArgument(theOption.hasArg(), "Can not get Pattern value from an Option parameter that does not pass Option.hasArg()");
        Pattern regexFilter = null;
        String optionName = null, optionValue = null;
        if (this.getifSetTrueOption(theOption)) {
            optionName = CMDLineUtil.getOptionName(theOption);
            optionValue = this.getStringOption(theOption);
            if (optionValue == null) {
                LOG.debug("The Option value was null. Option={}", optionName);
                return null;
            }
            if (optionValue.isEmpty()) {
                LOG.debug("The Option value was empty. Option={}", optionName);
                return null;
            }
            try {
                return Pattern.compile(optionValue);
            } catch (PatternSyntaxException e) {
                LOG.error(String.format("RegexFilter is not a valid pattern. Will not compile \"%s\"", optionValue), e);
                throw e;
            }
        }
        return null;
    }

    /**
     * Helper method for getting the value of an option that contains one or more
     * files and or directories.
     *
     * @param theOption The Option whose value is to be retrieved.
     * @param theFileFilter The filter used for files
     * @param theDirFilter The filter used for directories
     * @return A set of files from the option and or the directories contents
     */
    public Set<File> getFileSetRecursiveOption(final Option theOption, final IOFileFilter theFileFilter, final IOFileFilter theDirFilter) {
        checkNotNull(theOption, "Can not get File value from an option %s if parameter is null.", "theOption");
        checkArgument(theOption.hasArg(), "Can not get File value from an Option parameter that does not pass Option.hasArg()");
        final String optionName = getOptionName(theOption);
        if (!theCommandLine.hasOption(optionName)) {
            LOG.debug("The Option was not set. Option={}", optionName);
            return null;
        }
        final String[] optionValues = this.getStringOptionValues(theOption);
        if (optionValues == null) {
            LOG.debug("The Option values array is null. Option={}", optionName);
            return null;
        }
        if (optionValues.length == 0) {
            LOG.debug("The Option values array is zero length. Option={}", optionName);
            return null;
        }
        Set<File> theOutputSet = new HashSet<File>();
        File file;
        Collection<File> files;
        for (String path : optionValues) {
            file = new File(path);
            if (file.isDirectory()) {
                files = FileUtils.listFiles(file, theFileFilter, theDirFilter);
                if (files != null) {
                    theOutputSet.addAll(files);
                }
            } else {
                theOutputSet.add(file);
            }
        }
        return theOutputSet;
    }

    /**
     * Helper method for getting the value of an option that contains one or more
     * files and or directories.
     *
     * @param theOption The Option whose value is to be retrieved.
     * @param theFileFilter The filter used for files
     * @param theDirFilter The filter used for directories
     * @return A set of files from the option and or the directories contents
     */
    public List<File> getFileListRecursiveOption(final Option theOption, final IOFileFilter theFileFilter, final IOFileFilter theDirFilter) {
        checkNotNull(theOption, "Can not get File value from an option %s if parameter is null.", "theOption");
        checkArgument(theOption.hasArg(), "Can not get File value from an Option parameter that does not pass Option.hasArg()");
        final String optionName = getOptionName(theOption);
        if (!theCommandLine.hasOption(optionName)) {
            LOG.debug("The Option was not set. Option={}", optionName);
            return null;
        }
        final String[] optionValues = this.getStringOptionValues(theOption);
        if (optionValues == null) {
            LOG.debug("The Option values array is null. Option={}", optionName);
            return null;
        }
        if (optionValues.length == 0) {
            LOG.debug("The Option values array is zero length. Option={}", optionName);
            return null;
        }
        List<File> theOutputSet = new LinkedList<File>();
        File file;
        Collection<File> files;
        for (String path : optionValues) {
            file = new File(path);
            if (file.isDirectory()) {
                files = FileUtils.listFiles(file, theFileFilter, theDirFilter);
                if (files != null) {
                    theOutputSet.addAll(files);
                }
            } else {
                theOutputSet.add(file);
            }
        }
        return theOutputSet;
    }

    /**
     * A utility method for setting a classes log level from a {@link CommandLine} {@link Option}.
     * Sets log level to off by default if option has not been set.
     *
     * @param theClass The class whose logger should be configured.
     * @param theOption The Option whose value is used to determine the log Level.
     * @return true on success, false otherwise. NOTE: a lack of value is considered success.
     */
    public boolean setLogLevelFromOptions(final Class theClass, final Option theOption) {
        return setLogLevelFromOptions(theClass, theOption, Level.OFF);
    }

    /**
     * A utility method for setting a classes log level from a {@link CommandLine} {@link Option}.
     * Sets log level to off by default if option has not been set.
     *
     * @param theClasses The set of class whose logger should be configured.
     * @param theOption The Option whose value is used to determine the log Level.
     * @return true on success, false otherwise. NOTE: a lack of value is considered success.
     */
    public boolean setLogLevelFromOptions(final Set<Class> theClasses, final Option theOption) {
        for (Class aClass : theClasses) {
            if (!setLogLevelFromOptions(aClass, theOption, Level.OFF)) {
                LOG.error("Unable to set log level for class {}", aClass.getName());
                return false;
            }
        }
        return true;
    }

    /**
     * A utility method for setting a classes log level from a {@link CommandLine} {@link Option}.
     *
     * @param theClass The class whose logger should be configured.
     * @param theOption The Option whose value is used to determine the log Level.
     * @param theDeafultLevel The level that should be set if the Option has not been set.
     * @return true on success, false otherwise. NOTE: a lack of value is considered success.
     */
    public boolean setLogLevelFromOptions(final Class theClass, final Option theOption, final Level theDeafultLevel) {
        checkNotNull(theClass, "Can not set log level from an option if parameter %s is null.", "theClass");
        checkNotNull(theOption, "Can not set log level from an option if parameter %s is null.", "theOption");
        checkNotNull(theDeafultLevel, "Can not set log level from an option %s if parameter %s is null.", "theDeafultLevel");
        checkArgument(theOption.hasArg(), "Can not set log level from an Option parameter that does not pass Option.hasArg()");
        final String optionName = getOptionName(theOption);
        boolean isValidLogLevel;
        final LogLevelUtil aLogLevelUtil = new LogLevelUtil(theClass);
        LOG.debug("Attempting to set log level for class {} from option {}", theClass.getName(), optionName);
        if (!theCommandLine.hasOption(optionName)) {
            aLogLevelUtil.setLogLevel(theDeafultLevel);
            LOG.debug("The Option was not set, The deafult level of {} was used. Class={} Option={} Current Level={}", theDeafultLevel, theClass.getName(), optionName, aLogLevelUtil.getLogLevel());
            return false;
        }
        final String optionValue = theCommandLine.getOptionValue(optionName);
        switch (optionValue) {
            case "off":
                aLogLevelUtil.setLogLevel(Level.OFF);
                isValidLogLevel = true;
                break;
            case "all":
                aLogLevelUtil.setLogLevel(Level.ALL);
                isValidLogLevel = true;
                break;
            case "trace":
                aLogLevelUtil.setLogLevel(Level.TRACE);
                isValidLogLevel = true;
                break;
            case "debug":
                aLogLevelUtil.setLogLevel(Level.DEBUG);
                isValidLogLevel = true;
                break;
            case "info":
                aLogLevelUtil.setLogLevel(Level.INFO);
                isValidLogLevel = true;
                break;
            case "warn":
                aLogLevelUtil.setLogLevel(Level.WARN);
                isValidLogLevel = true;
                break;
            case "error":
                aLogLevelUtil.setLogLevel(Level.ERROR);
                isValidLogLevel = true;
                break;
            default:
                isValidLogLevel = false;
                break;
        }
        Logger aLogger = aLogLevelUtil.getLogger();
        if (isValidLogLevel) { //Log valid value
            aLogger.info("Class={} Option={} value={} is a valid log level and has been set.", theClass.getName(), optionName, optionValue);
            return true;
        } else { //Log invalid value
            aLogger.error("Class={} Option={} value={} is not a valid log level", theClass.getName(), optionName, optionValue);
            return false;
        }
    }

    /**
     * Checks a file parameter and exits with the code {@link CMDLineUtil#PARAM_ERROR_CODE} if there is a failure.
     *
     * @param theOptionName The name of the option this file came from for use when printing help messages on failure.
     * @param theFile The file to be checked.
     * @param checkExists If True a check to see if the file exists will be run.
     * @param checkIsFile If True a check to see if the file is a file will be run.
     * @param checkIsDirectory If True a check to see if the file is a directory will be run.
     * @param checkCanRead If True a check for read permission will be run.
     * @param checkCanWrite If True a check for write permissions will be run.
     * @return true if the file passes all tests, false otherwise.
     */
    public boolean checkFile(final String theOptionName, final File theFile, final boolean checkExists, final boolean checkIsFile, final boolean checkIsDirectory, final boolean checkCanRead, final boolean checkCanWrite) {
        if (checkExists && !theFile.exists()) {
            logErrorAndPrintHelp(String.format("File does not exist. OPTION=%s FILE=%s", theOptionName, theFile.getAbsolutePath()), null);
            return false;
        }
        if (checkIsFile && !theFile.isFile()) {
            logErrorAndPrintHelp(String.format("File is not a file. OPTION=%s FILE=%s", theOptionName, theFile.getAbsolutePath()), null);
            return false;
        }
        if (checkIsDirectory && !theFile.isDirectory()) {
            logErrorAndPrintHelp(String.format("File is not a directory. OPTION=%s FILE=%s", theOptionName, theFile.getAbsolutePath()), null);
            return false;
        }
        if (checkCanRead && !theFile.canRead()) {
            logErrorAndPrintHelp(String.format("File user does not have read permissions. OPTION=%s FILE=%s USER=%s", theOptionName, theFile.getAbsolutePath(), System.getProperty("user.name")), null);
            return false;
        }
        if (checkCanWrite && !theFile.canWrite()) {
            logErrorAndPrintHelp(String.format("File user does not have write permissions. OPTION=%s FILE=%s USER=%s", theOptionName, theFile.getAbsolutePath(), System.getProperty("user.name")), null);
            return false;
        }
        return true;
    }

    /**
     * Checks a file parameter and exits with the code {@link CMDLineUtil#PARAM_ERROR_CODE} if there is a failure.
     *
     * @param theOption The Option this file parameter came from for use when printing help messages on failure.
     * @param theFile The file to be checked.
     * @param checkExists If True a check to see if the file exists will be run.
     * @param checkIsFile If True a check to see if the file is a file will be run.
     * @param checkIsDirectory If True a check to see if the file is a directory will be run.
     * @param checkCanRead If True a check for read permission will be run.
     * @param checkCanWrite If True a check for write permissions will be run.
     * @return true if the file passes all tests, false otherwise.
     */
    public boolean checkFile(final Option theOption, final File theFile, final boolean checkExists, final boolean checkIsFile, final boolean checkIsDirectory, final boolean checkCanRead, final boolean checkCanWrite) {
        checkNotNull(theOption, "Can not checkFile with a null theOption parameter");
        return checkFile(CMDLineUtil.getOptionName(theOption), theFile, checkExists, checkIsFile, checkIsDirectory, checkCanRead, checkCanWrite);
    }

    /**
     * Tells if the command line has the passed option set or not.
     *
     * @param theOption An option to test for.
     * @return true if the option is set, false otherwise.
     */
    public boolean hasOption(final Option theOption) {
        return this.theCommandLine.hasOption(CMDLineUtil.getOptionName(theOption));
    }

    /**
     * Logs an error to a Logger, prints help to stdout.
     *
     * @param theMessage The error message.
     * @param theThrowable A Throwable to log.
     */
    public void logErrorAndPrintHelp(final String theMessage, final Throwable theThrowable) {
        if (theThrowable == null) {
            theLogger.error(theMessage);
        } else {
            theLogger.error(theMessage, theThrowable);
        }
        HELP_FORMATTER.printHelp(theUtilityName, theOptions);
    }

    /**
     * Logs an error to a Logger, prints help to stdout, and exits via <code>System.exit()</code>with the passed code.
     *
     * @param theMessage The error message.
     * @param theThrowable A Throwable to log.
     * @param theExitCode An exit code.
     */
    public void logErrorPrintHelpAndExit(final String theMessage, final Throwable theThrowable, final int theExitCode) {
        logErrorAndPrintHelp(theMessage, theThrowable);
        System.exit(theExitCode);
    }

    /**
     * Returns the first non null option name, prefers the long version.
     *
     * @param theOption An option to get the name of.
     * @return the option name or null if neither a long or short name is set.
     */
    public static String getOptionName(final Option theOption) {
        checkNotNull(theOption, "Can not get the option name for a null theOption parameter.");
        String optionName = theOption.getLongOpt();
        if (optionName == null) {
            optionName = theOption.getOpt();
        }
        return optionName;
    }

    /**
     * Utility method for get and parse to {@link Integer} value from a command line option.
     *
     * @param theOption the option to get a value for.
     * @param theDefaultValue The default value to return if no option is found.
     * @return The value of the option as an Integer, or the passed default value.
     * @throws NumberFormatException if a value is set but can not be parsed by <code>Integer.parseInt(optionValue);</code>
     * @pre theOption!=null
     * @pre Option.hasArg()
     */
    public Integer getIntOption(final Option theOption, final Integer theDefaultValue) throws NumberFormatException {
        checkNotNull(theOption, "Can not get an %s option value with a null %s parameter.", "Integer", "theOption");
        checkArgument(theOption.hasArg(), "Can not get an %s option value with an Option parameter that does not pass precondition %s.", "Integer", "Option.hasArg()");
        Integer result = theDefaultValue;
        String optionName = getOptionName(theOption);
        if (theCommandLine.hasOption(optionName)) {
            String optionValue = theCommandLine.getOptionValue(optionName);
            if (optionValue == null) {
                theLogger.info("{} is defaulting to {}", optionName, theDefaultValue);
                result = theDefaultValue;
            } else {
                try {
                    result = Integer.parseInt(optionValue);
                } catch (NumberFormatException e) {
                    logErrorAndPrintHelp(String.format("Value for option must be an integer. NAME=%s VALUE=%s", optionName, optionValue), e);
                    throw e;
                }
            }
        }
        theLogger.info("{} is set to {}", optionName, result);
        return result;
    }

    /**
     * Utility method for get and parse to {@link Long} value from a command line option.
     *
     * @param theOption the option to get a value for.
     * @param theDefaultValue The default value to return if no option is found.
     * @return The value of the option as an Long, or the passed default value.
     * @throws NumberFormatException if a value is set but can not be parsed by <code>Long.parseLong(optionValue);</code>
     * @pre theOption!=null
     * @pre Option.hasArg()
     */
    public Long getLongOption(final Option theOption, final Long theDefaultValue) throws NumberFormatException {
        checkNotNull(theOption, "Can not get an %s option value with a null %s parameter", "Long", "theOption");
        checkArgument(theOption.hasArg(), "Can not get an %s option value with an Option parameter that does not pass precondition %s", "Long", "Option.hasArg()");
        Long result = theDefaultValue;
        String optionName = getOptionName(theOption);
        if (theCommandLine.hasOption(optionName)) {
            String optionValue = theCommandLine.getOptionValue(optionName);
            if (optionValue == null) {
                theLogger.info("{} is defaulting to {}", optionName, theDefaultValue);
                result = theDefaultValue;
            } else {
                try {
                    result = Long.parseLong(optionValue);
                } catch (NumberFormatException e) {
                    logErrorAndPrintHelp(String.format("Value for option must be a Long. NAME=%s VALUE=%s", optionName, optionValue), e);
                    throw e;
                }
            }
        }
        theLogger.info("{} is set to {}", optionName, result);
        return result;
    }

    /**
     * Utility method for finding out if an option has been set. Handy for empty options.
     *
     * @param theOption The the option to check if set.
     * @return True if the option has been set, false otherwise.
     */
    public boolean getifSetTrueOption(final Option theOption) {
        boolean result = false;
        String optionName = getOptionName(theOption);
        if (theCommandLine.hasOption(optionName)) {
            result = true;
            if (theLogger.isInfoEnabled()) {
                theLogger.info("{} is set to {}", optionName, result);
            }
        }
        return result;
    }

    /**
     * Takes an option and loads the file in its value into a Properties object.
     *
     * @param theOption the option to use.
     * @return A properties object loaded from the option or null on failure.
     */
    public Properties getPropsFromFileOption(final Option theOption) {
        File aPropertiesFile;
        Properties aProperties = null;
        String optionName = CMDLineUtil.getOptionName(theOption);
        if (this.hasOption(theOption)) {
            String optionValue = this.getStringOption(theOption);
            aPropertiesFile = new File(optionValue);
            if (!this.checkFile(optionName, aPropertiesFile, /*checkExists*/ true, /*checkIsFile*/ true, /*checkIsDirectory*/ false, /*checkCanRead*/ true, /*checkCanWrite*/ false)) {
                this.logErrorAndPrintHelp(String.format("The value for an option is not valid. An existing file with read permissions is required. OPTION=%s VALUE=%s USER=%s", optionName, optionValue, System.getProperty("user.name")), null);
            }
            aProperties = new Properties();
            InputStream is = null;
            try {
                is = FileUtils.openInputStream(aPropertiesFile);
                aProperties.load(is);
            } catch (IOException e) {
                this.logErrorAndPrintHelp(String.format("The value for an option is not valid. An existing file with read permissions is required. OPTION=%s VALUE=%s USER=%s", optionName, optionValue, System.getProperty("user.name")), e);
                aProperties = null;
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return aProperties;
    }

    /**
     * The {@link CommandLine} Object used to get properties.
     *
     * @return the theCommandLine
     */
    public CommandLine getTheCommandLine() {
        return theCommandLine;
    }

}
