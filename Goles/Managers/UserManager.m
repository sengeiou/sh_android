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
    return [[self mUser] sessionToken];
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
-(NSInteger)getUserPreviousPlayerId {
    
    // Get playerID from User defaults
    NSUserDefaults *localStorage = [NSUserDefaults standardUserDefaults];
    NSInteger idPlayerDefaults = [localStorage integerForKey:kJSON_ID_USER];
    
    // Get playerID from iCloud
    NSUbiquitousKeyValueStore *iCloudStorage = [NSUbiquitousKeyValueStore defaultStore];
    NSNumber *idPlayerICloud = [iCloudStorage objectForKey:kJSON_ID_USER];
    
    // Synchronize id's
    if ( idPlayerDefaults != [idPlayerICloud integerValue] ) {

        //Save the oldest player in both environments
        if ( [idPlayerICloud integerValue] > idPlayerDefaults ){
            [localStorage setObject:idPlayerICloud forKey:kJSON_ID_USER];
            [localStorage synchronize];
        } else {
            [iCloudStorage setObject:@(idPlayerDefaults) forKey:kJSON_ID_USER];
            [iCloudStorage synchronize];
        }
    }
    
    return idPlayerDefaults;
}

//------------------------------------------------------------------------------
-(void)setUserPreviousPlayerId:(NSNumber *)idPlayer {
    
    // Set playerID to User defaults
    NSUserDefaults *localStorage = [NSUserDefaults standardUserDefaults];
    [localStorage setObject:idPlayer forKey:kJSON_ID_USER];
    [localStorage synchronize];
    
    // Set playerID to iCloud
    NSUbiquitousKeyValueStore *iCloudStorage = [NSUbiquitousKeyValueStore defaultStore];
    [iCloudStorage setObject:idPlayer forKey:kJSON_ID_USER];
    [iCloudStorage synchronize];
}


//------------------------------------------------------------------------------
- (NSDictionary *)getRequesterDictionary {
    
    NSMutableDictionary *requester = [[NSMutableDictionary alloc] initWithObjectsAndKeys:[[self getDevice] idDevice],kJSON_ID_DEVICE, nil];
    if ( [self mUser] )
        requester[kJSON_ID_USER] = [self getUserId];
    
    return requester;
}
//------------------------------------------------------------------------------
- (void)getPlayerInfoDidResponse:(NSInteger)playerStatus {
    
//    if ( playerStatus == 2)       // player inexistent -> create new player
//        [[RestConsumer sharedInstance] createAnonymousPlayer:[[[[UserManager singleton] getDevice] idDevice] intValue] withDelegate:self];
}

//------------------------------------------------------------------------------
- (void)createAnonymousPlayerDidResponse {
    
    
}

#pragma mark - Singleton overwritten methods
//------------------------------------------------------------------------------
- (id)init {
	self = [super init];
	if (self != nil) {
//        [self setMclient:[[WSClient alloc] init]];
//        [[self mclient] setDelegate:self];
//        [[self mclient] setShowHUD:NO];
	}
	return self;
}

//------------------------------------------------------------------------------
- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

@end
