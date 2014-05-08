

import org.openiot.lsm.server.LSMTripleStore;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;

public class TestCAS {
	public static LSMTripleStore lsmStore = new LSMTripleStore("http://localhost:8080/lsm-light.server/");
	public static void main(String[] args){
		AccessControlUtil accessControlUtil = AccessControlUtil.getRestInstance();
		OAuthorizationCredentials credential = accessControlUtil.login("lsm-light.client", "nmqhoan");
		accessControlUtil.getOAuthorizationCredentials();
		String token = credential.getAccessToken();
		String clientId = credential.getClientId();
		System.out.println("clientId:"+clientId);
		System.out.println("token:"+token);
		String triples = "<http://lsm.deri.ie/resource/138>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Property>.";
    	lsmStore.pushRDF("http://test/sensormeta#", triples, clientId, token);
	}
}
