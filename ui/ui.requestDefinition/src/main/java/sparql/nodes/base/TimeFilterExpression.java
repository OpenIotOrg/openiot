package sparql.nodes.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class TimeFilterExpression extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;

	public TimeFilterExpression(String nodeId) {
		super();
		
		// Generate Expr
		appendToScope(new Expression("?" + nodeId + "_record <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?" + nodeId + "_recordTime ."));
	}


	@Override
	public String generate() {
		String pad = generatePad(getDepth());
		return pad + StringUtils.join(generateChildren(), "\n").replace("\n", "\n" + pad) + "\n";
	}
}
