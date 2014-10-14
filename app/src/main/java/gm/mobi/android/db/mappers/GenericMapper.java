package gm.mobi.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import gm.mobi.android.db.GMContract.SyncColumns;
import gm.mobi.android.db.objects.Synchronized;
import java.util.Date;
import java.util.Map;

public abstract class GenericMapper {

    public static final String CSYS_BIRTH = SyncColumns.CSYS_BIRTH;
    public static final String CSYS_DELETED = SyncColumns.CSYS_DELETED;
    public static final String CSYS_MODIFIED = SyncColumns.CSYS_MODIFIED;
    public static final String CSYS_REVISION = SyncColumns.CSYS_REVISION;
    public static final String CSYS_SYNCHRONIZED = SyncColumns.CSYS_SYNCHRONIZED;

    protected void setSynchronizedfromCursor(Cursor c, Synchronized s) {
        long date = c.getLong(c.getColumnIndex(CSYS_BIRTH));
        s.setCsys_birth(date != 0L ? new Date(date) : null);

        date = c.getLong(c.getColumnIndex(CSYS_DELETED));
        s.setCsys_deleted(date != 0L ? new Date(date) : null);

        date = c.getLong(c.getColumnIndex(CSYS_MODIFIED));
        s.setCsys_modified(date != 0L ? new Date(date) : null);

        s.setCsys_revision(c.getInt(c.getColumnIndex(CSYS_REVISION)));
        s.setCsys_synchronized(c.getString(c.getColumnIndex(CSYS_SYNCHRONIZED)));
    }

    protected void setSynchronizedtoContentValues(Synchronized s, ContentValues cv) {
        Date birth = s.getCsys_birth();
        cv.put(CSYS_BIRTH, birth!=null ? birth.getTime() : null);
        Date deleted = s.getCsys_deleted();
        cv.put(CSYS_DELETED, deleted!=null ? deleted.getTime() : null);
        Date modified = s.getCsys_modified();
        cv.put(CSYS_MODIFIED, modified!=null ? modified.getTime() : null);

        cv.put(CSYS_REVISION, s.getCsys_revision());
        cv.put(CSYS_SYNCHRONIZED, s.getCsys_synchronized());
    }

    protected void setSynchronizedfromDto(Map<String, Object> dto, Synchronized s) {
        Long date =  (Long)dto.get(CSYS_BIRTH);
        s.setCsys_birth(date != null ? new Date(date) : null);

        date = (Long) dto.get(CSYS_DELETED);
        s.setCsys_deleted(date != null ? new Date(date) : null);

        date = (Long) dto.get(CSYS_MODIFIED);
        s.setCsys_modified(date != null ? new Date(date) : null);

        s.setCsys_revision((Integer) dto.get(CSYS_REVISION));
    }

    protected void setSynchronizedtoDto(Synchronized s, Map<String, Object> dto) {
        dto.put(CSYS_BIRTH, s == null ? null : s.getCsys_birth());
        dto.put(CSYS_DELETED, s == null ? null : s.getCsys_deleted());
        dto.put(CSYS_MODIFIED, s == null ? null : s.getCsys_modified());
        dto.put(CSYS_REVISION, s == null ? null : s.getCsys_revision());
    }
}
