//
//  ShotManager.h
//
//  Created by Christian Cabarrocas on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CoreDataManager.h"

@interface ShotManager : NSObject

+ (ShotManager *)singleton;
+ (ShotManager *)sharedInstance;

- (NSFetchedResultsController *)getShotsForTimeLine;
- (NSArray *) getShotsForTimeLineBetweenHours;
- (void)createShotWithComment:(NSString *)comment andDelegate:(id)delegate;

@end
