package gm.mobi.android.service;

import java.io.IOException;

import gm.mobi.android.db.objects.User;

public interface BagdadService {

    public User login(String id, String password) throws IOException;


}
