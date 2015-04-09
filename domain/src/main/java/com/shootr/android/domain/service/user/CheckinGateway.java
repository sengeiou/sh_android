package com.shootr.android.domain.service.user;

import java.io.IOException;

public interface CheckinGateway {

    void performCheckin(Long idUser, Long idEvent) throws IOException;
}
