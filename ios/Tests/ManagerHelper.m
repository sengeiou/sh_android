//
//  ManagerHelper.m
//  Shootr
//
//  Created by Christian Cabarrocas on 25/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ManagerHelper.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "UserManager.h"

@implementation ManagerHelper

+(NSArray *)createThreeUsersWithDict{
    //Populate DB
    NSDictionary *userOne = @{kJSON_ID_USER:@1,kJSON_USERNAME:@"userOneNick",kJSON_NAME:@"userOne",kJSON_SESSIONTOKEN:@"123"};
    NSDictionary *userTwo = @{kJSON_ID_USER:@2,kJSON_USERNAME:@"userTwoNick",kJSON_NAME:@"userTwo"};
    NSDictionary *userThree = @{kJSON_ID_USER:@3,kJSON_USERNAME:@"userThreeNick",kJSON_NAME:@"userThree"};
    NSArray *shots = @[userOne,userTwo,userThree];
    
    return shots;
}

+(User *)createUserFromDict:(NSDictionary *) userData{
    
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"User" inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    User *user = [[User alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
    [user setUserName:[userData objectForKey:kJSON_USERNAME]];
    [user setIdUser:[userData objectForKey:kJSON_ID_USER]];
    [user setName:[userData objectForKey:kJSON_NAME]];
     
    return user;
    
}

+(NSArray *)createListOfUsersFromDictArray:(NSArray*) usersDataList{
    
    NSMutableArray *returnedArray = [[NSMutableArray alloc] init];
    for (NSDictionary* userData in usersDataList) {
        [returnedArray addObject:[self createUserFromDict:userData]];
    }
    return returnedArray;
}

+(void)populateThreeUsers {
    
    NSArray *users = [self createThreeUsersWithDict];
    
    
    NSArray *usersInserted = [[CoreDataManager singleton] insertEntities:[User class] WithArray:users];
    if (usersInserted > 0) {
        [[CoreDataManager singleton] saveContext];
        NSLog(@"Users created");
    }
    
}

+(NSNumber *)createEpochFromNow {
    
    NSTimeInterval nowDate = [[NSDate date] timeIntervalSince1970]*1000;
    NSNumber *date = [NSNumber numberWithDouble:nowDate];
    return date;
}

+(void) emptyUsersDataBase{
    [[CoreDataManager singleton] deleteAllEntities:[User class]];
    [[CoreDataManager singleton] saveContext];
    
}

@end
