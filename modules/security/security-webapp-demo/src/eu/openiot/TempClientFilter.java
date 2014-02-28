package eu.openiot;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.AuthenticationException;

import io.buji.pac4j.ClientFilter;


public class TempClientFilter extends ClientFilter{

	@Override
    protected boolean onLoginFailure(final AuthenticationToken token, final AuthenticationException ae,
                                     final ServletRequest request, final ServletResponse response) {
		ae.printStackTrace();
		return super.onLoginFailure(token, ae, request, response);
	}
}
