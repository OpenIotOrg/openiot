package sparql.nodes.base;

import java.io.Serializable;

public class From extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;
	private String URI;

	public From(String URI) {
		super();
		this.URI = URI;
	}

	@Override
	public String generate() {
		String pad = generatePad(getDepth());
		return pad + "FROM " + URI + "\n";
	}
}
