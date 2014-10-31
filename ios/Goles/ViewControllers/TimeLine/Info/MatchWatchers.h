//
//  MatchWatchers.h
//  Shootr
//
//  Created by Christian Cabarrocas on 30/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "User.h"
#import "Watch.h"
#import "Match.h"

@interface MatchWatchers : NSObject

@property (nonatomic,strong)    NSString            *matchName;
@property (nonatomic,strong)    NSMutableArray      *userArray;
@property (nonatomic,strong)    NSString            *matchDate;
@property (nonatomic,strong)    Match               *match;

@end
