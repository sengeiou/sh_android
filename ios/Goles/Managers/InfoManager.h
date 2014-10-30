//
//  InfoManager.h
//  Shootr
//
//  Created by Christian Cabarrocas on 30/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Match.h"

@interface InfoManager : NSObject

+ (InfoManager *)singleton;

+ (Match *)getUserNextMatch;

@end
