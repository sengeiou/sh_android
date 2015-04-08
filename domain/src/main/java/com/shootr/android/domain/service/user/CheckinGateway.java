package com.shootr.android.domain.service.user;

public interface CheckinGateway {

    void performCheckin(Long idUser, Long idEvent);
}
