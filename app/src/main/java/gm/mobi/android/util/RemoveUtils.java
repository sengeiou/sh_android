package gm.mobi.android.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import javax.inject.Inject;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.GeneralManager;
import gm.mobi.android.db.manager.SyncTableManager;
import timber.log.Timber;

public class RemoveUtils {

    public static boolean removeOldShots(SQLiteDatabase db){
        boolean res = true;
        long number;
        int maxNumRows = SyncTableManager.getNumMaxOfRowsByEntity(db, GMContract.ShotTable.TABLE);
        long currentNumRows = GeneralManager.numberOfRows(db, GMContract.ShotTable.TABLE);
        if(maxNumRows<currentNumRows){
            //We have to delete the older ones
            number = currentNumRows-maxNumRows;
            int resRemoved = deleteRows(db,number);
            Timber.e("Borrar los más antiguos. Borrados: %d",resRemoved);
        }else{
            Timber.e("El numero de filas en la tabla Shot es menor que el número máximo");
        }
        return res;
    }

    private static int deleteRows(SQLiteDatabase db, long number){
        String sql = "SELECT min(idShot) as idShot FROM (SELECT "+GMContract.ShotTable.ID_SHOT +" FROM "+ GMContract.ShotTable.TABLE+" order by "+ GMContract.ShotTable.ID_SHOT+" DESC LIMIT 10)";
        int idShot = 0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            idShot = c.getInt(c.getColumnIndex(GMContract.ShotTable.ID_SHOT));
            Log.e("IDSHOT",String.valueOf(idShot));
        }
        c.close();
        return db.delete(GMContract.ShotTable.TABLE, GMContract.ShotTable.ID_SHOT+"<"+idShot,null);

    }

}

