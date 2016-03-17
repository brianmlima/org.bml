package org.bml.util.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.regex.Pattern;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.bml.util.IOUtil;
import org.slf4j.Logger;

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
/**
 * A base class for reading logs, parsing lines, and entering into a data source.
 *
 * @author Brian M. Lima
 * @param <T> A LineParsable, PreparedStatementAble, Validatable object. IE the data object for a single log line.
 */
public abstract class LogReaderUtilityBase<T extends LineParsable> {

    /**
     * Provides a hook where implementations can see each LineParsable immediately after it is parsed.
     *
     * @param aParsedLineParsable the LineParsable after it has been parsed.
     */
    public abstract void parsed(final T aParsedLineParsable);

    /**
     * Creates a T object with a line of log data.
     *
     * @param logLine a line from a log.
     * @return T
     */
    public abstract T makeLineObject(final String logLine);

    /**
     * The default timeout in milliseconds.
     */
    public static final int DEFAULT_TIMEOUT_IN_MILLISECONDS = 2000;

    /**
     * The default timeout in milliseconds.
     */
    public static final int DEFAULT_REPORT_AT_IN_MILLISECONDS = 2000;

    /**
     * The default batch size.
     */
    public static final int DEFAULT_BATCH_SIZE = 2000;

    /**
     * The default number of lines worked on between reports.
     */
    public static final int DEFAULT_REPORT_AT = DEFAULT_BATCH_SIZE;

    /**
     * The error code sent to <code>System.exit()</code>.
     */
    public static final int PARAM_ERR = 1;

    /**
     * The report interval in milliseconds.
     */
    private int reportAt = DEFAULT_REPORT_AT;
    /**
     * should reports be issued.
     */
    private boolean report = false;
    /**
     * should read from stdin.
     */
    private boolean readFromStdin = false;
    /**
     * Should use filter.
     */
    private boolean filter = false;

    /**
     * The filtration {@link Pattern}.
     */
    private Pattern regexFilter = null;
    /**
     * Should format output as JSON.
     */
    private boolean outputJSON = false;
    /**
     * Should attempt to load parsed lines into a JDBC datbase.
     */
    private boolean sqlLoad = false;
    /**
     * The batch size for sql batch inserts.
     */
    private int sqlBatchSize = DEFAULT_BATCH_SIZE;

    /**
     * Getter for a prepared statement to enter T into a data source.
     *
     * @return A prepared statement.
     */
    public abstract String getPreparedStatementSQL();

    /**
     * Getter for class logger.
     *
     * @return a Logger
     */
    public abstract Logger getLog();

    //Line tracking counters
    /**
     * The line counter.
     */
    private int lineCounter = 0,
            /**
             * The unknown host counter.
             */
            unknownHostCounter = 0,
            /**
             * The parse exception counter.
             */
            parseExceptionCounter = 0,
            /**
             * The sql exception counter.
             */
            sqlExceptionCounter = 0,
            /**
             * The valid line counter.
             */
            validCounter = 0,
            /**
             * The invalid line counter.
             */
            invalidCounter = 0,
            /**
             * The filtered counter.
             */
            filteredCounter = 0,
            /**
             * The inserted counter.
             */
            insertedCounter = 0;

    /**
     * should read from stdin.
     *
     * @return the readFromStdin
     */
    public boolean isReadFromStdin() {
        return readFromStdin;
    }

    /**
     * Enumeration for incrementing counters.
     */
    public static enum COUNTER {

        /**
         * The line counter.
         */
        LINE,
        /**
         * The unknown host counter.
         */
        UNKNOWN_HOST,
        /**
         * The parse exception counter.
         */
        PARSE_EXCEPTION,
        /**
         * The sql exception counter.
         */
        SQL_EXCEPTION,
        /**
         * The valid line counter.
         */
        VALID,
        /**
         * The invalid line counter.
         */
        INVALID,
        /**
         * The filtered counter.
         */
        FILTERED,
        /**
         * The inserted counter.
         */
        INSERTED;
    }

    /**
     * Increments a counter based on the COUNTER enum passed.
     *
     * @param aCOUNTERS A COUNTER enumeration.
     * @pre aCOUNTERS!=null;
     */
    public void incrementCounter(final COUNTER aCOUNTERS) {
        checkNotNull(aCOUNTERS, "Can not increment a counter with a null COUNTER enumeration.");
        switch (aCOUNTERS) {
            case LINE:
                lineCounter++;
                break;
            case UNKNOWN_HOST:
                unknownHostCounter++;
                break;
            case PARSE_EXCEPTION:
                parseExceptionCounter++;
                break;
            case INVALID:
                invalidCounter++;
                break;
            case FILTERED:
                filteredCounter++;
                break;
            case INSERTED:
                insertedCounter++;
                break;
            case SQL_EXCEPTION:
                sqlExceptionCounter++;
                break;
            case VALID:
                validCounter++;
                break;

            default:
                break;
        }
    }

    /**
     * The database connection pool.
     */
    private ComboPooledDataSource theComboPooledDataSource = null;

    /**
     * Sets the sql batch size.
     *
     * @param sqlBatchSize the batch size
     */
    public void setSqlBatchSize(final int sqlBatchSize) {
        this.sqlBatchSize = sqlBatchSize;
    }

    /**
     * should read from stdin.
     *
     * @param readFromStdin the readFromStdin to set
     */
    public void setReadFromStdin(final boolean readFromStdin) {
        this.readFromStdin = readFromStdin;
    }

    /**
     * Sets theComboPooledDataSource.
     *
     * @param theComboPooledDataSource a ComboPooledDataSource.
     */
    public void setTheComboPooledDataSource(final ComboPooledDataSource theComboPooledDataSource) {
        checkNotNull(theComboPooledDataSource, "Can not set theComboPooledDataSource with a null ComboPooledDataSource.");
        this.theComboPooledDataSource = theComboPooledDataSource;
    }

    /**
     * Sets the sqlLoad parameter.
     *
     * @param sqlLoad true to load, false otherwise.
     */
    public void setSqlLoad(final boolean sqlLoad) {
        this.sqlLoad = sqlLoad;
    }

    /**
     * Sets the outputJSON parameter.
     *
     * @param outputJSON true to output JSON, false otherwise.
     */
    public void setOutputJSON(final boolean outputJSON) {
        this.outputJSON = outputJSON;
    }

    /**
     * Sets the regexFilter parameter.
     *
     * @param regexFilter a regular expression Pattern.
     */
    public void setRegexFilter(final Pattern regexFilter) {
        if (regexFilter == null) { //handle null as disable
            this.regexFilter = null;
            this.filter = false;
            return;
        }
        this.regexFilter = regexFilter;
        this.filter = true;
    }

    /**
     * Sets the autoCommit on a {@link Connection}.
     *
     * @param theConnection A connection to work on.
     * @param newValue The autocommit value to set to.
     */
    private void setAutoComit(final Connection theConnection, final boolean newValue) {
        try { //Clean up autocommit settings.
            if (theConnection.getAutoCommit() != newValue) {
                theConnection.setAutoCommit(newValue);
            }
        } catch (SQLException sqle) {
            getLog().warn(String.format("Unable to set Connecction AutoCommit to %s. Connection will be returned with value unchanged.", newValue), sqle);
        }
    }

    /**
     * Helper method to encapsulate a raw input line into JSON.
     *
     * @param line the line to encode.
     * @return a line encoded in JSON.
     */
    private String lineToJSON(final String line) {
        return new StringBuilder().append("{\"line\":\"").append(StringEscapeUtils.escapeJavaScript(line)).append("\"}").toString();
    }

    /**
     * The types of expected results for line parsing.
     */
    public enum LINE_RESULT {

        /**
         * A Valid line.
         */
        VALID,
        /**
         * A Invalid line.
         */
        INVALID,
        /**
         * A filtered line.
         */
        FILTERED,
        /**
         * A parse exception.
         */
        PARSE_EXCEPTION,
        /**
         * A JSON line.
         */
        JSON,
        /**
         * A SQL Exception line.
         */
        SQL_EXCEPTION;
    }

    /**
     * A handler for line results. Logs errors and increments counters.
     *
     * @param result The result of the parse attempt.
     * @param line The raw line.
     * @param theThrowable A Throwable thrown while working on the line.
     * @pre result!=null
     */
    public void handleLineResult(final LINE_RESULT result, final String line, final Throwable theThrowable) {
        checkNotNull(result, "Can not handle line result with a null %s parameter", "result");
        switch (result) {
            case FILTERED:
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Line filtered. {\"regex\":\"" + StringEscapeUtils.escapeJavaScript(this.regexFilter.toString()) + "\"} " + lineToJSON(line));
                }
                this.incrementCounter(COUNTER.FILTERED);
                break;
            case PARSE_EXCEPTION:
                if (getLog().isDebugEnabled()) {
                    getLog().debug("ParseException caught while parsing " + lineToJSON(line), theThrowable);
                }
                this.incrementCounter(COUNTER.PARSE_EXCEPTION);
                break;
            case INVALID:
                invalidCounter++;
                if (getLog().isDebugEnabled()) {
                    getLog().debug("IllegalArgumentException caught while parsing " + lineToJSON(line), theThrowable);
                }
                this.incrementCounter(COUNTER.INVALID);
                break;
            case SQL_EXCEPTION:
                if (getLog().isErrorEnabled()) {
                    getLog().error("SQLException caught while processing data for " + lineToJSON(line), theThrowable);
                    if (theThrowable instanceof BatchUpdateException) {
                        getLog().error("BatchUpdateException caught while processing data for " + lineToJSON(line), ((BatchUpdateException) theThrowable).getNextException());
                    }
                }
                this.incrementCounter(COUNTER.SQL_EXCEPTION);
                break;
            case VALID:
                this.incrementCounter(COUNTER.VALID);
                break;
            case JSON:
                break;
            default:
                break;
        }
    }

    /**
     * Reads a Stream of data as "UTF-8", parses lines, and attempt to load
     * data into a database if configured to do so.
     *
     * @param theInputStream The stream to read.
     * @throws IOException if there is an issue reading the stream.
     * @throws java.sql.SQLException If there is an issue communicating with the data source
     * @throws Exception if there is an un-handled issue.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    public void readStream(final InputStream theInputStream) throws IOException, SQLException, Exception {
        checkNotNull(theInputStream, "Can not read from a null InputStream parameter.");
        LineIterator lineIterator;
        String line = null;
        T logLine;
        ObjectMapper anObjectMapper = null;
        StringWriter writer;
        Connection connection = null;
        PreparedStatement ps = null;
        boolean defaultAutoCommit = false;
        boolean allowBatch = true;

        if (this.outputJSON) {
            anObjectMapper = new ObjectMapper();
        }

        lineIterator = IOUtils.lineIterator(theInputStream, "UTF-8");
        try {
            if (this.sqlLoad) {
                //Get connection
                connection = theComboPooledDataSource.getConnection();
                //handle autocomit to false;
                defaultAutoCommit = connection.getAutoCommit();
                if (defaultAutoCommit) {
                    try {
                        connection.setAutoCommit(false);
                    } catch (SQLException sqle) {
                        getLog().warn("Unable to set Connecction AutoCommit to false. UNABLE TO ISOLATE INSERTS AS A SINGLE TRANSACTION!");
                    }
                }
                //Prepare statement.
                ps = connection.prepareStatement(getPreparedStatementSQL());
            }
            while (lineIterator.hasNext()) {
                lineCounter++;
                line = lineIterator.nextLine();
                if (this.filter && this.regexFilter.matcher(line).find()) {
                    this.handleLineResult(LINE_RESULT.FILTERED, line, null);
                    continue; //SKIP
                }
                try {
                    logLine = makeLineObject(line);
                } catch (IllegalArgumentException ex) {
                    this.handleLineResult(LINE_RESULT.INVALID, line, null);
                    continue; //SKIP
                }
                try {
                    logLine.parseLine();
                    logLine.validate();
                    if (this.sqlLoad) {
                        logLine.addToStatemet(ps);
                        ps.addBatch();
                        ps.clearParameters();
                        if (lineCounter % this.sqlBatchSize == 0) {
                            getLog().debug("Attempting to execute batch insert.");
                            try {
                                ps.executeBatch();
                            } catch (SQLException e) {
                                getLog().error(String.format("SQLException caught while executing batch insert. SQL=%s LINE=%s", getPreparedStatementSQL(), line), e.getNextException() != null ? e.getNextException() : e);
                                throw e;
                            }
                        }
                    }
                    if (this.outputJSON) {
                        writer = new StringWriter();
                        anObjectMapper.writeValue(writer, logLine);
                        this.handleLineResult(LINE_RESULT.JSON, writer.toString(), null);
                    }
                } catch (ParseException e) {
                    this.handleLineResult(LINE_RESULT.PARSE_EXCEPTION, line, e);
                } catch (IllegalArgumentException e) {
                    this.handleLineResult(LINE_RESULT.INVALID, line, e);
                }
                if (this.report && lineCounter % this.reportAt == 0) {
                    getLog().info("TOTAL=" + lineCounter + " FILTERED=" + filteredCounter + " EXPECTED_INSERTED=" + (lineCounter - filteredCounter + parseExceptionCounter + invalidCounter) + " PARSE_EXCEPTIONS=" + parseExceptionCounter + " INVALID=" + invalidCounter);
                }
            }
            if (this.sqlLoad) {
                ps.executeBatch();
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            handleLineResult(LINE_RESULT.SQL_EXCEPTION, line, e);
            throw e;
        } finally {
            if (this.sqlLoad) {
                setAutoComit(connection, defaultAutoCommit);
            }
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(ps);
        }
        getLog().info("TOTAL=" + lineCounter + " FILTERED=" + filteredCounter + " EXPECTED_INSERTED=" + (lineCounter - filteredCounter + parseExceptionCounter + invalidCounter) + " PARSE_EXCEPTIONS=" + parseExceptionCounter + " INVALID=" + invalidCounter);
    }

    /**
     * Processes a File.
     *
     * @param processFile The File to process
     * @throws Exception if there is an issue reading or processing the file
     */
    public void processFile(final File processFile) throws Exception {
        InputStream anInputStream = null;
        try {
            anInputStream = IOUtil.inputStreamFromFile(processFile);
            this.readStream(anInputStream);
        } catch (Exception ex) {
            getLog().error(String.format("An %s was encountered while trying to open and read the log file at %s", ex.getClass().getName(), processFile.getAbsolutePath()), ex);
        } finally {
            IOUtils.closeQuietly(anInputStream);
        }
    }

}
