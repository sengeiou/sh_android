//
//  ShotManager.m
//
//  Created by Christian Cabarrocas on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ShotManager.h"
#import "UserManager.h"
#import "CoreDataManager.h"
#import "FavRestConsumer.h"
#import "CoreDataParsing.h"
#import "Shot.h"

@implementation ShotManager

#pragma mark - SINGLETON
//------------------------------------------------------------------------------
+ (ShotManager *)singleton {
    
    static ShotManager *sharedShotManager = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedShotManager = [[ShotManager alloc] init];
    });
    return sharedShotManager;
}

//------------------------------------------------------------------------------
+ (ShotManager *)sharedInstance {
    return [self singleton];
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

#pragma mark - SHOT REQUEST
//------------------------------------------------------------------------------
- (NSArray *)getShotsForTimeLine {

    //NSTimeInterval nowDate = [[NSDate date] timeIntervalSince1970];
    //NSString *nowDateString = [NSString stringWithFormat:@"%f", nowDate*1000];
   // NSPredicate *predicate = [NSPredicate predicateWithFormat:@"user.idUser IN %@ AND csys_birth < %@",[[UserManager singleton] getActiveUsersIDs],nowDateString];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"user.idUser IN %@",[[UserManager singleton] getActiveUsersIDs]];

    return [[CoreDataManager sharedInstance] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:NO withPredicate:predicate];
//    return [[CoreDataManager singleton] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:NO withFetchLimit:@300];
}

//------------------------------------------------------------------------------
- (NSArray *)getShotsForTimeLineBetweenHours {
    
    NSDate *now = [NSDate date];
    NSDate *oneDayAgo = [now dateByAddingTimeInterval:-1*24*60*60];
    NSTimeInterval oneDayAgoDate = [oneDayAgo timeIntervalSince1970];
    NSString *oneDayAgoString = [NSString stringWithFormat:@"%f", oneDayAgoDate*1000];
    
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"user.idUser == %@ AND csys_birth > %@",[[UserManager singleton] getUserId],oneDayAgoString];
    
    return [[CoreDataManager sharedInstance] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:NO withPredicate:predicate];
    
}


#pragma mark - SHOT CREATION
//------------------------------------------------------------------------------
- (void)createShotWithComment:(NSString *)comment andDelegate:(id)delegate{

    User *user = [[UserManager singleton] getActiveUser];
    NSDictionary *key = @{kJSON_SHOT_IDSHOT:[NSNull null]};
    NSDictionary *sendData = [[NSMutableDictionary alloc] initWithDictionary:@{kJSON_ID_USER: user.idUser,
                                                                               kJSON_SHOT_IDSHOT:[NSNull null],
                                                                               kJSON_SHOT_COMMENT:comment}];

    [[FavRestConsumer sharedInstance] createEntity:K_COREDATA_SHOT withData:@[sendData] andKey:key andDelegate:delegate withOperation:K_OP_INSERT];

}


@end
