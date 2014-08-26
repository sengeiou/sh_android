//
//  FavGeneralDAO.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 14/01/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Match.h"

@interface FavGeneralDAO : NSObject

//Generic parser
+(void)genericParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error))completionBlock;

//Cuotas parsers
+(void)cuotasParserForData:(NSDictionary *)data andMatch:(Match *)match withdelegate:(id)delegate;
+(BOOL)providerAndBetTypeAndBetTypeOddParser:(NSDictionary *)dict forMatchBetType:(NSDictionary *)matchBetType andMatch:(Match *)match;

//Team Calendar View parser
+(NSArray *)parseMatchesForCalendar:(NSDictionary *)dict;

//Device
+(BOOL)parseDevice:(NSDictionary *)deviceData;


@end
