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
#import "CoreDataManager.h"
#import "Match.h"
#import "Message.h"
#import "SML.h"
#import "AppAdvice.h"
#import "Team.h"
#import "Shot.h"
#import "Follow.h"
#import "User.h"

@implementation FilterCreation

//-----------------------------------------------------------------------------
+ (NSDictionary *)getFilterForEntity:(Class)entity {
    
    NSNumber *userID = [[UserManager singleton] getUserId];
    NSNumber *temporaryDateFilter = @1408779530000;
    
    NSDictionary *filterDate = @{K_WS_FILTERITEMS:@[@{K_WS_COMPARATOR: K_WS_OPS_GE,K_CD_NAME:K_WS_OPS_UPDATE_DATE,K_CD_VALUE:temporaryDateFilter},
                                                    @{K_WS_COMPARATOR: K_WS_OPS_GE,K_CD_NAME:K_WS_OPS_DELETE_DATE,K_CD_VALUE:temporaryDateFilter}],
                                 K_WS_FILTERS:[NSNull null],
                                 K_WS_OPS_NEXUS:K_WS_OPS_OR};
    
    if ([entity isSubclassOfClass:[Follow class]]){
        
        NSArray *filterItemsFollow = @[@{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_USER,K_CD_VALUE:userID},
                                       @{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:kJSON_FOLLOW_IDUSERFOLLOWED,K_CD_VALUE:[NSNull null]}];
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItemsFollow,K_WS_FILTERS:@[filterDate]}};
        return filter;
    }

    if ([entity isSubclassOfClass:[User class]]){
        
        NSArray *users = [[CoreDataManager singleton] getAllEntities:[User class]];
        NSMutableArray *usersArray = [[NSMutableArray alloc] initWithCapacity:users.count];
        
        for (User *userItem in users) {
            [usersArray addObject:@{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_USER,K_CD_VALUE:userItem.idUser}];
        }
        
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_OR,K_WS_FILTERITEMS:[usersArray copy],K_WS_FILTERS:@[filterDate]}};
        return filter;
    }
    
    if ([entity isSubclassOfClass:[Shot class]]){
        NSArray *filterItems = @[@{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:K_WS_OPS_UPDATE_DATE,K_CD_VALUE:[NSNull null]},
                                 @{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:K_WS_OPS_DELETE_DATE,K_CD_VALUE:[NSNull null]},
                                 @{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_USER,K_CD_VALUE:userID}];
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItems,K_WS_FILTERS:@[]}};
        return filter;
    }
    
    return @{};
}


@end
