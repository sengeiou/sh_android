package gm.mobi.android.db.model;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by InmaculadaAlcon on 11/08/2014.
 */
public class GMContract {

    public interface Tables{
        static final String APP_ADVICE = "AppAdvice";
        static final String BET_TYPE = "BetType";
        static final String BET_TYPE_ODD = "BetTypeOdd";
        static final String CLASSIFICATION = "Classification";
        static final String TEAM = "Team";
        static final String MODE = "Mode";
        static final String DEVICE = "Device";
        static final String EVENT_OF_MATCH = "EventOfMatch";
        static final String ROUND = "Round";
        static final String MODE_TOURNAMENT = "LeagueTournament";
        static final String LINEUP = "LineUp";
        static final String MATCH = "Match";
        static final String MATCH_BET_TYPE ="MatchBetType";
        static final String MESSAGE = "Message";
        static final String PROVIDER = "Provider";
        static final String SML = "SML";
        static final String SUBSCRIPTION = "Subscription";
        static final String TV = "TV";
        static final String TV_MATCH = "TVMatch";
        static final String TEAM_CALENDAR = "TeamCalendar";
        static final String TOURNAMENT = "Tournament";
        static final String PLAYER = "Player";
    }
    public static interface SyncColumns extends BaseColumns {
        static final String CSYS_BIRTH = "csys_birth";
        static final String CSYS_MODIFIED = "csys_modified";
        static final String CSYS_DELETED = "csys_deleted";
        static final String CSYS_REVISION = "csys_revision";
        static final String CSYS_SYNCHRONIZED = "csys_synchronized";
    }

    /*
    * Mode columns
    * */
    public interface ModeColumns{
        public static final String ID_MODE = "idMode";
        public static final String ORDER_BY = "orderBy";
        public static final String ID_LANGUAGE = "idLanguage";
        public static final String NAME = "name";
    }

    /*
    * AppAdvice columns
    * */

    public interface AppAdviceColumns {
        /**
         * Unique int identifying AppAdvice *
         */
        public static final String ID_APPADVICE = "idAppAdvice";
        /**
         * Path where the appAdvice Screen will be show up*
         * Example: /s_partidos/opLoadView; /s_classification/opLoadView;...
         */
        public static final String PATH = "path";
        /**
         * Unique int identifying the Message will be show up in the appAdvice Screen. ForeignKey *
         */
        public static final String ID_MESSAGE = "idMessage";
        /**
         * Unique int identifying platform id, ANDROID = 0*
         */
        public static final String PLATFORM = "platform";
        /**
         * Status of the AppAddvice, 0 = don't; 1 = do show up*
         */
        public static final String STATUS = "status";
        /**
         * Int that returns 1 if the button will be show up, 0 if it doesn't*
         */
        public static final String VISIBLE_BUTTON = "visibleButton";
        /**
         * String that identify the action of the button. Values: "continue, url+continue, url"
         * Example: "continue" after click on the button will be continue the normal navigation
         */
        public static final String BUTTON_ACTION = "buttonAction";
        /**
         * Identify the id of the message will be show up in the button. This information come from Message table*
         */
        public static final String BUTTON_TEXT_ID = "buttonTextId";
        /**
         * Url in case acts like link of a web or something related *
         */
        public static final String BUTTON_DATA = "buttonData";
        /**
         * Long that inform about the minimum version has to show up the appAdvice*
         */
        public static final String START_VERSION = "startVersion";
        /**
         * Long that inform about the maximum version has to show up the appAdvice*
         */
        public static final String END_VERSION = "endVersion";
        /**
         * Date informs about the start Date the appAdvice has to show up *
         */
        public static final String START_DATE = "startDate";
        /**
         * Date informs about the end Date the appAdvice has to show up *
         */
        public static final String END_DATE = "endDate";
        /**
         * Weight of importance*
         */
        public static final String WEIGHT = "weight";
    }

    public interface BetTypeColumns {
        /**
         * Unique int identifying BetType*
         */
        public static final String ID_BETTYPE = "idBetType";
        /**
         * Unique identifier not incremental for each kind of odd*
         */
        public static final String UNIQUE_KEY = "uniqueKey";
        /**
         * Bet type name*
         */
        public static final String NAME = "name";
        /**
         * Bet type description*
         */
        public static final String COMMENT = "comment";
        /**
         * If is informed , 1 is always visible and 0 doesn't *
         */
        public static final String ALWAYS_VISIBLE = "alwaysVisible";
        /**
         * It will define level of importance for order in the view*
         */
        public static final String WEIGHT = "weight";
        /**
         * Title of benefit column*
         */
        public static final String TITLE = "title";
    }

    public interface  BetTypeOddColumns {
        /**
         * Unique int identifying BetType odd*
         */
        public static final String ID_BETTYPEODD = "idBetTypeOdd";
        /**
         * Unique int identifying the relationship between match and BetTypeOdd*
         */
        public static final String ID_MATCHBETTYPE = "idMatchBetType";
        /**
         * Odd name*
         */
        public static final String NAME = "name";
        /**
         * Odd description*
         */
        public static final String COMMENT = "comment";
        /**
         * Odd value*
         */
        public static final String VALUE = "value";
        /**
         * Url betslip for this current odd*
         */
        public static final String URL = "url";
    }

    public interface ClassificationColumns {
        /**
         * Identify the idTournament of the classification*
         */
        public static final String ID_TOURNAMENT = "idTournament";
        /**
         * Identify the team int this classification*
         */
        public static final String ID_TEAM = "idTeam";
        /**
         * Played matches in local way *
         */
        public static final String PL = "pl";
        /**
         * Won matches in local way *
         */
        public static final String WL = "wl";
        /**
         * Lost matches in local way *
         */
        public static final String LL = "ll";
        /**
         * Drawn matches in local way *
         */
        public static final String DL = "dl";
        /**
         * Goals for local way*
         */
        public static final String GFL = "gfl";
        /**
         * Goals against local way*
         */
        public static final String GAL = "gal";
        /**
         * Played matches in visitor way*
         */
        public static final String PV = "pv";
        /**
         * Won matches in visitor way*
         */
        public static final String WV = "wv";
        /**
         * Lost matches in visitor way*
         */
        public static final String LV = "lv";
        /**
         * Drawn matches in visitor way*
         */
        public static final String DV = "dv";
        /**
         * Goals for visitor way*
         */
        public static final String GFV = "gfv";
        /**
         * Goals against visitor way*
         */
        public static final String GAV = "gav";
        /**
         * Points*
         */
        public static final String POINTS = "points";
        /**
         * Position in the clasification*
         */
        public static final String WEIGHT = "weight";
    }

    public interface EventOfMatchColumns {
        /**
         * Unique int identify the event in the match*
         */
        public static final String ID_EVENTOFMATCH = "idEventOfMatch";
        /**
         * Identify the match that the event belongs to*
         */
        public static final String ID_MATCH = "idMatch";
        /**
         * Identify the kind of event From the table Events*
         */
        public static final String ID_EVENT = "idEvent";
        /**
         * Date where the event happens during the match time*
         */
        public static final String DATE_IN = "dateIn";
        /**
         * Match minute when the event happens*
         */
        public static final String MATCH_MINUTE = "matchMinute";
        /**
         * Status of the match, not started, playing, halftime ... *
         */
        public static final String STATUS = "status";
        /**
         * It is an identifier from Periods table*
         */
        public static final String ID_PERIOD = "idPeriod";
        /**
         * Identify the team that event belongs to*
         */
        public static final String ID_TEAM = "idTeam";
        /**
         * Local score*
         */
        public static final String LOCAL_SCORE = "localScore";
        /**
         * Visitor score*
         */
        public static final String VISITOR_SCORE = "visitorScore";
        /**
         * It is the player make the action*
         */
        public static final String ACTOR_TRANSMITTER_NAME = "actorTransmitterName";
        /**
         * Receptor of the event*
         */
        public static final String ACTOR_IN_TRANSMITTER_NAME = "actorInTransmitterName";
        /**
         * Receptor of the event*
         */
        public static final String ACTOR_RECEPTOR_NAME = "actorReceptorName";
        /**
         *Flag indicates if the event goal is own. 1->Do, 0->Don't
         */
        public static final String IS_OWN_GOAL = "isOwnGoal";
        /**
         * Flag indicates if the goal is a penalty. 1->Do; 0->Don't*
         */
        public static final String IS_PENALTY_GOAL = "isPenaltyGoal";
        /**
         *Indicates the penalties score
         */
        public static final String PENALTIES_SCORE = "penaltiesScore";
    }

    public interface RoundColumns {
        /**
         * Unique int identifier for Round*
         */
        public static final String ID_ROUND = "idRound";
        /**
         * Int foreign key from Tournament table. It links the table Round and Tournament, each round belongs to one tournament*
         */
        public static final String ID_TOURNAMENT = "idTournament";
        /**
         * Date that the round starts*
         */
        public static final String START_DATE = "startDate";
        /**
         * Date that the round ends*
         */
        public static final String END_DATE = "endDate";

        /**
         * It will have value 0, away round. 1, rematch and 2 normal match *
         */
        public static final String ROUND_TYPE = "roundType";
        /**
         * Round name, come from RoundTitle table*
         */
        public static final String NAME = "name";
    }


    public interface ModeTournamentColumns {
        /**
         * Unique int identifier for LeagueTournament*
         */
        public static final String ID_MODE_TOURNAMENT = "idModeTournament";
        /**
         * Identify the idTournament from Tournament table*
         */
        public static final String ID_TOURNAMENT = "idTournament";
        /**
         * Identify the League*
         */
        public static final String ID_MODE = "idMode";
    }

    public interface LineUpColumns {
        /**Atributos en servidor**/
        /**
         * All the players. Main lineup *
         */
        public static final String LINEUP = "lineUp";
        /**
         * Lineup extra info --> reserve *
         */
        public static final String LINEUP_EXTRA_INFO = "lineUpExtraInfo";

        /**Atributos que han de estar en la BD*/
        /**Lineup identifier**/
        public static final String ID_LINEUP ="idLineUp";
        /**Match identifier where the lineup belongs to**/
        public static final String ID_MATCH = "idMatch";
        /**Team identifier where the lineup belongs to**/
        public static final String ID_TEAM  = "idTeam";
        /**Formation Ex. 4-3-3; 5-3-2,...**/
        public static final String FORMATION = "formation";
        /**Coach name**/
        public static final String COACH = "coach";
        /**Goalkeeper name**/
        public static final String GOALKEEPER = "goalkeeper";
        /**String defender names**/
        public static final String DEFENDERS = "defenders";
        /**String midfielder names**/
        public static final String MIDFIELDER = "midfielder";
        /**String midfielder2 names**/
        public static final String MIDFIELDER2 = "midfielder2";
        /**String striker names**/
        public static final String STRIKER = "striker";
        /**String reserve names**/
        public static final String RESERVE = "reserve";
    }

    public interface MatchColumns {
        /**
         * Unique identifier for a Match*
         */
        public static final String ID_MATCH = "idMatch";
        /**
         * Local team identifier*
         */
        public static final String ID_LOCAL_TEAM = "idLocalTeam";
        /**
         * Visitor team identifier*
         */
        public static final String ID_VISITOR_TEAM = "idVisitorTeam";
        /**
         * Fixture identifier*
         */
        public static final String ID_ROUND = "idRound";
        /**
         * Match date*
         */
        public static final String MATCH_DATE = "matchDate";
        /**
         * Not confirmed match date*
         */
        public static final String NOT_CONFIRMED_MATCH_DATE = "notConfirmedMatchDate";
        /**
         * Local score*
         */
        public static final String LOCAL_SCORE = "localScore";
        /**
         * Visitor score*
         */
        public static final String VISITOR_SCORE = "visitorScore";
        /**
         * Match type can be 0: normal; 1: final; 2: classified*
         */
        public static final String MATCH_TYPE = "matchType";
        /**
         * Local score team penalties goals*
         */
        public static final String SCORE_PENALTIES_LOCAL_TEAM = "scorePenaltiesLocalTeam";
        /**
         * Visitor score team penalties goals*
         */
        public static final String SCORE_PENALTIES_VISITOR_TEAM = "scorePenaltiesVisitorTeam";
        /**
         * It seems like end time match*
         */
        public static final String END_DATE = "endDate";
        /**
         * Match state*
         */
        public static final String MATCH_STATE = "matchState";
        /**
         * Identify the previous match in case is a return match*
         */
        public static final String ID_PREVIOUS_MATCH = "idPreviousMatch";
        /**
         * Identify winner team*
         */
        public static final String ID_WINNER_TEAM = "idWinnerTeam";
        /**
         * Overtime start time*
         */
        public static final String OVERTIME_START_DATE = "overTimeStartDate";
        /**
         * 2half time start*
         */
        public static final String SECOND_HALF_START_DATE = "secondHalfStartDate";
        /**
         * Startdate match*
         */
        public static final String START_DATE = "startDate";
    }

    public interface MatchBetTypeColumns {
        /**
         * MatchBetType unique Int identifier *
         */
        public static final String ID_MATCH_BETTYPE = "idMatchBetType";
        /**
         * Provider identifier *
         */
        public static final String ID_PROVIDER = "idProvider";
        /**
         * Match identifier which the bettype belongs to *
         */
        public static final String ID_MATCH = "idMatch";
        /**
         * Bet type identifier, It's a foreign key from bettype table *
         */
        public static final String ID_BETTYPE = "idBetType";
    }

    public interface MessageColumns {
        /**
         * Unique int identifier Message*
         */
        public static final String ID_MESSAGE = "idMessage";
        /**
         * Platform: Could be 0 -> ANDROID ; 1 -> iOS; 2 -> WP*
         */
        public static final String PLATFORM = "platform";
        /**
         * String which determines locale. Could be for example : "es_ES";"ca_ES" *
         */
        public static final String LOCALE = "locale";
        /**
         * String message information*
         */
        public static final String MESSAGE = "message";
    }

    public interface ProviderColumns {
        /**
         * Unique int identifier Provider*
         */
        public static final String ID_PROVIDER = "idProvider";
        /**
         * Provider name*
         */
        public static final String NAME = "name";

        /**
         * Unique not incremental identifier for each provider*
         */
        public static final String UNIQUE_KEY = "uniqueKey";
        /**
         * Is it visible this provider for iOS devices *
         */
        public static final String IOS_VISIBLE = "IOSVisible";
        /**
         * Is it visible this provider for Android devices 1->yes; 0 -> false*
         */
        public static final String ANDROID_VISIBLE = "AndroidVisible";
        /**
         * Is it visible this provider for WP devices *
         */
        public static final String WP_VISIBLE = "WPVisible";
        /**
         * Url for the registry in the provider*
         */
        public static final String REGISTRY_URL = "registryURL";
    }

    public interface SMLColumns {
        /**
         * SML Unique identifier *
         */
        public static final String ID_SML = "idSML";
        /**
         * It's a value which could be 0-> Default System sound; 1->Goal sound; 2->Applause; 3 -> whistles*
         */
        public static final String SOUND = "sound";
        /**
         * It identifies the message to show up in the notifications. Could be 0 -> 'Gol'; 1->'Goool' ; 2->'Gooooool'*
         */
        public static final String MESSAGE = "message";
        /**
         * It identifies tupla message_sound language. Could be 0->? 1->?*
         */
        public static final String LANGUAGE = "language";
    }


    public interface SubscriptionColumns {
        /**
         * Subscription Identifier This must be the unique identifier*
         */
        public static final String ID_SUBSCRIPTION = "idSubscription";
        /**
         * Device identifier which one is the subscriptor*
         */
        public static final String ID_DEVICE = "idDevice";
        /**
         * Match identifier for match subscription or null otherwise*
         */
        public static final String ID_MATCH = "idMatch";
        /**
         * Team identifier for team subcription or null otherwise*
         */
        public static final String ID_TEAM = "idTeam";
        /**
         * Composed Number for identifier the different subscription types *
         */
        public static final String ID_ALLEVENTS = "idAllEvents";
        /**
         * SML identifier belongs to  SML table*
         */
        public static final String ID_SML = "idSML";
        /**
         * It's a value which mutes matches. If its value is 1 the match is muted*
         */
        public static final String NEGATION = "negation";
    }

    public interface TVColumns{
        /**
         * Unique tv identifier
         */
        public static final String ID_TVS = "idTVs";
        /**
         * Tv name
         */
        public static final String NAME = "name";
        /**
         * TvType. Could be 1 or 2. The value 2 means ppv, otherwise open channel
         */
        public static final String TV_TYPE = "tvType";
    }
    public interface TVMatchColumns{
        /**
         * Match identifier
         */
        public static final String ID_MATCH = "idMatch";
        /**
         * TV identifier
         */
        public static final String ID_TVS = "idTVs";
    }

    public interface TeamColumns {
        /**
         * Unique identifier Team
         */
        public static final String ID_TEAM = "idTeam";
        /**
         * Team name*
         */
        public static final String NAME = "name";
        /**
         * Team short name*
         */
        public static final String SHORT_NAME = "shortName";
        /**
         * Team tiny name*
         */
        public static final String TINY_NAME = "tinyName";
        /**
         * URL Image*
         */
        public static final String IMAGE_URL = "imageUrl";
        /**
         * Mode identifier*
         */
        public static final String ID_MODE = "idMode";
        /**
         * Value 1 is nationalTeam*
         */
        public static final String IS_NATIONAL_TEAM = "isNationalTeam";
    }

    public interface TeamCalendarColumns {
        /**
         * Local team identifier*
         */
        public static final String ID_LOCAL_TEAM = "idTeamLocal";
        /**
         * Visitor team identifier*
         */
        public static final String ID_VISITOR_TEAM = "idTeamVisitor";
        /**
         * ShortName localTeam*
         */
        public static final String SHORT_LOCAL_NAME = "localNameShort";
        /**
         * ShortName visitorTeam*
         */
        public static final String SHORT_VISITOR_NAME = "visitorNameShort";
        /**
         * TinyName localTeam*
         */
        public static final String TINY_LOCAL_NAME = "localNameTiny";
        /**
         * TinyName visitorTeam*
         */
        public static final String TINY_VISITOR_NAME = "visitorNameTiny";
        /**
         * Round identifier, last fixture id*
         */
        public static final String ID_ROUND = "idRound";
        /**
         * Round description, last fixture description*
         */
        public static final String ROUND_DESCRIPTION = "roundDescription";
        /**
         * Round start date *
         */
        public static final String ROUND_START_DATE = "roundStartDate";
        /**
         * Round end date *
         */
        public static final String ROUND_END_DATE = "roundEndDate";
        /**
         * Match identifier*
         */
        public static final String ID_MATCH = "idMatch";
        /**
         * Match date*
         */
        public static final String DATE_MATCH = "dateMatch";
        /**
         * NotConfirmedMatchDate*
         */
        public static final String NOT_CONFIRMED_MATCH_DATE = "dateMatchNotConfirmed";
        /**
         * tvList*
         */
        public static final String TV_LIST = "listTV";
        /**
         * localScore*
         */
        public static final String LOCAL_SCORE = "scoreLocal";
        /**
         * visitorScore*
         */
        public static final String VISITOR_SCORE = "scoreVisitor";
        /**
         * matchState*
         */
        public static final String MATCH_STATE = "matchState";
        /**
         * tournament identifier*
         */
        public static final String ID_TOURNAMENT = "idTournament";
        /**
         * tournament name*
         */
        public static final String NAME_TOURNAMENT = "tournamentName";
        /**
         * tournament status*
         */
        public static final String TOURNAMENT_STATUS = "tournamentStatus";
        /**
         * mode identifier*
         */
        public static final String ID_MODE = "idMode";
        /**
         * mode name*
         */
        public static final String MODE_NAME = "modeName";
    }

    public interface TournamentColumns {
        /**
         * Tournament identifier*
         */
        public static final String ID_TOURNAMENT = "idTournament";
        /**
         * Tournament year*
         */
        public static final String YEAR_TOURNAMENT = "yearTournament";
        /**
         * Tournament status is for knowing if the tournament is active, for showing it up*
         */
        public static final String STATUS = "status";
        /**
         * Tournament start date*
         */
        public static final String START_DATE = "dateStart";
        /**
         * Tournament end date*
         */
        public static final String END_DATE = "dateEnd";
        /**
         * Image Tournament name*
         */
        public static final String IMAGE_NAME = "imageName";
        /**
         * 1 if it's a league*
         */
        public static final String IS_LEAGUE = "isLeague";
        /**
         * Tournament name*
         */
        public static final String NAME = "name";
        /**
         * order for showing it up in Tournaments screen*
         */
        public static final String ORDER_BY = "orderBy";
        /**
         * First color*
         */
        public static final String FIRST_C = "firstC";
        /**
         * Second color*
         */
        public static final String SECOND_C = "secondC";
        /**
         * Third color*
         */
        public static final String THIRD_C = "thirdC";
        /**
         * Fourth color*
         */
        public static final String FOURTH_C = "fourthC";

        /**
         * visible in the app?? then why we do use also status. We use both of them for compatibility*
         */
        public static final String VISIBLE_APP = "visibleApp";
    }

    public interface DeviceColumns{
        public static final String ID_DEVICE = "idDevice";
        public static final String ID_PLAYER = "idPlayer";
        public static final String NOTIFICATION_TOKEN = "notificationToken";
        public static final String OS_VERSION = "osVersion";
        public static final String APP_VERSION = "appVersion";
        public static final String MODEL ="model";
        public static final String LOCALE = "locale";
        public static final String UDID = "udid";
    }

    public interface PlayerColumns{
        public static final String ID_PLAYER = "idPlayer";
        public static final String FBK_TOKEN = "facebookToken";
        public static final String EMAIL = "email";
        public static final String USERNAME = "userName";
        public static final String PASSWORD = "password";
        public static final String PHOTO_URL = "photoUrl";
        public static final String LAST_SESSION = "lastSession";

    }

    /**
     * The authority of the content provider
     **/
    public static final String AUTHORITY = "gm.mobi.android.db.provider";

    /**
     * The content URI for the top-level authority
     **/
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);

    /**
     * Paths for tables
     **/
    private static final String PATH_APPADVICE = "app_advice";
    private static final String PATH_BETTYPE = "bet_type";
    private static final String PATH_BETTYPEODD = "bet_type_odd";
    private static final String PATH_CLASSIFICATION = "classification";
    private static final String PATH_EVENTOFMATCH = "event_of_match";
    private static final String PATH_LINEUP = "lineup";
    private static final String PATH_MANAGE_SUBSCRIPTION = "manage_subscription";
    private static final String PATH_MATCH = "match";
    private static final String PATH_MATCH_BET_TYPE = "match_bet_type";
    private static final String PATH_MESSAGE = "message";
    private static final String PATH_MODE = "mode";
    private static final String PATH_MODE_TOURNAMENT = "mode_tournament";
    private static final String PATH_PROVIDER = "provider";
    private static final String PATH_ROUND = "round";
    private static final String PATH_SML = "sml";
    private static final String PATH_SUBSCRIPTION = "subscription";
    private static final String PATH_TV = "tv";
    private static final String PATH_TV_MATCH = "tv_match";
    private static final String PATH_TEAM = "team";
    private static final String PATH_TEAM_CALENDAR = "team_calendar";
    private static final String PATH_TOURNAMENT = "tournament";


}
