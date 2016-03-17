package org.bml.util.useragent.uapool;

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
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.apache.commons.pool.BasePoolableObjectFactory;

/**
 * A pool for {@link UserAgentStringParser}.
 *
 * By default this {@link PoolableObjectFactory} uses
 * <code>UADetectorServiceFactory.getResourceModuleParser()</code>.
 * Override makeObject to use different parsers.
 *
 * @author Brian M. Lima
 */
public class UserAgentStringParserFactory extends BasePoolableObjectFactory<UserAgentStringParser> {

    /**
     * Creates a new instance of UserAgentStringParserFactory.
     */
    public UserAgentStringParserFactory() {
        super();
    }

    @Override
    public UserAgentStringParser makeObject() {
        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
        return parser;
    }

    @Override
    public void passivateObject(final UserAgentStringParser aUserAgentStringParser) {
    }
}
