package sparql.nodes.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Select extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;

	public Select() {
		super();
	}

	@Override
	public String generate() {
		String pad = generatePad(getDepth());
		String out = pad + "SELECT DISTINCT " + StringUtils.join(generateChildren(), " ") + "\n";
		return out;
	}

}
