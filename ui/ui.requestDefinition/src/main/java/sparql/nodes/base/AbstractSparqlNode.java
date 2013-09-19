package sparql.nodes.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractSparqlNode implements Serializable{
	public static int DEPTH_SPACES = 2;
	public static String GRAPH_META_URI = "<http://lsm.deri.ie/OpenIoT/sensormeta#>";
	public static String GRAPH_DATA_URI = "<http://lsm.deri.ie/OpenIoT/sensordata#>";
	
	private static final long serialVersionUID = 1L;
	private List<AbstractSparqlNode> scopedItems;
	private int depth = 0;
	
	public AbstractSparqlNode() {
		this.scopedItems = new ArrayList<AbstractSparqlNode>();
	}
	
	protected boolean existsInScope( AbstractSparqlNode node ){
		for( AbstractSparqlNode child : scopedItems ){
			if( child.equals(node) ){
				return true;
			}
		}
		return false;
	}
	
	public AbstractSparqlNode appendToScope( AbstractSparqlNode node ){
		if( existsInScope(node) ){
			return node;
		}
		this.scopedItems.add(node);
		node.setDepth(depth+1);
		return node;
	}
	
	public AbstractSparqlNode prependToScope( AbstractSparqlNode node ){
		if( existsInScope(node) ){
			return node;
		}
		this.scopedItems.add(0, node);
		node.setDepth(depth+1);
		return node;
	}
	
	public void setDepth( int depth ){
		this.depth = depth;
	}
	
	public int getDepth(){
		return this.depth;
	}
	
	public void removeFromScope(AbstractSparqlNode node){
		this.scopedItems.remove(node);
	}	
	
	public int getChildrenCount(){
		return this.scopedItems.size();
	}
	
	public List<String> generateChildren(){
		List<String> out = new ArrayList<String>(scopedItems.size());
		for( AbstractSparqlNode child : scopedItems ){
			out.add( child.generate() );
		}		
		return out;
	}	
	
	public String generatePad( int padDepth ){
		return StringUtils.leftPad("", DEPTH_SPACES * padDepth);
	}
	
	public abstract String generate();
}
