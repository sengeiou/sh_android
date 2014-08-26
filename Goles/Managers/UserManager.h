//
//  UserManager.h
//  Goles Messenger
//
//  Created by Delfín Pereiro on 17/07/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Player.h"


@interface UserManager : NSObject

@property (nonatomic, strong) Player    *mUser;

//DataAccessLayer singleton instance shared across application
+ (UserManager *)singleton;
+ (UserManager *)sharedInstance;
- (NSNumber *)getUserId;
- (Player *)getActiveUser;
- (BOOL)isAnonimousPlayer;
- (Device *)getDevice;
- (NSNumber *)getIdDevice;
- (void)setDeviceToken:(NSString *)token;
- (void)setIdDevice:(NSNumber *)idDevice;

//------------------------------------------------------------------------------
/**
 This method searchs for a previous idPlayer stored in NSUserDefaults. If it's not
 present, try to recover it from iCloud. Then if the recovered idPlayer is a valid
 one, it's stored in NSUserdefaults to keep data consistency between the two storages
 a return the number.

 @return    NSInteger    The found idPlayer. If no valid idPlayer is found, it returns 0
 */
//------------------------------------------------------------------------------
-(NSInteger)getUserPreviousPlayerId;


-(NSDictionary *)getRequesterDictionary;

@end
