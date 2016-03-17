package org.bml.util.cmd;

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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * A collection of commonly used options.
 *
 * @author Brian M. Lima
 */
public final class CommonOptions {

    /**
     * Disables the default constructor.
     *
     * @throws InstantiationException Always.
     */
    private CommonOptions() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden. FORBIDDEN!");
    }
    /**
     * The Log Directory Option.
     */
    public static final Option LOG_DIR_OPTION = OptionBuilder.hasArg().withArgName("loggingDirectory").withDescription("The path of a directory where this process will write logs.").withLongOpt("log_directory").create("ld");
    /**
     * The JDBC Properties File location Option.
     */
    public static final Option JDBC_PROPERTIES_FILE_OPTION = OptionBuilder.hasArg().withArgName("properties file").withDescription("The path of a properties file containing the JDBC properties (JDBC_DRIVER,JDBC_URL,DB_USER,DB_PASS).").withLongOpt("jdbc_properties_file").create("J");

    /**
     * The Generic Properties File location Option.
     */
    public static final Option PROPERTIES_FILE_OPTION = OptionBuilder.hasArg().withArgName("properties file").withDescription("The path of a properties file containing the properties necessary for this .").withLongOpt("properties_file").create("P");

    /**
     * The accessKey Option.
     */
    public static final Option ACCESS_KEY_OPTION = OptionBuilder.hasArg().withArgName("access key").withDescription("The AWS access key").withLongOpt("access_key").create("AK");
    /**
     * The secretKey Option.
     */
    public static final Option SECRET_KEY_OPTION = OptionBuilder.hasArg().withArgName("secret key").withDescription("The AWS secret key").withLongOpt("secret_key").create("SK");

    /**
     * The secretKey Option.
     */
    public static final Option DEBUG_OPTION = OptionBuilder.withDescription("Run debugging routines and log verbose. Note logging must be set below or at debug for output").withLongOpt("debug").create("d");

    /**
     * The endpoint Option.
     */
    public static final Option ENDPOINT_OPTION = OptionBuilder.hasArg().withArgName("http endpoint").withDescription("The AWS Ivona Cloud endpoint").withLongOpt("endpoint").create("E");

    /**
     * The SQL Load Option.
     */
    public static final Option SQL_LOAD_OPTION = OptionBuilder.withDescription("If set the utility will attempt to load data into the configured database.").withLongOpt("insert_into_database").create("I");
    /**
     * The Process File Option.
     */
    public static final Option PROCESS_FILE_OPTION = OptionBuilder.isRequired().hasArgs().withValueSeparator(' ').withArgName("file").withDescription("The path of a file, set of files, directories and or archives containing data to read from").withLongOpt("file").create("f");
    /**
     * The XXXXX Option.
     */
    public static final Option SQL_TIMEOUT_OPTION = OptionBuilder.hasArg().withType(Integer.class).withArgName("sql timeout").withDescription("The number of SECONDS to timeout attempted sql operations. NOTE: Must be a positive integer.").withLongOpt("sql_timeout").create("st");
    /**
     * The XXXXX Option.
     */
    public static final Option REPORT_AT_OPTION = OptionBuilder.hasArg().withType(Integer.class).withArgName("number of lines").withDescription("The number of lines to be read between reports. NOTE: Reports are loged at log level INFO. To supress reports set to anything less than 1. Defaults to 100.").withLongOpt("lines_between_reports").create("report_at");
    /**
     * The XXXXX Option.
     */
    public static final Option BATCH_SIZE_OPTION = OptionBuilder.hasArg().withType(Integer.class).withArgName("number of inserts").withDescription("The maximum number of inserts to execute in a batch").withLongOpt("batch_size").create("bs");
    /**
     * The XXXXX Option.
     */
    public static final Option READ_FROM_STDIN_OPTION = OptionBuilder.withDescription("Reads lines from stdin").withLongOpt("read_from_stdin").create("stdin");
    /**
     * The XXXXX Option.
     */
    public static final Option REGEX_FILTER_OPTION = OptionBuilder.hasArg().withArgName("filter regex").withDescription("A regular expression used to filter lines. The match process is exactly 'Pattern.compile(regex).matcher(line).find()'. If the result is true then the line is filtered.").withLongOpt("filter_regex").create("filter");
    /**
     * The XXXXX Option.
     */
    public static final Option JSON_OUTPUT_OPTION = OptionBuilder.withDescription("If set the processor will output JSON representations of all successfully parsed and validated lines.").withLongOpt("json_output").create("json");
    /**
     * The XXXXX Option.
     */
    public static final Option LOG_LEVEL_OPTION = OptionBuilder.hasArg().withArgName("log level").withDescription("The logging level to use. Acceptable values are off, all, trace, debug, info, warn, error, or fatal").withLongOpt("logging_level").create("ll");
    /**
     * The XXXXX Option.
     */
    public static final Option HELP_OPTION = OptionBuilder.withDescription("Print this message").withLongOpt("help").create("h");

    /**
     * The Pretty Print Option. Used for json / XML formatting.
     */
    public static final Option PRETTY_PRINT_OPTION = OptionBuilder.withDescription("Pretty print output").withLongOpt("pretty_print").create("pp");

    /**
     * The XXXXX Option.
     */
    public static final Option FAIL_FAST_OPTION = OptionBuilder.withDescription("Stop working if there are any errors").withLongOpt("fail_fast").create("ff");
}
