package sparql.nodes.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Scope extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;

	public Scope() {
		super();
	}

	@Override
	public String generate() {
		String pad = generatePad(getDepth());
		String childrenPad = generatePad(getDepth() + 1);
		String out = pad + "{\n" + StringUtils.join(generateChildren(), "\n") + "\n}";
		return out.replace("\n", "\n" + childrenPad) + "\n";
	}

}
