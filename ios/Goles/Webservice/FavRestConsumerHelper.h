//
//  FavRestConsumerHelper.h
//
//  Created by Christian Cabarrocas on 19/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "UserManager.h"

@interface FavRestConsumerHelper : NSObject

@property (nonatomic,strong) UserManager *userManager;

+ (FavRestConsumerHelper *)singleton;
+ (FavRestConsumerHelper *)sharedInstance;


//METHODS FOR TESTING
-(void)setUserManager:(UserManager *)userManager;

    

//------------------------------------------------------------------------------
/**
 @brief Alias name for the webservice requests
 
 @discussion The first item in the webservice request is the "alias" name for the request.
 It's used for statistics purposes and useful to debug and track requests in Charles proxy.
 Used in FavRestConsumer.
 
 @params Class The entity class that we want for the request
 
 @returns An NSString with with the alias name
 */
- (NSString *)getAliasForEntity:(Class)entity;

- (NSString *)getEntityForClass:(Class)entity;

- (NSString *)getClassForString:(NSString *)entityString;


//------------------------------------------------------------------------------
/**
 @brief Requestor block for the webservice request
 
 @discussion Creates an array with the requestor block in the webservices request.
 The array consists of 5 ordered items, all of them mandatory: idDevice, idUser, idPlatform, AppVersion and SystemTime.
 Used in FavRestConsumer.
 
 @returns An NSArray of 5 items.
 */
- (NSArray *)createREQ;

//------------------------------------------------------------------------------
/**
 @brief Creates a formatted app version
 
 @discussion Creates integer from the app version to send in the requestor block in the webservice request.
 The format needed in the server is 000000000
 Example: app version 1.5 must be formatted to 100500
 Example: app version 3.5.4 must be formatted to 300500400
 
 The array consists of 5 ordered items, all of them mandatory: idDevice, idUser, idPlatform, AppVersion and SystemTime.
 Used in createREQ method in FavRestConsumerHelper.
 
 @returns An integer with the app version
 */
- (NSInteger)getAppVersion;


- (NSDictionary *)createFilterForParameter:(NSString *)entity andValue:(NSNumber *)idToSearch;
- (NSDictionary *)createFilterForAllItems:(NSString *)entity;

/**
 @brief Creates the metadata block of a webservice request
 
 @discussion Creates dictionary with the parameters needed in metadata section.
 
 @params operation (required): The operation type to do in the request: Create, Retrieve, Update, Delete, UpdateCreate.
 @params entity (required): The entity name to acces to.
 @params includeDeleted (optional): a boolean value to choose to receive deleted entries in server or not.
 @params totalItems (not needed): not needed to inform in the request, must be null. The server returns the total items in the table, not in the response.
 @params items (optional): the number of the items that we want to be informed in the response. The size limit is defined in the data service policies.
 @params offset (optional): The first item to get from the server. Usefull for pagination purposes.
 @params filter (required): The filter to execute in the request. For full information abour filter.
 
 @returns An NSDictionary with metada block.
 */
- (NSDictionary *)createMetadataForOperation:(NSString *)operation andEntity:(NSString *)entity withItems:(NSNumber *)items withOffSet:(NSNumber *)offset andFilter:(NSDictionary *)filter;

//COMENTARIOOOOOOS
- (NSDictionary *)createMetadataForOperation:(NSString *)operation andEntity:(NSString *)entity withItems:(NSNumber *)items withOffSet:(NSNumber *)offset andKey:(NSDictionary *)key;

- (NSDictionary *)createMetadataForSearchPeopleWithItems:(NSNumber *)items withOffSet:(NSNumber *)offset andFilter:(NSDictionary *)filter;


@end
