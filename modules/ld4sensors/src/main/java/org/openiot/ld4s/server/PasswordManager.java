package org.openiot.ld4s.server;

import org.restlet.security.SecretVerifier;

public class PasswordManager extends SecretVerifier {

	@Override
	public boolean verify(String username, char[] passw)
			throws IllegalArgumentException {
		// Could check from a database (see in this case LocalVerifier).
        return (("scott".equals(username)) && compare("tiger".toCharArray(),
                passw))
                || (("admin".equals(username)) && compare("admin"
                        .toCharArray(), passw))
        || (("anonym".equals(username)) && compare("special_login"
                .toCharArray(), passw));
	}

	
}
