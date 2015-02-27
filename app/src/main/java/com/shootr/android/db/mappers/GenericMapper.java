package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.db.DatabaseContract.SyncColumns;
import com.shootr.android.data.entity.Synchronized;
import java.util.Date;
import java.util.Map;

public abstract class GenericMapper {

    public static final String CSYS_BIRTH = SyncColumns.CSYS_BIRTH;
    public static final String CSYS_DELETED = SyncColumns.CSYS_DELETED;
    public static final String CSYS_MODIFIED = SyncColumns.CSYS_MODIFIED;
    public static final String CSYS_REVISION = SyncColumns.CSYS_REVISION;
    public static final String CSYS_SYNCHRONIZED = SyncColumns.CSYS_SYNCHRONIZED;

    protected void setSynchronizedfromCursor(Cursor c, Synchronized s) {
        long birthDate = c.getLong(c.getColumnIndex(CSYS_BIRTH));
        s.setCsysBirth(birthDate != 0L ? new Date(birthDate) : null);

        long deletedDate = c.getLong(c.getColumnIndex(CSYS_DELETED));
        s.setCsysDeleted(deletedDate != 0L ? new Date(deletedDate) : null);

        long modifiedDate = c.getLong(c.getColumnIndex(CSYS_MODIFIED));
        s.setCsysModified(modifiedDate != 0L ? new Date(modifiedDate) : new Date(birthDate));

        s.setCsysRevision(c.getInt(c.getColumnIndex(CSYS_REVISION)));
        s.setCsysSynchronized(c.getString(c.getColumnIndex(CSYS_SYNCHRONIZED)));
    }

    protected void setSynchronizedtoContentValues(Synchronized s, ContentValues cv) {
        Date birth = s.getCsysBirth();
        cv.put(CSYS_BIRTH, birth!=null ? birth.getTime() : null);
        Date deleted = s.getCsysDeleted();
        cv.put(CSYS_DELETED, deleted!=null ? deleted.getTime() : null);
        Date modified = s.getCsysModified();
        if (modified != null || birth != null) {
            cv.put(CSYS_MODIFIED, modified!=null ? modified.getTime() : birth.getTime());
        }

        cv.put(CSYS_REVISION, s.getCsysRevision());
        cv.put(CSYS_SYNCHRONIZED, s.getCsysSynchronized());
    }

    protected void setSynchronizedfromDto(Map<String, Object> dto, Synchronized s) {
        Number date =  (Long)dto.get(CSYS_BIRTH);
        s.setCsysBirth(date != null ? new Date(date.longValue()) : null);

        date = (Number) dto.get(CSYS_DELETED);
        s.setCsysDeleted(date != null ? new Date(date.longValue()) : null);

        date = (Number) dto.get(CSYS_MODIFIED);
        s.setCsysModified(date != null ? new Date(date.longValue()) : null);

        s.setCsysRevision((Integer) dto.get(CSYS_REVISION));
    }

    protected void setSynchronizedtoDto(Synchronized s, Map<String, Object> dto) {
        dto.put(CSYS_BIRTH, s == null ? null : s.getCsysBirth());
        dto.put(CSYS_DELETED, s == null ? null : s.getCsysDeleted());
        dto.put(CSYS_MODIFIED, s == null ? null : s.getCsysModified());
        dto.put(CSYS_REVISION, s == null ? null : s.getCsysRevision());
    }
}
