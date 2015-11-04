package com.shootr.mobile.domain.repository;

import java.io.File;
import java.io.IOException;

public interface PhotoService {

    String uploadProfilePhotoAndGetUrl(File photoFile);

    String uploadShotImageAndGetUrl(File imageFile) throws IOException;

    String uploadStreamImageAndGetUrl(File imageFile, String idEvent) throws IOException;
}
