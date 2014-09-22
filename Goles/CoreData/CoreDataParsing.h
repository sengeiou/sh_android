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
extern NSString *const kJSON_WEIGHT;

#pragma mark - WEBSERVICE ALIAS

extern NSString *const kALIAS_GET_SML;
extern NSString *const kALIAS_GET_MESSAGE;
extern NSString *const kALIAS_GET_ADVICE;
extern NSString *const kALIAS_GET_ALL_TEAMS;
extern NSString *const kALIAS_REGISTER_DEVICE;
extern NSString *const kALIAS_LOGIN;
extern NSString *const kALIAS_FOLLOW;
extern NSString *const kALIAS_SHOT;

#pragma mark - CORE DATA ENTITY NAMES

extern NSString *const K_COREDATA_APPADVICE;
extern NSString *const K_COREDATA_DEVICE;
extern NSString *const K_COREDATA_MATCH;
extern NSString *const K_COREDATA_MESSAGE;
extern NSString *const K_COREDATA_USER;
extern NSString *const K_COREDATA_TEAM;
extern NSString *const K_CDENTITY_SYNC_CONTROL;
extern NSString *const K_COREDATA_SML;

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

// DEVICE
extern NSString * const kJSON_ID_DEVICE;
extern NSString * const kJSON_TOKEN;
extern NSString *const kJSON_DEVICE_OSVERSION;
extern NSString *const kJSON_DEVICE_MODEL;
extern NSString *const kJSON_DEVICE_APPVERSION;
extern NSString *const kJSON_DEVICE_LOCALE;

//FOLLOW
extern NSString *const kJSON_FOLLOW_IDUSERFOLLOWED;

// TEAM
extern NSString * const kJSON_TEAM_IDTEAM;
extern NSString * const kJSON_TEAM_NAMESHORT;

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

//MESSAGE
extern NSString *const kJSON_ID_MESSAGE;
extern NSString *const kJSON_MESSAGE_MESSAGE;
extern NSString *const kJSON_MESSAGE_LANGUAGE;
extern NSString *const kJSON_MESSAGE_PLATFORM;

// USER
extern NSString *const kJSON_ID_USER;
extern NSString *const kJSON_USERNAME;
extern NSString *const kJSON_ID_FAVOURITE_TEAM;
extern NSString *const kJSON_SESSIONTOKEN;
extern NSString *const kJSON_EMAIL;
extern NSString *const kJSON_NAME;
extern NSString *const kJSON_PASSWORD;
extern NSString *const kJSON_PHOTO;

// SML
extern NSString * const kJSON_SML;
extern NSString * const kJSON_ID_SML;
extern NSString * const kJSON_SOUND;
extern NSString * const kJSON_MESSAGE;
extern NSString * const kJSON_LANGUAGE;

//SHOT
extern NSString *const kJSON_SHOT_IDSHOT;
extern NSString *const kJSON_SHOT_COMMENT;

#pragma mark - SEGUES IDENTIFIERS
extern NSString *const kSEGUE_MATCH_DETAIL;

#endif
