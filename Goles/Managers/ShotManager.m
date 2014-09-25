//
//  ShotManager.m
//  Goles
//
//  Created by Christian Cabarrocas on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ShotManager.h"
#import "UserManager.h"
#import "CoreDataManager.h"
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

    NSTimeInterval nowDate = [[NSDate date] timeIntervalSince1970];
    NSString *nowDateString = [NSString stringWithFormat:@"%f", nowDate];
#warning Predicate not valid
//    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"user.idUser IN %@ AND %@ < %@",[[UserManager singleton] getActiveUsersIDs],kJSON_BIRTH,nowDateString];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"user.idUser IN %@",[[UserManager singleton] getActiveUsersIDs]];

    return [[CoreDataManager sharedInstance] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:NO withPredicate:predicate];
}


#pragma mark - SHOT CREATION
//------------------------------------------------------------------------------
- (BOOL)createShotWithComment:(NSString *)comment {

    User *user = [[UserManager singleton] mUser];
    NSNumber *revision = @0;
    NSTimeInterval birth = [[NSDate date] timeIntervalSince1970]*1000;
    NSTimeInterval modified = [[NSDate date] timeIntervalSince1970]*1000;
    
    NSDictionary *mutDict = [[NSMutableDictionary alloc] initWithDictionary:@{kJSON_ID_USER: user.idUser,
                                                                                     kJSON_BIRTH:[NSNumber numberWithLongLong:birth],
                                                                                     kJSON_MODIFIED:[NSNumber numberWithLongLong:modified],
                                                                                     kJSON_REVISION:revision,
                                                                                     kJSON_SHOT_COMMENT:comment}];
#warning Insert shot in CoreData and send it to the server
    return YES;
}


@end
