package test.com.fav24.dataservices.xml.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.xml.security.AccessPolicyDOM;


public class AccessPolicyDOMTest {
	
	@Test
	public void loadPolicyExample() {
		
		AccessPolicyDOM accessPolicy = null;
		
		try {
			accessPolicy = new AccessPolicyDOM(getClass().getResource("gm-entidades-base.xml"));
		} catch (ServerException e) {
			fail(e.getMessage());
		}
		
		assertNotNull(accessPolicy);
	}
}
