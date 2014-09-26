 //
//  UserManager.m
//  Goles Messenger
//
//  Created by Delfín Pereiro on 17/07/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "UserManager.h"
#import "Device.h"
#import "Services.pch"
#import "Constants.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "Follow.h"

@implementation UserManager

//------------------------------------------------------------------------------
//DataAccessLayer singleton instance shared across application
+ (UserManager *)singleton
{
    static UserManager *sharedUser = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedUser = [[UserManager alloc] init];
    });
    return sharedUser;
}

//------------------------------------------------------------------------------
+ (UserManager *)sharedInstance
{
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
- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

#pragma mark - public methods
//------------------------------------------------------------------------------
- (User *)getActiveUser {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"sessionToken != nil"];
    return [[[CoreDataManager singleton] getAllEntities:[User class] withPredicate:predicate] firstObject];

}


//------------------------------------------------------------------------------
-(NSNumber *)getUserId {
    return [[self getActiveUser] idUser];
}

//------------------------------------------------------------------------------
-(NSString *)getUserSessionToken {
   return [[self getActiveUser] sessionToken];
}


//------------------------------------------------------------------------------
-(Device *)getDevice{
    return [Device updateWithDictionary:nil];
}

//------------------------------------------------------------------------------
-(NSNumber *)getIdDevice{
    return [[self getDevice] idDevice];
}


//------------------------------------------------------------------------------
-(void)setDeviceToken:(NSString *)token {
    
    if ( token )
        [Device updateWithDictionary:@{kJSON_TOKEN:token}];
    else
        [[self getDevice] resetToken];
    
    [[CoreDataManager singleton] saveContext];
}

//------------------------------------------------------------------------------
-(void)setIdDevice:(NSNumber *)idDevice {
    
    [Device updateWithDictionary:@{kJSON_ID_DEVICE:idDevice}];
    [[CoreDataManager singleton] saveContext];
}


//------------------------------------------------------------------------------
- (NSArray *)getActiveUsersIDs {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@",[self getUserId]];
    NSArray *follows = [[CoreDataManager singleton] getAllEntities:[Follow class] withPredicate:predicate];
    NSMutableArray *idUsersArray = [[NSMutableArray alloc] initWithCapacity:follows.count+1];
    for (Follow *obj in follows) {
        [idUsersArray addObject:obj.idUserFollowed];
    }
    [idUsersArray addObject:[self getUserId]];
    
    if (idUsersArray.count > 0)
        return idUsersArray;
    
    return nil;
}

@end
