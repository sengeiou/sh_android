package gm.mobi.android.db;

import android.provider.BaseColumns;

public class GMContract {

    public static interface SyncColumns extends BaseColumns {
        static final String CSYS_BIRTH = "csys_birth";
        static final String CSYS_MODIFIED = "csys_modified";
        static final String CSYS_DELETED = "csys_deleted";
        static final String CSYS_REVISION = "csys_revision";
        static final String CSYS_SYNCHRONIZED = "csys_synchronized";

        public static final String SYNC_ID_NEW = "N";
        public static final String SYNC_ID_UPDATED = "U";
        public static final String SYNC_ID_SYNCHRONIZED = "S";
        public static final String SYNC_ID_DELETED = "D";
    }


    public static final class UserTable implements SyncColumns {

        public static final String TABLE = "User";

        public static final String ID = "idUser";
        public static final String FAVOURITE_TEAM_ID = "idFavouriteTeam";
        public static final String SESSION_TOKEN = "sessionToken";
        public static final String USER_NAME = "userName";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String PHOTO = "photo";

        public static final String[] PROJECTION = {
                ID,
                FAVOURITE_TEAM_ID,
                SESSION_TOKEN,
                USER_NAME,
                EMAIL,
                NAME,
                PHOTO,
                CSYS_BIRTH,
                CSYS_MODIFIED,
                CSYS_DELETED,
                CSYS_REVISION,
                CSYS_SYNCHRONIZED
        };
    }


}
