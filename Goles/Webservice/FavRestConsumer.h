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
#import "Provider.h"
#import "MatchBetType.h"
#import "BetType.h"
#import "BetTypeOdd.h"
#import "PlayerProvider.h"
#import "SML.h"
#import "Message.h"
#import "AppAdvice.h"
#import "Device.h"

typedef void (^FetchDataCompletionBlock)(NSDictionary *data, NSError *error);

@protocol ParserProtocol

- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error;

@end

@protocol FavRestConsumerCuotasProtocol

@required
- (void)getOddsForMatchResponse:(BOOL)betOddsForMatchCorrect;
- (void)getOddsForMatchResponseError;
@end

@protocol FavRestConsumerEventsProtocol

@required
-(void)updateEventsMatchList:(Match *)match;

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



//Special request methods

//------------------------------------------------------------------------------
/**
 @brief Request for all odds in a match.
 
 @discussion Creates a request for all MatchBetType's for a given match, and call
 a parse method (cuotasParserForData:andMatch:withdelegate in FavGeneralDAO) with the response data.
 
 @params Match      The match to search
 @params delegate   The class that's in charge for the request
 
 @returns The response of the request is automatically sent to FavGeneralDao to parse it.
 */
- (void)getOddsForMatch:(Match *)match withDelegate:(id)delegate;


//------------------------------------------------------------------------------
/**
 @brief Request for all BetTypes, BetTypeOdds and Providers in a match.
 
 @discussion Creates a request for all BetTypes, BetTypeOdds and Providers for every MatchBetType of a match, and call
 a parse method (providerAndBetTypeAndBetTypeOddParser:forMatchBetType:andMatch: in FavGeneralDAO) with the response data.
 
 @params MatchetType        The matchBetType to search
 @params delegate           The class that's in charge for the request
 @params Match              The match of the initial search
 
 @returns The response of the request is automatically sent to FavGeneralDao to parse it.
 */
- (void)getCompleteInfo:(NSArray *)matchBetTypesArray andMatch:(Match *)match andDelegate:(id)delegate;

//------------------------------------------------------------------------------
/**
 @brief Request for all matches of a teams array
 
 @discussion Creates a request for all matches of given Teams. It retrieves from a view in database.
 At this moment, this info is not saved in CoreData.
 
 @params NSArray            Array of Teams to retrieve matches from.
 @params delegate           The class that's in charge for the request
 @params Match              The match of the initial search
 
 @returns The response of the request is automatically sent to FavGeneralDao to parse it.
 */
- (void)getAllMatchesForTeams:(NSArray *)teams andDelegate:(id)delegate;

//------------------------------------------------------------------------------
/**
 @brief Request for all matches of a teams array
 
 @discussion Creates a request for all matches of given Teams. It retrieves from a view in database.
 At this moment, this info is not saved in CoreData.
 
 @params NSArray            Array of Teams to retrieve matches from.
 @params delegate           The class that's in charge for the request
 @params Match              The match of the initial search
 
 @returns The response of the request is automatically sent to FavGeneralDao to parse it.
 */
- (void)getAllEventsForMatch:(Match *)match andDelegate:(id)delegate;

- (void)getDeviceSuscriptions;
- (void)deviceRegistration:(Device *)device withDelegate:(id)delegate;

@end
