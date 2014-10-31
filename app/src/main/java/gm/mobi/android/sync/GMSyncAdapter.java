package gm.mobi.android.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import com.path.android.jobqueue.JobManager;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.constant.SyncConstants;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.UserEntity;
import javax.inject.Inject;
import timber.log.Timber;

public class GMSyncAdapter extends AbstractThreadedSyncAdapter {

    @Inject SQLiteOpenHelper mDbHelper;
    @Inject JobManager jobManager;
    @Inject UserManager userManager;
    @Inject InfoCleaner infoCleaner;

    /**
     * Compatibility with versions previous to 3.0
     */
    public GMSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        setup(context);
    }

    /**
     * Android > 3.0
     */
    public GMSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        setup(context);
    }

    /**
     * Any initial setup tasks
     */
    public void setup(Context context) {
        /* no-op */
        GolesApplication.get(context).inject(this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {

        Timber.e("Entra en onPerformSync");

        try {
            synchronized (this) {
                infoCleaner.clean();
            }
        } catch (IllegalStateException e) {
            Timber.e("Exception onPerformSync", e.getMessage());
        }
    }

}
