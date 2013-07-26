package sparql.nodes.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Where extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;

	public Where() {
		super();
	}

	@Override
	public String generate() {
		String pad = generatePad(getDepth());
		String childrenPad = generatePad(getDepth() + 1);
		String out = pad + "WHERE\n" + pad + "{\n" + StringUtils.join(generateChildren(), "\n").replace("\n", "\n" + childrenPad ) + "\n" + pad + "}\n";
		return out;
	}

}
