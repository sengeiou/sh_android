//
//  CleanManager.h
//  Goles
//
//  Created by Christian Cabarrocas on 25/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CleanManager : NSObject

+ (CleanManager *)singleton;
+ (CleanManager *)sharedInstance;

- (void)beginCleanProcess;

@end
