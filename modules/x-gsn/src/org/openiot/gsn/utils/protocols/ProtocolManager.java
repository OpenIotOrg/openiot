package org.openiot.gsn.utils.protocols;

import org.openiot.gsn.wrappers.AbstractWrapper;

import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;


/**
 * This class implements a generic finite state machine
 * for HostControllerInterface Protocols.
 * For simple protocols that never wait for an answer
 * from the controller, simply create a ProtocolManager
 * instance with the appropriate Protocol object and
 * then call the method sendQuery.
 * 
 *  Warning: other methods of this class may be refactored soon,
 *  and more states could be added.
 *  
 *  @see AbstractHCIProtocol
 */
public class ProtocolManager {
	private static final transient Logger logger = Logger.getLogger( ProtocolManager.class );
	private AbstractHCIProtocol protocol;
	private ProtocolStates currentState;
	private AbstractHCIQuery lastExecutedQuery = null;
	private Vector < Object > lastParams;
	private AbstractWrapper outputWrapper;

	private Timer timer;
	private TimerTask answerTimeout = new TimerTask() {

		public synchronized void run ( ) {
			lastExecutedQuery = null;
			currentState = ProtocolStates.READY;
		} 
	};

	public enum ProtocolStates {
		READY, WAITING
	}

	public ProtocolManager(AbstractHCIProtocol protocol, AbstractWrapper outputWrapper) {
		this.protocol = protocol;
		this.outputWrapper = outputWrapper;
		currentState = ProtocolStates.READY;
	}

	public synchronized ProtocolStates getCurrentState() {
		return currentState;
	}

	/*
	 * This method tries to execute a query named queryName with parameters params
	 * on the wrapper wrapper.
	 * If successful, it returns the raw command that has been sent.
	 */
	public synchronized byte[] sendQuery(String queryName, Vector<Object> params) {
		byte[] answer = null;
		if(currentState == ProtocolStates.READY) {
			AbstractHCIQuery query = protocol.getQuery( queryName );
			
			if(query != null) {
				if(logger.isDebugEnabled())
					logger.debug( "Retrieved query " + queryName + ", trying to build raw query.");

				byte[] queryBytes = query.buildRawQuery( params );
				if(queryBytes != null) {
					try {
						if(logger.isDebugEnabled())
							logger.debug("Built query, it looks like: " + new String(queryBytes));
						outputWrapper.sendToWrapper(null,null,new Object[] {queryBytes});
						lastExecutedQuery = query;
						lastParams = params;
						answer = queryBytes;
						if(logger.isDebugEnabled())
							logger.debug("Query succesfully sent!");
						if(query.needsAnswer( params )) {
							if(logger.isDebugEnabled())
								logger.debug("Now entering wait mode for answer.");
							timer = new Timer();
							currentState = ProtocolStates.WAITING;
							timer.schedule( answerTimeout , new Date());
						}
					} catch( OperationNotSupportedException e ) {
						if(logger.isDebugEnabled())
							logger.debug("Query could not be sent ! See error message.");
						logger.error( e.getMessage( ) , e );
						currentState = ProtocolStates.READY;
					}
				}
			} else {
				logger.warn("Query " + queryName + " found but no bytes produced to send to device. Implementation may be missing.");
			}

		}
		return answer;
	}

	/*
	 * This tries to match incoming data to the pattern
	 * expected by the query. If the pattern describes
	 * several groups then all the different String
	 * matching these groups are returned.
	 */
	public synchronized Object[] getAnswer(byte[] rawData) {
		Object[] answer = null;
		if(currentState == ProtocolStates.WAITING) {
			answer = lastExecutedQuery.getAnswers(rawData);
		}
		return answer;
	}

	/**
	 * @return
	 */
	public String getProtocolName() {
		if(protocol != null)
			return protocol.getName();
		return null;
	}

	/**
	 * @param string
	 * @return
	 */
	public AbstractHCIQuery getQuery(String string) {
		if(protocol != null)
			return protocol.getQuery(string);
		return null;
	}

	/**
	 * @return
	 */
	public Collection<AbstractHCIQuery> getQueries() {
		if(protocol != null)
			return protocol.getQueries();
		return null;
	}
}

