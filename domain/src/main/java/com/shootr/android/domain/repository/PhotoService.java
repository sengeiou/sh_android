package com.shootr.android.domain.repository;

import java.io.File;
import java.io.IOException;

public interface PhotoService {

    public String uploadProfilePhotoAndGetUrl(File photoFile) throws IOException;

    String uploadShotImageAndGetUrl(File imageFile) throws IOException;

    String uploadEventImageAndGetUrl(File imageFile, Long idEvent) throws IOException;
}
