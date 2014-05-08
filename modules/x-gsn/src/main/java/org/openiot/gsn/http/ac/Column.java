/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
 * @author Behnaz Bostanipour
*/

package org.openiot.gsn.http.ac;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 16, 2010
 * Time: 6:35:18 PM
 * To change this template use File | Settings | File Templates.
 */

/*
This class defines a column of a DB table:
 */
    
public class Column
{
    protected String columnLabel;//name of the column
    protected String columnValue; // value of the column

    public Column(String columnLabel)
    {
        this.columnLabel=columnLabel;
    }
    public Column(String columnLabel, String columnValue)
    {
        this.columnLabel=columnLabel;
        this.columnValue=columnValue;
    }
}
