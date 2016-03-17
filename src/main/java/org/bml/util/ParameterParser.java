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
package org.bml.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bml.util.token.TokenReadUtil;

/**
 * Parser for key/value parameter strings.
 *
 * @author Brian M. Lima
 */
public final class ParameterParser {

    /**
     * Disables the default constructor.
     *
     * @throws InstantiationException Always.
     */
    private ParameterParser() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden.");
    }

    /**
     * Parses a set of parameters with a space separator and quotes encapsulating values and an equals sign.
     *
     * @param parameterString the parameter string.
     * @return a Map of parameter keys and values.
     */
    public static Map<String, String> parseParams(final String parameterString) {
        final Map<String, String> paramMap = new HashMap<String, String>();

        final TokenReadUtil theTokenReadUtil = new TokenReadUtil(parameterString);
        String paramName, paramValue;

        while (!theTokenReadUtil.isEndReached()) {
            try {
                paramName = theTokenReadUtil.nextToken('=').trim();
                theTokenReadUtil.nextToken('"');
                paramValue = theTokenReadUtil.nextToken('"').trim();
                theTokenReadUtil.skipChars(1);
                if (paramName.isEmpty() && paramValue.isEmpty()) {
                    break;
                }
                paramMap.put(paramName, paramValue);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        return paramMap;
    }

    /**
     * Parses a set of parameters with a space separator and quotes encapsulating values and an equals sign.
     *
     * @param theTokenReadUtil A {@link TokenReadUtil} to read from.
     * @return a Map of parameter keys and values.
     */
    public static Map<String, String> parseParams(final TokenReadUtil theTokenReadUtil) {
        final Map<String, String> paramMap = new HashMap<String, String>();
        String paramName, paramValue;
        while (!theTokenReadUtil.isEndReached()) {
            try {
                paramName = theTokenReadUtil.nextToken('=').trim();
                theTokenReadUtil.nextToken('"');
                paramValue = theTokenReadUtil.nextToken('"').trim();
                theTokenReadUtil.skipChars(1);
                if (paramName.isEmpty() && paramValue.isEmpty()) {
                    break;
                }
                paramMap.put(paramName, paramValue);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        return paramMap;
    }

}
