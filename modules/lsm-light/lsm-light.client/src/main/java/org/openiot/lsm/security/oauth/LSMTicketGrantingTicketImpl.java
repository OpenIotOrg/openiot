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

package org.openiot.lsm.security.oauth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.ExpirationPolicy;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class LSMTicketGrantingTicketImpl extends LSMAbstractTicket implements TicketGrantingTicket,java.io.Serializable {

	/** Unique Id for serialization. */
    private static final long serialVersionUID = -5197946718924166491L;

    private static final Logger LOG = LoggerFactory.getLogger(LSMTicketGrantingTicketImpl.class);

    /** The authenticated object for which this ticket was generated for. 
    @Lob
    @Column(name="AUTHENTICATION", nullable=false)
    */
    private Authentication authentication;

    /** Flag to enforce manual expiration. 
    @Column(name="EXPIRED", nullable=false)
    */
    private Boolean expired = false;
    
    /**
    @Lob
    @Column(name="SERVICES_GRANTED_ACCESS_TO", nullable=false)
    */
    private HashMap<String,Service> services = new HashMap<String, Service>();
    
    public LSMTicketGrantingTicketImpl() {
        // nothing to do
    }

    /**
     * Constructs a new TicketGrantingTicket.
     * 
     * @param id the id of the Ticket
     * @param ticketGrantingTicket the parent ticket
     * @param authentication the Authentication request for this ticket
     * @param policy the expiration policy for this ticket.
     * @throws IllegalArgumentException if the Authentication object is null
     */
    public LSMTicketGrantingTicketImpl(final String id,
        final LSMTicketGrantingTicketImpl ticketGrantingTicket,
        final Authentication authentication, final ExpirationPolicy policy) {
        super(id, ticketGrantingTicket, policy);
        
        Assert.notNull(authentication, "authentication cannot be null");
        this.authentication = authentication;
    }

    /**
     * Constructs a new TicketGrantingTicket without a parent
     * TicketGrantingTicket.
     * 
     * @param id the id of the Ticket
     * @param authentication the Authentication request for this ticket
     * @param policy the expiration policy for this ticket.
     */
    public LSMTicketGrantingTicketImpl(final String id,
        final Authentication authentication, final ExpirationPolicy policy) {
        this(id, null, authentication, policy);
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }

    
    public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	public synchronized ServiceTicket grantServiceTicket(final String id,
        final Service service, final ExpirationPolicy expirationPolicy,
        final boolean credentialsProvided) {
        final ServiceTicket serviceTicket = new LSMServiceTicketImpl(id, this,
            service, this.getCountOfUses() == 0 || credentialsProvided,
            expirationPolicy);

        updateState();
        
        final List<Authentication> authentications = getChainedAuthentications();
        service.setPrincipal(authentications.get(authentications.size()-1).getPrincipal());
        
        this.services.put(id, service);

        return serviceTicket;
    }
    
    public HashMap<String, Service> getServices() {
		return services;
	}

    public void setServices(HashMap<String, Service> services) {
		this.services = services;
	}
    
	private void logOutOfServices() {
        for (final Entry<String, Service> entry : this.services.entrySet()) {

            if (!entry.getValue().logOutOfService(entry.getKey())) {
                LOG.warn("Logout message not sent to [" + entry.getValue().getId() + "]; Continuing processing...");   
            }
        }
    }

    public boolean isRoot() {
        return this.getGrantingTicket() == null;
    }

    public synchronized void expire() {
        this.expired = true;
        logOutOfServices();
    }

    public boolean isExpiredInternal() {
        return this.expired;
    }

    public List<Authentication> getChainedAuthentications() {
        final List<Authentication> list = new ArrayList<Authentication>();

        if (this.getGrantingTicket() == null) {
            list.add(this.getAuthentication());
            return Collections.unmodifiableList(list);
        }

        list.add(this.getAuthentication());
        list.addAll(this.getGrantingTicket().getChainedAuthentications());

        return Collections.unmodifiableList(list);
    }
    
    public final boolean equals(final Object object) {
        if (object == null
            || !(object instanceof TicketGrantingTicket)) {
            return false;
        }

        final Ticket ticket = (Ticket) object;
        
        return ticket.getId().equals(this.getId());
    }

}
