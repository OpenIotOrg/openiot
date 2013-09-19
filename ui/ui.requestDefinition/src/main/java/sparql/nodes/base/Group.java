package sparql.nodes.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Group extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;

	public Group() {
		super();
	}

	@Override
	public String generate() {
		if( getChildrenCount() == 0 ){
			return "";
		}
		String pad = generatePad(getDepth());
		String out = pad + "GROUP BY " + StringUtils.join(generateChildren(), " ") + "\n";
		return out;
	}

}
