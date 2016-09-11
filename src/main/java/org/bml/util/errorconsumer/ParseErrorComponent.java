package org.bml.util.errorconsumer;

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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The {@link ParseErrorComponent} provides a simple way to create and use the
 * ParseError component.
 *
 * @author Brian M. Lima
 */
public class ParseErrorComponent {

    public class ParseErrorComponentConfig {

        public BlockingQueue<ParseError> queueIn = null;
        public long timeout = 0;
        public TimeUnit timeUnit = null;
        public long waitOnEmptyQueueInMills = 0;

    }

    public ParseErrorComponent() {

    }

    //ParseErrorWorkerThreadObjectFactory(BlockingQueue<ParseError> queueIn, long timeout, TimeUnit unit, long waitOnEmptyQueueInMills)
}
