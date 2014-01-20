package org.openiot.lsm.beans;
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

/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */
public class RDFTuple implements java.io.Serializable{
	private String ntriple;
	private String graphURL;
	
	public RDFTuple(){
		
	}
	public RDFTuple(String graphURL,String n3){
		this.ntriple = n3;
		this.graphURL = graphURL;
	}
	
	public String getNtriple() {
		return ntriple;
	}
	public void setNtriple(String ntriple) {
		this.ntriple = ntriple;
	}
	public String getGraphURL() {
		return graphURL;
	}
	public void setGraphURL(String graphURL) {
		this.graphURL = graphURL;
	}
	
}
