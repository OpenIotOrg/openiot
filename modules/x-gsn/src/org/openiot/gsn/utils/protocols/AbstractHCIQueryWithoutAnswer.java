package org.openiot.gsn.utils.protocols;

import java.util.Vector;

/**
 * This class provides an empty implementation of the methods
 * getWaitTime, needsAnswer and getAnswers to make it
 * easier to implement queries that don't require an answer.
 */
public abstract class AbstractHCIQueryWithoutAnswer extends AbstractHCIQuery {

   public AbstractHCIQueryWithoutAnswer(String Name, String queryDescription, String[] paramsDescriptions) {
      super(Name, queryDescription, paramsDescriptions);
   }

   // we usually dont expect an answer
   public int getWaitTime ( Vector < Object > params ) {
      // TODO Auto-generated method stub
      return NO_WAIT_TIME;
   }
   
   /* 
    * By default we dont expect an answer. 
    */
   public boolean needsAnswer ( Vector < Object > params ) {
      return false;
   }
   
   /*
    * No answer by default so this is a placeholder method.
    */
   public Object[] getAnswers(byte[] rawAnswer) {
	   return null;
   }
}
