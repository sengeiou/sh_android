//
//  FilterCreation.m
//
//  Created by Christian Cabarrocas on 12/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FilterCreation.h"
#import "UserManager.h"
#import "CoreDataParsing.h"
#import "CoreDataManager.h"
#import "Shot.h"
#import "Follow.h"
#import "User.h"
#import "SyncManager.h"
#import "Team.h"

@implementation FilterCreation

//-----------------------------------------------------------------------------
+ (NSDictionary *)getFilterForEntity:(Class)entity {
    
    NSDictionary *filterDate = [self composeFilterDateForEntity:entity];
    
    if ([entity isSubclassOfClass:[Follow class]]){
        
        //Security check
        NSNumber *userID = [[UserManager singleton] getUserId];
        if (![userID isKindOfClass:[NSNumber class]])
            return @{};
        
        NSArray *filterItemsFollow = @[@{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_USER,K_CD_VALUE:userID},
                                       @{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:kJSON_FOLLOW_IDUSERFOLLOWED,K_CD_VALUE:[NSNull null]}];
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItemsFollow,K_WS_FILTERS:@[filterDate]}};
        return filter;

    }

    
    if ([entity isSubclassOfClass:[User class]]){
		
        //Security check
        NSNumber *userID = [[UserManager singleton] getUserId];
        if (![userID isKindOfClass:[NSNumber class]])
            return @{};
        
		NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@",userID];
		NSArray *follows = [[CoreDataManager singleton] getAllEntities:[Follow class] withPredicate:predicate];
		NSMutableArray *usersArray = [[NSMutableArray alloc] initWithCapacity:follows.count];
		for (Follow *followedUser in follows) {
			[usersArray addObject:@{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_USER,K_CD_VALUE:followedUser.idUserFollowed}];
		}
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:[NSNull null],K_WS_FILTERS:@[@{K_WS_FILTERITEMS:[usersArray copy],K_WS_FILTERS:[NSNull null],K_WS_OPS_NEXUS: K_WS_OPS_OR},filterDate]}};
        
        return filter;
    }
    
    if ([entity isSubclassOfClass:[Shot class]]){
        
        NSArray *filters = @[@{K_WS_FILTERITEMS:[self composeUsersToFilter],K_WS_FILTERS:[NSNull null],K_WS_OPS_NEXUS:K_WS_OPS_OR},
                             @{K_WS_FILTERITEMS:[NSNull null],K_WS_FILTERS:@[filterDate],K_WS_OPS_NEXUS:K_WS_OPS_OR}];
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:[NSNull null],K_WS_FILTERS:filters}};
        return filter;
    }
    
    if ([entity isSubclassOfClass:[Team class]]){
        
        NSArray *filters = @[@{K_WS_FILTERITEMS:[self composeUsersToFilter],K_WS_FILTERS:[NSNull null],K_WS_OPS_NEXUS:K_WS_OPS_OR},
                             @{K_WS_FILTERITEMS:[NSNull null],K_WS_FILTERS:@[filterDate],K_WS_OPS_NEXUS:K_WS_OPS_OR}];
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:[NSNull null],K_WS_FILTERS:filters}};
        return filter;
    }
    
    return @{};
}

//-----------------------------------------------------------------------------
+ (NSDictionary *)getFilterForUser:(User *)user {
    
    //Security check
    if (!user)
        return @{};
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@",user.idUser];
    NSArray *follows = [[CoreDataManager singleton] getAllEntities:[Follow class] withPredicate:predicate];
    NSMutableArray *usersArray = [[NSMutableArray alloc] initWithCapacity:follows.count];
    for (Follow *followedUser in follows) {
        [usersArray addObject:@{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_USER,K_CD_VALUE:followedUser.idUserFollowed}];
    }
    NSDictionary *filterDate = @{K_WS_FILTERITEMS:@[@{K_WS_COMPARATOR: K_WS_OPS_GE,K_CD_NAME:K_WS_OPS_UPDATE_DATE,K_CD_VALUE:@0},
                                                    @{K_WS_COMPARATOR: K_WS_OPS_GE,K_CD_NAME:K_WS_OPS_DELETE_DATE,K_CD_VALUE:@0}],
                                 K_WS_FILTERS:[NSNull null],
                                 K_WS_OPS_NEXUS:K_WS_OPS_OR};

    NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:[NSNull null],K_WS_FILTERS:@[@{K_WS_FILTERITEMS:[usersArray copy],K_WS_FILTERS:[NSNull null],K_WS_OPS_NEXUS: K_WS_OPS_OR},filterDate]}};
    
    return filter;

}

//-----------------------------------------------------------------------------
+ (NSDictionary *)getFilterForOldShots {
    
    NSDictionary *filterDate = [self composeFilterDateForOldShots];

    NSArray *filters = @[@{K_WS_FILTERITEMS:[self composeUsersToFilter],K_WS_FILTERS:[NSNull null],K_WS_OPS_NEXUS:K_WS_OPS_OR},
                         @{K_WS_FILTERITEMS:[NSNull null],K_WS_FILTERS:@[filterDate],K_WS_OPS_NEXUS:K_WS_OPS_OR}];
    NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:[NSNull null],K_WS_FILTERS:filters}};
    return filter;
}

//-----------------------------------------------------------------------------
+ (NSDictionary *)getFilterForFollowingsOfUser:(User *)user {
    
    //Security check
    if (!user)
        return @{};
    
    NSDictionary *filterDate = @{K_WS_FILTERITEMS:@[@{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:K_WS_OPS_UPDATE_DATE,K_CD_VALUE:[NSNull null]},
                                                    @{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:K_WS_OPS_DELETE_DATE,K_CD_VALUE:[NSNull null]}],
                                 K_WS_FILTERS:[NSNull null],
                                 K_WS_OPS_NEXUS:K_WS_OPS_OR};
    
    NSArray *filterItemsFollow = @[@{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_USER,K_CD_VALUE:user.idUser},
                                   @{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:kJSON_FOLLOW_IDUSERFOLLOWED,K_CD_VALUE:[NSNull null]}];
    NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItemsFollow,K_WS_FILTERS:@[filterDate]}};
    return filter;
}

//-----------------------------------------------------------------------------
+ (NSDictionary *)getFilterForFollowersOfUser:(User *)user {
    
    //Security check
    if (!user)
        return @{};
    
    NSDictionary *filterDate = @{K_WS_FILTERITEMS:@[@{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:K_WS_OPS_UPDATE_DATE,K_CD_VALUE:[NSNull null]},
                                                    @{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:K_WS_OPS_DELETE_DATE,K_CD_VALUE:[NSNull null]}],
                                 K_WS_FILTERS:[NSNull null],
                                 K_WS_OPS_NEXUS:K_WS_OPS_OR};
    
    NSArray *filterItemsFollow = @[@{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:kJSON_ID_USER,K_CD_VALUE:[NSNull null]},
                                   @{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_FOLLOW_IDUSERFOLLOWED,K_CD_VALUE:user.idUser}];
    NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItemsFollow,K_WS_FILTERS:@[filterDate]}};
    return filter;
}

#pragma mark - Helper methods
//-----------------------------------------------------------------------------
+ (NSDictionary *)composeFilterDateForEntity:(Class)entity {

    NSNumber *entityFilterDate = [[SyncManager singleton] getFilterDateForEntity:NSStringFromClass(entity)];
    
    NSDictionary *filterDate = @{K_WS_FILTERITEMS:@[@{K_WS_COMPARATOR: K_WS_OPS_GE,K_CD_NAME:K_WS_OPS_UPDATE_DATE,K_CD_VALUE:entityFilterDate},
                                                    @{K_WS_COMPARATOR: K_WS_OPS_GE,K_CD_NAME:K_WS_OPS_DELETE_DATE,K_CD_VALUE:entityFilterDate}],
                                 K_WS_FILTERS:[NSNull null],
                                 K_WS_OPS_NEXUS:K_WS_OPS_OR};
    return filterDate;
}

//-----------------------------------------------------------------------------
+ (NSDictionary *)composeFilterDateForFollowing {
    
    NSDictionary *filterDate = @{K_WS_FILTERITEMS:@[@{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:K_WS_OPS_UPDATE_DATE,K_CD_VALUE:[NSNull null]},
                                                    @{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:K_WS_OPS_DELETE_DATE,K_CD_VALUE:[NSNull null]}],
                                 K_WS_FILTERS:[NSNull null],
                                 K_WS_OPS_NEXUS:K_WS_OPS_OR};
    return filterDate;
}
//-----------------------------------------------------------------------------
+ (NSDictionary *)composeFilterDateForOldShots {
    
    NSNumber *entityFilterDate = [self getDateOfOlderShot];
    
    NSDictionary *filterDate = @{K_WS_FILTERITEMS:@[@{K_WS_COMPARATOR: K_WS_OPS_LT,K_CD_NAME:K_WS_OPS_UPDATE_DATE,K_CD_VALUE:entityFilterDate},
                                                    @{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:K_WS_OPS_DELETE_DATE,K_CD_VALUE:[NSNull null]}],
                                 K_WS_FILTERS:[NSNull null],
                                 K_WS_OPS_NEXUS:K_WS_OPS_AND};
    return filterDate;
}

//-----------------------------------------------------------------------------
+ (NSNumber *)getDateOfOlderShot {

    NSArray *shotsArray = [[CoreDataManager singleton] getAllEntities:[Shot class] orderedByKey:kJSON_MODIFIED ascending:YES withFetchLimit:@1];
    if (shotsArray.count == 1){
        
        return [shotsArray.firstObject csys_modified];
    }else
        return nil;
}

//-----------------------------------------------------------------------------
+ (NSArray *)composeUsersToFilter {
	
    //Security check
    NSNumber *userID = [[UserManager singleton] getUserId];
    if (![userID isKindOfClass:[NSNumber class]])
        return @[];
    
	NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@",userID];
	NSArray *follows = [[CoreDataManager singleton] getAllEntities:[Follow class] withPredicate:predicate];
	NSMutableArray *idUsersArray = [[NSMutableArray alloc] initWithCapacity:follows.count+1];
	for (Follow *obj in follows) {
		[idUsersArray addObject:@{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_USER,K_CD_VALUE:obj.idUserFollowed}];
	}
	
    if (userID != nil)
        [idUsersArray addObject:@{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:kJSON_ID_USER,K_CD_VALUE:userID}];
	
    if (idUsersArray.count > 0)
		return idUsersArray.copy;
	
	return nil;

}

@end
