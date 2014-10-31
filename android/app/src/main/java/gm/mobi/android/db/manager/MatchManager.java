package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.MatchMapper;
import gm.mobi.android.db.objects.MatchEntity;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class MatchManager extends AbstractManager{

    @Inject MatchMapper matchMapper;

    @Inject public MatchManager(MatchMapper matchMapper){
        this.matchMapper = matchMapper;
    }

    public void saveMatches(List<MatchEntity> matches){
        long res;
        for(MatchEntity matchEntity: matches){
            ContentValues contentValues = matchMapper.toContentValues(matchEntity);
            if (contentValues.getAsLong(GMContract.MatchTable.CSYS_DELETED) != null) {
                res = deleteMatch(matchEntity);
            } else {
                res = db.insertWithOnConflict(GMContract.WatchTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Timber.d("Shot inserted with result: %d", res);
            }
            insertInSync();
        }
    }

    private long deleteMatch(MatchEntity matchEntity){
        long res = 0;
        String args = GMContract.MatchTable.ID_MATCH + "=?";
        String[] stringArgs = new String[]{String.valueOf(matchEntity.getIdMatch())};
        Cursor c = db.query(GMContract.MatchTable.TABLE, GMContract.MatchTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
           res = db.delete(GMContract.MatchTable.TABLE, args, stringArgs);
        }
        c.close();
        return res;
    }


    public void insertInSync(){
        insertInTableSync(GMContract.MatchTable.TABLE,10,1000,0);
    }
}
