


import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;

public class TestCAS {

	public static void main(String[] args){
		AccessControlUtil accessControlUtil = AccessControlUtil.getRestInstance();
		OAuthorizationCredentials credential = accessControlUtil.login("nmqhoan", "nmqhoan");
//		assertNotNull(credential);
		System.out.println(credential.getUserId());
	}
}
