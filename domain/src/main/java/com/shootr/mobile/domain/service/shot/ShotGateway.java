package com.shootr.mobile.domain.service.shot;

import com.shootr.mobile.domain.model.shot.Shot;
import java.io.IOException;

public interface ShotGateway {

    Shot embedVideoInfo(Shot originalShot) throws IOException;
}
