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

#pragma mark - USER
//------------------------------------------------------------------------------
-(NSNumber *)getUserId {
    
    return [[self getActiveUser] idUser];
}

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
-(NSString *)getUserSessionToken {
   return [[self getActiveUser] sessionToken];
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
- (User *)getUserForId:(NSInteger )idUser {
    
    User *user = [[CoreDataManager singleton] getEntity:[User class] withId:idUser];
    if (user)
        return user;
    
    return nil;
}

//------------------------------------------------------------------------------
- (User *)createUserFromDict:(NSDictionary *)dict {
    
    NSEntityDescription *entity = [NSEntityDescription entityForName:K_COREDATA_USER inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    User *user = [[User alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
    
    NSNumber *idUser = [dict objectForKey:kJSON_ID_USER];
    if ( [idUser isKindOfClass:[NSNumber class]] )
        [user setIdUser:idUser];
    else
        return nil;
    
    NSString *userName = [dict objectForKey:kJSON_USERNAME ];
    if ([userName isKindOfClass:[NSString class]])
        [user setUserName:userName];
    else
        return nil;
    
    NSNumber *idFavoriteTeam = [dict objectForKey:kJSON_ID_FAVORITE_TEAM ];
    if ([idFavoriteTeam isKindOfClass:[NSNumber class]])
        [user setIdFavoriteTeam:idFavoriteTeam];
    
    NSString *favoriteTeamName = [dict objectForKey:kJSON_FAVORITE_TEAM_NAME ];
    if ([favoriteTeamName isKindOfClass:[NSString class]])
        [user setFavoriteTeamName:favoriteTeamName];
    
    NSString *sessionToken = [dict objectForKey:kJSON_SESSIONTOKEN ];
    if ([sessionToken isKindOfClass:[NSString class]])
        [user setSessionToken:sessionToken];
    
    
    NSString *email = [dict objectForKey:kJSON_EMAIL ];
    if ([email isKindOfClass:[NSString class]])
        [user setEMail:email];
    
    NSString *name = [dict objectForKey:kJSON_NAME ];
    if ([name isKindOfClass:[NSString class]])
        [user setName:name];
    
    NSString *photo = [dict objectForKey:kJSON_PHOTO ];
    if ([photo isKindOfClass:[NSString class]])
        [user setPhoto:photo];
    
    NSString *bio = [dict objectForKey:kJSON_BIO ];
    if ([bio isKindOfClass:[NSString class]])
        [user setBio:bio];
    
    NSString *website = [dict objectForKey:kJSON_WEBSITE ];
    if ([website isKindOfClass:[NSString class]])
        [user setWebsite:website];
    
    NSNumber *points = [dict objectForKey:kJSON_POINTS ];
    if ([points isKindOfClass:[NSNumber class]])
        [user setPoints:points];
    
    NSNumber *numFollowing = [dict objectForKey:kJSON_NUMFOLLOWING ];
    if ([numFollowing isKindOfClass:[NSNumber class]])
        [user setNumFollowing:numFollowing];
    
    NSNumber *numFollowers = [dict objectForKey:kJSON_NUMFOLLOWERS ];
    if ([numFollowers isKindOfClass:[NSNumber class]])
        [user setNumFollowers:numFollowers];
    
    NSNumber *rank = [dict objectForKey:kJSON_RANK ];
    if ([rank isKindOfClass:[NSNumber class]])
        [user setRank:rank];
    
    return user;
}

//------------------------------------------------------------------------------
-(void)setNumberFollowings:(NSNumber *)numberFollowing{
    
    User *user = [[UserManager sharedInstance]getActiveUser];
    
    if (numberFollowing && user){
        [user setNumFollowing: numberFollowing];
       // [[CoreDataManager singleton] saveContext];
    }
}

#pragma mark - DEVICE
//------------------------------------------------------------------------------
-(Device *)getDevice{
    Device *device = [[self getActiveUser] device];
    if (device) {
        return device;
    }
    return nil;
}

//------------------------------------------------------------------------------
-(NSNumber *)getIdDevice{
    
    if ([[self getDevice] isKindOfClass:[Device class]]) {
        return [[self getDevice] idDevice];
    }
    return nil;
}


//------------------------------------------------------------------------------
-(void)setDeviceToken:(NSString *)token {
    NSTimeInterval epochTime = [[NSDate date] timeIntervalSince1970]*1000;

    if ( token ){
 
        Device *currentDBDevice = [self getDevice];
        
        //No Device on DB
        if (![currentDBDevice isKindOfClass:[Device class]]) {
            Device *device = [Device updateWithDictionary:@{kJSON_ID_DEVICE:[NSNull null],kJSON_TOKEN:token,kJSON_SYNCRONIZED:kJSON_SYNCRO_NEW,kJSON_REVISION:@0,kJSON_BIRTH:[NSNumber numberWithLongLong:epochTime],kJSON_MODIFIED:[NSNumber numberWithLongLong:epochTime],kJSON_DEVICE_PLATFORM:@1}];
            if (device)
                [[CoreDataManager singleton] saveContext];

        }
        //Device exists
        else if (![token isEqualToString:[self getDevice].token]) {
            NSNumber *revision = [NSNumber numberWithLong:currentDBDevice.csys_revisionValue+1];
            Device *device = [Device updateWithDictionary:@{kJSON_TOKEN:token,kJSON_SYNCRONIZED:kJSON_SYNCRO_UPDATED,kJSON_REVISION:revision}];
            if (device)
                [[CoreDataManager singleton] saveContext];
        }
    }
}

#pragma mark - FOLLOW / UNFOLLOW
//------------------------------------------------------------------------------
- (NSArray *)getFollowingUsersOfUser:(User *)user {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@ && csys_syncronized != %@ ",user.idUser,  @"d"];
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
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@ && csys_syncronized != %@ ",[[UserManager singleton] getUserId], @"d"];
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
	
	NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUserFollowed == %@  && csys_syncronized != %@ ",user.idUser, @"d"];
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
- (BOOL)checkIfImFollowingUser:(User *)user {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@ && csys_syncronized != %@",[[UserManager singleton] getUserId],  @"d"];
    NSArray *follows = [[CoreDataManager singleton] getAllEntities:[Follow class] withPredicate:predicate];

    for (Follow *follow in follows) {
        
        if (follow.idUserFollowed == user.idUser)
            return YES;
    }
    return NO;
}


//------------------------------------------------------------------------------
- (BOOL)startFollowingUser:(User *)user {
    NSTimeInterval epochTime = [[NSDate date] timeIntervalSince1970]*1000;

    User *userCheck = [self getUserForId:user.idUserValue];
    User *activeUser = [[UserManager sharedInstance] getActiveUser];
    
    if (userCheck) {
        NSDictionary *followDict = @{kJSON_ID_USER:[self getUserId],kJSON_FOLLOW_IDUSERFOLLOWED:user.idUser,kJSON_SYNCRONIZED:kJSON_SYNCRO_NEW,K_WS_OPS_REVISION:@0,K_WS_OPS_BIRTH_DATE:userCheck.csys_birth,K_WS_OPS_UPDATE_DATE:[NSNumber numberWithLongLong:epochTime],K_WS_OPS_DELETE_DATE:[NSNull null]};
        Follow *follow = [Follow updateWithDictionary:followDict];
        if (follow){
            [[UserManager sharedInstance]setNumberFollowings:[NSNumber numberWithInt:[activeUser.numFollowing intValue] + 1]];

            [[CoreDataManager singleton] saveContext];
            return YES;
        }
    }
    else {

        User *newUser = [User createUserWithUser:user];
        
        if (newUser){
            NSDictionary *followDict = @{kJSON_ID_USER:[self getUserId],kJSON_FOLLOW_IDUSERFOLLOWED:newUser.idUser,kJSON_SYNCRONIZED:kJSON_SYNCRO_NEW,K_WS_OPS_REVISION:@0,K_WS_OPS_BIRTH_DATE:[NSNumber numberWithLongLong:epochTime],K_WS_OPS_UPDATE_DATE:[NSNumber numberWithLongLong:epochTime],K_WS_OPS_DELETE_DATE:[NSNull null]};
            Follow *follow = [Follow updateWithDictionary:followDict];
            if (follow){
                [[UserManager sharedInstance]setNumberFollowings:[NSNumber numberWithInt:[activeUser.numFollowing intValue] + 1]];

                [[CoreDataManager singleton] saveContext];
                return YES;
            }
        }
    }
    
    return NO;
}

//------------------------------------------------------------------------------
- (BOOL)stopFollowingUser:(User *)user {
    
    User *activeUser = [[UserManager sharedInstance] getActiveUser];

    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@ && idUserFollowed == %@",[self getUserId] ,user.idUser];
    Follow *follow = [[[CoreDataManager singleton] getAllEntities:[Follow class] withPredicate:predicate] firstObject];
    if (follow){
        NSDate *date = [NSDate date];
        NSTimeInterval timeInt = [date timeIntervalSince1970];
        follow.csys_deleted = [NSNumber numberWithDouble:timeInt*1000];
        follow.csys_syncronized = kJSON_SYNCRO_DELETED;
        
        [[UserManager sharedInstance]setNumberFollowings:[NSNumber numberWithInt:[activeUser.numFollowing intValue] - 1]];

        [[CoreDataManager singleton] saveContext];
        return YES;
    }
    
    return NO;
        
}

@end
