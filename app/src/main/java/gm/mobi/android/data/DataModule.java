package gm.mobi.android.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

@Module(
        complete = false,
        library = true
)
public class DataModule {
    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("bagdad", MODE_PRIVATE);
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application app) {
        return new Picasso.Builder(app)
//                .downloader(new OkHttpDownloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Timber.e(e, "Failed to load image: %s", uri);
                    }
                })
                .build();
    }
}
