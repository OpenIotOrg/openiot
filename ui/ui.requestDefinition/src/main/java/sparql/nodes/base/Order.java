package sparql.nodes.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Order extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;

	public Order() {
		super();
	}

	@Override
	public String generate() {
		if( getChildrenCount() == 0 ){
			return "";
		}
		String pad = generatePad(getDepth());
		String out = pad + "ORDER BY " + StringUtils.join(generateChildren(), " ") + "\n";
		return out;
	}

}
