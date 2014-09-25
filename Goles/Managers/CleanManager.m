//
//  CleanManager.m
//  Goles
//
//  Created by Christian Cabarrocas on 25/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "CleanManager.h"
#import "CoreDataParsing.h"
#import "CoreDataManager.h"

#import "Shot.h"

@implementation CleanManager

#pragma mark - SINGLETON
//------------------------------------------------------------------------------
+ (CleanManager *)singleton {
    
    static CleanManager *sharedCleanManager = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedCleanManager = [[CleanManager alloc] init];
    });
    return sharedCleanManager;
}

//------------------------------------------------------------------------------
+ (CleanManager *)sharedInstance {
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

#pragma mark - CLEAN PROCESS CONTROL

//Check for the last clean process date
//------------------------------------------------------------------------------
-(void) updateLastActiveDate{
    BOOL updateUserDefaultsDate = NO;
    NSDate *previousDate =[[NSUserDefaults standardUserDefaults] objectForKey:UD_LAST_DELETE_DATE];
    if (previousDate && [previousDate isKindOfClass:[NSDate class]]) {
        NSDate *nowDate = [NSDate date];
        if ([nowDate timeIntervalSinceDate:previousDate] > 60*60*24){			// > 1 day 60*60*24
            [self beginCleanProcess];
            updateUserDefaultsDate = YES;
        }
    }
    else{
        updateUserDefaultsDate = YES;
    }
    if (updateUserDefaultsDate) {
        [[NSUserDefaults standardUserDefaults] setObject:[NSDate date] forKey:UD_LAST_DELETE_DATE];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

#pragma mark - GET THE SHOTS TO BE DELETED

//Check if we have more than X shots in DB
//------------------------------------------------------------------------------
- (void)beginCleanProcess {
    
    NSArray *shotsArray = [[CoreDataManager singleton] getAllEntities:[Shot class]];
    if (shotsArray.count > 1000) {
        [self deleteOldShots];
    }
}

#pragma mark - DELETE OLD SHOTS

//Get the shots to be deleted
//------------------------------------------------------------------------------
-(void)deleteOldShots {
    
    NSArray *newShots = [[CoreDataManager singleton] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:NO withFetchLimit:@1000];
    NSArray *shotsToDelete = [[CoreDataManager singleton] deleteEntities:[Shot class] NotIn:newShots];
    if (shotsToDelete.count > 0)
        [[CoreDataManager singleton] saveContext];
    
}

@end
