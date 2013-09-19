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
*/

package org.openiot.gsn.utils.protocols;

import java.util.Vector;

/**
 * This class provides an empty implementation of the methods
 * getWaitTime, needsAnswer and getAnswers to make it
 * easier to implement queries that don't require an answer.
 */
public abstract class AbstractHCIQueryWithoutAnswer extends AbstractHCIQuery {

   public AbstractHCIQueryWithoutAnswer(String Name, String queryDescription, String[] paramsDescriptions) {
      super(Name, queryDescription, paramsDescriptions);
   }

   // we usually dont expect an answer
   public int getWaitTime ( Vector < Object > params ) {
      // TODO Auto-generated method stub
      return NO_WAIT_TIME;
   }
   
   /* 
    * By default we dont expect an answer. 
    */
   public boolean needsAnswer ( Vector < Object > params ) {
      return false;
   }
   
   /*
    * No answer by default so this is a placeholder method.
    */
   public Object[] getAnswers(byte[] rawAnswer) {
	   return null;
   }
}
