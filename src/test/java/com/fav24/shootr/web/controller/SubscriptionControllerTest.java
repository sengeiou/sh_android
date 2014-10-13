package com.fav24.shootr.web.controller;


public class SubscriptionControllerTest extends BaseControllerTest {

	//private String endPoint = "/manageSubscription";
	
	/*@Test
	public void testNewDevice() throws Exception {
		String uri = baseUrl+endPoint;
		ManageSubscription to = new ManageSubscription();
		to.setIdDevice(3381508l);
		to.setHash(DigestUtils.md5Hex("pastagana"+to.getIdDevice()));
		to.setIdAllEvents(4194302l);
		to.setIdTeam(15l);
		to.setIdSML(10l);
		to.setNegation(0);
		
		BaseDTO dto = new BaseDTO();
		dto.setJsonAttributes(ObjectConverter.toJson(to));
		dto.setService(contextPath + endPoint);
		dto.setStatus(new Status());
		
		ResponseEntity<BaseDTO> entity = null;
		ManageSubscription response = null;
		BaseDTO responseBaseDTO = null;
		try { 
			entity = restTemplate.postForEntity(uri, dto, BaseDTO.class);
			responseBaseDTO = entity.getBody();
			response = ObjectConverter.fromJson(responseBaseDTO.getJsonAttributes(), ManageSubscription.class);
		} catch (Exception e){
			fail(e.getMessage());
		}
		
		System.out.println(responseBaseDTO + " --> "+ response);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertNotNull(response);
		assertNotNull(response.getIdSubscription());
		assertEquals(to.getIdAllEvents(), response.getIdAllEvents());
	}
	*/
}
