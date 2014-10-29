//
//  ShotManager.m
//
//  Created by Christian Cabarrocas on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ShotManager.h"
#import "UserManager.h"
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
- (NSFetchedResultsController *)getShotsForTimeLine {

    NSManagedObjectContext *context = [[CoreDataManager singleton] getInsertContext];
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:K_COREDATA_SHOT inManagedObjectContext:context];
    [fetchRequest setEntity:entity];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"user.idUser IN %@",[[UserManager singleton] getActiveUsersIDs]];
    [fetchRequest setPredicate:predicate];
    
    NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:kJSON_BIRTH ascending:NO];
    [fetchRequest setSortDescriptors:[NSArray arrayWithObject:sort]];
    
    [fetchRequest setFetchBatchSize:50];
    
    NSFetchedResultsController *theFetchedResultsController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest
                                                                                                  managedObjectContext:context
                                                                                                    sectionNameKeyPath:nil
                                                                                                             cacheName:@"ShotCache"];
    return theFetchedResultsController;

}

//------------------------------------------------------------------------------
- (NSArray *)getShotsForTimeLineBetweenHours {
    
    NSDate *now = [NSDate date];
    NSDate *oneDayAgo = [now dateByAddingTimeInterval:-1*24*60*60];
    NSTimeInterval oneDayAgoDate = [oneDayAgo timeIntervalSince1970];
    NSString *oneDayAgoString = [NSString stringWithFormat:@"%f", oneDayAgoDate*1000];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"user.idUser == %@ AND csys_birth > %@",[[UserManager singleton] getUserId],oneDayAgoString];
    
    NSArray *array = [[CoreDataManager sharedInstance] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:NO withPredicate:predicate];
    return array;
    
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
