package com.shootr.android.domain.service.user;

import com.shootr.android.domain.exception.InvalidCheckinException;

public interface CheckinGateway {

    void performCheckin(String idUser, String idEvent) throws InvalidCheckinException;
}
