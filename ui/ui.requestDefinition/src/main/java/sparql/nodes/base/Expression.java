package sparql.nodes.base;

import java.io.Serializable;

public class Expression extends AbstractSparqlNode implements Serializable{

	private static final long serialVersionUID = 1L;
	private String expr;

	public Expression(String expr) {
		super();
		this.expr = expr;
	}

	@Override
	public String generate() {
		return expr;
	}
	
	@Override public boolean equals(Object aThat) {
	    //check for self-comparison
	    if ( this == aThat ) return true;

	    if ( !(aThat instanceof Expression) ){
	    	return false;
	    }
	    
	    //cast to native object is now safe
	    Expression that = (Expression)aThat;

	    return expr.equals(that.expr);
	      
	  }
}
