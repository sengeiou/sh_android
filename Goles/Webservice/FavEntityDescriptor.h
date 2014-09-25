//
//  FavEntityDescriptor.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 19/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FavEntityDescriptor : NSObject

//------------------------------------------------------------------------------
/**
 @brief Creates a dictionary with all the properties of a CoreData entity.
 
 @discussion It's used in the creation of the webservice request. 
 We use a restful webservice so we need to list all the properties we want to be returned.
 Used in FavRestConsumer.
 
 @params Class The entity class that we want for the request
 
 @returns An NSDictionary with all properties of the entity
 */
+ (NSDictionary *)createPropertyListForEntity:(Class)entityClass;

+ (NSDictionary *)createPropertyListForLogin;

@end
