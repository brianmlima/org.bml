
package org.bml.util.useragent.uapool;

/*
 * #%L
 * orgbml
 * %%
 * Copyright (C) 2008 - 2013 Brian M. Lima
 * %%
 * This file is part of org.bml.
 * 
 * org.bml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.bml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with org.bml.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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