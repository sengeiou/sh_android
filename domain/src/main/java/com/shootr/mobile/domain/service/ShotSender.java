package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.Shot;

import java.io.File;

public interface ShotSender {

    void sendShot(Shot shot, File shotImage);
}
