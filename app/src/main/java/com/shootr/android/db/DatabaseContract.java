package com.shootr.android.db;

public class DatabaseContract {

    private DatabaseContract() {

    }

    public interface LocalSyncColumns {

     String SYNCHRONIZED = "synchronizedStatus";
    }

    public interface SyncColumns extends LocalSyncColumns {

        String BIRTH = "birth";
        String MODIFIED = "modified";
        String DELETED = "deleted";
        String REVISION = "revision";
    }

    public static final class TablesSync implements SyncColumns {

        private TablesSync() {

        }

        public static final String TABLE = "TablesSync";

        public static final String ORDER = "idOrder";
        public static final String ENTITY = "entity";
        public static final String FREQUENCY = "frequency";
        public static final String MAX_TIMESTAMP = "max_timestamp";
        public static final String MIN_TIMESTAMP = "min_timestamp";
        public static final String MIN_ROWS = "min_rows";
        public static final String MAX_ROWS = "max_rows";
        public static final String DIRECTION = "direction";

        public static final String[] PROJECTION = {
          ORDER, ENTITY, FREQUENCY, MAX_ROWS, MIN_ROWS, MAX_TIMESTAMP, MIN_TIMESTAMP, DIRECTION
        };
    }

    public static final class UserTable implements SyncColumns {

        private UserTable() {

        }

        public static final String TABLE = "UserMongo";

        public static final String ID = "idUser";
        public static final String SESSION_TOKEN = "sessionToken";
        public static final String USER_NAME = "userName";
        public static final String PASSWORD = "password"; // Only used for Login request, never in Database
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String PHOTO = "photo";
        public static final String NUM_FOLLOWERS = "numFollowers";
        public static final String NUM_FOLLOWINGS = "numFollowings";
        public static final String POINTS = "points";
        public static final String WEBSITE = "website";
        public static final String BIO = "bio";
        public static final String RANK = "rank";
        public static final String NAME_NORMALIZED = "nameNormalized";
        public static final String USER_NAME_NORMALIZED = "userNameNormalized";
        public static final String EMAIL_NORMALIZED = "emailNormalized";
        public static final String ID_WATCHING_STREAM = "idWatchingStream";
        public static final String WATCHING_STREAM_TITLE = "watchingStreamTitle";
        public static final String JOIN_STREAM_DATE = "joinStreamDate";

        public static final String[] PROJECTION = {
          ID, SESSION_TOKEN, USER_NAME, EMAIL, NAME, PHOTO, NUM_FOLLOWERS,
          NUM_FOLLOWINGS, POINTS, WEBSITE, BIO, RANK, JOIN_STREAM_DATE, ID_WATCHING_STREAM, WATCHING_STREAM_TITLE,
          BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
        };
    }

    public static final class ShotTable implements SyncColumns {

        private ShotTable() {

        }

        public static final String TABLE = "ShotMongo";

        public static final String ID_SHOT = "idShot";
        public static final String ID_USER = "idUser";
        public static final String USERNAME = "userName";
        public static final String USER_PHOTO = "userPhoto";
        public static final String COMMENT = "comment";
        public static final String IMAGE = "image";
        public static final String ID_STREAM = "idStream";
        public static final String STREAM_TAG = "streamTag";
        public static final String STREAM_TITLE = "streamTitle";
        public static final String NICE_COUNT = "niceCount";
        public static final String TYPE = "type";

        public static final String ID_SHOT_PARENT = "idShotParent";
        public static final String ID_USER_PARENT = "idUserParent";
        public static final String USERNAME_PARENT = "userNameParent";

        public static final String VIDEO_URL = "videoUrl";
        public static final String VIDEO_TITLE = "videoTitle";
        public static final String VIDEO_DURATION = "videoDuration";

        public static final String[] PROJECTION = {
          ID_SHOT, ID_USER, USERNAME, USER_PHOTO, COMMENT, IMAGE, ID_STREAM, STREAM_TAG, STREAM_TITLE,
          NICE_COUNT, TYPE,
          ID_SHOT_PARENT, ID_USER_PARENT, USERNAME_PARENT,
          VIDEO_URL, VIDEO_TITLE, VIDEO_DURATION, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
        };
    }

    public static final class FollowTable implements SyncColumns {

        private FollowTable() {

        }

        public static final String TABLE = "FollowMongo";
        public static final String ID_USER = "idUser";
        public static final String ID_FOLLOWED_USER = "idFollowedUser";

        public static final String[] PROJECTION = {
          ID_USER, ID_FOLLOWED_USER, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
        };
    }

    public static final class DeviceTable implements SyncColumns {

        private DeviceTable() {

        }

        public static final String TABLE = "DeviceMongo";
        public static final String ID_DEVICE = "idDevice";
        public static final String ID_USER = "idUser";
        public static final String TOKEN = "token";
        public static final String ID_PUSH_ENGINE = "idPushEngine";
        public static final String UNIQUE_DEVICE_ID = "uniqueDeviceID";
        public static final String STATUS = "status";
        public static final String MODEL = "model";
        public static final String OS_VERSION = "osVer";
        public static final String PLATFORM = "platform";
        public static final String APP_VERSION = "appVer";
        public static final String LOCALE = "locale";

        public static final String[] PROJECTION = {
          ID_DEVICE, ID_USER, PLATFORM, TOKEN, ID_PUSH_ENGINE, UNIQUE_DEVICE_ID, STATUS, MODEL, PLATFORM, OS_VERSION,
          APP_VERSION, LOCALE, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
        };
    }

    public static class StreamTable implements SyncColumns {

        private StreamTable() {

        }

        public static final String TABLE = "StreamMongo";
        public static final String ID_STREAM = "idStream";
        public static final String ID_USER = "idUser";
        public static final String ID_USER_STREAM = "idUserStream";
        public static final String USERNAME = "userName";
        public static final String TITLE = "title";
        public static final String PHOTO = "photo";
        public static final String TAG = "tag";
        public static final String DESCRIPTION = "description";
        public static final String NOTIFY_CREATION = "notifyCreation";
        public static final String LAST_UPDATED_USER = "lastUpdatedUser";
        public static final String LOCALE = "locale";

        public static final String[] PROJECTION = {
          ID_STREAM, ID_USER, ID_USER_STREAM, USERNAME, TITLE,
                PHOTO, TAG, DESCRIPTION, BIRTH, MODIFIED, LAST_UPDATED_USER, LOCALE, DELETED, REVISION, SYNCHRONIZED
        };
    }

    public static class StreamSearchTable extends StreamTable {

        public static final String TABLE = "SearchStreamMongo";

        public static final String WATCHERS = "watchers";

        public static final String[] PROJECTION = {
          WATCHERS, LOCALE, ID_STREAM, ID_USER, USERNAME, TITLE, PHOTO, TAG, DESCRIPTION, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
        };

    }

    public static final class ShotQueueTable implements SyncColumns {

        private ShotQueueTable() {
        }

        public static final String TABLE = "ShotQueue";
        public static final String ID_QUEUE = "idQueue";
        public static final String FAILED = "failed";
        public static final String IMAGE_FILE = "imageFile";
        public static final String ID_SHOT = "idShot";
        public static final String ID_USER = "idUser";
        public static final String USERNAME = "userName";
        public static final String USER_PHOTO = "userPhoto";
        public static final String COMMENT = "comment";
        public static final String IMAGE = "image";
        public static final String ID_STREAM = "idStream";
        public static final String STREAM_TAG = "streamTag";
        public static final String STREAM_TITLE = "streamTitle";
        public static final String TYPE = "type";

        public static final String ID_SHOT_PARENT = "idShotParent";
        public static final String ID_USER_PARENT = "idUserParent";
        public static final String USERNAME_PARENT = "userNameParent";

        public static final String VIDEO_URL = "videoUrl";
        public static final String VIDEO_TITLE = "videoTitle";
        public static final String VIDEO_DURATION = "videoDuration";

        public static final String[] PROJECTION = {
          ID_QUEUE, FAILED, IMAGE_FILE, ID_SHOT, ID_USER, USERNAME, USER_PHOTO, COMMENT, IMAGE, ID_STREAM, STREAM_TAG,
          STREAM_TITLE,
          TYPE,
          ID_SHOT_PARENT, ID_USER_PARENT, USERNAME_PARENT,
          VIDEO_URL, VIDEO_TITLE, VIDEO_DURATION, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
        };
    }

    public static final class FavoriteTable implements SyncColumns {

        private FavoriteTable() {
        }

        public static final String TABLE = "Favorite";

        public static final String ID_STREAM = "idStream";
        public static final String ORDER = "position";

        public static final String[] PROJECTION = {
          ID_STREAM, ORDER,
          SYNCHRONIZED,
        };

    }

    public static final class ActivityTable implements SyncColumns {

        private ActivityTable() {

        }

        public static final String TABLE = "ActivityMongo";

        public static final String ID_ACTIVITY = "idActivity";
        public static final String ID_USER = "idUser";
        public static final String USERNAME = "userName";
        public static final String ID_STREAM = "idStream";
        public static final String USER_PHOTO = "userPhoto";
        public static final String STREAM_TAG = "streamTag";
        public static final String STREAM_TITLE = "streamTitle";
        public static final String COMMENT = "comment";
        public static final String TYPE = "type";

        public static final String[] PROJECTION = {
          ID_ACTIVITY, ID_USER, USERNAME, ID_STREAM, USER_PHOTO, STREAM_TAG, STREAM_TITLE,
          COMMENT,TYPE, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
        };
    }
}
