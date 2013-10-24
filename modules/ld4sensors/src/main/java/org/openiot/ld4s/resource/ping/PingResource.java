package org.openiot.ld4s.resource.ping;

import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.restlet.resource.Get;
import org.restlet.security.Role;


/**
 * The PingResource responds to a GET {host}/ping with the string "LD4Sensors" if the user 
 * is not authenticated. 
 * It responds to GET {host}/ping with "LD4Sensors authenticated as ..." if the
 * user and password are valid; while the Unauthorized HTTP status is triggered if not valid.
 *
 * @author Myriam Leggieri <iammyr@email.com>
 */
public class PingResource extends LD4SDataResource {




	@Get
	public String toText() {
		// List the role(s) of the current user.
		StringBuilder sb = new StringBuilder();
		if (user != null){
			sb.append(LD4SConstants.AUTHENTICATED_PINGTEXT);
			if (roles != null && !roles.isEmpty()) {
//				if (roles.size() == 1) {
//					sb.append(" Here is your role => ");
//				} else {
//					sb.append(" Here are your roles => ");
//				}

				for (Role role : roles) {
					sb.append(role.getName());
					sb.append(" ");
				}
			}
		}else{
			sb.append(LD4SConstants.PINGTEXT);
		}
		return sb.toString();
	}

}
