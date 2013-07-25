package org.openiot.gsn.beans;

import java.io.Serializable;

public class WebInput implements Serializable{
   
	private static final long serialVersionUID = 1587176728962536853L;

	private String name;
  
  private DataField[] parameters;
   
   /**
    * @return the commandName
    */
   public String getName ( ) {
      return name;
   }
   
   public void setName(String name){
	   this.name = name;
   }
   
   /**
    * @return the inputParams
    */
   public DataField [ ] getParameters ( ) {
      return parameters;
   }
   
   public void setParameters(DataField[ ] parameters){
	   this.parameters = parameters;
   } 
}
