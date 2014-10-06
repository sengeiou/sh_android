//
//  FavRestConsumer.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 13/01/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AFNetworking.h"
#import "Match.h"
#import "Team.h"
#import "SML.h"
#import "Message.h"
#import "AppAdvice.h"
#import "Device.h"

typedef void (^FetchDataCompletionBlock)(NSDictionary *data, NSError *error);

@protocol ParserProtocol

- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh: (BOOL) refresh;

@optional

- (void)parserResponseFromLoginWithStatus:(BOOL)status andError:(NSError *)error;
- (void)createShotResponseWithStatus:(BOOL)status andError:(NSError *)error;

@end


@interface FavRestConsumer : AFHTTPSessionManager

//Singleton methods
+ (instancetype)sharedInstance;

//------------------------------------------------------------------------------
/**
 @brief Requests server time
 
 @discussion Retrieves the server time in epoch miliseconds.
 Used to ensure the server is up and client has valid internet connection

 @returns Nothing. Must be used with AFNetworking in a POST request and use the block response method.
 Example of use in LadingViewController
 */
- (void)getServerTime:(FetchDataCompletionBlock)completionBlock;

//------------------------------------------------------------------------------
/**
 @brief Generic request creator
 
 @discussion Creates basic requests with only 1 operation block.
 The request is formed of: Alias, Status, Req ands Ops.
 This method also uses some helper method to create all the blocks that form it.
 
 @params Class          The entity class used to create the request
 @params delegate       The class that's in charge for the request
 
 @returns The response of the request is automatically sent to FavGeneralDao to parse it.
 */
- (void)getAllEntitiesFromClass:(Class)entityClass withDelegate:(id)delegate;

- (void)getOldShotsWithDelegate:(id)delegate;
- (void)getEntityFromClass:(Class)entityClass withKey:(NSDictionary *)key withDelegate:(id)delegate;
- (void)getFollowingUsersOfUser:(User *)user withDelegate:(id)delegate;
- (void)getFollowersOfUser:(User *)user withDelegate:(id)delegate;

//Special request methods

- (void)createEntity:(NSString *)entity withData:(NSArray *)dictArray andKey:(NSDictionary *)key andDelegate:(id)delegate;
- (void)deleteEntity:(NSString *)entity withKey:(NSDictionary *)key andData:(NSArray *)data andDelegate:(id)delegate;
- (void)userLoginWithKey:(NSDictionary *)key withDelegate:(id)delegate;

@end
