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
#import "Watch.h"
#import "Match.h"
#import "User.h"
#import "MatchWatchers.h"

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

//------------------------------------------------------------------------------
+ (NSArray *)getMatchesWatchers {

    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"csys_deleted != %@",@"d"];
    NSArray *watches = [[CoreDataManager singleton] getAllEntities:[Watch class] withPredicate:predicate];
    return [self matchesWatchersConstructor:watches];
}

//------------------------------------------------------------------------------
+ (NSArray *)matchesWatchersConstructor:(NSArray *)watchesArray {

    NSMutableArray *watchesMatchesArray = [[NSMutableArray alloc] init];
    for (Watch *watch in watchesArray){
        if (![watchesMatchesArray containsObject:watch.match]) {
            MatchWatchers *matchW = [[MatchWatchers alloc] init];
            matchW.match = watch.match;
            [watchesMatchesArray addObject:matchW];
        }
    }
    
    for (MatchWatchers *matchWatch in watchesMatchesArray) {
        for (Watch *watch in watchesArray){
            if (matchWatch.match == watch.match)
                [matchWatch.userArray addObject:watch.user];
            
        }
    }
    
    return watchesMatchesArray;
}

@end
