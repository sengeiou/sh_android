//
//  SearchManager.m
//  Shootr
//
//  Created by Christian Cabarrocas on 10/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "SearchManager.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "User.h"

@implementation SearchManager

#pragma mark - SINGLETON
//------------------------------------------------------------------------------
+ (SearchManager *)singleton {
    
    static SearchManager *sharedSearchManager = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedSearchManager = [[SearchManager alloc] init];
    });
    return sharedSearchManager;
}

//------------------------------------------------------------------------------
+ (SearchManager *)sharedInstance {
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
- (id)copyWithZone:(NSZone *)zone {
    return self;
}


#pragma mark - SEARCH METHODS
//------------------------------------------------------------------------------
+ (NSArray *)searchPeopleLocal:(NSString *)stringToSearch {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"name contains[cd] %@ || userName contains[cd] %@", stringToSearch,stringToSearch];
    NSArray *peopleArray = [[CoreDataManager singleton] getAllEntities:[User class] orderedByKey:kJSON_NAME ascending:NO withPredicate:predicate];
    if (peopleArray.count > 0)
        return peopleArray;
    else
        return nil;
}

@end
