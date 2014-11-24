package com.shootr.android.util;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import com.shootr.android.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.File;

public class PicassoWrapper {

    private final Picasso picasso;
    private final int defaultImageRes;

    public PicassoWrapper(Picasso picasso) {
        this.picasso = picasso;
        defaultImageRes = R.drawable.ic_contact_picture_default;
    }

    public RequestCreator load(String path) {
        boolean isValidPhoto = path != null && !path.isEmpty();
        if (isValidPhoto) {
            return picasso.load(path);
        } else {
            return loadDefaultImage();
        }
    }

    private RequestCreator loadDefaultImage() {
        return load(defaultImageRes);
    }

    public RequestCreator load(Uri uri) {
        return picasso.load(uri);
    }
    public RequestCreator load(File file) {
        return picasso.load(file);
    }

    public RequestCreator load(@DrawableRes int resource) {
        return picasso.load(resource);
    }



}
