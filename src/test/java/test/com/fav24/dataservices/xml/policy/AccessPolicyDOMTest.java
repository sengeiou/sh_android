package test.com.fav24.dataservices.xml.policy;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.xml.policy.AccessPolicyDOM;


public class AccessPolicyDOMTest {
	
	@Test
	public void canLoadPolicyFile() throws ServerException {
		
		AccessPolicyDOM accessPolicy = null;
		
		accessPolicy = new AccessPolicyDOM(getClass().getResource("gm-entidades-base.xml"));
		assertNotNull(accessPolicy);
	}
}
