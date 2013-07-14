
package org.bml.util.errorconsumer;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Holder for data related to pixel parse errors.
 *
 * @author Brian M. Lima
 */
public class ParseError extends Throwable {

    /**
     * STD Logging
     */
    private static Log LOG = LogFactory.getLog(ParseError.class);
    /**
     * A String representation of the reporting class Name.
     */
    private String className = null;
    /**
     * A String representation of the request URI.
     */
    private String queryString = null;
    /**
     * A String description of the error encountered.
     */
    private String reason = null;

    /**
     * @param request A String representation of the request URI.
     * @param error A String description of the error encountered.
     */
    public ParseError(String className, String queryString, String error) {
        super(error);
        this.queryString = queryString;
        this.reason = error;
        this.className = className;
    }

    /**
     * A String representation of the request URI.
     *
     * @return the request
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * A String representation of the request URI.
     *
     * @param request the request to set
     */
    public void setQueryString(String request) {
        this.queryString = request;
    }

    /**
     * A String description of the error encountered.
     *
     * @return the error
     */
    public String getReason() {
        return reason;
    }

    /**
     * A String description of the error encountered.
     *
     * @param error the error to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    public Map<String, String> toParamMap() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("class_name".intern(), this.getClassName());
        paramMap.put("uri".intern(), this.getQueryString());
        paramMap.put("reason".intern(), this.getReason());
        return paramMap;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }
}
