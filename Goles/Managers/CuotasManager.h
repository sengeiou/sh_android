//
//  CuotasManager.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 05/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Match.h"
#import "BetTypeOdd.h"

@interface CuotasManager : NSObject

+ (CuotasManager *)singleton;
+ (CuotasManager *)sharedInstance;

- (NSString *)get1X2OddsForMatch:(Match *)match;
- (NSArray *)getCompleteOddsForMatch:(Match *)match;
- (Provider *)getProviderForCurrentOddsForMatch:(Match *)match;
- (BOOL)isThereAnyOddAvailableForMatch:(Match *)match;
- (BOOL)isProviderActive;
- (void)deleteAllCuotasForMatch:(Match *)match;
- (void)deleteAllCuotasInArray:(NSArray *)cuotasArray;
- (void)markAllCuotasForMatchToDelete:(Match *)match;
- (void)deleteOldCuotas;

@end
