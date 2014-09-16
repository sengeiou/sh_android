package gm.mobi.android.service;

import gm.mobi.android.db.objects.User;

public class BagdadMockService implements BagdadService {

    @Override
    public User login(String id, String password) {
        if (id.equals("rafa.vazsan@gmail.com") || id.equals("sloydev")) {
            User mockUser = new User();
            mockUser.setId(1);
            mockUser.setName("Rafa");
            mockUser.setSessionToken("Nnananananananana");
            mockUser.setEmail("rafa.vazsan@gmail.com");
            mockUser.setUserName("rafavazsan");
            mockUser.setFavouriteTeamId(1);
            mockUser.setPhoto("http://example.com");
            return mockUser;
        }
        return null;
    }
}
