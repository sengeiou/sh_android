package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.db.DatabaseContract;

public class BlockEntityDBMapper extends GenericDBMapper {

    public static final String ID_USER = DatabaseContract.BlockTable.ID_USER;
    public static final String ID_BLOCKED_USER = DatabaseContract.BlockTable.ID_BLOCKED_USER;

    public BlockEntity fromCursor(Cursor c) {
        BlockEntity block = new BlockEntity();
        block.setIdUser(c.getString(c.getColumnIndex(ID_USER)));
        block.setIdBlockedUser(c.getString(c.getColumnIndex(ID_BLOCKED_USER)));
        return block;
    }

    public ContentValues toContentValues(BlockEntity f) {
        ContentValues cv = new ContentValues();
        cv.put(ID_USER, f.getIdUser());
        cv.put(ID_BLOCKED_USER, f.getIdBlockedUser());
        return cv;
    }

}
