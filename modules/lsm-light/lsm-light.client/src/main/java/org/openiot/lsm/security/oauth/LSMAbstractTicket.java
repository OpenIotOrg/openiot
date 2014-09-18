package org.openiot.lsm.security.oauth;

import org.jasig.cas.ticket.ExpirationPolicy;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketState;
import org.springframework.util.Assert;

public abstract class LSMAbstractTicket implements Ticket, TicketState {

	private static final long serialVersionUID = 1533453496420317658L;

	private ExpirationPolicy expirationPolicy;

	/**
	 * The unique identifier for this ticket.
	 */
	private String id;

	/**
	 * The TicketGrantingTicket this is associated with.
	 */
	private LSMTicketGrantingTicketImpl ticketGrantingTicket;

	/**
	 * The last time this ticket was used.
	 */
	private long lastTimeUsed;

	/**
	 * The previous last time this ticket was used.
	 */
	private long previousLastTimeUsed;

	/**
	 * The time the ticket was created.
	 */
	private long creationTime;

	/**
	 * The number of times this was used.
	 */
	private int countOfUses;

	protected LSMAbstractTicket() {
		// nothing to do
	}

	/**
	 * Constructs a new Ticket with a unique id, a possible parent Ticket (can be null) and a
	 * specified Expiration Policy.
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

	public void setId(String id) {
		this.id = id;
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

	public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
		this.expirationPolicy = expirationPolicy;
	}

	public ExpirationPolicy getExpirationPolicy() {
		return this.expirationPolicy;
	}

	public void setTicketGrantingTicket(LSMTicketGrantingTicketImpl ticketGrantingTicket) {
		this.ticketGrantingTicket = ticketGrantingTicket;
	}

	public void setPreviousLastTimeUsed(long previousLastTimeUsed) {
		this.previousLastTimeUsed = previousLastTimeUsed;
	}

	public void setLastTimeUsed(long lastTimeUsed) {
		this.lastTimeUsed = lastTimeUsed;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public void setCountOfUses(int countOfUses) {
		this.countOfUses = countOfUses;
	}

}
