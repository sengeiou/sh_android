//
//  TimeLineHelper.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 02/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "TimeLineHelper.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "UserManager.h"

@implementation TimeLineHelper


+(NSArray *)createThreeShotsWithDict{
    //Populate DB
    NSDictionary *shotsOne = @{kJSON_SHOT_IDSHOT:@1,kJSON_SHOT_COMMENT:@"",K_WS_OPS_BIRTH_DATE:[self createEpochFromNow],kJSON_ID_USER:[[UserManager singleton] getUserId]};
    NSDictionary *shotsTwo = @{kJSON_SHOT_IDSHOT:@2,kJSON_SHOT_COMMENT:@"Texte repetit",K_WS_OPS_BIRTH_DATE:[self createEpochFromNow],kJSON_ID_USER:[[UserManager singleton] getUserId]};
    NSDictionary *shotsThree = @{kJSON_SHOT_IDSHOT:@3,kJSON_SHOT_COMMENT:@"TExte aL3ator1",K_WS_OPS_BIRTH_DATE:[self createEpochFromNow],kJSON_ID_USER:[[UserManager singleton] getUserId]};
    NSArray *shots = @[shotsOne,shotsTwo,shotsThree];
    
    return shots;
}

+(Shot *)createShotFromDict:(NSDictionary *) shotData{
    
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Shot" inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    Shot *shot = [[Shot alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
    [shot setComment:[shotData objectForKey:kJSON_SHOT_COMMENT]];
    
    return shot;
    
}

+(NSArray *)createListOfShotsFromDictArray:(NSArray*) shotDataList{
    
    NSMutableArray *returnedArray = [[NSMutableArray alloc] init];
    for (NSDictionary* shotData in shotDataList) {
        [returnedArray addObject:[self createShotFromDict:shotData]];
    }
    return returnedArray;
}

+(NSArray *) getThreeShots{
    
    
    return [self createListOfShotsFromDictArray:[self createThreeShotsWithDict]];
    
}

+(void)populateThreeShots {
    
    NSArray *shots = [self createThreeShotsWithDict];
    
    
    NSArray *shotsInserted = [[CoreDataManager singleton] insertEntities:[Shot class] WithArray:shots];
    if (shotsInserted > 0) {
        [[CoreDataManager singleton] saveContext];
        NSLog(@"Shots created");
    }
    
}

+(NSNumber *)createEpochFromNow {
    
    NSTimeInterval nowDate = [[NSDate date] timeIntervalSince1970]*1000;
    NSNumber *date = [NSNumber numberWithDouble:nowDate];
    return date;
}

+(void) emptyShotsDataBase{
    [[CoreDataManager singleton] deleteAllEntities:[Shot class]];
    [[CoreDataManager singleton] saveContext];

}

@end
