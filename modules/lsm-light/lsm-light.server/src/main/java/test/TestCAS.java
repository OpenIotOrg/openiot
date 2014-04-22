package test;

import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;

public class TestCAS {

	/**
	 * @param args
	 */
	public static void main(String[] args){
		AccessControlUtil accessControlUtil = AccessControlUtil.getRestInstance();
		OAuthorizationCredentials lsmCredential = accessControlUtil.login("nmqhoan", "nmqhoan");
		accessControlUtil.getOAuthorizationCredentials();
		OAuthorizationCredentials lsmCredential2 = AccessControlUtil.getRestInstance().getOAuthorizationCredentials();
		System.out.println(lsmCredential.getUserId());
		
	}

}
