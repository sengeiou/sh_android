package com.shootr.android.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.inject.Inject;
import timber.log.Timber;

public class BitmapImageResizer implements ImageResizer {

    private static final int MAX_SIZE = 960;
    public static final String OUTPUT_IMAGE_NAME = "profileUploadResized.jpg";

    private final Context context;

    @Inject public BitmapImageResizer(Application application) {
        this.context = application.getApplicationContext();
    }

    @Override public File getResizedCroppedImageFile(File originalImageFile) throws IOException {
        Bitmap originalImage = getBitmapFromFile(originalImageFile);

        Bitmap orientedImage = correctImageRotationIfNeeded(originalImage, originalImageFile.getAbsolutePath());
        if (orientedImage != originalImage) {
            originalImage.recycle();
            originalImage = null;
        }

        Timber.d("Cropping image...");
        Bitmap squareImage = cropImage(orientedImage);
        orientedImage.recycle();
        orientedImage = null;

        Timber.d("Scaling image to %d px...", MAX_SIZE);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(squareImage, MAX_SIZE, MAX_SIZE, true);
        squareImage.recycle();
        squareImage = null;

        Timber.d("Storing image in file...");
        File finalImageFile = storeImageInFile(bitmapResized);
        bitmapResized.recycle();
        bitmapResized = null;

        Timber.d("Image resizing complete. Output file: %s", finalImageFile.getAbsolutePath());
        return finalImageFile;
    }

    private Bitmap correctImageRotationIfNeeded(Bitmap originalImage, String imagePath) throws IOException {
        ExifInterface exif = new ExifInterface(imagePath);
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        boolean imageIsRotatedWithExif = rotation != 0f;
        if (imageIsRotatedWithExif) {
            int rotationInDegrees = exifToDegrees(rotation);
            return rotateImage(originalImage, rotationInDegrees);
        } else {
            return originalImage;
        }
    }

    private Bitmap rotateImage(Bitmap originalImage, int rotationInDegrees) {
        Timber.d("Rotating image %d degrees...", rotationInDegrees);
        Matrix matrix = new Matrix();
        matrix.preRotate(rotationInDegrees);
        return Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), matrix,
          true);
    }

    private int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private File storeImageInFile(Bitmap bitmapResized) throws IOException {
        File imageFile = new File(getOutputDirectory(), getOutputImageName());
        OutputStream out;
        out = new FileOutputStream(imageFile);
        bitmapResized.compress(Bitmap.CompressFormat.JPEG, 80, out);
        out.flush();
        out.close();
        return imageFile;
    }

    private String getOutputImageName() {
        return OUTPUT_IMAGE_NAME;
    }

    private File getOutputDirectory() {
        return context.getExternalFilesDir("tmp");
    }

    private Bitmap getBitmapFromFile(File imageFile) {
        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        bmpOptions.inJustDecodeBounds = false;
        bmpOptions.inSampleSize = 4; //TODO calculate on runtime
        bmpOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmpOptions);
    }

    private Bitmap cropImage(Bitmap originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        Bitmap bitmapSquare;
        if (height > width) {
            bitmapSquare = Bitmap.createBitmap(originalImage, 0, height / 2 - width / 2, width, width);
        } else {
            bitmapSquare = Bitmap.createBitmap(originalImage, width / 2 - height / 2, 0, height, height);
        }
        return bitmapSquare;
    }
}
