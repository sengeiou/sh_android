//
//  UserManager.h
//  Goles Messenger
//
//  Created by Delfín Pereiro on 17/07/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "User.h"


@interface UserManager : NSObject

@property (nonatomic, strong) User    *mUser;

//DataAccessLayer singleton instance shared across application
+ (UserManager *)singleton;
+ (UserManager *)sharedInstance;
- (NSNumber *)getUserId;
- (User *)getActiveUser;
- (Device *)getDevice;
- (NSNumber *)getIdDevice;
- (void)setDeviceToken:(NSString *)token;
- (void)setIdDevice:(NSNumber *)idDevice;
- (NSString *)getUserSessionToken;
- (NSDictionary *)getRequesterDictionary;
- (NSArray *)getActiveUsersIDs;

@end
