package com.shootr.android.task.jobs.profile;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.PhotoService;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.profile.UploadProfilePhotoEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.util.FileChooserUtils;
import com.squareup.otto.Bus;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import javax.inject.Inject;
import org.json.JSONException;
import timber.log.Timber;

public class UploadProfilePhotoJob extends ShootrBaseJob<UploadProfilePhotoEvent> {

    private static final int PRIORITY = 5;
    private final ShootrService shootrService;
    private final PhotoService photoService;
    private final UserManager userManager;
    private final SessionManager sessionManager;

    private Uri photoUri;

    @Inject public UploadProfilePhotoJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService shootrService, PhotoService photoService,
      UserManager userManager, SessionManager sessionManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.shootrService = shootrService;
        this.photoService = photoService;
        this.userManager = userManager;
        this.sessionManager = sessionManager;
    }

    public void init(Uri uri) {
        photoUri = uri;
    }

    @Override protected void run() throws SQLException, IOException, Exception {
        File newPhotoFile = new File(FileChooserUtils.getPath(getContext(), photoUri));
        File imageFile = getResizedImage(newPhotoFile);

        String photoUrl = uploadPhoto(imageFile);
        setCurrentUserPhoto(photoUrl);
    }

    private void setCurrentUserPhoto(String photoUrl) throws IOException {
        UserEntity currentUser = sessionManager.getCurrentUser();
        currentUser.setPhoto(photoUrl);
        userManager.saveUser(currentUser);
        shootrService.saveUserProfile(currentUser);
    }

    private String uploadPhoto(File imageFile) throws IOException, JSONException {
        return photoService.uploadPhotoAndGetUrl(imageFile);
    }

    private File getResizedImage(File newPhotoFile) {
        // Resize
        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        bmpOptions.inJustDecodeBounds = false;
        bmpOptions.inSampleSize = 4;
        bmpOptions.inPurgeable = true;

        Bitmap bitmapLoaded = BitmapFactory.decodeFile(newPhotoFile.getAbsolutePath(), bmpOptions);
        int width = bitmapLoaded.getWidth();
        int height = bitmapLoaded.getHeight();
        int MAX_SIZE = 960;
        Bitmap bitmapSquare;
        if (height > width) {
            bitmapSquare = Bitmap.createBitmap(bitmapLoaded, 0, height / 2 - width / 2, width, width);
        } else {
            bitmapSquare = Bitmap.createBitmap(bitmapLoaded, width/ 2 - height/ 2, 0, height, height);
        }

        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmapSquare, MAX_SIZE, MAX_SIZE, false);

        // Store
        File imageFile = new File(getContext().getExternalFilesDir("tmp") + "profileUploadResized.jpg");
        try {
            OutputStream out;
            out = new FileOutputStream(imageFile);
            bitmapResized.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Timber.e(e, "Error storing resized bitmap");
        }
        return imageFile;
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
