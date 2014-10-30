//
//  SearchManager.h
//  Shootr
//
//  Created by Christian Cabarrocas on 10/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SearchManager : NSObject

+ (SearchManager *)singleton;
+ (SearchManager *)sharedInstance;

+ (NSArray *)searchPeopleLocal:(NSString *)stringToSearch;

@end
