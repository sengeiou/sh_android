package gm.mobi.android.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.constant.SyncConstants;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.dataservice.dto.TimelineDtoFactory;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.jobs.timeline.TimelineJob;
import gm.mobi.android.util.RemoveUtils;
import timber.log.Timber;

public class GMSyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver mContentResolver;
    @Inject
    SQLiteOpenHelper mDbHelper;
    /**
     * Compatibility with versions previous to 3.0
     */
    public GMSyncAdapter(Context context, boolean autoInitialize){
        super(context,autoInitialize);
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
    public void setup(Context context){
        /* no-op */
        mContentResolver = context.getContentResolver();
        GolesApplication.get(context).inject(this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // Big boys stuff
        Timber.e("Entra en onPerformSync");
        try{
            synchronized (this){
                int callType = extras.getInt(SyncConstants.CALL_TYPE);
                if(callType == 0) return;
                if(callType == SyncConstants.REMOVE_OLD_SHOTS){
                    Timber.e("Entra en la sincro para hacer el remove");
                    RemoveUtils.removeOldShots(mDbHelper.getReadableDatabase());
                    Timber.e("Entra en la sincronización para el tipo de llamada : %d",callType);
                }
            }
        }catch(IllegalStateException e){
            Timber.e("Exception onPerformSync",e.getMessage());
        }

    }
}
