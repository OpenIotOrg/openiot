/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */

package org.openiot.security.oauth.lsm;

import org.jasig.cas.ticket.ExpirationPolicy;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketState;
import org.springframework.util.Assert;

/**
 * @author Mehdi Riahi
 *
 */
public abstract class LSMAbstractTicket implements Ticket, TicketState {

	private static final long serialVersionUID = 1533453496420317658L;

	private ExpirationPolicy expirationPolicy;

	/**
	 * The unique identifier for this ticket.
	 * 
	 * @Column(name="ID", nullable=false)
	 */
	private String id;

	/**
	 * The TicketGrantingTicket this is associated with.
	 * 
	 * @ManyToOne
	 */
	private LSMTicketGrantingTicketImpl ticketGrantingTicket;

	/**
	 * The last time this ticket was used.
	 * 
	 * @Column(name="LAST_TIME_USED")
	 */
	private long lastTimeUsed;

	/**
	 * The previous last time this ticket was used.
	 * 
	 * @Column(name="PREVIOUS_LAST_TIME_USED")
	 */
	private long previousLastTimeUsed;

	/**
	 * The time the ticket was created.
	 * 
	 * @Column(name="CREATION_TIME")
	 */
	private long creationTime;

	/**
	 * The number of times this was used.
	 * 
	 * @Column(name="NUMBER_OF_TIMES_USED")
	 */
	private int countOfUses;

	protected LSMAbstractTicket() {
		// nothing to do
	}

	/**
	 * Constructs a new Ticket with a unique id, a possible parent Ticket (can
	 * be null) and a specified Expiration Policy.
	 * 
	 * @param id
	 *            the unique identifier for the ticket
	 * @param ticket
	 *            the parent TicketGrantingTicket
	 * @param expirationPolicy
	 *            the expiration policy for the ticket.
	 * @throws IllegalArgumentException
	 *             if the id or expiration policy is null.
	 */
	public LSMAbstractTicket(final String id, final LSMTicketGrantingTicketImpl ticket, final ExpirationPolicy expirationPolicy) {
		Assert.notNull(expirationPolicy, "expirationPolicy cannot be null");
		Assert.notNull(id, "id cannot be null");

		this.id = id;
		this.creationTime = System.currentTimeMillis();
		this.lastTimeUsed = System.currentTimeMillis();
		this.expirationPolicy = expirationPolicy;
		this.ticketGrantingTicket = ticket;
	}

	public final String getId() {
		return this.id;
	}

	protected final void updateState() {
		this.previousLastTimeUsed = this.lastTimeUsed;
		this.lastTimeUsed = System.currentTimeMillis();
		this.countOfUses++;
	}

	public final int getCountOfUses() {
		return this.countOfUses;
	}

	public final long getCreationTime() {
		return this.creationTime;
	}

	public final TicketGrantingTicket getGrantingTicket() {
		return this.ticketGrantingTicket;
	}

	public final long getLastTimeUsed() {
		return this.lastTimeUsed;
	}

	public final long getPreviousTimeUsed() {
		return this.previousLastTimeUsed;
	}

	public final boolean isExpired() {
		return this.expirationPolicy.isExpired(this) || (getGrantingTicket() != null && getGrantingTicket().isExpired()) || isExpiredInternal();
	}

	protected boolean isExpiredInternal() {
		return false;
	}

	public final int hashCode() {
		return 34 ^ this.getId().hashCode();
	}

	public final String toString() {
		return this.id;
	}
}
