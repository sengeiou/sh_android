package com.shootr.mobile.domain.service.shot;

import com.shootr.mobile.domain.model.shot.BaseMessage;
import java.io.IOException;

public interface ShotGateway {

    BaseMessage embedVideoInfo(BaseMessage originalShot) throws IOException;
}
