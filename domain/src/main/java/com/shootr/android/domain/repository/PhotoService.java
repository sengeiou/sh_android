package com.shootr.android.domain.repository;

import java.io.File;
import java.io.IOException;
import org.json.JSONException;

public interface PhotoService {

    public String uploadProfilePhotoAndGetUrl(File photoFile) throws IOException, JSONException;

    String uploadShotImageAndGetUrl(File imageFile) throws IOException;
}
