 //
//  UserManager.m
//
//  Created by Christian Cabarrocas on 10/09/14.
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
    NSArray *usersWithProfile = [[CoreDataManager singleton] getAllEntities:[User class] withPredicate:predicate];
    
    if ( usersWithProfile.count == 1 )
        return [usersWithProfile firstObject];
    else if ( usersWithProfile.count > 1 )
        DLog(@"[SHOOTR ERROR]: Existe más de un player en CoreData con Profile activo!!!");
    
    return nil;
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
    
    if ([self getUserId] != nil)
        [idUsersArray addObject:[self getUserId]];
    
    if (idUsersArray.count > 0)
        return idUsersArray;
    
    return nil;
}

//------------------------------------------------------------------------------
- (NSArray *)getFollowingUsersOfUser:(User *)user {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@",user.idUser];
    NSArray *follows = [[CoreDataManager singleton] getAllEntities:[Follow class] orderedByKey:kJSON_MODIFIED ascending:NO withPredicate:predicate];
    NSMutableArray *idUsersArray = [[NSMutableArray alloc] initWithCapacity:follows.count];
    for (Follow *obj in follows) {
        User *user = [[CoreDataManager singleton] getEntity:[User class] withId:obj.idUserFollowedValue];
        if (user)
            [idUsersArray addObject:user];
    }

    if (idUsersArray.count > 0)
        return idUsersArray;
    
    return nil;
}

//------------------------------------------------------------------------------
- (NSArray *)getFollowingPeopleForMe {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@",[[UserManager singleton] getUserId]];
    NSArray *follows = [[CoreDataManager singleton] getAllEntities:[Follow class] withPredicate:predicate];
    NSMutableArray *idUsersArray = [[NSMutableArray alloc] initWithCapacity:follows.count];
    for (Follow *obj in follows) {
        User *user = [[CoreDataManager singleton] getEntity:[User class] withId:obj.idUserFollowedValue];
        if (user)
            [idUsersArray addObject:user];
    }
    
    NSSortDescriptor *valueDescriptor = [[NSSortDescriptor alloc] initWithKey:kJSON_NAME ascending:YES];
    NSArray *descriptors = [NSArray arrayWithObject:valueDescriptor];
    NSArray *sortedArray = [idUsersArray sortedArrayUsingDescriptors:descriptors];
    
    if (sortedArray.count > 0)
        return sortedArray;
    
    return nil;
}

//------------------------------------------------------------------------------
- (NSArray *)getFollowersOfUser:(User *)user {
	
	NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUserFollowed == %@",user.idUser];
	NSArray *follows = [[CoreDataManager singleton] getAllEntities:[Follow class] orderedByKey:kJSON_MODIFIED ascending:NO withPredicate:predicate];
	NSMutableArray *idUsersArray = [[NSMutableArray alloc] initWithCapacity:follows.count];
	for (Follow *obj in follows) {
		User *user = [[CoreDataManager singleton] getEntity:[User class] withId:obj.idUserValue];
		if (user)
			[idUsersArray addObject:user];
	}
	
	if (idUsersArray.count > 0)
		return idUsersArray;
	
	return nil;
}

//------------------------------------------------------------------------------
- (BOOL)isLoggedUserFollowing:(User *)user {

	NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@",[self getUserId]];
	NSArray *follows = [[CoreDataManager singleton] getAllEntities:[Follow class] withPredicate:predicate];
	for (Follow *followedUser in follows) {
		if (followedUser.idUserFollowed == user.idUser)
			return YES;
	}
	
	return NO;
}


//------------------------------------------------------------------------------
- (User *)getUserForId:(NSInteger )idUser {
    
    User *user = [[CoreDataManager singleton] getEntity:[User class] withId:idUser];
    if (user)
        return user;
    
    return nil;
}

@end
