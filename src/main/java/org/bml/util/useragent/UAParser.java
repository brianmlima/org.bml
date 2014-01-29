/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bml.util.useragent;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2008 - 2014 Brian M. Lima
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

/**
 *
 * @author brianmlima
 */
public interface UAParser {
 
    public boolean isMobileBrowser(String userAgent);
    public boolean isDesktopBrowser(String userAgent);
    public boolean isSmartTvBrowser(String userAgent);
    
    public boolean isBot(String userAgent);
    
    
    
}
