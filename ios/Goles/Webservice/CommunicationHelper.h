//
//  CommunicationHelper.h
//  Shootr
//
//  Created by Christian Cabarrocas on 28/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AFNetworking.h"


typedef void (^FetchDataCompletionBlock)(NSDictionary *data, NSError *error);

@interface CommunicationHelper : AFHTTPSessionManager

+ (CommunicationHelper *)singleton;

-(void)postRequest:(NSDictionary *)data onCompletion:(void (^) (NSDictionary *response, NSError *error))handler;


@end
