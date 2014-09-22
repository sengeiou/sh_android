//
//  FavRestConsumerHelper.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 19/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FavRestConsumerHelper.h"
#import "UserManager.h"
#import "CoreDataParsing.h"
#import "Match.h"
#import "Message.h"
#import "SML.h"
#import "AppAdvice.h"
#import "Team.h"
#import "User.h"

@implementation FavRestConsumerHelper


//-----------------------------------------------------------------------------
+ (NSString *)getAliasForEntity:(Class)entity {
    
    if ([entity isSubclassOfClass:[Match class]])
        return @"Get Matches of Shooter";
    else if ([entity isSubclassOfClass:[Message class]])
        return kALIAS_GET_MESSAGE;
    else if ([entity isSubclassOfClass:[AppAdvice class]])
        return kALIAS_GET_ADVICE;
    else if ([entity isSubclassOfClass:[SML class]])
        return kALIAS_GET_SML;
    else if ([entity isSubclassOfClass:[Team class]])
        return kALIAS_GET_ALL_TEAMS;
    else if ([entity isSubclassOfClass:[User class]])
        return kALIAS_LOGIN;
    
    
    return nil;
}

//-----------------------------------------------------------------------------
+ (NSString *)getEntityForClass:(Class)entity {
    
  if ([entity isSubclassOfClass:[User class]])
        return @"Login";
    
    
    return nil;
}

//-----------------------------------------------------------------------------
+ (NSString *)getClassForString:(NSString *)entityString {
    
    if ([entityString isEqualToString:@"Login"])
        return @"User";
    
    return nil;
}


//-----------------------------------------------------------------------------
+ (NSArray *)createREQ {
    
    //Create 'req' array
    NSMutableArray *req = [[NSMutableArray alloc] initWithCapacity:5];
    
    NSNumber *idDevice = [[UserManager singleton] getIdDevice];
    NSNumber *idPlayer = [[UserManager singleton] getUserId];
    NSString *sessionToken = [[UserManager singleton] getUserSessionToken];
    
    NSNumber *idPlatform = @1;
    NSInteger appVersion = [self getAppVersion];
    NSTimeInterval epochTime = [[NSDate date] timeIntervalSince1970]*1000;
    
    if (idDevice)
        [req addObject:idDevice];
    if (idPlayer)
        [req addObject:idPlayer];
    else
        [req addObject:@0];
    
    [req addObject:idPlatform];
    [req addObject:[NSNumber numberWithInt:appVersion]];
    [req addObject:[NSNumber numberWithLongLong:epochTime]];
    
    if (sessionToken != nil)
        [req addObject:sessionToken];
    
    return [req copy];
}

+ (NSInteger)getAppVersion {
    
    NSString *bundleVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString *)kCFBundleVersionKey];
    NSArray *versionArray = [bundleVersion componentsSeparatedByString:@"."];
    
    NSString *thirdNumber = @"000";
    NSString *secondNumber = @"000";
    NSString *firstNumber = @"000";
    
    if ([versionArray count] > 2) {
        thirdNumber = [NSString stringWithFormat:@"00%@",[versionArray objectAtIndex:2]];
        secondNumber = [NSString stringWithFormat:@"00%@",[versionArray objectAtIndex:1]];
        firstNumber = [NSString stringWithFormat:@"00%@",[versionArray objectAtIndex:0]];
    }else if ([versionArray count] == 2) {
        secondNumber = [NSString stringWithFormat:@"00%@",[versionArray objectAtIndex:1]];
        firstNumber = [NSString stringWithFormat:@"00%@",[versionArray objectAtIndex:0]];
    }else if ([versionArray count] == 1) {
        firstNumber = [NSString stringWithFormat:@"00%@",[versionArray objectAtIndex:0]];
    }else
        return 0;
    
    NSString *appVersion = [[firstNumber stringByAppendingString:secondNumber] stringByAppendingString:thirdNumber];
    
    return [appVersion integerValue];
}


//-----------------------------------------------------------------------------
+ (NSDictionary *)createMetadataForOperation:(NSString *)operation andEntity:(NSString *)entity withItems:(NSNumber *)items withOffSet:(NSNumber *)offset andFilter:(NSDictionary *)filter {

    //Create 'metadata' block
    NSDictionary *meta = @{K_WS_OPS_OPERATION:operation,K_WS_OPS_ENTITY:entity};
    NSMutableDictionary *metadata = [[NSMutableDictionary alloc] initWithDictionary:meta];
    [metadata addEntriesFromDictionary:filter];
    [metadata addEntriesFromDictionary:@{K_WS_OPS_TOTAL_ITEMS:[NSNull null]}];
    [metadata addEntriesFromDictionary:@{K_WS_OPS_INCLUDE_DELETED:K_WS_TRUE}];
    [metadata addEntriesFromDictionary:@{K_WS_OPS_ITEMS:items}];
    [metadata addEntriesFromDictionary:@{K_WS_OPS_OFFSET:offset}];
    [metadata addEntriesFromDictionary:@{K_WS_OPS_FILTER:filter}];
    
    return metadata;
}

//-----------------------------------------------------------------------------
+ (NSDictionary *)createMetadataForOperation:(NSString *)operation andEntity:(NSString *)entity withItems:(NSNumber *)items withOffSet:(NSNumber *)offset andKey:(NSDictionary *)key {
    
    //Create 'metadata' block
    NSDictionary *meta = @{K_WS_OPS_OPERATION:operation,K_WS_OPS_ENTITY:entity};
    NSMutableDictionary *metadata = [[NSMutableDictionary alloc] initWithDictionary:meta];
    [metadata addEntriesFromDictionary:key];
    [metadata addEntriesFromDictionary:@{K_WS_OPS_TOTAL_ITEMS:[NSNull null]}];
    [metadata addEntriesFromDictionary:@{K_WS_OPS_INCLUDE_DELETED:K_WS_TRUE}];
    [metadata addEntriesFromDictionary:@{K_WS_OPS_ITEMS:items}];
    [metadata addEntriesFromDictionary:@{K_WS_OPS_OFFSET:offset}];
    [metadata addEntriesFromDictionary:@{K_WS_OPS_KEY:key}];
    
    return metadata;
}

//-----------------------------------------------------------------------------
+ (NSDictionary *)createFilterForParameter:(NSString *)entity andValue:(NSNumber *)idToSearch {
    
    if ([entity isKindOfClass:[NSString class]] && [idToSearch isKindOfClass:[NSNumber class]]) {
        NSArray *filterItems = @[@{K_WS_COMPARATOR: K_WS_OPS_EQ,K_CD_NAME:entity,K_CD_VALUE:idToSearch}];
        NSDictionary *filter = @{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItems,K_WS_FILTERS:@[]};
        return filter;
    }else {
        NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS: [NSNull null],K_WS_FILTERITEMS:[NSNull null],K_WS_FILTERS:@[]}};
        return filter;
    }
    return @{};
}

//-----------------------------------------------------------------------------
+ (NSDictionary *)createFilterForAllItems:(NSString *)entity{
    
    if ([entity isKindOfClass:[NSString class]]) {
        NSArray *filterItems = @[@{K_WS_COMPARATOR: K_WS_OPS_NE,K_CD_NAME:entity,K_CD_VALUE:[NSNull null]}];
        NSDictionary *filter = @{K_WS_OPS_NEXUS: K_WS_OPS_AND,K_WS_FILTERITEMS:filterItems,K_WS_FILTERS:@[]};
        return filter;
    }
    return @{};
}
@end
