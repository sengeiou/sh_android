package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ShootrServerException;
import java.io.IOException;

public interface ShotGateway {

    Shot embedVideoInfo(Shot originalShot) throws IOException;
}
