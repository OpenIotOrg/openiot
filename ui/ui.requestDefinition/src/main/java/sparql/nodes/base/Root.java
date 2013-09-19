package sparql.nodes.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Root extends AbstractSparqlNode implements Serializable {

	private static final long serialVersionUID = 1L;

	public Root() {
		super();
	}

	@Override
	public String generate() {
		return StringUtils.join(generateChildren(), "\n");
	}

}
