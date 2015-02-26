package com.shootr.android.domain.service;

import com.shootr.android.domain.Shot;
import java.io.File;

public interface ShotSender {

    void sendShot(Shot shot, File shotImage);
}
