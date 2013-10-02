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

import org.apache.commons.lang3.StringUtils;

public class SensorSelectExpression extends AbstractSparqlNode implements Serializable {

	private static final long serialVersionUID = 1L;

	public SensorSelectExpression(String nodeId, Object lat, Object lon, Object rad, boolean includeGeoCoordFields) {
		super();

		// Generate Expr
		appendToScope(new Expression("?" + nodeId + "_record <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?" + nodeId + "_sensor ."));
		appendToScope(new Expression("?" + nodeId + "_sensor <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?" + nodeId + "_sensorId ."));
		Scope scope = new Scope();
		appendToScope(scope);
		scope.appendToScope(new Expression("SELECT ?" + nodeId + "_sensorId"));
		if (includeGeoCoordFields) {
			scope.appendToScope(new Expression("?" + nodeId + "_lat"));
			scope.appendToScope(new Expression("?" + nodeId + "_lon"));
		}
		scope.appendToScope(new From(AbstractSparqlNode.GRAPH_META_URI));
		Where where = new Where();
		scope.appendToScope(where);
		where.appendToScope(new Expression("?" + nodeId + "_sensorId <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?" + nodeId + "_sensorType ."));
		where.appendToScope(new Expression("?" + nodeId + "_sensorType  <http://www.w3.org/2000/01/rdf-schema#label> 'gsn' ."));
		where.appendToScope(new Expression("?" + nodeId + "_sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?" + nodeId + "_loc ."));
		where.appendToScope(new Expression("?" + nodeId + "_loc geo:geometry ?" + nodeId + "_geo ."));
		where.appendToScope(new Expression("?" + nodeId + "_loc geo:lat ?" + nodeId + "_lat ."));
		where.appendToScope(new Expression("?" + nodeId + "_loc geo:long ?" + nodeId + "_lon ."));
		// Note lat/lon are flipped!
		where.appendToScope(new Expression("FILTER (<bif:st_intersects>(?" + nodeId + "_geo, <bif:st_point>( " + lon + ", " + lat + "), " + rad + ")) ."));
	}
	
	@Override
	public void setDepth( int depth ){
		// Indentation patch
		super.setDepth(depth - 1);
	}

	@Override
	public String generate() {
		return StringUtils.join(generateChildren(), "\n");
	}
}
