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
NSString *const k_SYNC_LASTCALL                        = @"lastCall";
NSString *const k_SYNC_PRIORITY                        = @"updatePriority";
NSString *const k_SYNC_ALIAS                           = @"aliasView";


#pragma mark - INTERNAL NOTIFICATIONS
NSString *const K_NOTIF_SHOT_END                           = @"shots_synchro_end";

#pragma mark - REQUEST CREATION

//Operation Types
NSString *const K_OP_RETREAVE                           = @"retrieve";
NSString *const K_OP_INSERT                             = @"create";
NSString *const K_OP_CREATE_UPDATE                      = @"createUpdate";
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
NSString *const K_WS_OPS_GE                             = @"ge";
NSString *const K_WS_OPS_LT                             = @"lt";
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
NSString *const kJSON_WEIGHT                            = @"weight";
NSString *const kJSON_LOGIN                             = @"Login";
NSString *const UD_LAST_DELETE_DATE                     = @"userDefaultsLastDeleteOlds";

#pragma mark - WEBSERVICE ALIAS

NSString *const kALIAS_GET_SML                         = @"GET_SML";
NSString *const kALIAS_GET_MESSAGE                     = @"GET_MESSAGE";
NSString *const kALIAS_GET_ADVICE                      = @"GET_ADVICE";
NSString *const kALIAS_GET_ALL_TEAMS                   = @"GET_TEAMS";
NSString *const kALIAS_REGISTER_DEVICE                 = @"REGISTER_DEVICE";
NSString *const kALIAS_LOGIN                           = @"USER_LOGIN";
NSString *const kALIAS_FOLLOW                          = @"GET_FOLLOWINGS";
NSString *const kALIAS_FOLLOWERS                       = @"GET_FOLLOWERS";
NSString *const kALIAS_SHOT                            = @"GET_SHOTS";
NSString *const kALIAS_OLDER_SHOTS                     = @"GET_OLDER_SHOTS";
NSString *const kALIAS_USER                            = @"GET_USERS";

#pragma mark - CORE DATA ENTITY NAMES

NSString *const K_COREDATA_APPADVICE                    = @"AppAdvice";
NSString *const K_COREDATA_DEVICE                       = @"Device";
NSString *const K_COREDATA_MATCH                        = @"Match";
NSString *const K_COREDATA_MESSAGE                      = @"Message";
NSString *const K_COREDATA_USER                         = @"User";
NSString *const K_COREDATA_TEAM                         = @"Team";
NSString *const K_CDENTITY_SYNC_CONTROL                 = @"SyncControl";
NSString *const K_COREDATA_SML                          = @"SML";
NSString *const K_COREDATA_FOLLOW                       = @"Follow";
NSString *const K_COREDATA_SHOT                         = @"Shot";

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

//FOLLOW
NSString *const kJSON_FOLLOW_IDUSERFOLLOWED            = @"idFollowedUser";

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

// TEAM
NSString * const kJSON_TEAM_IDTEAM                      =@"idTeam";
NSString * const kJSON_CLUB_NAME                        =@"clubName";
NSString * const kJSON_OFICIAL_NAME                     =@"oficialName";
NSString * const kJSON_SHORT_NAME                       =@"shortName";
NSString * const kJSON_TLA_NAME                         =@"tlaName";


//MESSAGE
NSString *const kJSON_ID_MESSAGE                        = @"idMessage";
NSString *const kJSON_MESSAGE_MESSAGE                   = @"message";
NSString *const kJSON_MESSAGE_LANGUAGE                  = @"locale";
NSString *const kJSON_MESSAGE_PLATFORM                  = @"platform";

//USER
NSString *const kJSON_ID_USER                          = @"idUser";
NSString *const kJSON_USERNAME                         = @"userName";
NSString *const kJSON_ID_FAVOURITE_TEAM                = @"idFavouriteTeam";
NSString *const kJSON_SESSIONTOKEN                     = @"sessionToken";
NSString *const kJSON_EMAIL                            = @"email";
NSString *const kJSON_NAME                             = @"name";
NSString *const kJSON_PASSWORD                         = @"password";
NSString *const kJSON_PHOTO                            = @"photo";
NSString *const kJSON_BIO                              = @"bio";
NSString *const kJSON_WEBSITE                          = @"website";
NSString *const kJSON_POINTS                           = @"points";
NSString *const kJSON_NUMFOLLOWING                     = @"numFollowings";
NSString *const kJSON_NUMFOLLOWERS                     = @"numFollowers";
NSString *const kJSON_RANK                             = @"rank";


//SML
NSString *const kJSON_SML                              = @"SML";
NSString *const kJSON_ID_SML                           = @"idSML";
NSString *const kJSON_SOUND                            = @"sound";
NSString *const kJSON_MESSAGE                          = @"message";
NSString *const kJSON_LANGUAGE                         = @"language";

//SHOT
NSString *const kJSON_SHOT_IDSHOT                      = @"idShot";
NSString *const kJSON_SHOT_COMMENT                     = @"comment";

#pragma mark - SEGUES IDENTIFIERS
NSString *const kSEGUE_MATCH_DETAIL              = @"toMatchDetail";

#endif
