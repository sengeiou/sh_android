package gm.mobi.android.db;

import android.provider.BaseColumns;

import com.google.android.gms.maps.Projection;

public class GMContract {

    public static interface SyncColumns extends BaseColumns {
        static final String CSYS_BIRTH = "birth";
        static final String CSYS_MODIFIED = "modified";
        static final String CSYS_DELETED = "deleted";
        static final String CSYS_REVISION = "revision";
        static final String CSYS_SYNCHRONIZED = "synchronized";

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
        public static final String PASSWORD = "password"; // Only used for Login request, never in Database
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

    public static final class ShotTable implements SyncColumns {

        public static final String TABLE = "Shot";

        public static final String ID_SHOT = "idShot";
        public static final String ID_USER = "idUser";
        public static final String COMMENT = "comment";

        public static final String[] PROJECTION = {
                ID_SHOT,
                ID_USER,
                COMMENT,
                CSYS_BIRTH,
                CSYS_MODIFIED,
                CSYS_DELETED,
                CSYS_REVISION,
                CSYS_SYNCHRONIZED
        };
    }

    public static final class FollowTable implements SyncColumns {
        public static final String TABLE = "Follow";
        public static final String ID_USER = "idUser";
        public static final String ID_FOLLOWED_USER = "idFollowedUser";

        public static final String[] PROJECTION = {
                ID_USER,
                ID_FOLLOWED_USER,
                CSYS_BIRTH,
                CSYS_MODIFIED,
                CSYS_DELETED,
                CSYS_REVISION,
                CSYS_SYNCHRONIZED
        };
    }

}
