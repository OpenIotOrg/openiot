package org.openiot.security.oauth.lsm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.registry.AbstractDistributedTicketRegistry;
import org.openiot.lsm.security.oauth.LSMServiceTicketImpl;
import org.openiot.lsm.security.oauth.LSMTicketGrantingTicketImpl;

public class LSMTicketRegistry extends AbstractDistributedTicketRegistry {

	private String ticketGrantingTicketPrefix = "TGT";
	private LSMOAuthManager manager = LSMOAuthManager.getInstance();

	protected void updateTicket(final Ticket ticket) {
		if (ticket instanceof LSMTicketGrantingTicketImpl) {
			LSMTicketGrantingTicketImpl tgt = (LSMTicketGrantingTicketImpl) ticket;
			manager.deleteTicketGranting(tgt.getId());
			manager.addTicketGrangtingTicket(tgt);
		} else {
			LSMServiceTicketImpl serviceTicket = (LSMServiceTicketImpl) ticket;
			manager.deleteServiceTicketImpl(serviceTicket.getId());
			manager.addServiceTicketImpl(serviceTicket);
		}

		log.debug("Updated ticket [{}].", ticket);
	}

	public void addTicket(final Ticket ticket) {
		boolean added = true;
		if (ticket instanceof LSMTicketGrantingTicketImpl) {
			LSMTicketGrantingTicketImpl tgt = (LSMTicketGrantingTicketImpl) ticket;
			if (manager.getTicketGranting(tgt.getId()) == null)
				manager.addTicketGrangtingTicket(tgt);
			else
				added = false;

		} else {
			LSMServiceTicketImpl serviceTicket = (LSMServiceTicketImpl) ticket;
			if (manager.getServiceTicketImpl(serviceTicket.getId()) == null)
				manager.addServiceTicketImpl(serviceTicket);
			else
				added = false;
		}

		if (added)
			log.debug("Added ticket [{}] to registry.", ticket);
		else
			log.debug("Ticket [{}] cannot be added to the registery. It already exists.", ticket);
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
		final List<LSMTicketGrantingTicketImpl> ticketGrantingTicketImpls = manager.getAllTicketsOfTicketGrantingTicket(ticket.getId());

		final List<LSMServiceTicketImpl> serviceTicketImpls = manager.getAllServiceTicketsOfTicketGrantingTicket(ticket.getId());

		for (final LSMServiceTicketImpl s : serviceTicketImpls) {
			removeTicket(s);
		}

		for (final LSMTicketGrantingTicketImpl t : ticketGrantingTicketImpls) {
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

			if (ticket instanceof LSMTicketGrantingTicketImpl)
				manager.deleteTicketGranting(ticket.getId());
			else
				manager.deleteServiceTicketImpl(ticket.getId());

		} catch (final Exception e) {
			log.error("Error removing {} from registry.", ticket, e);
		}
	}

	public Ticket getTicket(final String ticketId) {
		return getProxiedTicketInstance(getRawTicket(ticketId));
	}

	private Ticket getRawTicket(final String ticketId) {
		try {
			if (ticketId.startsWith(this.ticketGrantingTicketPrefix))
				return manager.getTicketGranting(ticketId);
			else
				return manager.getServiceTicketImpl(ticketId);
		} catch (final Exception e) {
			log.error("Error getting ticket {} from registry.", ticketId, e);
		}
		return null;
	}

	public Collection<Ticket> getTickets() {
		final List<LSMTicketGrantingTicketImpl> tgts = manager.getAllTicketGrantingTickets();

		final List<LSMServiceTicketImpl> sts = manager.getAllServiceTickets();

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

		return manager.getTicketGrantingTicketsCount();
	}

	public int serviceTicketCount() {
		return manager.getServiceTicketsCount();
	}

}
