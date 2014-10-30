//
//  CoreDataGenerator.h
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FavRestConsumer.h"

@interface CoreDataGenerator: NSObject <ParserProtocol>

+ (CoreDataGenerator *)singleton;
- (void) generateDefaultCoreDataBase;


@end
