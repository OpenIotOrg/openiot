package org.openiot.security.oauth.lsm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;
import javax.validation.constraints.NotNull;

import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.ServiceTicketImpl;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.registry.AbstractDistributedTicketRegistry;
import org.springframework.transaction.annotation.Transactional;

public class LSMTicketRegistry extends AbstractDistributedTicketRegistry {

	private String ticketGrantingTicketPrefix = "TGT";

	protected void updateTicket(final Ticket ticket) {
		/********************************
		 * To be retrieved from LSM *
		 ********************************/

		if (ticket instanceof LSMTicketGrantingTicketImpl) {
			// TODO: Update the ticket instance
		} else {
			// TODO: Update the ticket instance
		}

		log.debug("Updated ticket [{}].", ticket);
	}

	public void addTicket(final Ticket ticket) {
		/********************************
		 * To be retrieved from LSM *
		 ********************************/

		if (ticket instanceof LSMTicketGrantingTicketImpl) {
			// TODO: Save the ticket instance
		} else {
			// TODO: Save the ticket instance
		}

		log.debug("Added ticket [{}] to registry.", ticket);
	}

	public boolean deleteTicket(final String ticketId) {
		final Ticket ticket = getRawTicket(ticketId);

		if (ticket == null) {
			return false;
		}

		if (ticket instanceof ServiceTicket) {
			removeTicket(ticket);
			log.debug("Deleted ticket [{}] from the registry.", ticket);
			return true;
		}

		deleteTicketAndChildren(ticket);
		log.debug("Deleted ticket [{}] and its children from the registry.", ticket);
		return true;
	}

	private void deleteTicketAndChildren(final Ticket ticket) {
		/********************************
		 * To be retrieved from LSM *
		 ********************************/
		final List<TicketGrantingTicketImpl> ticketGrantingTicketImpls = null;
		// TODO: Retrieve the tickets similarly to the following query

		// final List<TicketGrantingTicketImpl> ticketGrantingTicketImpls =
		// entityManager
		// .createQuery("select t from TicketGrantingTicketImpl t where t.ticketGrantingTicket.id = :id",
		// TicketGrantingTicketImpl.class)
		// .setLockMode(LockModeType.PESSIMISTIC_WRITE).setParameter("id",
		// ticket.getId()).getResultList();

		/********************************
		 * To be retrieved from LSM *
		 ********************************/
		final List<ServiceTicketImpl> serviceTicketImpls = null;
		// TODO: Retrieve the tickets similarly to the following query

		// final List<ServiceTicketImpl> serviceTicketImpls = entityManager
		// .createQuery("select s from ServiceTicketImpl s where s.ticketGrantingTicket.id = :id",
		// ServiceTicketImpl.class)
		// .setParameter("id", ticket.getId()).getResultList();

		for (final ServiceTicketImpl s : serviceTicketImpls) {
			removeTicket(s);
		}

		for (final TicketGrantingTicketImpl t : ticketGrantingTicketImpls) {
			deleteTicketAndChildren(t);
		}

		removeTicket(ticket);
	}

	private void removeTicket(final Ticket ticket) {
		try {
			if (log.isDebugEnabled()) {
				final Date creationDate = new Date(ticket.getCreationTime());
				log.debug("Removing Ticket [{}] created: {}", ticket, creationDate.toString());
			}
			/********************************
			 * To be retrieved from LSM *
			 ********************************/
			// TODO: Remove the ticket

		} catch (final Exception e) {
			log.error("Error removing {} from registry.", ticket, e);
		}
	}

	public Ticket getTicket(final String ticketId) {
		return getProxiedTicketInstance(getRawTicket(ticketId));
	}

	private Ticket getRawTicket(final String ticketId) {
		try {
			if (ticketId.startsWith(this.ticketGrantingTicketPrefix)) {
				/********************************
				 * To be retrieved from LSM *
				 ********************************/
				// TODO: Retrieve TicketGrantingTicket similarly to the
				// following

//				return entityManager.find(TicketGrantingTicketImpl.class, ticketId, LockModeType.PESSIMISTIC_WRITE);
				return null;
			}

			/********************************
			 * To be retrieved from LSM *
			 ********************************/
			// TODO: Retrieve ServiceTicket similarly to the following
			// return entityManager.find(ServiceTicketImpl.class, ticketId);

			return null;
		} catch (final Exception e) {
			log.error("Error getting ticket {} from registry.", ticketId, e);
		}
		return null;
	}

	public Collection<Ticket> getTickets() {
		final List<TicketGrantingTicketImpl> tgts = null;
		/********************************
		 * To be retrieved from LSM *
		 ********************************/
		// TODO: Retrieve TicketGrantingTickets similarly to the following
		
//		final List<TicketGrantingTicketImpl> tgts = entityManager.createQuery("select t from TicketGrantingTicketImpl t", TicketGrantingTicketImpl.class)
//				.getResultList();
		
		final List<ServiceTicketImpl> sts = null;
		/********************************
		 * To be retrieved from LSM *
		 ********************************/
		// TODO: Retrieve ServiceTickets similarly to the following
		
//		final List<ServiceTicketImpl> sts = entityManager.createQuery("select s from ServiceTicketImpl s", ServiceTicketImpl.class).getResultList();

		final List<Ticket> tickets = new ArrayList<Ticket>();
		tickets.addAll(tgts);
		tickets.addAll(sts);

		return tickets;
	}

	public void setTicketGrantingTicketPrefix(final String ticketGrantingTicketPrefix) {
		this.ticketGrantingTicketPrefix = ticketGrantingTicketPrefix;
	}

	@Override
	protected boolean needsCallback() {
		return false;
	}

	public int sessionCount() {
		/********************************
		 * To be retrieved from LSM *
		 ********************************/
		// TODO: Perform the corresponding query
//		return countToInt(entityManager.createQuery("select count(t) from TicketGrantingTicketImpl t").getSingleResult());
		
		return -1;
	}

	public int serviceTicketCount() {
		/********************************
		 * To be retrieved from LSM *
		 ********************************/
		// TODO: Perform the corresponding query
//		return countToInt(entityManager.createQuery("select count(t) from ServiceTicketImpl t").getSingleResult());
		
		return -1;
	}

	private int countToInt(final Object result) {
		final int intval;
		if (result instanceof Long) {
			intval = ((Long) result).intValue();
		} else if (result instanceof Integer) {
			intval = (Integer) result;
		} else {
			// Must be a Number of some kind
			intval = ((Number) result).intValue();
		}
		return intval;
	}

}
