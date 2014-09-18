package org.openiot.lsm.security.oauth;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.ExpirationPolicy;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.util.Assert;

public class LSMServiceTicketImpl extends LSMAbstractTicket implements ServiceTicket, java.io.Serializable {

	/** Unique Id for serialization. */
	private static final long serialVersionUID = -4223319704861765405L;

	/**
	 * The service this ticket is valid for.
	 * 
	 */
	private Service service;

	/**
	 * Is this service ticket the result of a new login.
	 * 
	 */
	private boolean fromNewLogin;

	private Boolean grantedTicketAlready = false;

	public LSMServiceTicketImpl() {
		// exists for JPA purposes
	}

	/**
	 * Constructs a new ServiceTicket with a Unique Id, a TicketGrantingTicket, a Service,
	 * Expiration Policy and a flag to determine if the ticket creation was from a new Login or not.
	 * 
	 * @param id
	 *            the unique identifier for the ticket.
	 * @param ticket
	 *            the TicketGrantingTicket parent.
	 * @param service
	 *            the service this ticket is for.
	 * @param fromNewLogin
	 *            is it from a new login.
	 * @param policy
	 *            the expiration policy for the Ticket.
	 * @throws IllegalArgumentException
	 *             if the TicketGrantingTicket or the Service are null.
	 */
	protected LSMServiceTicketImpl(final String id, final LSMTicketGrantingTicketImpl ticket, final Service service, final boolean fromNewLogin,
			final ExpirationPolicy policy) {
		super(id, ticket, policy);

		Assert.notNull(ticket, "ticket cannot be null");
		Assert.notNull(service, "service cannot be null");

		this.service = service;
		this.fromNewLogin = fromNewLogin;
	}

	public boolean isFromNewLogin() {
		return this.fromNewLogin;
	}

	public Service getService() {
		return this.service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public void setFromNewLogin(boolean fromNewLogin) {
		this.fromNewLogin = fromNewLogin;
	}

	public boolean isValidFor(final Service serviceToValidate) {
		updateState();
		return serviceToValidate.matches(this.service);
	}

	public TicketGrantingTicket grantTicketGrantingTicket(final String id, final Authentication authentication, final ExpirationPolicy expirationPolicy) {
		synchronized (this) {
			if (this.grantedTicketAlready) {
				throw new IllegalStateException(
						"TicketGrantingTicket already generated for this ServiceTicket.  Cannot grant more than one TGT for ServiceTicket");
			}
			this.grantedTicketAlready = true;
		}

		return new LSMTicketGrantingTicketImpl(id, (LSMTicketGrantingTicketImpl) this.getGrantingTicket(), authentication, expirationPolicy);
	}

	public Authentication getAuthentication() {
		return null;
	}

	public final boolean equals(final Object object) {
		if (object == null || !(object instanceof ServiceTicket)) {
			return false;
		}

		final Ticket serviceTicket = (Ticket) object;

		return serviceTicket.getId().equals(this.getId());
	}

}
