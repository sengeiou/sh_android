//
//  SubscriptionManager.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 27/03/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "SubscriptionManager.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "Subscription.h"
#import "Team.h"
#import "Event.h"

@implementation SubscriptionManager

#pragma mark - SINGLETON CREATION
//------------------------------------------------------------------------------
+ (SubscriptionManager *)singleton {
    
    static SubscriptionManager *sm = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sm = [[SubscriptionManager alloc] init];
    });
    return sm;
    
}

//------------------------------------------------------------------------------
+ (SubscriptionManager *)sharedInstance {
    
    return [self singleton];
}

#pragma mark - Singleton overwritten methods

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

#pragma mark - Public methods

//General methods
//------------------------------------------------------------------------------
- (BOOL)intenseModeON:(Match *)match {
    return ((match.subscription.idAllEventsValue == [[self calculateIntenseModeID] integerValue]) ||
            (match.teamLocal.subscription.idAllEventsValue == [[self calculateIntenseModeID] integerValue]) ||
            (match.teamVisitor.subscription.idAllEventsValue == [[self calculateIntenseModeID] integerValue]));
}

//------------------------------------------------------------------------------
- (BOOL)basicModeON:(Match *)match {
    return ((match.subscription.idAllEventsValue == [[self calculateBasicModeID] integerValue]) ||
            (match.teamLocal.subscription.idAllEventsValue == [[self calculateBasicModeID] integerValue]) ||
            (match.teamVisitor.subscription.idAllEventsValue == [[self calculateBasicModeID] integerValue]));
}

//------------------------------------------------------------------------------
- (BOOL)silenceModeON:(Match *)match {
    return (match.subscription.negationValue);
}

//------------------------------------------------------------------------------
- (BOOL)allSubscriptionsOFF:(Match *)match {
    return !((match.subscription && match.subscription.csys_syncronized != kJSON_SYNCRO_DELETED) || match.teamLocal.subscription || match.teamVisitor.subscription);
}

//------------------------------------------------------------------------------
- (BOOL)allTeamSubscriptionsOFF:(Match *)match {
    return !(match.teamLocal.subscription || match.teamVisitor.subscription);
}

//------------------------------------------------------------------------------
- (BOOL)anyNotDefinedSubscription:(Match *)match {
    return (((match.subscription.idAllEventsValue > 0) &&
            (match.subscription.idAllEventsValue != [[self calculateBasicModeID] integerValue]) &&
            (match.subscription.idAllEventsValue != [[self calculateIntenseModeID] integerValue])) ||
            ((match.teamLocal.subscription.idAllEvents > 0) &&
             (match.teamLocal.subscription.idAllEventsValue != [[self calculateBasicModeID] integerValue]) &&
             (match.teamLocal.subscription.idAllEventsValue != [[self calculateIntenseModeID] integerValue])) ||
            ((match.teamVisitor.subscription.idAllEvents > 0) &&
             (match.teamVisitor.subscription.idAllEventsValue != [[self calculateBasicModeID] integerValue]) &&
             (match.teamVisitor.subscription.idAllEventsValue != [[self calculateIntenseModeID] integerValue])));
}

//Match methods
//------------------------------------------------------------------------------
- (BOOL)isMatchInIntenseMode:(Match *)match {
    return (match.subscription.idAllEventsValue == [[self calculateIntenseModeID] integerValue]);
}

//------------------------------------------------------------------------------
- (BOOL)isMatchInBasicMode:(Match *)match {
    return (match.subscription.idAllEventsValue == [[self calculateBasicModeID] integerValue]);
}

//Team methods
//------------------------------------------------------------------------------
- (BOOL)isTeamInIntenseMode:(Match *)match {
    return ((match.teamLocal.subscription.idAllEventsValue == [[self calculateIntenseModeID] integerValue]) ||
    (match.teamVisitor.subscription.idAllEventsValue == [[self calculateIntenseModeID] integerValue]));
}

//------------------------------------------------------------------------------
- (BOOL)isTeamInBasicMode:(Match *)match {
    return ((match.teamLocal.subscription.idAllEventsValue == [[self calculateBasicModeID] integerValue]) ||
            (match.teamVisitor.subscription.idAllEventsValue == [[self calculateBasicModeID] integerValue]));
}

//------------------------------------------------------------------------------
- (BOOL)teamBasicModeOn:(Team *)team {
    return team.subscription.idAllEventsValue == [[self calculateBasicModeID] integerValue];
}
//------------------------------------------------------------------------------
- (BOOL)teamAnySubscription:(Match *)match{
    return match.teamLocal.subscription.idAllEventsValue != 0 || match.teamVisitor.subscription.idAllEventsValue != 0;
}


//Setters
//------------------------------------------------------------------------------
- (void)setSubscriptionMode:(int)type forMatch:(Match *)match to:(BOOL)active withDelegate:(id)delegate{

    Subscription *subscription = match.subscription;
    
    if (!subscription)
        subscription = [Subscription insertWithDictionary:@{kJSON_ID_MATCH:match.idMatch,kJSON_ID_SML:@"2",kJSON_ID_ALL_EVENTS:@0}];

    NSNumber *silenceMode = @0;
    
    if (active){
        switch (type) {
            case 0:
                subscription.idAllEvents = @0;
                silenceMode = @1;
                break;
            case 1:
                subscription.idAllEvents = @45470;
                break;
            case 2:
                subscription.idAllEvents = @4194302;
            default:
                break;
        }
    }
    else {
        subscription.idAllEvents = @0;
        silenceMode = @0;
    }
    
    match.subscription = subscription;
    match.subscription.negation = silenceMode;
    match.subscription.csys_syncronized = kJSON_SYNCRO_UPDATED;
    
    if (match.subscription.idAllEventsValue == 0 && match.subscription.negationValue == NO) {
        match.subscription.csys_syncronized = kJSON_SYNCRO_DELETED;
    }
    
    [[CoreDataManager sharedInstance] saveContext];
}


//Math methods
//------------------------------------------------------------------------------
- (NSNumber *)calculateIntenseModeID {

    NSArray *events = [[CoreDataManager singleton] getAllEntities:[Event class]];
    int64_t magicNumber = 0;
    
    for (Event *event in events) {
        magicNumber = magicNumber + [self getPowOfNumber:[event.idEvent intValue]];
    }
    return [NSNumber numberWithLongLong:magicNumber];
}

//------------------------------------------------------------------------------
- (NSNumber *)calculateAdicionalesModeID {
    NSArray *events = [[CoreDataManager singleton] getAllEntities:[Event class]];
    int64_t magicNumber = 0;
    
    NSArray *adicionalEvents = @[@14,@5,@6,@17,@9,@10,@11,@20,@16,@18,@21];
    
    for (Event *event in events) {
        for (NSNumber *eventValue in adicionalEvents) {
            if (event.idEventValue == [eventValue integerValue])
                magicNumber = magicNumber + [self getPowOfNumber:[event.idEvent intValue]];
        }
    }
    return [NSNumber numberWithLongLong:magicNumber];

}

//------------------------------------------------------------------------------
- (NSNumber *)calculateBasicModeID {
    
    NSArray *events = [[CoreDataManager singleton] getAllEntities:[Event class]];
    int64_t magicNumber = 0;
    
    NSArray *basicEvents = @[@1,@2,@3,@4,@7,@8,@12,@13,@15];
    
    for (Event *event in events) {
        for (NSNumber *eventValue in basicEvents) {
            if (event.idEventValue == [eventValue integerValue])
                magicNumber = magicNumber + [self getPowOfNumber:[event.idEvent intValue]];
        }
    }
    return [NSNumber numberWithLongLong:magicNumber];
}

//------------------------------------------------------------------------------
/*MARI*/

- (int64_t)getPowOfNumber:(int)number {
    
    switch (number) {
        case 1:
            return 2;
            break;
        case 2:
            return 4;
            break;
        case 3:
            return 8;
            break;
        case 4:
            return 16;
            break;
        case 5:
            return 32;
            break;
        case 6:
            return 64;
            break;
        case 7:
            return 128;
            break;
        case 8:
            return 256;
            break;
        case 9:
            return 512;
            break;
        case 10:
            return 1024;
            break;
        case 11:
            return 2048;
            break;
        case 12:
            return 4096;
            break;
        case 13:
            return 8192;
            break;
        case 14:
            return 16384;
            break;
        case 15:
            return 32768;
            break;
        case 16:
            return 65536;
            break;
        case 17:
            return 131072;
            break;
        case 18:
            return 262144;
            break;
        case 19:
            return 524288;
            break;
        case 20:
            return 1048576;
            break;
        case 21:
            return 2097152;
            break;
        default:
            break;
    }
    return 0;
}


@end
