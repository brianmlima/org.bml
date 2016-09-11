/*
 */
package org.bml.util.data;

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
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

/**
 * Interface for reading - setting and getting a field. This is handy for
 * field sets that can be read and wrote to and from multiple storage formats.
 *
 * @author Brian M. Lima
 * @param <T> The Type of the fields value.
 * @param <R> The data container object where the field is set and retrieved.
 */
public interface FieldUtil<T, R> extends GetUtil<T, R> {

    /**
     * Reads a field from a {@link Reader} and adds it to the dataObject.
     *
     * @param reader A reader.
     * @param dataObject The object to add the field to.
     * @throws ParseException If there is an issue with the parsing or the parsed data is malformed.
     * @throws IOException If there is an issue reading the underlying reader.
     */
    void readAndSet(final Reader reader, final R dataObject) throws ParseException, IOException;
}
