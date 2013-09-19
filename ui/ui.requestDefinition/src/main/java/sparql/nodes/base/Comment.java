package sparql.nodes.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Comment extends AbstractSparqlNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<String> content;

	public Comment() {
		super();
		content = new ArrayList<String>();
	}

	public void appendComment(String data) {
		content.add(data);
	}

	@Override
	public String generate() {
		String pad = generatePad(getDepth()) + "# ";
		return pad + StringUtils.join(content, "\n" + pad) + "\n";
	}

}
