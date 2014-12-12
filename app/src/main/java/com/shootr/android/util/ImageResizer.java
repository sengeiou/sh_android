package com.shootr.android.util;

import java.io.File;
import java.io.IOException;

public interface ImageResizer {

    public File getResizedCroppedImageFile(File originalImageFile) throws IOException;

    File getResizedImageFile(File originalImageFile) throws IOException;
}
