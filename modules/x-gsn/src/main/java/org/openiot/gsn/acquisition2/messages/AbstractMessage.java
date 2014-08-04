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
 * @author Timotee Maret
 * @author Ali Salehi
*/

package org.openiot.gsn.acquisition2.messages;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class AbstractMessage implements Serializable {
  
private static final long serialVersionUID = 6359213795370724295L;

protected transient Logger                                logger                              = Logger.getLogger ( this.getClass());

}
