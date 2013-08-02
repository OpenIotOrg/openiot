package org.openiot.gsn.acquisition2.messages;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class AbstractMessage implements Serializable {
  
private static final long serialVersionUID = 6359213795370724295L;

protected transient Logger                                logger                              = Logger.getLogger ( this.getClass());

}
