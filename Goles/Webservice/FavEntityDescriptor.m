//
//  FavEntityDescriptor.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 19/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FavEntityDescriptor.h"
#import "CoreDataParsing.h"
#import "EventOfMatch.h"
#import "Match.h"
#import "Team.h"
#import "Subscription.h"
#import "SML.h"
#import "Message.h"
#import "AppAdvice.h"
#import "Tournament.h"

@implementation FavEntityDescriptor

//------------------------------------------------------------------------------
+ (NSDictionary *)createPropertyListForEntity:(Class)entityClass {
    
    if ([entityClass isSubclassOfClass:[Match class]]) {
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_ID_TEAM_LOCAL:[NSNull null],
                 kJSON_ID_TEAM_VISITOR:[NSNull null],
                 kJSON_ID_ROUND:[NSNull null],
                 kJSON_DATE_MATCH:[NSNull null],
//                 kJSON_LOCAL_MATCH_SCORE:[NSNull null],
//                 kJSON_VISITOR_MATCH_SCORE:[NSNull null],
                 kJSON_MATCH_TYPE:[NSNull null],
//                 kJSON_ELAPSED_MINUTES:[NSNull null],
                 kJSON_SCORE_TEAMLOCAL_PENALTIES:[NSNull null],
                 kJSON_SCORE_TEAMVISITOR_PENALTIES:[NSNull null],
                 kJSON_DATE_START:[NSNull null],
                 kJSON_DATE_FINAL:[NSNull null],
                 kJSON_MATCH_STATE:[NSNull null],
                 kJSON_ID_MATCH:[NSNull null],
//                 kJSON_TWITTERLOCAL:[NSNull null],
//                 kJSON_TWITTERVISITOR:[NSNull null],
//                 kJSON_MATCH_REFEREE:[NSNull null],
//                 kJSON_PREVIOUS_SCORELOCAL:[NSNull null],
//                 kJSON_PREVIOUS_SCOREVISITOR:[NSNull null]
                 };
    }
    else if ([entityClass isSubclassOfClass:[EventOfMatch class]]) {
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_ID_EVENT_OF_MATCH:[NSNull null],
                 kJSON_ID_MATCH:[NSNull null],
                 kJSON_EVENT_ID:[NSNull null],
                 kJSON_EVENT_MINUTEOFMATCH:[NSNull null],
                 kJSON_EVENT_STATUS:[NSNull null],
                 kJSON_EVENT_DATEIN:[NSNull null],
                 kJSON_EVENT_LOCAL_SCORE:[NSNull null],
                 kJSON_EVENT_VISITOR_SCORE:[NSNull null],
                 kJSON_EVENT_IDPERIOD:[NSNull null],
                 kJSON_EVENT_ACTOR_TRANSMITTER_NAME:[NSNull null],
                 kJSON_EVENT_ACTORIN_TRANSMITTER_NAME:[NSNull null],
                 kJSON_EVENT_ACTOR_RECEPTOR_NAME:[NSNull null],
                 //kJSON_EVENT_OWNER:[NSNull null],
                 //kJSON_EVENT_COMMENTS:[NSNull null],
                 //kJSON_EVENT_URL_GOALFILM:[NSNull null],
                 kJSON_EVENT_ISOWNGOAL:[NSNull null],
                 kJSON_EVENT_ISPENALTY_GOAL:[NSNull null],
                 //kJSON_EVENT_ISPENALTY_MISSEDSTICK:[NSNull null],
                 //kJSON_EVENT_ISPENALTY_MISSEDSTOPPED:[NSNull null],
                 //kJSON_EVENT_ISPENALTY_MISSEDOUT:[NSNull null]
                 kJSON_EVENT_ID:[NSNull null],
                 };
        
    }
    else if ([entityClass isSubclassOfClass:[Team class]]) {
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_NAME:[NSNull null],
                 kJSON_NAME_SHORT:[NSNull null],
                 kJSON_URL_IMAGE:[NSNull null],
                 kJSON_ID_TEAM:[NSNull null],
                 kJSON_NAME_TINY:[NSNull null],
                 kJSON_IS_NATIONAL_TEAM:[NSNull null],
                 kJSON_ID_MODE:[NSNull null]};
        
    }
    else if ([entityClass isSubclassOfClass:[Subscription class]]) {
        
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_ID_SML:[NSNull null],
                 kJSON_ID_SUBSCRIPTION:[NSNull null],
                 kJSON_ID_TEAM:[NSNull null],
                 kJSON_ID_MATCH:[NSNull null],
                 kJSON_NEGATION:[NSNull null],
                 kJSON_ID_ALL_EVENTS:[NSNull null]};
    }
    else if ([entityClass isSubclassOfClass:[SML class]]) {
        
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_ID_SML:[NSNull null],
                 kJSON_SOUND:[NSNull null],
                 kJSON_MESSAGE:[NSNull null],
                 kJSON_LANGUAGE:[NSNull null]};
    }
    else if ([entityClass isSubclassOfClass:[Message class]]) {
        
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_ID_MESSAGE:[NSNull null],
                 kJSON_MESSAGE_LANGUAGE:[NSNull null],
                 kJSON_MESSAGE_PLATFORM:[NSNull null],
                 kJSON_MESSAGE_MESSAGE:[NSNull null]};
    }
    
    else if ([entityClass isSubclassOfClass:[AppAdvice class]]) {
        
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_ID_APPADVICE:[NSNull null],
                 kJSON_ID_MESSAGE:[NSNull null],
                 kJSON_ADVICE_PATH:[NSNull null],
                 kJSON_ADVICE_PLATFORM:[NSNull null],
                 kJSON_ADVICE_STATUS:[NSNull null],
                 kJSON_ADVICE_BUTTON_VISIBLE:[NSNull null],
                 kJSON_ADVICE_DATESTART:[NSNull null],
                 kJSON_ADVICE_DATEEND:[NSNull null],
                 kJSON_ADVICE_WEIGHT:[NSNull null],
                 kJSON_ADVICE_BUTTON_ACTION:[NSNull null],
                 kJSON_ADVICE_BUTTON_DATA:[NSNull null],
                 kJSON_ADVICE_VERSION_START:[NSNull null],
                 kJSON_ADVICE_VERSION_END:[NSNull null],
                 kJSON_ADVICE_BUTTON_TEXTID:[NSNull null]};
    }
    
    else if ([entityClass isSubclassOfClass:[Tournament class]]) {
        
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_TOURNAMENT_ID_TOURNAMENT:[NSNull null],
//                 kJSON_TOURNAMENT_YEAR:[NSNull null],
                 kJSON_TOURNAMENT_IMAGE_NAME:[NSNull null],
                 kJSON_TOURNAMENT_IS_LEAGUE:[NSNull null],
                 kJSON_TOURNAMENT_DATE_START:[NSNull null],
                 kJSON_TOURNAMENT_DATE_END:[NSNull null],
                 kJSON_TOURNAMENT_NAME:[NSNull null],
                 kJSON_TOURNAMENT_ORDERBY:[NSNull null],
                 kJSON_TOURNAMENT_VISIBLE:[NSNull null],
                 kJSON_TOURNAMENT_COLOR_FIRST:[NSNull null],
                 kJSON_TOURNAMENT_COLOR_SECOND:[NSNull null],
                 kJSON_TOURNAMENT_COLOR_THIRD:[NSNull null],
                 kJSON_TOURNAMENT_COLOR_FOURTH:[NSNull null]};
    }

    return nil;
}

@end
