//
//  FilterCreation.h
//
//  Created by Christian Cabarrocas on 12/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "User.h"

@interface FilterCreation : NSObject

+ (NSDictionary *)getFilterForEntity:(Class)entity;
+ (NSDictionary *)getFilterForOldShots;
+ (NSDictionary *)getFilterForFollowingsOfUser:(User *)user;
+ (NSDictionary *)getFilterForFollowersOfUser:(User *)user;
+ (NSDictionary *)getFilterForUser:(User *)user;
+ (NSDictionary *)getFilterForPeopleSearch;

@end
