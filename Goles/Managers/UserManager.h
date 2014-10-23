//
//  UserManager.h
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "User.h"


@interface UserManager : NSObject

//DataAccessLayer singleton instance shared across application
+ (UserManager *)singleton;
+ (UserManager *)sharedInstance;


//User
- (NSNumber *)getUserId;
- (User *)getActiveUser;
- (NSString *)getUserSessionToken;
- (NSArray *)getActiveUsersIDs;
- (User *)getUserForId:(NSInteger )idUser;
- (User *)createUserFromDict:(NSDictionary *)dict;
- (void)setNumberFollowings:(NSNumber *)numberFollowing;
- (void)setNumberFollowers:(NSNumber *)numberFollowers forUSer:(User *)user; 

//Device
- (Device *)getDevice;
- (NSNumber *)getIdDevice;
- (void)setDeviceToken:(NSString *)token;


//Follow / unfollow
- (BOOL)startFollowingUser:(User *)user;
- (BOOL)stopFollowingUser:(User *)user;
- (NSArray *)getFollowingUsersOfUser:(User *)user;
- (NSArray *)getFollowingPeopleForMe;
- (NSArray *)getFollowersOfUser:(User *)user;
- (BOOL)checkIfImFollowingUser:(User *)user;

@end
