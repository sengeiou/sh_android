package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.WatchMapper;
import gm.mobi.android.db.objects.WatchEntity;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class WatchManager extends AbstractManager{


    @Inject WatchMapper watchMapper;

    @Inject
    public WatchManager(WatchMapper watchMapper) {
        this.watchMapper = watchMapper;
    }


    public void saveWatches(List<WatchEntity> watches){
        long res = 0;
        for (WatchEntity watchEntity : watches) {
            ContentValues contentValues = watchMapper.toContentValues(watchEntity);
            if (contentValues.getAsLong(GMContract.WatchTable.CSYS_DELETED) != null) {
                res = deleteWatch(watchEntity);
            } else {
                res = db.insertWithOnConflict(GMContract.WatchTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Timber.d("Shot inserted with result: %d", res);
            }
            insertInSync();
        }
    }

    private long deleteWatch(WatchEntity watchEntity){
        long res = 0;
        String args = GMContract.WatchTable.ID_USER + "=? AND "+ GMContract.WatchTable.ID_MATCH+"=?";
        String[] stringArgs = new String[]{String.valueOf(watchEntity.getIdUser()), String.valueOf(watchEntity.getIdMatch())};
        Cursor c = db.query(GMContract.WatchTable.TABLE, GMContract.WatchTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(GMContract.WatchTable.TABLE, args, stringArgs);
        }
        c.close();
        return res;
    }


    public void insertInSync(){
        insertInTableSync(GMContract.WatchTable.TABLE,7,1000,0);
    }
}
