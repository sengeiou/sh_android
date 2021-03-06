package com.shootr.mobile.domain.utils;

import java.io.File;
import java.io.IOException;

public interface ImageResizer {

    File getResizedCroppedImageFile(File originalImageFile) throws IOException;

    File getResizedImageFile(File originalImageFile) throws IOException;

    void setFromProfile(boolean fromProfile);
}
