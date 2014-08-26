//
//  SubscriptionManager.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 27/03/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Match.h"	
#import "Team.h"

@interface SubscriptionManager : NSObject 

+ (SubscriptionManager *)singleton;
+ (SubscriptionManager *)sharedInstance;

//General methods
- (BOOL)intenseModeON:(Match *)match;
- (BOOL)basicModeON:(Match *)match;
- (BOOL)silenceModeON:(Match *)match;
- (BOOL)allSubscriptionsOFF:(Match *)match;
- (BOOL)allTeamSubscriptionsOFF:(Match *)match;
- (BOOL)anyNotDefinedSubscription:(Match *)match;

//Match methods
- (BOOL)isMatchInIntenseMode:(Match *)match;
- (BOOL)isMatchInBasicMode:(Match *)match;

//Team methods
- (BOOL)isTeamInIntenseMode:(Match *)match;
- (BOOL)isTeamInBasicMode:(Match *)match;
- (BOOL)teamBasicModeOn:(Team *)team;
- (BOOL)teamAnySubscription:(Match *)match;

//Math methods
- (NSNumber *)calculateIntenseModeID;
- (NSNumber *)calculateAdicionalesModeID;

//Setters
- (void)setSubscriptionMode:(int)type forMatch:(Match *)match to:(BOOL)active withDelegate:(id)delegate;

@end
