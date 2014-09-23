//
//  FavGeneralDAO.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 14/01/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FavGeneralDAO : NSObject

//Generic parser
+(void)genericParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error))completionBlock;


@end
