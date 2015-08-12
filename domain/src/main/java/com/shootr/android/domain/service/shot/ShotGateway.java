package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.Shot;
import java.io.IOException;

public interface ShotGateway {

    Shot embedVideoInfo(Shot originalShot) throws IOException;

    void markNiceShot(String idShot);

    void unmarkNiceShot(String idShot);
}
