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
package org.bml.util.token;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

/**
 * A utility for reading and tokenization of {@link Reader} and {@link String} objects.
 *
 * @author Brian M. Lima
 */
public class TokenReadUtil implements Closeable {

    /**
     * EOF character.
     */
    private static final char EOF = (char) -1;

    /**
     * Escape character.
     */
    private static final char ESC = '\\';

    /**
     * The reader.
     */
    private final Reader theReader;

    /**
     * The String to parse parameters from.
     */
    private final String theOriginalString;

    /**
     * True if the string has been fully read.
     */
    private boolean endReached = false;

    /**
     * Creates a new instance of {@link TokenReadUtil}.
     *
     * @param theOriginalString String to parse parameters from.
     */
    public TokenReadUtil(final String theOriginalString) {
        this.theOriginalString = theOriginalString;
        this.theReader = new StringReader(theOriginalString);
    }

    /**
     * Checks a String value for null and empty.
     *
     * @param tokenName the tokens name.
     * @param fieldValue the field value.
     * @param theOriginalString the String the token was parsed for, used for exception messages.
     * @throws ParseException if a field value does not pass check.
     * @pre fieldKey!=null
     * @pre theOriginalString!=null
     */
    private static void checkString(final String tokenName, final String fieldValue, final String theOriginalString) throws ParseException {
        checkNotNull(tokenName, "Can not check a String with a null tokenName. PARAMETERS fieldValue=\"%s\" theOriginalString=\"%s\"", fieldValue, theOriginalString);
        checkNotNull(fieldValue, "Can not check a String with a null fieldValue. PARAMETERS tokenName=\"%s\" theOriginalString=\"%s\"", tokenName, theOriginalString);
        checkNotNull(theOriginalString, "Can not check FIELD %s with a null theOriginalString.", tokenName);
        if (fieldValue.isEmpty()) {
            throw new ParseException(String.format("CheckString failed. Empty Value. FIELD=%s VALUE= LINE=%s", tokenName, theOriginalString), 0);
        }
    }

    /**
     * Utility method for skipping through chars on a {@link Reader}.
     *
     * @param numChars The number of chars to red.
     * @throws IOException If there is an issue reading the {@link Reader}
     * @pre reader!=null
     */
    public void skipChars(final int numChars) throws IOException {
        checkArgument(numChars > -1, "");
        final long numSkiped = theReader.skip(numChars);
        if (numSkiped < numChars) {
            this.endReached = true;
        }

    }

    /**
     * Reads the token from the {@link Reader} and checks parse result if necessary.
     *
     * @param tokenName The tokens name.
     * @param endDelim The char to read to.
     * @param skipChars Number of characters to skip before reading token.
     * @param checkString If true the resulting token will be checked for null and empty.
     * @return The nest token.
     * @throws IOException If there is an issue with the reader.
     * @throws ParseException If there parsed token does not pass {@link #checkString(java.lang.String, java.lang.String)}.
     */
    public String nextToken(final String tokenName, final char endDelim, final int skipChars, final boolean checkString) throws IOException, ParseException {
        skipChars(skipChars);
        String aTomen = nextToken(endDelim);
        if (checkString) {
            checkString(tokenName, aTomen, theOriginalString);
        }
        return aTomen;
    }

    /**
     * Enforces length rule.
     *
     * @param tokenName The tokens name.
     * @param value The value.
     * @param maxLength The maximum length of a value.
     * @throws IllegalArgumentException If a token value exceeds the maximum length.
     */
    public void enforceLength(final String tokenName, final String value, final int maxLength) throws IllegalArgumentException {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(String.format("Field %s exceeds its max length restriction. LENGTH=%s MAX_LENGTH=%s VALUE=%s LINE=%s", tokenName, value.length(), maxLength, value, this.theOriginalString));
        }
    }

    /**
     * Reads the {@link Reader} and produces a string token.
     * Accounts for escaped delimiters.
     *
     * @param endDelim the character to read up to.
     * @return The token that has been read.
     * @throws IOException If there is an issue with the reader.
     * @pre reader!=null
     */
    public String nextToken(final char endDelim) throws IOException {
        checkNotNull(theReader, "Can not read next token on a null Reader.");
        boolean isEscaped = false;
        char aCharacter;
        final StringBuilder returnBuffer = new StringBuilder();
        while ((aCharacter = (char) theReader.read()) != EOF) {
            if (aCharacter == endDelim && !isEscaped) {
                return returnBuffer.toString();
            } else if (aCharacter == ESC) {
                if (isEscaped) {
                    isEscaped = false;
                    returnBuffer.append(ESC);
                } else {
                    isEscaped = true;
                }
            } else {
                isEscaped = false;
                returnBuffer.append(aCharacter);
            }
        }
        this.endReached = true;
        return returnBuffer.toString();
    }

    @Override
    public void close() throws IOException {
        this.theReader.close();
    }

    /**
     * True if the string has been fully read.
     *
     * @return the endReached
     */
    public boolean isEndReached() {
        return endReached;
    }

    /**
     * Calls reset on the underlying {@link Reader}.
     *
     * @exception IOException If the stream has not been marked,
     * or if the mark has been invalidated,
     * or if the stream does not support reset(),
     * or if some other I/O error occurs
     */
    public void reset() throws IOException {
        theReader.reset();
        this.endReached = false;
    }

}
