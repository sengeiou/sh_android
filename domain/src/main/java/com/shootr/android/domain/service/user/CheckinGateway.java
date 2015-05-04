package com.shootr.android.domain.service.user;

import java.io.IOException;

public interface CheckinGateway {

    void performCheckin(String idUser, String idEvent) throws IOException;
}
