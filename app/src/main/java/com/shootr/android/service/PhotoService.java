package com.shootr.android.service;

import android.graphics.Bitmap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.json.JSONException;

public interface PhotoService {

    public String uploadProfilePhotoAndGetUrl(File photoFile) throws IOException, JSONException;

}
