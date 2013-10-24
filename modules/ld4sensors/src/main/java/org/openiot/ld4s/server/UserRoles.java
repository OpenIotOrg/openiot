package org.openiot.ld4s.server;

import org.restlet.data.ClientInfo;
import org.restlet.security.Enroler;

public class UserRoles implements Enroler{

	@Override
	public void enrole(ClientInfo clientInfo) {
        if ("scott".equals(clientInfo.getUser().getIdentifier())) {
            clientInfo.getRoles().add(ServerProperties.PUBLISHER);
        } else if ("admin".equals(clientInfo.getUser().getIdentifier())) {
            clientInfo.getRoles().add(ServerProperties.ADMINISTRATOR);
        } else if ("anonym".equals(clientInfo.getUser().getIdentifier())) {
            clientInfo.getRoles().add(ServerProperties.ANONYMOUS);
        }		
	}

}
