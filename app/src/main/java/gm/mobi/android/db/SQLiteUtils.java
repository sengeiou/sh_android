package gm.mobi.android.db;

import gm.mobi.android.db.provider.GMContract.Tables;
import gm.mobi.android.db.provider.GMContract.AppAdviceColumns;
import gm.mobi.android.db.provider.GMContract.SyncColumns;
import gm.mobi.android.db.provider.GMContract.MessageColumns;
import gm.mobi.android.db.provider.GMContract.BetTypeColumns;
import gm.mobi.android.db.provider.GMContract.BetTypeOddColumns;
import gm.mobi.android.db.provider.GMContract.ProviderColumns;
import gm.mobi.android.db.provider.GMContract.TVColumns;
import gm.mobi.android.db.provider.GMContract.SMLColumns;
import gm.mobi.android.db.provider.GMContract.RoundColumns;
import gm.mobi.android.db.provider.GMContract.ModeColumns;
import gm.mobi.android.db.provider.GMContract.TeamColumns;
import gm.mobi.android.db.provider.GMContract.MatchColumns;
import gm.mobi.android.db.provider.GMContract.MatchBetTypeColumns;
import gm.mobi.android.db.provider.GMContract.TournamentColumns;
import gm.mobi.android.db.provider.GMContract.TVMatchColumns;
import gm.mobi.android.db.provider.GMContract.TeamCalendarColumns;
import gm.mobi.android.db.provider.GMContract.ClassificationColumns;
import gm.mobi.android.db.provider.GMContract.EventOfMatchColumns;
import gm.mobi.android.db.provider.GMContract.ModeTournamentColumns;
import gm.mobi.android.db.provider.GMContract.LineUpColumns;
import gm.mobi.android.db.provider.GMContract.SubscriptionColumns;
/**;
 * Created by InmaculadaAlcon on 11/08/2014.
 */
public class SQLiteUtils {

    /**
     * Message table creation String
     * */
    public static final String MESSAGE_TABLE_CREATE = "CREATE IF NOT EXISTS "+ Tables.MESSAGE+"("
            +MessageColumns.ID_MESSAGE+" INT NOT NULL PRIMARY KEY,"
            +MessageColumns.PLATFORM+" INT,"
            +MessageColumns.LOCALE+" VARCHAR(255),"
            +MessageColumns.MESSAGE+" VARCHAR(2048),"
            +SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            +SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            +SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            +SyncColumns.CSYS_REVISION+" INT NULL,"
            +SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL"
            +" PRIMARY KEY("+MessageColumns.ID_MESSAGE+","+MessageColumns.PLATFORM+","+MessageColumns.LOCALE+")";


    /**
     * AppAdvice table creation String
     * */
    public static final String APPADVICE_TABLE_CREATE = "CREATE IF NOT EXISTS "+ Tables.APP_ADVICE+"("
            + AppAdviceColumns.ID_APPADVICE +" INT NOT NULL PRIMARY KEY,"
            + AppAdviceColumns.PATH + " VARCHAR(255) NOT NULL,"
            + AppAdviceColumns.ID_MESSAGE + " INT NOT NULL,"
            + AppAdviceColumns.PLATFORM +" INT NOT NULL,"
            + AppAdviceColumns.STATUS+" INT NOT NULL,"
            + AppAdviceColumns.VISIBLE_BUTTON+" INT NOT NULL,"
            + AppAdviceColumns.BUTTON_ACTION+" VARCHAR(255) NOT NULL,"
            + AppAdviceColumns.BUTTON_TEXT_ID+" INT,"
            + AppAdviceColumns.BUTTON_DATA+" VARCHAR(2048),"
            + AppAdviceColumns.START_VERSION+" INT,"
            + AppAdviceColumns.END_VERSION+" INT,"
            + AppAdviceColumns.START_DATE+" DATETIME,"
            + AppAdviceColumns.END_DATE+" DATETIME,"
            + AppAdviceColumns.WEIGHT+" INT"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL, "
            + " CONSTRAINT 'FK_BUTTON_TEXT_MESSAGES' FOREIGN KEY ("+AppAdviceColumns.BUTTON_TEXT_ID+","+AppAdviceColumns.PLATFORM+") "
            + " REFERENCES "+Tables.MESSAGE+"("+MessageColumns.ID_MESSAGE+","+MessageColumns.PLATFORM+") ON DELETE NO ACTION ON UPDATE NO ACTION,"
            + " CONSTRAINT 'FK_MESSAGES' FOREIGN KEY ("+MessageColumns.ID_MESSAGE+","+ MessageColumns.PLATFORM+")"
            + " REFERENCES "+Tables.MESSAGE+"("+MessageColumns.ID_MESSAGE+","+MessageColumns.PLATFORM+") ON DELETE NO ACTION ON UPDATE NO ACTION))";

    /**
     * BetType
     * */
    public static final String BETTYPE_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.BET_TYPE+"("
            + BetTypeColumns.ID_BETTYPE+" INT NOT NULL PRIMARY KEY,"
            + BetTypeColumns.UNIQUE_KEY+" INT NOT NULL,"
            + BetTypeColumns.NAME+" VARCHAR(32) NOT NULL,"
            + BetTypeColumns.TITLE+" VARCHAR(50) NULL,"
            + BetTypeColumns.COMMENT+" VARCHAR(256) NULL,"
            + BetTypeColumns.ALWAYS_VISIBLE+" INT,"
            + BetTypeColumns.WEIGHT+" INT,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL )";

    /**
     * Provider
     * */
    public static final String PROVIDER_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.PROVIDER+"("
            + ProviderColumns.ID_PROVIDER+" INT NOT NULL PRIMARY KEY,"
            + ProviderColumns.NAME+" VARCHAR(100) NOT NULL,"
            + ProviderColumns.UNIQUE_KEY+" VARCHAR(3) NOT NULL,"
            + ProviderColumns.IOS_VISIBLE+" INT,"
            + ProviderColumns.ANDROID_VISIBLE+" INT,"
            + ProviderColumns.WP_VISIBLE+" INT,"
            + ProviderColumns.REGISTRY_URL+" VARCHAR(255)"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL )";
    /**
     * TV
     * */
    public static final String TV_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.TV+"("
            + TVColumns.ID_TVS+" INT NOT NULL PRIMARY KEY,"
            + TVColumns.NAME+" VARCHAR(100) NOT NULL, "
            + TVColumns.TV_TYPE+" INT,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL )";

    /**
     * SML
     * */
    public static final String SML_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.SML+"("
            + SMLColumns.ID_SML+" INT NOT NULL PRIMARY KEY,"
            + SMLColumns.LANGUAGE+" INT NOT NULL,"
            + SMLColumns.MESSAGE+" INT NOT NULL,"
            + SMLColumns.SOUND+" INT NOT NULL,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    /**
     * ROUND/ OLD FIXTURE
     * */
    public static final String ROUND_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.ROUND+"("
            + RoundColumns.ID_ROUND+" INT NOT NULL PRIMARY KEY,"
            + RoundColumns.ID_TOURNAMENT+" INT,"
            + RoundColumns.NAME+" VARCHAR(255),"
            + RoundColumns.START_DATE+" DATETIME NULL,"
            + RoundColumns.END_DATE+" DATETIME NULL,"
            + RoundColumns.ROUND_TYPE+" INT NULL,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    /**
     * MODE
     * */
    public static final String MODE_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.MODE+"("
            + ModeColumns.ID_MODE+" INT NOT NULL PRIMARY KEY,"
            + ModeColumns.ID_LANGUAGE+" INT NOT NULL,"
            + ModeColumns.ORDER_BY+" INT NOT NULL,"
            + ModeColumns.NAME+" VARCHAR(255) NOT NULL,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    /**
     *TEAM
     * */
    public static final String TEAM_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.TEAM+"("
            + TeamColumns.ID_TEAM+" INT NOT NULL PRIMARY KEY,"
            + TeamColumns.ID_MODE+" INT NULL,"
            + TeamColumns.IS_NATIONAL_TEAM+" INT NULL,"
            + TeamColumns.NAME+" VARCHAR(255) NULL,"
            + TeamColumns.SHORT_NAME+" VARCHAR(255) NULL,"
            + TeamColumns.TINY_NAME+" VARCHAR(10) NULL,"
            + TeamColumns.IMAGE_URL+" VARCHAR(255),"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL,"
            + " CONSTRAINT 'FK_TEAM_MODE1' FOREIGN KEY('"+TeamColumns.ID_MODE+"') REFERENCES "+Tables.MODE+"("+TeamColumns.ID_MODE+") ON DELETE NO ACTION ON UPDATE NO ACTION))";

    /**
     *MATCH
     **/
    public static final String MATCH_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.MATCH+"("
            + MatchColumns.ID_MATCH +" INT NOT NULL PRIMARY KEY,"
            + MatchColumns.ID_LOCAL_TEAM +" INT,"
            + MatchColumns.ID_VISITOR_TEAM +" INT,"
            + MatchColumns.ID_ROUND + " INT,"
            + MatchColumns.MATCH_DATE+" DATETIME,"
            + MatchColumns.LOCAL_SCORE+" INT,"
            + MatchColumns.VISITOR_SCORE+" INT,"
            + MatchColumns.END_DATE+" DATETIME,"
            + MatchColumns.ID_PREVIOUS_MATCH+" INT,"
            + MatchColumns.MATCH_STATE+" INT,"
            + MatchColumns.START_DATE+" DATETIME,"
            + MatchColumns.NOT_CONFIRMED_MATCH_DATE+" DATETIME,"
            + MatchColumns.MATCH_TYPE+" INT,"
            + MatchColumns.SCORE_PENALTIES_LOCAL_TEAM+" INT,"
            + MatchColumns.SCORE_PENALTIES_VISITOR_TEAM+" INT,"
            + MatchColumns.OVERTIME_START_DATE+" DATETIME,"
            + MatchColumns.SECOND_HALF_START_DATE+" DATETIME,"
            + MatchColumns.ID_WINNER_TEAM+" INT,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    /**
     *MATCHBETTYPE
     **/

    public static final String MATCHBETTYPE_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.MATCH_BET_TYPE+"("
            + MatchBetTypeColumns.ID_MATCH_BETTYPE+" INT NOT NULL PRIMARY KEY,"
            + MatchBetTypeColumns.ID_PROVIDER+" INT,"
            + MatchBetTypeColumns.ID_MATCH+" INT,"
            + MatchBetTypeColumns.ID_BETTYPE+" INT,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    /**
     *TOURNAMENT
     **/
    public static final String TOURNAMENT_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.TOURNAMENT+"("
            + TournamentColumns.ID_TOURNAMENT+" INT NOT NULL PRIMARY KEY,"
            + TournamentColumns.YEAR_TOURNAMENT+" INT,"
            + TournamentColumns.STATUS+" INT,"
            + TournamentColumns.START_DATE+" DATETIME,"
            + TournamentColumns.END_DATE+" DATETIME,"
            + TournamentColumns.IMAGE_NAME+" VARCHAR(100),"
            + TournamentColumns.IS_LEAGUE+" INT,"
            + TournamentColumns.NAME+" VARCHAR(100),"
            + TournamentColumns.ORDER_BY+" INT,"
            + TournamentColumns.FIRST_C+" INT,"
            + TournamentColumns.SECOND_C+" INT,"
            + TournamentColumns.THIRD_C+" INT,"
            + TournamentColumns.FOURTH_C+" INT,"
            + TournamentColumns.VISIBLE_APP+" INT,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    /**
     *TVMATCH
     **/
    public static final String TVMATCH_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.TV_MATCH+"("
            + TVMatchColumns.ID_MATCH+" INT NOT NULL PRIMARY KEY,"
            + TVMatchColumns.ID_TVS+" INT NOT NULL,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";
    /**
     *TEAMCALENDAR
     **/
    public static final String TEAMCALENDAR_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.TEAM_CALENDAR+"("
            + TeamCalendarColumns.ID_LOCAL_TEAM+" INT,"
            + TeamCalendarColumns.ID_VISITOR_TEAM+" INT,"
            + TeamCalendarColumns.SHORT_LOCAL_NAME+" VARCHAR(100),"
            + TeamCalendarColumns.TINY_LOCAL_NAME+" VARCHAR(10),"
            + TeamCalendarColumns.SHORT_VISITOR_NAME+" VARCHAR(100),"
            + TeamCalendarColumns.TINY_VISITOR_NAME+" VARCHAR(10),"
            + TeamCalendarColumns.ID_ROUND+" INT,"
            + TeamCalendarColumns.ROUND_DESCRIPTION+" VARCHAR(255) NOT NULL,"
            + TeamCalendarColumns.ROUND_START_DATE+" DATETIME,"
            + TeamCalendarColumns.ROUND_END_DATE+" DATETIME,"
            + TeamCalendarColumns.ID_MATCH+" INT,"
            + TeamCalendarColumns.DATE_MATCH+" DATETIME,"
            + TeamCalendarColumns.NOT_CONFIRMED_MATCH_DATE+" INT,"
            + TeamCalendarColumns.TV_LIST+" VARCHAR(255),"
            + TeamCalendarColumns.LOCAL_SCORE+" INT,"
            + TeamCalendarColumns.VISITOR_SCORE+" INT,"
            + TeamCalendarColumns.MATCH_STATE+" INT,"
            + TeamCalendarColumns.ID_TOURNAMENT+" INT,"
            + TeamCalendarColumns.NAME_TOURNAMENT+" VARCHAR(255),"
            + TeamCalendarColumns.TOURNAMENT_STATUS+" INT,"
            + TeamCalendarColumns.ID_MODE+" INT,"
            + TeamCalendarColumns.MODE_NAME+ "VARCHAR(255),"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    public static final String BETTYPEODD_TABLE_CREATE = "CREATE IF NOT EXISTS "+Tables.BET_TYPE_ODD+"("
            + BetTypeOddColumns.ID_BETTYPEODD +" INT NOT NULL PRIMARY KEY,"
            + BetTypeOddColumns.ID_MATCHBETTYPE +" INT,"
            + BetTypeOddColumns.NAME+" VARCHAR(255),"
            + BetTypeOddColumns.COMMENT+" VARCHAR(255),"
            + BetTypeOddColumns.VALUE+" VARCHAR(255),"
            + BetTypeOddColumns.URL +" VARCHAR(255),"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    public static final String CLASSIFICATION_CREATE_TABLE = "CREATE IF NOT EXISTS "+Tables.CLASSIFICATION+"("
            + ClassificationColumns.ID_TOURNAMENT+" INT NOT NULL,"
            + ClassificationColumns.ID_TEAM+" INT NOT NULL,"
            + ClassificationColumns.PL +" INT,"
            + ClassificationColumns.WL+" INT,"
            + ClassificationColumns.LL+" INT,"
            + ClassificationColumns.DL+" INT,"
            + ClassificationColumns.GFL+" INT,"
            + ClassificationColumns.GAL+" INT,"
            + ClassificationColumns.PV+" INT,"
            + ClassificationColumns.WV+" INT,"
            + ClassificationColumns.LV+" INT,"
            + ClassificationColumns.DV+" INT,"
            + ClassificationColumns.GFV+" INT,"
            + ClassificationColumns.GAV+" INT,"
            + ClassificationColumns.POINTS+" INT,"
            + ClassificationColumns.WEIGHT+" INT,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    public static final String EVENTOFMATCH_CREATE_TABLE = "CREATE IF NOT EXISTS "+ Tables.EVENT_OF_MATCH+"("
            + EventOfMatchColumns.ID_EVENTOFMATCH+" INT NOT NULL PRIMARY KEY,"
            + EventOfMatchColumns.ID_MATCH+" INT,"
            + EventOfMatchColumns.ID_EVENT+" INT,"
            + EventOfMatchColumns.ID_PERIOD+" INT,"
            + EventOfMatchColumns.ID_TEAM+" INT,"
            + EventOfMatchColumns.DATE_IN+" DATETIME,"
            + EventOfMatchColumns.MATCH_MINUTE+" INT,"
            + EventOfMatchColumns.STATUS+" INT,"
            + EventOfMatchColumns.LOCAL_SCORE+" INT,"
            + EventOfMatchColumns.VISITOR_SCORE+" INT,"
            + EventOfMatchColumns.ACTOR_TRANSMITTER_NAME+" VARCHAR(75),"
            + EventOfMatchColumns.ACTOR_IN_TRANSMITTER_NAME+" VARCHAR(75),"
            + EventOfMatchColumns.ACTOR_RECEPTOR_NAME+" VARCHAR(75),"
            + EventOfMatchColumns.IS_OWN_GOAL+" INT,"
            + EventOfMatchColumns.IS_PENALTY_GOAL+" INT,"
            + EventOfMatchColumns.PENALTIES_SCORE+" VARCHAR(200),"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";


    public static final String MODETOURNAMENT_CREATE_TABLE ="CREATE IF NOT EXISTS "+Tables.MODE_TOURNAMENT+"("
            + ModeTournamentColumns.ID_MODE_TOURNAMENT+" INT NOT NULL PRIMARY KEY,"
            + ModeTournamentColumns.ID_MODE+" INT,"
            + ModeTournamentColumns.ID_TOURNAMENT+" INT,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    public static final String LINEUP_CREATE_TABLE="CREATE IF NOT EXISTS "+Tables.LINEUP+"("
            + LineUpColumns.ID_LINEUP+" INT NOT NULL PRIMARY KEY,"
            + LineUpColumns.ID_MATCH+" INT NOT NULL,"
            + LineUpColumns.ID_TEAM+" INT NOT NULL,"
            + LineUpColumns.FORMATION+" VARCHAR(10),"
            + LineUpColumns.COACH +" VARCHAR(100),"
            + LineUpColumns.GOALKEEPER+" VARCHAR(255),"
            + LineUpColumns.DEFENDERS+" VARCHAR(255),"
            + LineUpColumns.MIDFIELDER+" VARCHAR(255),"
            + LineUpColumns.MIDFIELDER2+" VARCHAR(255),"
            + LineUpColumns.STRIKER+" VARCHAR(255),"
            + LineUpColumns.RESERVE+" VARCHAR(255),"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    public static final String SUBSCRIPTION_CREATE_TABLE = "CREATE IF NOT EXISTS "+Tables.SUBSCRIPTION+"("
            + SubscriptionColumns.ID_SUBSCRIPTION+" INT NOT NULL PRIMARY KEY,"
            + SubscriptionColumns.ID_DEVICE+" INT,"
            + SubscriptionColumns.ID_MATCH+" INT,"
            + SubscriptionColumns.ID_TEAM+" INT,"
            + SubscriptionColumns.ID_ALLEVENTS+" INT,"
            + SubscriptionColumns.ID_SML+" INT,"
            + SubscriptionColumns.NEGATION+" INT,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

//    public static final String DEVICE_CREATE_TABLE = "CREATE IF NOT EXISTS "+Tables.DEVICE+"("


 }
