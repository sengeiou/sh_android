package com.shootr.mobile.domain.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PhotoService {

    String uploadProfilePhotoAndGetUrl(File photoFile);

    List<String> uploadShotImageAndGetUrl(File imageFile) throws IOException;

    String uploadStreamImageAndGetIdMedia(File imageFile, String idEvent) throws IOException;
}
