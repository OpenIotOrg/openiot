package sparql.nodes.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class SensorSelectExpression extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;

	public SensorSelectExpression(String nodeId, Object lat, Object lon, Object rad) {
		super();
		
		// Generate Expr
		appendToScope(new Expression("?" + nodeId + "_record <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?" + nodeId + "_sensor ."));
		appendToScope(new Expression("?" + nodeId + "_sensor <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?" + nodeId + "_sensorId ."));
		Scope scope = new Scope();
		appendToScope(scope);
		scope.appendToScope(new Expression("SELECT ?" + nodeId + "_sensorId"));
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
	public String generate() {
		String pad = generatePad(getDepth());
		return pad + StringUtils.join(generateChildren(), "\n").replace("\n", "\n" + pad) + "\n";
	}
}
