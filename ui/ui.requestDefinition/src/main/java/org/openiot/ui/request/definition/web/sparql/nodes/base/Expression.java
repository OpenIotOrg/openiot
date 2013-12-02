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
package org.openiot.ui.request.definition.web.sparql.nodes.base;
import java.io.Serializable;

public class Expression extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;
	private String expr;

	public Expression(String expr) {
		super();
		this.expr = expr;
	}

	@Override
	public String generate() {
		if( expr == null || expr.isEmpty() ){
			return "";
		}
		return generatePad(getDepth()) + expr;
	}
	
	@Override public boolean equals(Object aThat) {
	    //check for self-comparison
	    if ( this == aThat ) return true;

	    if ( !(aThat instanceof Expression) ){
	    	return false;
	    }
	    
	    //cast to native object is now safe
	    Expression that = (Expression)aThat;

	    return expr.equals(that.expr);
	      
	  }
}
