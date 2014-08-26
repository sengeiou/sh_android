//
//  LeaguesManager.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 14/10/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Mode.h"

#define kCHANGE_LEAGUE_NOTIFICATION         @"changeLeagueNotification"

@interface LeaguesManager : NSObject 

@property (nonatomic, strong) Mode      *mSelectedMode;

+ (LeaguesManager *)singleton;
+ (LeaguesManager *)sharedInstance;

- (NSArray *)getModes;
- (Mode *)getSelectedMode;
- (Mode *)getModeWithId:(NSUInteger)idMode;
- (NSArray *)getLeaguesFromCurrentMode;
- (NSArray *)getLeaguesFromMode:(NSNumber *)idMode;
- (NSArray *)getCurrentRounds;
//- (NSArray *)getPredictableMatchesInModes:(NSArray *)selectedModes andAlreadyPredictedMatches:(NSArray *)predictedMatches;
- (NSArray *)getSubscribedTeams;
- (BOOL)changeMode:(Mode *)newMode;

@end
