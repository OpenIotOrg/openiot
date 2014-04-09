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
 * @author Mehdi Riahi
 * @author gsn_devs
 * @author Ali Salehi
 * @author Timotee Maret
*/

package org.openiot.gsn.beans;

import java.io.Serializable;

public class WebInput implements Serializable{
   
	private static final long serialVersionUID = 1587176728962536853L;

	private String name;
  
  private DataField[] parameters;
   
   /**
    * @return the commandName
    */
   public String getName ( ) {
      return name;
   }
   
   public void setName(String name){
	   this.name = name;
   }
   
   /**
    * @return the inputParams
    */
   public DataField [ ] getParameters ( ) {
      return parameters;
   }
   
   public void setParameters(DataField[ ] parameters){
	   this.parameters = parameters;
   } 
}
