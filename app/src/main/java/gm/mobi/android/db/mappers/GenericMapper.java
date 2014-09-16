package gm.mobi.android.db.mappers;


import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;
import java.util.Map;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.SyncColumns;
import gm.mobi.android.db.objects.Synchronized;

public abstract class GenericMapper {

    protected static void setSynchronizedFromCursor(Cursor c, Synchronized s) {
        long date = c.getLong(c.getColumnIndex(GMContract.SyncColumns.CSYS_BIRTH));
        s.setCsys_birth(new Date(date));

        date = c.getLong(c.getColumnIndex(GMContract.SyncColumns.CSYS_DELETED));
        s.setCsys_deleted(new Date(date));

        date = c.getLong(c.getColumnIndex(GMContract.SyncColumns.CSYS_MODIFIED));
        s.setCsys_modified(new Date(date));

        s.setCsys_revision(c.getInt(c.getColumnIndex(GMContract.SyncColumns.CSYS_REVISION)));
        s.setCsys_synchronized(c.getString(c.getColumnIndex(GMContract.SyncColumns.CSYS_SYNCHRONIZED)));
    }

    protected static void setSynchronizedToContentValues(ContentValues cv, Synchronized s) {
        Date birth = s.getCsys_birth();
        if (birth != null)
            cv.put(SyncColumns.CSYS_BIRTH, birth.getTime());
        Date deleted = s.getCsys_deleted();
        if (deleted != null)
            cv.put(SyncColumns.CSYS_DELETED, deleted.getTime());
        Date modified = s.getCsys_modified();
        if(modified!=null)
            cv.put(SyncColumns.CSYS_MODIFIED, modified.getTime());

        cv.put(SyncColumns.CSYS_REVISION, s.getCsys_revision());
        cv.put(SyncColumns.CSYS_SYNCHRONIZED, s.getCsys_synchronized());
    }

    protected static void setSynchronizedFromDto(Map<String, Object> dto, Synchronized s) {
        Long date = (Long) dto.get(SyncColumns.CSYS_BIRTH);
        if(date!=null)
            s.setCsys_birth(new Date(date));

        date = (Long) dto.get(SyncColumns.CSYS_DELETED);
        if(date!=null)
            s.setCsys_deleted(new Date(date));

        date = (Long) dto.get(SyncColumns.CSYS_MODIFIED);
        if (date != null) {
            s.setCsys_modified(new Date(date));
        }

        s.setCsys_revision((Integer) dto.get(SyncColumns.CSYS_REVISION));
    }

    protected static void setSynchronizedToDto(Map<String, Object> dto, Synchronized s) {
        dto.put(SyncColumns.CSYS_BIRTH, s == null ? null : s.getCsys_birth());
        dto.put(SyncColumns.CSYS_DELETED, s == null ? null : s.getCsys_deleted());
        dto.put(SyncColumns.CSYS_MODIFIED, s == null ? null : s.getCsys_modified());
        dto.put(SyncColumns.CSYS_REVISION, s == null ? null : s.getCsys_revision());
    }
}
