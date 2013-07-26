package sparql.nodes.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class AggregateExpression extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;
	private String expr;

	public AggregateExpression(String expr) {
		super();
		this.expr = expr;
	}

	@Override
	public String generate() {
		return expr + "(" + StringUtils.join(generateChildren(), "") + ")";
	}
}
