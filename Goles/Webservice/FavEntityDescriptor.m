//
//  FavEntityDescriptor.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 19/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FavEntityDescriptor.h"
#import "CoreDataParsing.h"
#import "Match.h"
#import "Team.h"
#import "SML.h"
#import "Message.h"
#import "AppAdvice.h"
#import "User.h"
#import "Follow.h"
#import "Shot.h"

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
                 kJSON_DATE_MATCH:[NSNull null],
                 kJSON_MATCH_TYPE:[NSNull null],
                 kJSON_SCORE_TEAMLOCAL_PENALTIES:[NSNull null],
                 kJSON_SCORE_TEAMVISITOR_PENALTIES:[NSNull null],
                 kJSON_DATE_START:[NSNull null],
                 kJSON_DATE_FINAL:[NSNull null],
                 kJSON_MATCH_STATE:[NSNull null],
                 kJSON_ID_MATCH:[NSNull null],
                 };
    }
    else if ([entityClass isSubclassOfClass:[Team class]]) {
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 K_CD_NAME:[NSNull null],
                 kJSON_TEAM_NAMESHORT:[NSNull null],
                 kJSON_TEAM_IDTEAM:[NSNull null]};
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
    else if ([entityClass isSubclassOfClass:[User class]]) {
        
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_ID_USER:[NSNull null],
                 kJSON_USERNAME:[NSNull null],
                 kJSON_ID_FAVOURITE_TEAM:[NSNull null],
                 kJSON_NAME:[NSNull null],
                 kJSON_PHOTO:[NSNull null]};

    }
    
    else if ([entityClass isSubclassOfClass:[Follow class]]) {
        
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_ID_USER:[NSNull null],
                 kJSON_FOLLOW_IDUSERFOLLOWED:[NSNull null]};
    }
    
    else if ([entityClass isSubclassOfClass:[Shot class]]) {
        
        return @{K_WS_OPS_REVISION:[NSNull null],
                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                 K_WS_OPS_DELETE_DATE:[NSNull null],
                 kJSON_ID_USER:[NSNull null],
                 kJSON_SHOT_IDSHOT:[NSNull null],
                 kJSON_SHOT_COMMENT:[NSNull null]};
    }



  
    return nil;
}


+ (NSDictionary *)createPropertyListForLogin {

    return @{K_WS_OPS_REVISION:[NSNull null],
             K_WS_OPS_BIRTH_DATE:[NSNull null],
             K_WS_OPS_UPDATE_DATE:[NSNull null],
             K_WS_OPS_DELETE_DATE:[NSNull null],
             kJSON_ID_USER:[NSNull null],
             kJSON_USERNAME:[NSNull null],
             kJSON_ID_FAVOURITE_TEAM:[NSNull null],
             kJSON_NAME:[NSNull null],
             kJSON_PHOTO:[NSNull null],
             kJSON_SESSIONTOKEN:[NSNull null]};

}

@end
