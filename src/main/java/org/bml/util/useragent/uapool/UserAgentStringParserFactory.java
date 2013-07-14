
package org.bml.util.useragent.uapool;

import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.apache.commons.pool.BasePoolableObjectFactory;

/**
 * @author Brian M. Lima
 */
public class UserAgentStringParserFactory extends BasePoolableObjectFactory<UserAgentStringParser> {

    /**
     * @param stringFormat a SimpleDateFormat string
     */
    public UserAgentStringParserFactory() {
        super();
    }

    @Override
    public UserAgentStringParser makeObject() {
        UserAgentStringParser parser = UADetectorServiceFactory.getCachingAndUpdatingParserHolder();
        return parser;
    }

    @Override
    public void passivateObject(UserAgentStringParser aUserAgentStringParser) {
    }
}
