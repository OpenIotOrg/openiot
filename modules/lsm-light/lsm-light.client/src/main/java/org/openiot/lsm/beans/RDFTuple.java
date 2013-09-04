package org.openiot.lsm.beans;
/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
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
