package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.db.DatabaseContract;
import java.util.Date;
import java.util.Map;

public abstract class GenericMapper {

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

    protected void setSynchronizedfromDto(Map<String, Object> dto, Synchronized s) {
        Number date =  (Number)dto.get(DatabaseContract.SyncColumns.BIRTH);
        s.setBirth(date != null ? new Date(date.longValue()) : null);

        date = (Number) dto.get(DatabaseContract.SyncColumns.DELETED);
        s.setDeleted(date != null ? new Date(date.longValue()) : null);

        date = (Number) dto.get(DatabaseContract.SyncColumns.MODIFIED);
        s.setModified(date != null ? new Date(date.longValue()) : null);

        Number revision = (Number) dto.get(DatabaseContract.SyncColumns.REVISION);
        s.setRevision(revision.intValue());
    }

    protected void setSynchronizedtoDto(Synchronized s, Map<String, Object> dto) {
        dto.put(DatabaseContract.SyncColumns.BIRTH, s == null ? null : s.getBirth());
        dto.put(DatabaseContract.SyncColumns.DELETED, s == null ? null : s.getDeleted());
        dto.put(DatabaseContract.SyncColumns.MODIFIED, s == null ? null : s.getModified());
        dto.put(DatabaseContract.SyncColumns.REVISION, s == null ? null : s.getRevision());
    }
}
