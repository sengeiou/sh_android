package com.shootr.mobile.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import com.shootr.mobile.domain.utils.ImageResizer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.inject.Inject;
import timber.log.Timber;

public class BitmapImageResizer implements ImageResizer {

  private static final int MAX_SIZE = 640;
  private static final long MAX_WEIGHT_KB = 300;

  public static final String OUTPUT_IMAGE_NAME = "profileUploadResized.jpg";
  public static final int INITIAL_COMPRESSION_QUALITY = 100;
  public static final int COMPRESSION_QUALITY_DECREMENT = 5;

  private final Context context;

  @Inject public BitmapImageResizer(Application application) {
    this.context = application.getApplicationContext();
  }

  @Override public File getResizedCroppedImageFile(File originalImageFile) throws IOException {
    Bitmap originalImage = getBitmapFromFile(originalImageFile);

    Bitmap orientedImage =
        correctImageRotationIfNeeded(originalImage, originalImageFile.getAbsolutePath());
    if (orientedImage != originalImage) {
      originalImage.recycle();
    }

    Timber.d("Cropping image...");
    Bitmap squareImage = cropSquareImage(orientedImage);
    if (squareImage != orientedImage) {
      orientedImage.recycle();
    }

    Timber.d("Scaling image to %d px...", MAX_SIZE);
    Bitmap bitmapResized = getScaledBitmapWithMaxDimension(squareImage, MAX_SIZE);
    if (bitmapResized != squareImage) {
      squareImage.recycle();
    }

    Timber.d("Storing image in file...");
    File finalImageFile = storeCompressedImageInFile(bitmapResized);
    bitmapResized.recycle();

    Timber.d("Image resizing complete. Output file: %s", finalImageFile.getAbsolutePath());
    return finalImageFile;
  }

  @Override public File getResizedImageFile(File originalImageFile)
      throws IOException, NullPointerException {
    Bitmap originalImage = getBitmapFromFile(originalImageFile);

    Bitmap orientedImage =
        correctImageRotationIfNeeded(originalImage, originalImageFile.getAbsolutePath());
    if (orientedImage != originalImage) {
      originalImage.recycle();
    }

    Timber.d("Scaling image to %d px...", MAX_SIZE);
    Bitmap bitmapResized = getScaledBitmapWithMaxDimension(orientedImage, MAX_SIZE);
    if (bitmapResized != orientedImage) {
      orientedImage.recycle();
    }

    Timber.d("Storing image in file...");
    File finalImageFile = storeCompressedImageInFile(bitmapResized);
    bitmapResized.recycle();

    Timber.d("Image resizing complete. Output file: %s", finalImageFile.getAbsolutePath());
    return finalImageFile;
  }

  private Bitmap getScaledBitmapWithMaxDimension(Bitmap orientedImage, int maxDimensionSize)
      throws NullPointerException {
    float originalWidth = orientedImage.getWidth();
    float originalHeight = orientedImage.getHeight();
    float currentMaxDimension = originalHeight > originalWidth ? originalHeight : originalWidth;
    if (currentMaxDimension < maxDimensionSize) {
      return orientedImage;
    }
    float finalWidth;
    float finalHeight;
    boolean isSquareImage = originalHeight == originalWidth;
    if (isSquareImage) {
      finalWidth = maxDimensionSize;
      finalHeight = maxDimensionSize;
    } else {
      boolean isLandscape = originalWidth > originalHeight;
      if (isLandscape) {
        finalWidth = maxDimensionSize;
        finalHeight = originalHeight * finalWidth / originalWidth;
      } else { // is portrait
        finalHeight = maxDimensionSize;
        finalWidth = originalWidth * finalHeight / originalHeight;
      }
    }
    return Bitmap.createScaledBitmap(orientedImage, (int) finalWidth, (int) finalHeight, true);
  }

  private Bitmap correctImageRotationIfNeeded(Bitmap originalImage, String imagePath)
      throws IOException {
    ExifInterface exif = new ExifInterface(imagePath);
    int rotation =
        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
    boolean imageIsRotatedWithExif = rotation != 0f;
    if (imageIsRotatedWithExif) {
      int rotationInDegrees = exifToDegrees(rotation);
      try {
        return rotateImage(originalImage, rotationInDegrees);
      } catch (OutOfMemoryError error) {
        return originalImage;
      }
    } else {
      return originalImage;
    }
  }

  private Bitmap rotateImage(Bitmap originalImage, int rotationInDegrees) {
    Timber.d("Rotating image %d degrees...", rotationInDegrees);
    Matrix matrix = new Matrix();
    matrix.preRotate(rotationInDegrees);
    return Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(),
        originalImage.getHeight(), matrix, true);
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

  private File storeCompressedImageInFile(Bitmap bitmapResized) throws IOException {
    File imageFile = new File(getOutputDirectory(), getOutputImageName());

    int compressionQuality = INITIAL_COMPRESSION_QUALITY;
    boolean needsCompression = true;

    while (needsCompression) {
      Timber.i("Performing JPEG compression with quality of %d", compressionQuality);
      FileOutputStream compressedImageStream = new FileOutputStream(imageFile);
      bitmapResized.compress(Bitmap.CompressFormat.JPEG, compressionQuality, compressedImageStream);
      // Check size
      compressedImageStream.flush();

      long imageSizeInKb = imageFile.length() / 1000;
      Timber.i("Image size: %s KB", imageSizeInKb);
      needsCompression = imageSizeInKb > MAX_WEIGHT_KB && compressionQuality > 0;
      compressionQuality -= COMPRESSION_QUALITY_DECREMENT;
      compressedImageStream.close();
    }
    return imageFile;
  }

  private String getOutputImageName() {
    return OUTPUT_IMAGE_NAME;
  }

  private File getOutputDirectory() {
    return context.getExternalFilesDir("tmp");
  }

  private Bitmap getBitmapFromFile(File imageFile) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

    options.inSampleSize = calculateInSampleSize(options, MAX_SIZE, MAX_SIZE);

    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
  }

  private Bitmap cropSquareImage(Bitmap originalImage) {
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

  public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
      int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }
}
