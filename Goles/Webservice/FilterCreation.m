//
//  FilterCreation.m
//  Goles
//
//  Created by Christian Cabarrocas on 12/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FilterCreation.h"
#import "UserManager.h"
#import "CoreDataParsing.h"
#import "Match.h"
#import "Message.h"
#import "SML.h"
#import "AppAdvice.h"
#import "Team.h"

@implementation FilterCreation

//-----------------------------------------------------------------------------
+ (NSDictionary *)getFilterForEntity:(Class)entity {
    
    if ([entity isSubclassOfClass:[AppAdvice class]]){
        NSArray *filterItems = @[@{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:K_WS_OPS_UPDATE_DATE,K_CD_VALUE:[NSNull null]},
                                 @{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:K_WS_OPS_DELETE_DATE,K_CD_VALUE:[NSNull null]},
                                 @{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_MESSAGE_PLATFORM,K_CD_VALUE:@1}];
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItems,K_WS_FILTERS:@[]}};
        return filter;
    }
    
    else if ([entity isSubclassOfClass:[Match class]]){
        NSArray *filterItemsTeams = @[@{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_TEAM_LOCAL,K_CD_VALUE:@3},
                                      @{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_TEAM_VISITOR,K_CD_VALUE:@3}];
        
        NSDictionary *filter = @{K_WS_OPS_NEXUS: K_WS_OPS_OR,K_WS_FILTERITEMS:filterItemsTeams,K_WS_FILTERS:@[]};
        return filter;
    }
    
    else if ([entity isSubclassOfClass:[Message class]]){
        NSArray *filterItems = @[@{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:kJSON_ID_MESSAGE,K_CD_VALUE:[NSNull null]},
                                 @{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_MESSAGE_PLATFORM,K_CD_VALUE:@1},
                                 @{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:kJSON_MESSAGE_LANGUAGE,K_CD_VALUE:[NSNull null]}];
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItems,K_WS_FILTERS:@[]}};
        return filter;
    }
    else if ([entity isSubclassOfClass:[SML class]]){
        NSArray *filterItems = @[@{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:kJSON_ID_SML,K_CD_VALUE:[NSNull null]}];
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItems,K_WS_FILTERS:@[]}};
        return filter;
    }
    
    else if ([entity isSubclassOfClass:[Team class]]){
        NSArray *filterItemsTeams = @[@{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:kJSON_TEAM_IDTEAM,K_CD_VALUE:[NSNull null]}];
        
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItemsTeams,K_WS_FILTERS:@[]}};
        return filter;
    }
    
    return @{};
}


@end
