//
//  FilterCreation.h
//  Goles
//
//  Created by Christian Cabarrocas on 12/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FilterCreation : NSObject

+ (NSDictionary *)getFilterForEntity:(Class)entity;
+ (NSDictionary *)getFilterForOldShots;
@end
