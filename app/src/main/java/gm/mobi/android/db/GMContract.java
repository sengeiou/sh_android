package gm.mobi.android.db;

import android.provider.BaseColumns;
import gm.mobi.android.db.objects.Synchronized;

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


    public static final class TablesSync implements SyncColumns {
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
                ORDER,
                ENTITY,
                FREQUENCY,
                MAX_ROWS,
                MIN_ROWS,
                MAX_TIMESTAMP,
                MIN_TIMESTAMP,
                DIRECTION
        };
    }

    public static final class UserTable implements SyncColumns {

        public static final String TABLE = "User";

        public static final String ID = "idUser";
        public static final String FAVORITE_TEAM_ID = "idFavoriteTeam";
        public static final String FAVORITE_TEAM_NAME = "favoriteTeamName";
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

        public static final String[] PROJECTION = {
                ID,
                FAVORITE_TEAM_ID,
                FAVORITE_TEAM_NAME,
                SESSION_TOKEN,
                USER_NAME,
                EMAIL,
                NAME,
                PHOTO,
                NUM_FOLLOWERS,
                NUM_FOLLOWINGS,
                POINTS,
                WEBSITE,
                BIO,
                RANK,
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

    public static final class DeviceTable implements SyncColumns{

        public static final String TABLE = "Device";
        public static final String ID_DEVICE = "idDevice";
        public static final String ID_USER = "idUser";
        public static final String TOKEN = "token";
        public static final String UNIQUE_DEVICE_ID = "uniqueDeviceID";
        public static final String MODEL = "model";
        public static final String OS_VERSION = "osVer";
        public static final String PLATFORM = "platform";

        public static final String[] PROJECTION = {
          ID_DEVICE,
          ID_USER,
          TOKEN,
          UNIQUE_DEVICE_ID,
          MODEL,
          PLATFORM,
          OS_VERSION,
          CSYS_BIRTH,
          CSYS_MODIFIED,
          CSYS_DELETED,
          CSYS_REVISION,
          CSYS_SYNCHRONIZED
        } ;
    }

    public static final class TeamTable implements SyncColumns{
        public static final String TABLE = "Team";
        public static final String ID_TEAM = "idTeam";
        public static final String CLUB_NAME="clubName";
        public static final String OFFICIAL_NAME="officialName";
        public static final String SHORT_NAME="shortName";
        public static final String TLA_NAME="tlaName";

        public static final String[] PROJECTION = {
          ID_TEAM,
          CLUB_NAME,
          OFFICIAL_NAME,
          SHORT_NAME,
          TLA_NAME,
          CSYS_BIRTH,
          CSYS_MODIFIED,
          CSYS_DELETED,
          CSYS_REVISION,
          CSYS_SYNCHRONIZED
        };
    }

    public static final class MatchTable implements SyncColumns{
        public static final String TABLE = "Match";
        public static final String ID_MATCH = "idMatch";
        public static final String MATCH_DATE = "matchDate";
        public static final String ID_LOCAL_TEAM = "idLocalTeam";
        public static final String ID_VISITOR_TEAM = "idVisitorTeam";
        public static final String LOCAL_TEAM_NAME = "localTeamName";
        public static final String VISITOR_TEAM_NAME = "visitorTeamName";
        public static final String STATUS = "status";

        public static final String[] PROJECTION = {
          ID_MATCH,
          MATCH_DATE,
          ID_LOCAL_TEAM,
          ID_VISITOR_TEAM,
          LOCAL_TEAM_NAME,
          VISITOR_TEAM_NAME,
          STATUS,
          CSYS_BIRTH,
          CSYS_MODIFIED,
          CSYS_DELETED,
          CSYS_REVISION,
          CSYS_SYNCHRONIZED
        };
    }

    public static final class WatchTable implements  SyncColumns{
        public static final String TABLE = "Watch";
        public static final String ID_USER = "idUser";
        public static final String ID_MATCH = "idMatch";
        public static final String STATUS = "status";

        public static final String[] PROJECTION = {
          ID_USER,
          ID_MATCH,
          STATUS,
          CSYS_BIRTH,
          CSYS_MODIFIED,
          CSYS_DELETED,
          CSYS_REVISION,
          CSYS_SYNCHRONIZED
        };
    }


}