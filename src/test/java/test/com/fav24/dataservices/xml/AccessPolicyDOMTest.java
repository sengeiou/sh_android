package test.com.fav24.dataservices.xml;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.xml.AccessPolicyDOM;


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
