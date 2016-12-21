package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.model.shot.Sendable;
import java.io.File;

public interface MessageSender {

    void sendMessage(Sendable shot, File shotImage);

}
