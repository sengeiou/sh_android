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
#import "Device.h"

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
    
    return self.mUser;
}


//------------------------------------------------------------------------------
-(NSNumber *)getUserId{
    return [[self mUser] idUser];
}

//------------------------------------------------------------------------------
-(NSString *)getUserSessionToken{

    return self.mUser.sessionToken;
}


//------------------------------------------------------------------------------
-(Device *)getDevice{
    return [Device updateWithDictionary:nil];
}

//------------------------------------------------------------------------------
- (NSNumber *)getIdDevice{
    return [[self getDevice] idDevice];
}


//------------------------------------------------------------------------------
- (void)setDeviceToken:(NSString *)token {
    
    if ( token )
        [Device updateWithDictionary:@{kJSON_TOKEN:token}];
    else
        [[self getDevice] resetToken];
    
    [[CoreDataManager singleton] saveContext];
}

//------------------------------------------------------------------------------
- (void)setIdDevice:(NSNumber *)idDevice {
    
    [Device updateWithDictionary:@{kJSON_ID_DEVICE:idDevice}];
    [[CoreDataManager singleton] saveContext];
}


//------------------------------------------------------------------------------
- (NSDictionary *)getRequesterDictionary {
    
    NSMutableDictionary *requester = [[NSMutableDictionary alloc] initWithObjectsAndKeys:[[self getDevice] idDevice],kJSON_ID_DEVICE, nil];
    if ( [self mUser] )
        requester[kJSON_ID_USER] = [self getUserId];
    
    return requester;
}

@end
