//
//  CoreDataParsing.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 16/10/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#ifndef Goles_Messenger_CoreDataParsing_pch
#define Goles_Messenger_CoreDataParsing_pch

#pragma mark - SYNCRO

extern NSString * const kJSON_UPDATED;
extern NSString * const kJSON_URLIMAGE;
extern NSString * const kJSON_REVISION;
extern NSString * const kJSON_BIRTH;
extern NSString * const kJSON_MODIFIED;
extern NSString * const kJSON_DELETED;
extern NSString * const kJSON_SYNCRONIZED;
extern NSString * const kJSON_SYNCRO_NEW;
extern NSString * const kJSON_SYNCRO_UPDATED;
extern NSString * const kJSON_SYNCRO_DELETED;
extern NSString * const kJSON_SYNCRO_SYNCRONIZED;

//SYNC_CONTROL
extern NSString *const k_SYNC_NAME_ENTITY;
extern NSString *const k_SYNC_LASTSERVER_DATE;
extern NSString *const k_SYNC_LASTCALL;
extern NSString *const k_SYNC_PRIORITY;
extern NSString *const k_SYNC_ALIAS;

#pragma mark - REQUEST CREATION

//Operation Types
extern NSString *const K_OP_RETREAVE;
extern NSString *const K_OP_INSERT;
extern NSString *const K_OP_UPDATE;
extern NSString *const K_OP_DELETE;

//General blocks
extern NSString *const K_WS_REQ;
extern NSString *const K_WS_OPS;
extern NSString *const K_WS_STATUS;

//Status block
extern NSString *const K_WS_STATUS_CODE;
extern NSString *const K_WS_STATUS_MESSAGE;
extern NSString *const K_WS_STATUS_OK;
extern NSString *const K_WS_STATUS_KO;
extern NSString *const K_WS_ALIAS;

//Metadata block
extern NSString *const K_WS_OPS_METADATA;
extern NSString *const K_WS_OPS_OPERATION;
extern NSString *const K_WS_OPS_ENTITY;
extern NSString *const K_WS_OPS_TOTAL_ITEMS;
extern NSString *const K_WS_OPS_INCLUDE_DELETED;
extern NSString *const K_WS_OPS_OFFSET;
extern NSString *const K_WS_OPS_ITEMS;
extern NSString *const K_WS_OPS_KEY;
extern NSString *const K_WS_OPS_FILTER;
extern NSString *const K_WS_OPS_NEXUS;
extern NSString *const K_WS_OPS_AND;
extern NSString *const K_WS_OPS_OR;
extern NSString *const K_WS_OPS_GT;
extern NSString *const K_WS_OPS_EQ;
extern NSString *const K_WS_OPS_NE;
extern NSString *const K_WS_FILTERITEMS;
extern NSString *const K_WS_COMPARATOR;
extern NSString *const K_WS_FILTERS;
extern NSString *const K_WS_EPOCH;
extern NSString *const K_WS_TRUE;
extern NSString *const K_WS_FALSE;

//Synchro
extern NSString *const K_WS_OPS_DATA;
extern NSString *const K_WS_OPS_REVISION;
extern NSString *const K_WS_OPS_BIRTH_DATE;
extern NSString *const K_WS_OPS_UPDATE_DATE;
extern NSString *const K_WS_OPS_DELETE_DATE;

#pragma mark - GENERAL

extern NSString *const K_CD_NAME;
extern NSString *const K_CD_VALUE;
extern NSString * const kCUOTAS_USERCLICK;
extern NSString * const kFIRSTTIMEALERTMORE17;
extern NSString * const kJSON_WEIGHT;

#pragma mark - WEBSERVICE ALIAS

extern NSString *const kALIAS_GETALL_PROVIDERS;
extern NSString *const kALIAS_GET_MATCHBETTYPES;
extern NSString *const kALIAS_GET_BETINFO;
extern NSString *const kALIAS_GETALL_TEAMS;
extern NSString *const kALIAS_GETALL_TOURNAMENTS;
extern NSString *const kALIAS_GETALL_SUSCRIPTIONS;
extern NSString *const kALIAS_GET_CALENDAR;
extern NSString *const kALIAS_GET_SML;
extern NSString *const kALIAS_GET_MESSAGE;
extern NSString *const kALIAS_GET_ADVICE;
extern NSString *const kALIAS_GET_MATCHES_FOR_TEAM;
extern NSString *const kALIAS_GET_ALL_TEAMS;
extern NSString *const kALIAS_GETALL_CLASSIFICATIONS;
extern NSString *const kALIAS_GET_ALL_EVENTS_FOR_MATCH;
extern NSString *const kALIAS_REGISTER_DEVICE;

#pragma mark - CORE DATA ENTITY NAMES

extern NSString *const K_COREDATA_APPADVICE;
extern NSString *const K_COREDATA_CLASSIFICATION;
extern NSString *const K_COREDATA_DEVICE;
extern NSString *const K_COREDATA_EVENT;
extern NSString *const K_COREDATA_EVENTOFMATCH;
extern NSString *const K_COREDATA_ROUND;
extern NSString *const K_COREDATA_MATCH;
extern NSString *const K_COREDATA_MESSAGE;
extern NSString *const K_COREDATA_MODE;
extern NSString *const K_COREDATA_PLAYER;
extern NSString *const K_COREDATA_PROVIDER;
extern NSString *const K_COREDATA_TEAM;
extern NSString *const K_COREDATA_TOURNAMENT;
extern NSString *const K_COREDATA_MATCH_ODD;
extern NSString *const K_COREDATA_MATCHBETTYPE;
extern NSString *const K_COREDATA_BETTYPE;
extern NSString *const K_COREDATA_BETTYPEODD;
extern NSString *const K_COREDATA_PLAYERPROVIDER;
extern NSString *const K_CDENTITY_SYNC_CONTROL;
extern NSString *const K_COREDATA_SUSCRIPTIONS;
extern NSString *const K_COREDATA_SML;
extern NSString *const K_COREDATA_LINEUP;

#pragma mark - CORE DATA CLASSES

// APPADVICE
extern NSString *const kJSON_ID_APPADVICE;
extern NSString *const kJSON_ADVICE_PATH;
extern NSString *const kJSON_ADVICE_PLATFORM;
extern NSString *const kJSON_ADVICE_STATUS;
extern NSString *const kJSON_ADVICE_DATESTART;
extern NSString *const kJSON_ADVICE_DATEEND;
extern NSString *const kJSON_ADVICE_WEIGHT;
extern NSString *const kJSON_ADVICE_BUTTON_VISIBLE;
extern NSString *const kJSON_ADVICE_BUTTON_ACTION;
extern NSString *const kJSON_ADVICE_BUTTON_DATA;
extern NSString *const kJSON_ADVICE_VERSION_START;
extern NSString *const kJSON_ADVICE_VERSION_END;
extern NSString *const kJSON_ADVICE_BUTTON_TEXTID;
extern NSString *const kJSON_ADVICE_IDMESSAGE;

// BETTYPE
extern NSString *const kJSON_ID_BETTYPE;
extern NSString *const kJSON_BETTYPE;
extern NSString *const kJSON_ALWAYS_VISIBLE;
extern NSString *const kJSON_COMMENT;
extern NSString *const kJSON_TITLE;

// BETTYPEODD
extern NSString *const kJSON_BETTYPEODD;
extern NSString *const kJSON_ID_BETTYPEODD;
extern NSString *const kJSON_VALUE;
extern NSString *const kJSON_URL;

// CLASSIFICATION
extern NSString *const kJSON_CLASSIFICATION_PL;
extern NSString *const kJSON_CLASSIFICATION_WL;
extern NSString *const kJSON_CLASSIFICATION_LL;
extern NSString *const kJSON_CLASSIFICATION_DL;
extern NSString *const kJSON_CLASSIFICATION_GFL;
extern NSString *const kJSON_CLASSIFICATION_GAL;
extern NSString *const kJSON_CLASSIFICATION_PV;
extern NSString *const kJSON_CLASSIFICATION_WV;
extern NSString *const kJSON_CLASSIFICATION_LV;
extern NSString *const kJSON_CLASSIFICATION_DV;
extern NSString *const kJSON_CLASSIFICATION_GFV;
extern NSString *const kJSON_CLASSIFICATION_GAV;
extern NSString *const kJSON_CLASSIFICATION_POINTS;
extern NSString *const kJSON_CLASSIFICATION_WEIGHT;

// DEVICE
extern NSString * const kJSON_ID_DEVICE;
extern NSString * const kJSON_TOKEN;
extern NSString *const kJSON_DEVICE_OSVERSION;
extern NSString *const kJSON_DEVICE_MODEL;
extern NSString *const kJSON_DEVICE_APPVERSION;
extern NSString *const kJSON_DEVICE_LOCALE;

// EVENT
extern NSString * const kJSON_EVENT_ID;

// EVENTS OF MATCH
extern NSString * const kJSON_ID_EVENT_OF_MATCH;
extern NSString * const kJSON_EVENT_LOCAL_SCORE;
extern NSString * const kJSON_EVENT_VISITOR_SCORE;
extern NSString * const kJSON_EVENT_COMMENTS;
extern NSString * const kJSON_EVENT_MINUTEOFMATCH;
extern NSString * const kJSON_EVENT_STATUS;
extern NSString * const kJSON_EVENT_DATEIN;
extern NSString * const kJSON_EVENT_IDPERIOD;
extern NSString * const kJSON_EVENT_ACTOR_TRANSMITTER_NAME;
extern NSString * const kJSON_EVENT_ACTORIN_TRANSMITTER_NAME;
extern NSString * const kJSON_EVENT_ACTOR_RECEPTOR_NAME;
extern NSString * const kJSON_EVENT_ISOWNGOAL;
extern NSString * const kJSON_EVENT_ISPENALTY_GOAL;

// ROUND
extern NSString * const kJSON_ROUND;
extern NSString * const kJSON_ROUND_NAME;
extern NSString * const kJSON_ID_ROUND;
extern NSString * const kJSON_ROUND_START_DATE;
extern NSString * const kJSON_ROUND_END_DATE;
extern NSString * const kJSON_ROUND_TYPE;
extern NSString * const kJSON_MATCHES;

// LINEUP
extern NSString *const kJSON_LINEUP_ID;
extern NSString *const kJSON_LINEUP_GOALKEEPER;
extern NSString *const kJSON_LINEUP_DEFENDERS;
extern NSString *const kJSON_LINEUP_MIDFIELDER01;
extern NSString *const kJSON_LINEUP_MIDFIELDER02;
extern NSString *const kJSON_LINEUP_STRIKER;
extern NSString *const kJSON_LINEUP_RESERVE;
extern NSString *const kJSON_LINEUP_COACH;
extern NSString *const kJSON_LINEUP_FORMATION;

// MATCH
extern NSString * const kJSON_MATCHLIST;
extern NSString * const kJSON_ID_MATCH;
extern NSString * const kJSON_MATCH_STATE;
extern NSString * const kJSON_LOCAL_NAME;
extern NSString * const kJSON_LOCAL_MATCH_SCORE;
extern NSString * const kJSON_ID_TEAM_LOCAL;
extern NSString * const kJSON_VISITOR_NAME;
extern NSString * const kJSON_VISITOR_MATCH_SCORE;
extern NSString * const kJSON_ID_TEAM_VISITOR;
extern NSString * const kJSON_MATCH_ODD;
extern NSString * const kJSON_DATE_MATCH;
extern NSString * const kJSON_ELAPSED_MINUTES;
extern NSString * const kJSON_TIME_MATCH;
extern NSString * const kJSON_EVENT_DATE;
extern NSString * const kJSON_MATCH_TYPE;
extern NSString * const kJSON_SCORE_TEAMLOCAL_PENALTIES;
extern NSString * const kJSON_SCORE_TEAMVISITOR_PENALTIES;
extern NSString * const kJSON_DATE_FINAL;
extern NSString * const kJSON_ID_MATCHOFFER;
extern NSString * const kJSON_TWITTERLOCAL;
extern NSString * const kJSON_TWITTERVISITOR;
extern NSString * const kJSON_MATCH_REFEREE;
extern NSString * const kJSON_PREVIOUS_SCORELOCAL;
extern NSString * const kJSON_PREVIOUS_SCOREVISITOR;
extern NSString * const kJSON_SCORE_LOCAL;
extern NSString * const kJSON_SCORE_VISITOR;
extern NSString * const kJSON_DATE_START;

//MATCHBETTYPE
extern NSString * const kJSON_ID_MATCHBETTYPE;

//MESSAGE
extern NSString *const kJSON_ID_MESSAGE;
extern NSString *const kJSON_MESSAGE_MESSAGE;
extern NSString *const kJSON_MESSAGE_LANGUAGE;
extern NSString *const kJSON_MESSAGE_PLATFORM;

// MODE
extern NSString * const kJSON_ID_MODE;
extern NSString * const kJSON_LEAGUES;
extern NSString * const kJSON_TEAMS;

// PLAYER
extern NSString * const kJSON_PLAYER_ID;
extern NSString * const kJSON_USER_NAME;
extern NSString * const kJSON_ID_PLAYER;

// PLAYERPROVIDER
extern NSString *const kJSON_ID_PLAYERPROVIDER;
extern NSString *const kJSON_STATUS;

// PROVIDER
extern NSString * const kJSON_PROVIDER;
extern NSString * const kJSON_ID_PROVIDER;
extern NSString * const kJSON_NAME;
extern NSString * const kJSON_SWEEPSTAKE_ACTIVE;
extern NSString * const kJSON_MATCH_ODDS;
extern NSString * const kJSON_PREDICTION_ODDS;
extern NSString * const kJSON_UNIQUE_KEY;
extern NSString * const kJSON_VISIBLE;
extern NSString * const kJSON_PROVIDER_ACTIVE;
extern NSString * const kJSON_DISCLAIMER;
extern NSString * const kJSON_TRACKER;
extern NSString * const kJSON_ZONEID;
extern NSString * const kJSON_REGISTRYURL;
extern NSString * const kJSON_PARTNERID;

// SWEEPSTAKEROUND
extern NSString * const kJSON_ID_TEAM;
extern NSString * const kJSON_NAME_SHORT;
extern NSString * const kJSON_NAME_TINY;
extern NSString * const kJSON_URL_IMAGE;
extern NSString * const kJSON_ID_LEAGUE;
extern NSString * const kJSON_IS_NATIONAL_TEAM;

// SML
extern NSString * const kJSON_SML;
extern NSString * const kJSON_ID_SML;
extern NSString * const kJSON_SOUND;
extern NSString * const kJSON_MESSAGE;
extern NSString * const kJSON_LANGUAGE;

// SUBSCRIPTION
extern NSString * const kJSON_ID_SUBSCRIPTION;
extern NSString * const kJSON_SUBSCRIPTIONS;
extern NSString * const kJSON_GOAL;
extern NSString * const kJSON_STARTENDMATCH;
extern NSString * const kJSON_RED_CARD;
extern NSString * const kJSON_INTENSE_MODE_1H;
extern NSString * const kJSON_INTENSE_MODE_HALF_TIME;
extern NSString * const kJSON_INTENSE_MODE_ALINEACION;
extern NSString * const kJSON_INTENSE_MODE_PENALTY;
extern NSString * const kJSON_INTENSE_MODE_YELLOW;
extern NSString * const kJSON_INTENSE_MODE_CAMBIOS;
extern NSString * const kJSON_INTENSE_MODE_OFERTA_PARTIDO;
extern NSString * const kJSON_ID_ALL_EVENTS;
extern NSString * const kJSON_NEGATION;

// TOURNAMENT
extern NSString *const kJSON_TOURNAMENT_ID_TOURNAMENT;
extern NSString *const kJSON_TOURNAMENT_YEAR;
extern NSString *const kJSON_TOURNAMENT_YEAR_TOURNAMENT;
extern NSString *const kJSON_TOURNAMENT_IMAGE_NAME;
extern NSString *const kJSON_TOURNAMENT_IS_LEAGUE;
extern NSString *const kJSON_TOURNAMENT_DATE_START;
extern NSString *const kJSON_TOURNAMENT_START_DATE;
extern NSString *const kJSON_TOURNAMENT_END_DATE;
extern NSString *const kJSON_TOURNAMENT_DATE_END;
extern NSString *const kJSON_TOURNAMENT_NAME;
extern NSString *const kJSON_TOURNAMENT_ORDERBY;
extern NSString *const kJSON_TOURNAMENT_VISIBLE;
extern NSString *const kJSON_TOURNAMENT_COLOR_FIRST;
extern NSString *const kJSON_TOURNAMENT_COLOR_SECOND;
extern NSString *const kJSON_TOURNAMENT_COLOR_THIRD;
extern NSString *const kJSON_TOURNAMENT_COLOR_FOURTH;



#pragma mark - SEGUES IDENTIFIERS
extern NSString *const kSEGUE_MATCH_DETAIL;

#endif
