//
//  FavGeneralDAO.h
//
//  Created by Christian Cabarrocas on 14/01/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FavGeneralDAO : NSObject

+(void)genericParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error, BOOL refresh))completionBlock;
+(void)shotParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error, BOOL refresh))completionBlock;
+(void)searchParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error,NSArray *data, int totalItems))completionBlock;
+(void)watchParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error, BOOL refresh))completionBlock;

@end
