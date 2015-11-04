package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.db.DatabaseContract;
import java.util.Date;

public abstract class GenericDBMapper {

    protected void setSynchronizedfromCursor(Cursor c, Synchronized s) {
        long birthDate = c.getLong(c.getColumnIndex(DatabaseContract.SyncColumns.BIRTH));
        s.setBirth(birthDate != 0L ? new Date(birthDate) : null);

        long deletedDate = c.getLong(c.getColumnIndex(DatabaseContract.SyncColumns.DELETED));
        s.setDeleted(deletedDate != 0L ? new Date(deletedDate) : null);

        long modifiedDate = c.getLong(c.getColumnIndex(DatabaseContract.SyncColumns.MODIFIED));
        s.setModified(modifiedDate != 0L ? new Date(modifiedDate) : new Date(birthDate));

        s.setRevision(c.getInt(c.getColumnIndex(DatabaseContract.SyncColumns.REVISION)));
        s.setSynchronizedStatus(c.getString(c.getColumnIndex(DatabaseContract.SyncColumns.SYNCHRONIZED)));
    }

    protected void setSynchronizedtoContentValues(Synchronized s, ContentValues cv) {
        Date birth = s.getBirth();
        cv.put(DatabaseContract.SyncColumns.BIRTH, birth!=null ? birth.getTime() : null);
        Date deleted = s.getDeleted();
        cv.put(DatabaseContract.SyncColumns.DELETED, deleted!=null ? deleted.getTime() : null);
        Date modified = s.getModified();
        if (modified != null || birth != null) {
            cv.put(DatabaseContract.SyncColumns.MODIFIED, modified!=null ? modified.getTime() : birth.getTime());
        }

        cv.put(DatabaseContract.SyncColumns.REVISION, s.getRevision());
        cv.put(DatabaseContract.SyncColumns.SYNCHRONIZED, s.getSynchronizedStatus());
    }
}
