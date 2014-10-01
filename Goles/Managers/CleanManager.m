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
    __block BOOL updateUserDefaultsDate = NO;
    
    NSDate *previousDate =[[NSUserDefaults standardUserDefaults] objectForKey:UD_LAST_DELETE_DATE];
    if (previousDate && [previousDate isKindOfClass:[NSDate class]]) {
        NSDate *nowDate = [NSDate date];
        if ([nowDate timeIntervalSinceDate:previousDate] > 60*60*24){			// > 1 day 60*60*24
            [self beginCleanProcessOnCompletion:^(BOOL success, NSError *error) {
                updateUserDefaultsDate = YES;

            }];
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
- (void)beginCleanProcessOnCompletion:(booleanReturnBlock)completion{
    
    NSArray *shotsArray = [[CoreDataManager singleton] getAllEntities:[Shot class]];
    
    if (shotsArray.count > 1000){
        
        [self deleteOldShotsOnCompletion:^(BOOL success, NSError *error) {
            if (success)
                completion(YES, nil);
            else{
                completion(YES, nil);
                 if (error) NSLog(@"%s %@",__PRETTY_FUNCTION__,error);
            }
        }];
        
    }else{
        if (completion)
            completion(YES, nil);
    }
}

#pragma mark - DELETE OLD SHOTS

//Get the shots to be deleted
//------------------------------------------------------------------------------
-(void)deleteOldShotsOnCompletion:(booleanReturnBlock)completion{
    
    NSArray *newShots = [[CoreDataManager singleton] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:NO withFetchLimit:@1000];
    NSArray *shotsToDelete;
    
    if (newShots > 0)
        shotsToDelete = [[CoreDataManager singleton] deleteEntities:[Shot class] NotIn:newShots];
    
    if (shotsToDelete.count > 0){
        [[CoreDataManager singleton] saveContext];
        
        if (completion)
            completion(YES, nil);
        else
            completion(NO, nil);
        
    }else{
           completion(YES, nil);
    }
}

@end
