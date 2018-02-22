package com.shootr.mobile.db;

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

  public interface BaseMessageTable extends SyncColumns {
    String COMMENT = "comment";
    String IMAGE = "image";
    String IMAGE_WIDTH = "imageWidth";
    String IMAGE_HEIGHT = "imageHeight";
    String VIDEO_URL = "videoUrl";
    String VIDEO_TITLE = "videoTitle";
    String VIDEO_DURATION = "videoDuration";
    String ID_USER = "idUser";
    String VERIFIED_USER = "verifiedUser";
    String USERNAME = "username";
    String ENTITIES = "entities";
  }

  public static class UserTable implements SyncColumns {

    private UserTable() {

    }

    public static final String TABLE = "User";

    public static final String ID = "idUser";
    public static final String USER_NAME = "userName";
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
    public static final String EMAIL_CONFIRMED = "emailConfirmed";
    public static final String USER_VERIFIED = "userVerified";
    public static final String ID_WATCHING_STREAM = "idWatchingStream";
    public static final String WATCHING_STREAM_TITLE = "watchingStreamTitle";
    public static final String JOIN_STREAM_DATE = "joinStreamDate";
    public static final String WATCHING_SYNCHRONIZED = "watchingSynchronized";
    public static final String CREATED_STREAMS_COUNT = "createdStreamsCount";
    public static final String FAVORITED_STREAMS_COUNT = "favoritedStreamsCount";
    public static final String SOCIAL_LOGIN = "socialLogin";
    public static final String RECEIVED_REACTIONS = "receivedReactions";
    public static final String ANALYTICS_USER_TYPE = "analyticsUserType";
    public static final String NUM_MUTUALS = "numMutuals";
    public static final String FIRST_SESSION_ACTIVATION = "firstSessionActivation";
    public static final String STRATEGIC = "strategic";
    public static final String FOLLOWING = "following";
    public static final String MUTED = "muted";
    public static final String SHARE_LINK = "shareLink";

    public static final String[] PROJECTION = {
        ID, USER_NAME, EMAIL, EMAIL_CONFIRMED, USER_VERIFIED, NAME, PHOTO, NUM_FOLLOWERS,
        NUM_FOLLOWINGS, POINTS, WEBSITE, BIO, RANK, JOIN_STREAM_DATE, ID_WATCHING_STREAM,
        WATCHING_STREAM_TITLE, WATCHING_SYNCHRONIZED, CREATED_STREAMS_COUNT,
        FAVORITED_STREAMS_COUNT, SOCIAL_LOGIN, RECEIVED_REACTIONS, ANALYTICS_USER_TYPE, NUM_MUTUALS,
        FIRST_SESSION_ACTIVATION, STRATEGIC, FOLLOWING, MUTED, SHARE_LINK, BIRTH, MODIFIED, DELETED,
        REVISION, SYNCHRONIZED
    };
  }

  public static class ShotTable implements SyncColumns {

    private ShotTable() {

    }

    public static final String TABLE = "Shot";

    public static final String ID_SHOT = "idShot";
    public static final String ID_USER = "idUser";
    public static final String USERNAME = "userName";
    public static final String USER_PHOTO = "userPhoto";
    public static final String COMMENT = "comment";
    public static final String IMAGE = "image";
    public static final String IMAGE_WIDTH = "imageWidth";
    public static final String IMAGE_HEIGHT = "imageHeight";
    public static final String ID_STREAM = "idStream";
    public static final String STREAM_TITLE = "streamTitle";
    public static final String NICE_COUNT = "niceCount";
    public static final String TYPE = "type";
    public static final String VIEWS = "views";
    public static final String LINK_CLICKS = "linkClicks";
    public static final String ID_SHOT_PARENT = "idShotParent";
    public static final String ID_USER_PARENT = "idUserParent";
    public static final String USERNAME_PARENT = "userNameParent";
    public static final String VIDEO_URL = "videoUrl";
    public static final String VIDEO_TITLE = "videoTitle";
    public static final String VIDEO_DURATION = "videoDuration";
    public static final String PROFILE_HIDDEN = "profileHidden";
    public static final String REPLY_COUNT = "replyCount";
    public static final String RESHOOT_COUNT = "reshootCount";
    public static final String CTA_CAPTION = "CTACaption";
    public static final String CTA_BUTTON_LINK = "CTAButtonLink";
    public static final String CTA_BUTTON_TEXT = "CTAButtonText";
    public static final String PROMOTED = "Promoted";
    public static final String VERIFIED_USER = "verifiedUser";
    public static final String IS_PADDING = "isPadding";
    public static final String FROM_HOLDER = "fromHolder";
    public static final String FROM_CONTRIBUTOR = "fromContributor";
    public static final String ENTITIES = "entities";
    public static final String NICED = "niced";
    public static final String NICED_TIME = "nicedTime";
    public static final String RESHOOTED = "reshooted";
    public static final String RESHOOTED_TIME = "reshootedTime";
    public static final String FLAGS = "flags";

    public static final String[] PROJECTION = {
        ID_SHOT, ID_USER, USERNAME, USER_PHOTO, COMMENT, IMAGE, IMAGE_WIDTH, IMAGE_HEIGHT,
        ID_STREAM, STREAM_TITLE, NICE_COUNT, TYPE, ID_SHOT_PARENT, ID_USER_PARENT, USERNAME_PARENT,
        VIDEO_URL, VIDEO_TITLE, VIDEO_DURATION, PROFILE_HIDDEN, REPLY_COUNT, VIEWS, LINK_CLICKS,
        RESHOOT_COUNT, CTA_CAPTION, CTA_BUTTON_LINK, CTA_BUTTON_TEXT, PROMOTED, VERIFIED_USER,
        IS_PADDING, FROM_HOLDER, FROM_CONTRIBUTOR, ENTITIES, NICED, NICED_TIME, RESHOOTED,
        RESHOOTED_TIME, FLAGS, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
    };
  }

  public static final class FollowTable {

    private FollowTable() {
            /* no instances */
    }

    public static final String TABLE = "Follow";
    public static final String TYPE = "type";
    public static final String ID_FOLLOWED_ENTITY = "idFollowedEntity";
    public static final String IS_FOLLOWING = "isFollowing";

    public static final String[] PROJECTION = {
        ID_FOLLOWED_ENTITY, IS_FOLLOWING, TYPE
    };
  }

  public static final class BlockTable implements SyncColumns {

    private BlockTable() {
            /* no instances */
    }

    public static final String TABLE = "Block";
    public static final String ID_USER = "idUser";
    public static final String ID_BLOCKED_USER = "idBlockedUser";

    public static final String[] PROJECTION = {
        ID_USER, ID_BLOCKED_USER
    };
  }

  public static final class DeviceTable {

    private DeviceTable() {

    }

    public static final String TABLE = "Device";
    public static final String ID_DEVICE = "idDevice";
    public static final String TOKEN = "token";
    public static final String UNIQUE_DEVICE_ID = "uniqueDeviceID";
    public static final String MODEL = "model";
    public static final String OS_VERSION = "osVer";
    public static final String PLATFORM = "platform";
    public static final String APP_VERSION = "appVer";
    public static final String LOCALE = "locale";

    public static final String[] PROJECTION = {
        ID_DEVICE, PLATFORM, TOKEN, UNIQUE_DEVICE_ID, MODEL, PLATFORM, OS_VERSION, APP_VERSION,
        LOCALE,
    };
  }

  public static class StreamTable implements SyncColumns {

    private StreamTable() {

    }

    public static final String TABLE = "Stream";
    public static final String ID_STREAM = "idStream";
    public static final String ID_USER = "idUser";
    public static final String ID_USER_STREAM = "idUserStream";
    public static final String USERNAME = "userName";
    public static final String TITLE = "title";
    public static final String PHOTO = "photo";
    public static final String LANDSCAPE_PHOTO = "LandscapePhoto";
    public static final String DESCRIPTION = "description";
    public static final String LAST_UPDATED_USER = "lastUpdatedUser";
    public static final String COUNTRY = "country";
    public static final String MEDIA_COUNT = "mediaCount";
    public static final String REMOVED = "removed";
    public static final String TOTAL_FOLLOWERS = "totalFollowers";
    public static final String TOTAL_WATCHERS = "totalWatchers";
    public static final String TOPIC = "topic";
    public static final String READ_WRITE_MODE = "readWriteMode";
    public static final String VERIFIED_USER = "verifiedUser";
    public static final String CONTRIBUTORS_COUNT = "contributorsCount";
    public static final String I_AM_CONTRIBUTOR = "iAmContributor";
    public static final String TOTAL_FOLLOWING_WATCHERS = "totalFollowingWatchers";
    public static final String STRATEGIC = "strategic";
    public static final String MUTED = "muted";
    public static final String FOLLOWING = "following";
    public static final String VIEWS = "views";
    public static final String LAST_TIME_SHOOTED = "lastTimeShooted";

    public static final String PERMISSIONS = "permissions";

    public static final String[] PROJECTION = {
        ID_STREAM, ID_USER, ID_USER_STREAM, USERNAME, TITLE, MEDIA_COUNT, PHOTO, LANDSCAPE_PHOTO,
        DESCRIPTION, TOPIC, BIRTH, MODIFIED, LAST_UPDATED_USER, COUNTRY, TOTAL_FOLLOWERS,
        TOTAL_WATCHERS, READ_WRITE_MODE, VERIFIED_USER, CONTRIBUTORS_COUNT, I_AM_CONTRIBUTOR,
        TOTAL_FOLLOWING_WATCHERS, STRATEGIC, FOLLOWING, VIEWS, PERMISSIONS, MUTED, LAST_TIME_SHOOTED, REMOVED,
        DELETED, REVISION, SYNCHRONIZED

    };
  }

  public static class StreamSearchTable extends StreamTable {

    public static final String TABLE = "SearchStream";

    public static final String WATCHERS = "watchers";

    public static final String[] PROJECTION = {
        WATCHERS, COUNTRY, ID_STREAM, ID_USER, USERNAME, TITLE, PHOTO, LANDSCAPE_PHOTO, DESCRIPTION,
        TOPIC, REMOVED, MEDIA_COUNT, TOTAL_FOLLOWERS, TOTAL_WATCHERS, READ_WRITE_MODE,
        VERIFIED_USER, CONTRIBUTORS_COUNT, I_AM_CONTRIBUTOR, TOTAL_FOLLOWING_WATCHERS, STRATEGIC,
        MUTED, LAST_TIME_SHOOTED, PERMISSIONS, FOLLOWING, VIEWS, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
    };
  }

  public static final class ShotQueueTable extends ShotTable {

    private ShotQueueTable() {
    }

    public static final String TABLE = "ShotQueue";
    public static final String ID_QUEUE = "idQueue";
    public static final String FAILED = "failed";
    public static final String IMAGE_FILE = "imageFile";

    public static final String[] PROJECTION = {
        ID_QUEUE, FAILED, IMAGE_FILE, ID_SHOT, ID_USER, USERNAME, USER_PHOTO, COMMENT, IMAGE,
        ID_STREAM, STREAM_TITLE, TYPE, ID_SHOT_PARENT, ID_USER_PARENT, USERNAME_PARENT, VIDEO_URL,
        VIDEO_TITLE, VIDEO_DURATION, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
    };
  }

  public static final class MessageQueueTable implements BaseMessageTable {

    private MessageQueueTable() {
    }

    public static final String TABLE = "MessageQueue";
    public static final String ID_QUEUE = "idQueue";
    public static final String ID_PRIVATE_MESSAGE = "idPrivateMessage";
    public static final String ID_PRIVATE_MESSAGE_CHANNEL = "idPrivateMessageChannel";
    public static final String TITLE = "title";
    public static final String ID_TARGET_USER = "idTargetUser";
    public static final String FAILED = "failed";
    public static final String IMAGE_FILE = "imageFile";

    public static final String[] PROJECTION = {
        ID_QUEUE, FAILED, IMAGE_FILE, ID_PRIVATE_MESSAGE, ID_USER, ID_TARGET_USER, USERNAME,
        COMMENT, IMAGE, ID_PRIVATE_MESSAGE_CHANNEL, TITLE, VIDEO_URL, VIDEO_TITLE, VIDEO_DURATION,
        BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
    };
  }

  public static final class ActivityTable implements SyncColumns {

    private ActivityTable() {

    }

    public static final String TABLE = "Activity";

    public static final String ID_ACTIVITY = "idActivity";
    public static final String ID_USER = "idUser";
    public static final String ID_TARGET_USER = "idTargetUser";
    public static final String USERNAME = "userName";
    public static final String ID_STREAM = "idStream";
    public static final String USER_PHOTO = "userPhoto";
    public static final String STREAM_TITLE = "streamTitle";
    public static final String COMMENT = "comment";
    public static final String TYPE = "type";
    public static final String ID_SHOT = "idShot";
    public static final String ID_STREAM_AUTHOR = "idStreamAuthor";
    public static final String ID_POLL = "idPoll";
    public static final String POLL_QUESTION = "pollQuestion";
    public static final String POLL_OPTION_TEXT = "pollOptiontext";
    public static final String IS_VERIFIED = "isVerified";
    public static final String IS_FOLLOWING = "isFollowing";
    public static final String NAME = "name";
    public static final String TARGET_NAME = "targetName";
    public static final String STREAM_PHOTO = "streamPhoto";
    public static final String TARGET_USER_PHOTO = "targetUserPhoto";
    public static final String TARGET_USERNAME = "targetUsername";

    public static final String[] PROJECTION = {
        ID_ACTIVITY, ID_USER, ID_TARGET_USER, USERNAME, ID_STREAM, USER_PHOTO, STREAM_TITLE,
        ID_SHOT, ID_STREAM_AUTHOR, COMMENT, ID_POLL, POLL_QUESTION, POLL_OPTION_TEXT, TYPE,
        IS_VERIFIED, IS_FOLLOWING, NAME, TARGET_NAME, STREAM_PHOTO, TARGET_USER_PHOTO,
        TARGET_USERNAME, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
    };
  }

  public static final class MeActivityTable implements SyncColumns {

    private MeActivityTable() {

    }

    public static final String TABLE = "MeActivity";

    public static final String ID_ACTIVITY = "idActivity";
    public static final String ID_USER = "idUser";
    public static final String ID_TARGET_USER = "idTargetUser";
    public static final String USERNAME = "userName";
    public static final String ID_STREAM = "idStream";
    public static final String USER_PHOTO = "userPhoto";
    public static final String STREAM_TITLE = "streamTitle";
    public static final String COMMENT = "comment";
    public static final String TYPE = "type";
    public static final String ID_SHOT = "idShot";
    public static final String ID_STREAM_AUTHOR = "idStreamAuthor";
    public static final String ID_POLL = "idPoll";
    public static final String POLL_QUESTION = "pollQuestion";
    public static final String POLL_OPTION_TEXT = "pollOptiontext";
    public static final String IS_VERIFIED = "isVerified";
    public static final String IS_FOLLOWING = "isFollowing";
    public static final String NAME = "name";
    public static final String TARGET_NAME = "targetName";
    public static final String STREAM_PHOTO = "streamPhoto";
    public static final String TARGET_USER_PHOTO = "targetUserPhoto";
    public static final String TARGET_USERNAME = "targetUsername";

    public static final String[] PROJECTION = {
        ID_ACTIVITY, ID_USER, ID_TARGET_USER, USERNAME, ID_STREAM, USER_PHOTO, STREAM_TITLE,
        ID_SHOT, ID_STREAM_AUTHOR, COMMENT, ID_POLL, POLL_QUESTION, POLL_OPTION_TEXT, TYPE,
        IS_VERIFIED, IS_FOLLOWING, NAME, TARGET_NAME, STREAM_PHOTO, TARGET_USER_PHOTO,
        TARGET_USERNAME, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
    };
  }

  public static final class ContributorTable implements SyncColumns {

    private ContributorTable() {
            /* no instances */
    }

    public static final String TABLE = "Contributor";
    public static final String ID_STREAM = "idStream";
    public static final String ID_USER = "idUser";
    public static final String ID_CONTRIBUTOR = "idContributor";

    public static final String[] PROJECTION = { ID_STREAM, ID_USER, ID_CONTRIBUTOR };
  }

  public static final class TimelineSyncTable {

    private TimelineSyncTable() {
            /* non instanciable */
    }

    public static final String TABLE = "TimelineSync";

    public static final String STREAM_ID = "idStream";
    public static final String DATE = "date";

    public static final String[] PROJECTION = new String[] {
        STREAM_ID, DATE
    };
  }

  public static final class PollTable implements SyncColumns {

    private PollTable() {

    }

    public static final String TABLE = "Poll";

    public static final String ID_POLL = "idPoll";
    public static final String ID_STREAM = "idStream";
    public static final String ID_USER = "idUser";
    public static final String QUESTION = "question";
    public static final String STATUS = "status";
    public static final String PUBLISHED = "published";
    public static final String VOTE_STATUS = "voteStatus";
    public static final String VOTE_PRIVACY = "votePrivacy";
    public static final String EXPIRATION_DATE = "expirationDate";
    public static final String VERIFIED_POLL = "verifiedPoll";
    public static final String HIDE_RESULTS = "hideResults";
    public static final String CAN_VOTE = "canVote";
    public static final String DAILY_POLL = "dailyPoll";

    public static final String[] PROJECTION = {
        ID_POLL, ID_STREAM, ID_USER, QUESTION, STATUS, VOTE_STATUS, VOTE_PRIVACY, PUBLISHED,
        EXPIRATION_DATE, VERIFIED_POLL, HIDE_RESULTS, CAN_VOTE, DAILY_POLL
    };
  }

  public static final class PollOptionTable implements SyncColumns {

    private PollOptionTable() {

    }

    public static final String TABLE = "PollOption";

    public static final String ID_POLL = "idPoll";
    public static final String ID_POLL_OPTION = "idPollOption";
    public static final String OPTION_IMAGE = "optionImage";
    public static final String TEXT = "text";
    public static final String VOTES = "votes";
    public static final String ORDER = "'order'";
    public static final String VOTED = "voted";

    public static final String[] PROJECTION = {
        ID_POLL, ID_POLL_OPTION, OPTION_IMAGE, TEXT, VOTES, ORDER, VOTED
    };
  }

  public static final class HighlightedShotTable extends ShotTable {

    private HighlightedShotTable() {

    }

    public static final String TABLE = "HighlightedShot";

    public static final String ID_HIGHLIGHTED_SHOT = "idHighlightedShot";
    public static final String ACTIVE = "active";
    public static final String VISIBLE = "visible";

    public static final String[] PROJECTION = {
        ID_HIGHLIGHTED_SHOT, ACTIVE, VISIBLE, ID_SHOT, ID_USER, USERNAME, USER_PHOTO, COMMENT,
        IMAGE, IMAGE_WIDTH, IMAGE_HEIGHT, ID_STREAM, STREAM_TITLE, NICE_COUNT, TYPE, ID_SHOT_PARENT,
        ID_USER_PARENT, USERNAME_PARENT, VIDEO_URL, VIDEO_TITLE, VIDEO_DURATION, PROFILE_HIDDEN,
        REPLY_COUNT, VIEWS, LINK_CLICKS, RESHOOT_COUNT, CTA_CAPTION, CTA_BUTTON_LINK,
        CTA_BUTTON_TEXT, PROMOTED, VERIFIED_USER, IS_PADDING, FROM_HOLDER, FROM_CONTRIBUTOR, BIRTH,
        MODIFIED, DELETED, REVISION, SYNCHRONIZED
    };
  }

  public static final class RecentSearchTable {

    private RecentSearchTable() {

    }

    public static final String TABLE = "RecentSearchTable";

    public static final String ID_SEARCH_ITEM = "idSearchItem";
    public static final String VISIT_DATE = "visitDate";
    public static final String ITEM_TYPE = "itemType";

    public static final String[] PROJECTION = {
        ID_SEARCH_ITEM, VISIT_DATE, ITEM_TYPE
    };
  }

  public static final class ShootrEventTable {

    private ShootrEventTable() {

    }

    public static final String TABLE = "ShootrEvent";

    public static final String ID_KEY = "idKey";
    public static final String TYPE = "type";
    public static final String TIMESTAMP = "timestamp";

    public static final String[] PROJECTION = {
        ID_KEY, TYPE, TIMESTAMP
    };
  }

  public static final class SynchroTable {

    private SynchroTable() {

    }

    public static final String TABLE = "SynchroTable";

    public static final String ENTITY = "entity";
    public static final String TIMESTAMP = "timestamp";

    public static final String[] PROJECTION = {
        ENTITY, TIMESTAMP
    };
  }

  public static final class PrivateMessageChannelTable implements SyncColumns {

    private PrivateMessageChannelTable() {

    }

    public static final String TABLE = "PrivateMessageChannel";

    public static final String ID_TARGET_USER = "idTargetUser";
    public static final String ID_PRIVATE_MESSAGE_CHANNEL = "idPrivateMessageChannel";
    public static final String TITLE = "title";
    public static final String IMAGE = "image";
    public static final String READ = "read";
    public static final String MUTED = "muted";
    public static final String LAST_MESSAGE_TIME = "lastMessageTime";
    public static final String LAST_MESSAGE_COMMENT = "lastMessageComment";

    public static final String[] PROJECTION = {
        ID_TARGET_USER, ID_PRIVATE_MESSAGE_CHANNEL, TITLE, IMAGE, READ, LAST_MESSAGE_TIME,
        LAST_MESSAGE_COMMENT, MUTED, BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
    };
  }

  public static final class PrivateMessageTable implements BaseMessageTable {

    private PrivateMessageTable() {

    }

    public static final String TABLE = "PrivateMessage";

    public static final String ID_PRIVATE_MESSAGE = "idPrivateMessage";
    public static final String ID_PRIVATE_MESSAGE_CHANNEL = "idPrivateMessageChannel";

    public static final String[] PROJECTION = {
        ID_PRIVATE_MESSAGE, ID_PRIVATE_MESSAGE_CHANNEL, ID_USER, USERNAME, COMMENT, IMAGE,
        IMAGE_WIDTH, IMAGE_HEIGHT, VIDEO_URL, VIDEO_TITLE, VIDEO_DURATION, VERIFIED_USER, ENTITIES,
        BIRTH, MODIFIED, DELETED, REVISION, SYNCHRONIZED
    };
  }

  public static final class StreamFilterTable {

    private StreamFilterTable() {
    }

    public static final String TABLE = "StreamFilter";

    public static final String ID_STREAM = "idStream";
    public static final String LAST_TIME_FILTERED = "lastTimeFiltered";

    public static final String[] PROJECTION = {
        ID_STREAM, LAST_TIME_FILTERED
    };
  }

  public static final class StreamConnectionsTable {

    private StreamConnectionsTable() {
    }

    public static final String TABLE = "StreamConnections";

    public static final String ID_STREAM = "idStream";
    public static final String CONNECTION_TIMES = "connectionsTime";

    public static final String[] PROJECTION = {
        ID_STREAM, CONNECTION_TIMES
    };
  }
}
