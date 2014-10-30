//
//  InfoManager.m
//  Shootr
//
//  Created by Christian Cabarrocas on 30/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "InfoManager.h"
#import "CoreDataManager.h"
#import "UserManager.h"

@implementation InfoManager

#pragma mark - SINGLETON
//------------------------------------------------------------------------------
+ (InfoManager *)singleton {
    
    static InfoManager *sharedInfoManager = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedInfoManager = [[InfoManager alloc] init];
    });
    return sharedInfoManager;
}

//------------------------------------------------------------------------------
- (id)init {
    self = [super init];
    if (self != nil) {
        
    }
    return self;
}

//------------------------------------------------------------------------------
- (id)copyWithZone:(NSZone *)zone {
    return self;
}

//------------------------------------------------------------------------------
+ (Match *)getUserNextMatch {
    
    NSInteger userTeamID = [[[UserManager singleton] getActiveUser] idFavoriteTeamValue];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idLocalTeam == %@ || idVisitorTeam = %@",userTeamID,userTeamID];
    NSArray *userMatchArray = [[CoreDataManager singleton] getAllEntities:[Match class] orderedByKey:kJSON_DATE_MATCH ascending:YES withPredicate:predicate];
    return userMatchArray.firstObject;
}

@end
