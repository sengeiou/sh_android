//
//  CoreDataParsing.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 16/10/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#ifndef Goles_Messenger_CoreDataParsing_pch
NSString *const Goles_Messenger_CoreDataParsing_pch;

#pragma mark - SYNCRO

NSString *const kJSON_UPDATED                           = @"updated";
NSString *const kJSON_URLIMAGE                          = @"urlImage";
NSString *const kJSON_REVISION                          = @"csys_revision";
NSString *const kJSON_BIRTH                             = @"csys_birth";
NSString *const kJSON_MODIFIED                          = @"csys_modified";
NSString *const kJSON_DELETED                           = @"csys_deleted";
NSString *const kJSON_SYNCRONIZED                       = @"csys_syncronized";
NSString *const kJSON_SYNCRO_NEW                        = @"n";
NSString *const kJSON_SYNCRO_UPDATED                    = @"u";
NSString *const kJSON_SYNCRO_DELETED                    = @"d";
NSString *const kJSON_SYNCRO_SYNCRONIZED                = @"s";

//SYNC_CONTROL
NSString *const k_SYNC_NAME_ENTITY                     = @"nameEntity";
NSString *const k_SYNC_LASTSERVER_DATE                 = @"lastServerDate";
NSString *const k_SYNC_LASTCALL                         = @"lastCall";
NSString *const k_SYNC_PRIORITY                        = @"updatePriority";
NSString *const k_SYNC_ALIAS                           = @"aliasView";

#pragma mark - REQUEST CREATION

//Operation Types
NSString *const K_OP_RETREAVE                           = @"retrieve";
NSString *const K_OP_INSERT                             = @"create";
NSString *const K_OP_UPDATE                             = @"update";
NSString *const K_OP_DELETE                             = @"delete";

//General blocks
NSString *const K_WS_REQ                                = @"req";
NSString *const K_WS_OPS                                = @"ops";
NSString *const K_WS_STATUS                             = @"status";

//Status block
NSString *const K_WS_STATUS_CODE                        = @"code";
NSString *const K_WS_STATUS_MESSAGE                     = @"message";
NSString *const K_WS_STATUS_OK                          = @"OK";
NSString *const K_WS_STATUS_KO                          = @"KO";
NSString *const K_WS_ALIAS                              = @"alias";

//Metadata block
NSString *const K_WS_OPS_METADATA                       = @"metadata";
NSString *const K_WS_OPS_OPERATION                      = @"operation";
NSString *const K_WS_OPS_ENTITY                         = @"entity";
NSString *const K_WS_OPS_TOTAL_ITEMS                    = @"totalItems";
NSString *const K_WS_OPS_INCLUDE_DELETED                = @"includeDeleted";
NSString *const K_WS_OPS_OFFSET                         = @"offset";
NSString *const K_WS_OPS_ITEMS                          = @"items";
NSString *const K_WS_OPS_KEY                            = @"key";
NSString *const K_WS_OPS_FILTER                         = @"filter";
NSString *const K_WS_OPS_NEXUS                          = @"nexus";
NSString *const K_WS_OPS_AND                            = @"and";
NSString *const K_WS_OPS_OR                             = @"or";
NSString *const K_WS_OPS_EQ                             = @"eq";
NSString *const K_WS_OPS_NE                             = @"ne";
NSString *const K_WS_OPS_GT                             = @"gt";
NSString *const K_WS_FILTERITEMS                        = @"filterItems";
NSString *const K_WS_COMPARATOR                         = @"comparator";
NSString *const K_WS_FILTERS                            = @"filters";
NSString *const K_WS_EPOCH                              = @"epoch";
NSString *const K_WS_TRUE                               = @"true";
NSString *const K_WS_FALSE                              = @"false";

//Synchro
NSString *const K_WS_OPS_DATA                           = @"data";
NSString *const K_WS_OPS_REVISION                       = @"revision";
NSString *const K_WS_OPS_BIRTH_DATE                     = @"birth";
NSString *const K_WS_OPS_UPDATE_DATE                    = @"modified";
NSString *const K_WS_OPS_DELETE_DATE                    = @"deleted";


#pragma mark - GENERAL

NSString *const K_CD_NAME                               = @"name";
NSString *const K_CD_VALUE                              = @"value";
NSString *const kCUOTAS_USERCLICK                       = @"userTapOnButtonInLandingCuotas";
NSString *const kFIRSTTIMEALERTMORE17                   = @"alertMore17isShowedAtFirstTime";
NSString *const kJSON_WEIGHT                            = @"weight";

#pragma mark - WEBSERVICE ALIAS

NSString *const kALIAS_GETALL_PROVIDERS                = @"GET_ALL_PROVIDERS";
NSString *const kALIAS_GET_MATCHBETTYPES               = @"GET_MATCH_BET_TYPES";
NSString *const kALIAS_GET_BETINFO                     = @"GET_MATCH_BETTYPE_ODD";
NSString *const kALIAS_GETALL_TEAMS                    = @"GET_ALL_TEAMS";
NSString *const kALIAS_GETALL_TOURNAMENTS              = @"GET_ALL_TOURNAMENTS";
NSString *const kALIAS_GETALL_SUSCRIPTIONS             = @"GET_ALL_DEVICE_SUSCRIPTIONS";
NSString *const kALIAS_GET_CALENDAR                    = @"GET_TEAMCALENDAR";
NSString *const kALIAS_GET_SML                         = @"GET_ALL_SML";
NSString *const kALIAS_GET_MESSAGE                     = @"GET_ALL_MESSAGE";
NSString *const kALIAS_GET_ADVICE                      = @"GET_ALL_ADVICE";
NSString *const kALIAS_GET_MATCHES_FOR_TEAM            = @"GET_ALL_MATCHES_FOR_TEAM";
NSString *const kALIAS_GET_ALL_TEAMS                   = @"GET_ALL_TEAMS";
NSString *const kALIAS_GETALL_CLASSIFICATIONS          = @"GET_ALL_CLASSIFICATIONS";
NSString *const kALIAS_GET_ALL_EVENTS_FOR_MATCH        = @"GET_ALL_EVENTS_FOR_MATCH";
NSString *const kALIAS_REGISTER_DEVICE                 = @"GET_ALL_EVENTS_FOR_MATCH";


#pragma mark - CORE DATA ENTITY NAMES

NSString *const K_COREDATA_APPADVICE                    = @"AppAdvice";
NSString *const K_COREDATA_CLASSIFICATION               = @"Classification";
NSString *const K_COREDATA_DEVICE                       = @"Device";
NSString *const K_COREDATA_EVENT                        = @"Event";
NSString *const K_COREDATA_EVENTOFMATCH                 = @"EventOfMatch";
NSString *const K_COREDATA_ROUND                        = @"Round";
NSString *const K_COREDATA_MATCH                        = @"Match";
NSString *const K_COREDATA_MESSAGE                      = @"Message";
NSString *const K_COREDATA_MODE                         = @"Mode";
NSString *const K_COREDATA_PLAYER                       = @"Player";
NSString *const K_COREDATA_TEAM                         = @"Team";
NSString *const K_COREDATA_TOURNAMENT                   = @"Tournament";
NSString *const K_COREDATA_MATCH_ODD                    = @"MatchOdd";
NSString *const K_COREDATA_SUSCRIPTIONS                 = @"Subscription";
NSString *const K_CDENTITY_SYNC_CONTROL                 = @"SyncControl";
NSString *const K_COREDATA_SML                          = @"SML";
NSString *const K_COREDATA_LINEUP                       = @"LineUp";


#pragma mark - CORE DATA CLASSES

//APPADVICE
NSString *const kJSON_ID_APPADVICE                      = @"idAppAdvice";
NSString *const kJSON_ADVICE_PATH                       = @"path";
NSString *const kJSON_ADVICE_PLATFORM                   = @"platform";
NSString *const kJSON_ADVICE_STATUS                     = @"status";
NSString *const kJSON_ADVICE_DATESTART                  = @"startDate";
NSString *const kJSON_ADVICE_DATEEND                    = @"endDate";
NSString *const kJSON_ADVICE_WEIGHT                     = @"weight";
NSString *const kJSON_ADVICE_BUTTON_VISIBLE             = @"visibleButton";
NSString *const kJSON_ADVICE_BUTTON_ACTION              = @"buttonAction";
NSString *const kJSON_ADVICE_BUTTON_DATA                = @"buttonData";
NSString *const kJSON_ADVICE_VERSION_START              = @"startVersion";
NSString *const kJSON_ADVICE_VERSION_END                = @"endVersion";
NSString *const kJSON_ADVICE_BUTTON_TEXTID              = @"buttonTextId";
NSString *const kJSON_ADVICE_IDMESSAGE                  = @"idMessage";


//DEVICE
NSString *const kJSON_ID_DEVICE                        = @"idDevice";
NSString *const kJSON_TOKEN                            = @"token";
NSString *const kJSON_DEVICE_OSVERSION                 = @"osVersion";
NSString *const kJSON_DEVICE_MODEL                     = @"model";
NSString *const kJSON_DEVICE_APPVERSION                = @"appVer";
NSString *const kJSON_DEVICE_LOCALE                    = @"locale";

//EVENT
NSString *const kJSON_EVENT_ID                         = @"idEvent";

//EVENTS OF MATCH
NSString *const kJSON_ID_EVENT_OF_MATCH                = @"idEventOfMatch";
NSString *const kJSON_EVENT_LOCAL_SCORE                = @"localScore";
NSString *const kJSON_EVENT_VISITOR_SCORE              = @"visitorScore";
NSString *const kJSON_EVENT_MINUTEOFMATCH              = @"matchMinute";
NSString *const kJSON_EVENT_STATUS                     = @"status";
NSString *const kJSON_EVENT_DATEIN                     = @"dateIn";
NSString *const kJSON_EVENT_IDPERIOD                   = @"idPeriod";
NSString *const kJSON_EVENT_ACTOR_TRANSMITTER_NAME     = @"actorTransmitterName";
NSString *const kJSON_EVENT_ACTORIN_TRANSMITTER_NAME   = @"actorInTransmitterName";
NSString *const kJSON_EVENT_ACTOR_RECEPTOR_NAME        = @"actorReceptorName";
NSString *const kJSON_EVENT_ISOWNGOAL                  = @"isOwnGoal";
NSString *const kJSON_EVENT_ISPENALTY_GOAL             = @"isPenaltyGoal";

// ROUND
NSString *const kJSON_ROUND                            = @"round";
NSString *const kJSON_ROUND_NAME                       = @"name";
NSString *const kJSON_ID_ROUND                         = @"idRound";
NSString *const kJSON_ROUND_START_DATE                 = @"startDate";
NSString *const kJSON_ROUND_END_DATE                   = @"endDate";
NSString *const kJSON_ROUND_TYPE                       = @"roundType";
NSString *const kJSON_MATCHES                          = @"matchesList";

//MATCH
NSString *const kJSON_MATCHLIST                        = @"matchList";
NSString *const kJSON_ID_MATCH                         = @"idMatch";
NSString *const kJSON_MATCH_STATE                      = @"matchState";
NSString *const kJSON_LOCAL_NAME                       = @"localName";
NSString *const kJSON_LOCAL_MATCH_SCORE                = @"localMatchScore";
NSString *const kJSON_ID_TEAM_LOCAL                    = @"idLocalTeam";
NSString *const kJSON_VISITOR_NAME                     = @"visitorName";
NSString *const kJSON_VISITOR_MATCH_SCORE              = @"visitorMatchScore";
NSString *const kJSON_ID_TEAM_VISITOR                  = @"idVisitorTeam";
NSString *const kJSON_MATCH_ODD                        = @"matchOdd";
NSString *const kJSON_DATE_MATCH                       = @"matchDate";
NSString *const kJSON_ELAPSED_MINUTES                  = @"minutes";
NSString *const kJSON_TIME_MATCH                       = @"timeMatch";
NSString *const kJSON_EVENT_DATE                       = @"dateIn";
NSString *const kJSON_MATCH_TYPE                       = @"matchType";
NSString *const kJSON_SCORE_TEAMLOCAL_PENALTIES        = @"scorePenaltiesLocalTeam";
NSString *const kJSON_SCORE_TEAMVISITOR_PENALTIES      = @"scorePenaltiesVisitorTeam";
NSString *const kJSON_DATE_FINAL                       = @"endTime";
NSString *const kJSON_TWITTERLOCAL                     = @"twitterLocal";
NSString *const kJSON_TWITTERVISITOR                   = @"twitterVisitor";
NSString *const kJSON_MATCH_REFEREE                    = @"referee";
NSString *const kJSON_PREVIOUS_SCORELOCAL              = @"previousScoreLocal";
NSString *const kJSON_PREVIOUS_SCOREVISITOR            = @"previousScoreVisitor";
NSString *const kJSON_SCORE_LOCAL                      = @"localStore";
NSString *const kJSON_SCORE_VISITOR                    = @"visitorStore";
NSString *const kJSON_DATE_START                       = @"startDate";


//MESSAGE
NSString *const kJSON_ID_MESSAGE                        = @"idMessage";
NSString *const kJSON_MESSAGE_MESSAGE                   = @"message";
NSString *const kJSON_MESSAGE_LANGUAGE                  = @"locale";
NSString *const kJSON_MESSAGE_PLATFORM                  = @"platform";

//MODE
NSString *const kJSON_ID_MODE                          = @"idMode";
NSString *const kJSON_LEAGUES                          = @"leagues";
NSString *const kJSON_TEAMS                            = @"teams";

//PLAYER
NSString *const kJSON_PLAYER_ID                        = @"playerId";
NSString *const kJSON_USER_NAME                        = @"userName";
NSString *const kJSON_ID_PLAYER                        = @"idPlayer";

//SWEEPSTAKEROUND
NSString *const kJSON_ID_TEAM                          = @"idTeam";
NSString *const kJSON_NAME_SHORT                       = @"nameShort";
NSString *const kJSON_NAME_TINY                        = @"nameTiny";
NSString *const kJSON_URL_IMAGE                        = @"urlImage";
NSString *const kJSON_ID_LEAGUE                        = @"idLeague";
NSString *const kJSON_IS_NATIONAL_TEAM                 = @"isNationalTeam";
NSString *const kJSON_NAME                             = @"name";
NSString *const kJSON_VALUE                            = @"value";

//SUBSCRIPTION
NSString *const kJSON_ID_SUBSCRIPTION                  = @"idSubscription";
NSString *const kJSON_SUBSCRIPTIONS                    = @"subscriptions";
NSString *const kJSON_GOAL                             = @"goal";
NSString *const kJSON_STARTENDMATCH                    = @"startEndMatch";
NSString *const kJSON_RED_CARD                         = @"redCard";
NSString *const kJSON_INTENSE_MODE_1H                  = @"intenseMode1H";
NSString *const kJSON_INTENSE_MODE_HALF_TIME           = @"intenseModeHALFTIME";
NSString *const kJSON_INTENSE_MODE_ALINEACION          = @"intenseModeALINEACION";
NSString *const kJSON_INTENSE_MODE_PENALTY             = @"intenseModePENALTY";
NSString *const kJSON_INTENSE_MODE_YELLOW              = @"intenseModeYELLOW";
NSString *const kJSON_INTENSE_MODE_CAMBIOS             = @"intenseModeCAMBIOS";
NSString *const kJSON_INTENSE_MODE_OFERTA_PARTIDO      = @"intenseModeOFERTAPARTIDO";
NSString *const kJSON_ID_ALL_EVENTS                    = @"idAllEvents";
NSString *const kJSON_NEGATION                         = @"negation";

//SUBSCRIPTION
NSString *const kJSON_SML                              = @"SML";
NSString *const kJSON_ID_SML                           = @"idSML";
NSString *const kJSON_SOUND                            = @"sound";
NSString *const kJSON_MESSAGE                          = @"message";
NSString *const kJSON_LANGUAGE                         = @"language";   // I know its incorrectly written but server seems to wait for it this way...

// TOURNAMENT
NSString *const kJSON_TOURNAMENT_ID_TOURNAMENT         = @"idTournament";
NSString *const kJSON_TOURNAMENT_YEAR                  = @"year";
NSString *const kJSON_TOURNAMENT_YEAR_TOURNAMENT       = @"YearTournament";
NSString *const kJSON_TOURNAMENT_IMAGE_NAME            = @"imageName";
NSString *const kJSON_TOURNAMENT_IS_LEAGUE             = @"isLeague";
NSString *const kJSON_TOURNAMENT_DATE_START            = @"dateStart";
NSString *const kJSON_TOURNAMENT_START_DATE            = @"startDate";
NSString *const kJSON_TOURNAMENT_END_DATE              = @"endDate";
NSString *const kJSON_TOURNAMENT_DATE_END              = @"dateEnd";
NSString *const kJSON_TOURNAMENT_NAME                  = @"name";
NSString *const kJSON_TOURNAMENT_ORDERBY               = @"orderBy";
NSString *const kJSON_TOURNAMENT_VISIBLE               = @"visibleApp";
NSString *const kJSON_TOURNAMENT_COLOR_FIRST           = @"firstC";
NSString *const kJSON_TOURNAMENT_COLOR_SECOND          = @"secondC";
NSString *const kJSON_TOURNAMENT_COLOR_THIRD           = @"thirdC";
NSString *const kJSON_TOURNAMENT_COLOR_FOURTH          = @"fourthC";


#pragma mark - SEGUES IDENTIFIERS
NSString *const kSEGUE_MATCH_DETAIL              = @"toMatchDetail";

#endif
