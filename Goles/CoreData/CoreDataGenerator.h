//
//  CoreDataGenerator.h
//  Goles Messenger
//
//  Created by Delfin Pereiro on 28/11/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FavRestConsumer.h"

@interface CoreDataGenerator: NSObject <ParserProtocol>

+ (CoreDataGenerator *)singleton;
- (void) generateDefaultCoreDataBase;


@end
