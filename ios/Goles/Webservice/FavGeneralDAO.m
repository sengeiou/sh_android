  //
//  FavGeneralDAO.m
//
//  Created by Christian Cabarrocas on 14/01/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FavGeneralDAO.h"
#import "FavRestConsumer.h"
#import "Constants.h"
#import "CoreDataParsing.h"
#import "CoreDataManager.h"
#import "FavRestConsumerHelper.h"
#import "SyncManager.h"
#import "UserManager.h"
#import "Shot.h"
#import "Watch.h"

static NSArray *cuotasToDelete;

@implementation FavGeneralDAO

#pragma mark - Public Methods

//------------------------------------------------------------------------------
+(void)genericParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error, BOOL refresh))completionBlock {

    BOOL statusOK = [[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_CODE] isEqualToString:K_WS_STATUS_OK];
    NSArray *ops = [dict objectForKey:K_WS_OPS];
    BOOL returnedItems = [[[[ops objectAtIndex:0] objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_ITEMS] integerValue] > 0;
    
    if (dict && statusOK) {
        
        NSArray *dataArray = [[ops objectAtIndex:0] objectForKey:K_WS_OPS_DATA];
        if (returnedItems) {
            
            //Check for entity class
            NSString *class = [[[ops objectAtIndex:0] objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_ENTITY];
            
            NSArray *insertedArray = [[CoreDataManager sharedInstance] updateEntities:NSClassFromString([[FavRestConsumerHelper singleton] getClassForString:class]) WithArray:dataArray];
            if (insertedArray.count > 0){
                if ([[CoreDataManager singleton] saveContext]){
                    NSNumber *value = [[CoreDataManager singleton] getMaxModifiedValueForEntity:class];
                    [[SyncManager singleton] setSyncData:dict withValue:value];
                }
            }
            completionBlock(YES,nil, YES);
        }else{
            completionBlock(NO,nil, YES);
        }
    }
    else if (!statusOK){
        
        DLog(@"\n\nSERVER RESPONSE STATUS KO\nSERVER MESSAGE:%@",[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_MESSAGE]);
        NSError *error = [NSError errorWithDomain:@"Data service error" code:0 userInfo:[dict objectForKey:K_WS_STATUS]];
        completionBlock(NO,error, NO);
    }
}

//------------------------------------------------------------------------------
+(void)shotParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error, BOOL refresh))completionBlock {
    
    BOOL statusOK = [[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_CODE] isEqualToString:K_WS_STATUS_OK];
    NSArray *ops = [dict objectForKey:K_WS_OPS];
    BOOL returnedItems = [[[[ops objectAtIndex:0] objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_ITEMS] integerValue] > 0;
    
    if (dict && statusOK) {
        
        NSArray *dataArray = [[ops objectAtIndex:0] objectForKey:K_WS_OPS_DATA];
        if (returnedItems) {
            
            NSArray *insertedArray = [[CoreDataManager sharedInstance] updateEntities:[Shot class] WithArray:dataArray];
            if (insertedArray.count > 0){
                if ([[CoreDataManager singleton] saveInsertContext]){
                    NSNumber *value = [[CoreDataManager singleton] getMaxModifiedValueForEntity:NSStringFromClass([Shot class])];
                    [[SyncManager singleton] setSyncData:dict withValue:value];
                }
            }
            completionBlock(YES,nil, YES);
        }else{
            completionBlock(NO,nil, NO);
        }
    }
    else if (!statusOK){
        
        DLog(@"\n\nSERVER RESPONSE STATUS KO\nSERVER MESSAGE:%@",[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_MESSAGE]);
        NSError *error = [NSError errorWithDomain:@"Data service error" code:0 userInfo:[dict objectForKey:K_WS_STATUS]];
        completionBlock(NO,error, NO);
    }
}

//------------------------------------------------------------------------------
+(void)searchParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error,NSArray *data, int totalItems))completionBlock {
    
    BOOL statusOK = [[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_CODE] isEqualToString:K_WS_STATUS_OK];
    NSArray *ops = [dict objectForKey:K_WS_OPS];

    NSUInteger returnedItems = [[[[ops objectAtIndex:0] objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_ITEMS] integerValue];
    NSUInteger totalItems = [[[[ops objectAtIndex:0] objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_TOTAL_ITEMS] integerValue];
    
    BOOL needToPaginate = totalItems > returnedItems;
    
    if (dict && statusOK) {
        
        NSArray *dataArray = [[ops objectAtIndex:0] objectForKey:K_WS_OPS_DATA];
        if (returnedItems > 0) {
            NSMutableArray *usersArray = [[NSMutableArray alloc] init];
            for (NSDictionary *dict in dataArray) {
                User *currentUser = [[UserManager singleton] createUserFromDict:dict];
                if (currentUser)
                    [usersArray addObject:currentUser];
                
            }
            NSLog(@"Users:%lu",(unsigned long)usersArray.count);
            completionBlock(YES,nil, usersArray,needToPaginate);
        }else{
            completionBlock(YES,nil, nil,needToPaginate);
        }
    }
    else if (!statusOK){
        
        DLog(@"\n\nSERVER RESPONSE STATUS KO\nSERVER MESSAGE:%@",[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_MESSAGE]);
        NSError *error = [NSError errorWithDomain:@"Data service error" code:0 userInfo:[dict objectForKey:K_WS_STATUS]];
        completionBlock(NO,error, nil,needToPaginate);
    }
}

//------------------------------------------------------------------------------
+(void)watchParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error, BOOL refresh))completionBlock {
    
    BOOL statusOK = [[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_CODE] isEqualToString:K_WS_STATUS_OK];
    NSArray *ops = [dict objectForKey:K_WS_OPS];
    BOOL returnedItems = [[[[ops objectAtIndex:0] objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_ITEMS] integerValue] > 0;
    
    if (dict && statusOK) {
        
        if (returnedItems) {
            
            NSArray *dataArray = [[ops objectAtIndex:0] objectForKey:K_WS_OPS_DATA];
            NSArray *insertedArray = [[CoreDataManager sharedInstance] updateEntities:[Watch class] WithArray:dataArray];
            if (insertedArray.count > 0){
                if ([[CoreDataManager singleton] saveContext]){
                    NSNumber *value = [[CoreDataManager singleton] getMaxModifiedValueForEntity:K_COREDATA_WATCH];
                    [[SyncManager singleton] setSyncData:dict withValue:value];
                }
            }
            completionBlock(YES,nil, YES);
        }else{
            completionBlock(NO,nil, YES);
        }
    }
    else if (!statusOK){
        
        DLog(@"\n\nSERVER RESPONSE STATUS KO\nSERVER MESSAGE:%@",[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_MESSAGE]);
        NSError *error = [NSError errorWithDomain:@"Data service error" code:0 userInfo:[dict objectForKey:K_WS_STATUS]];
        completionBlock(NO,error, NO);
    }
}

//------------------------------------------------------------------------------
+(void)checkForMatchDependenciesInWatchArray:(NSArray *)data withCompletion:(void (^)(BOOL status, NSError *error))completionBlock {

    NSMutableArray *matchesToSearchIDs = [[NSMutableArray alloc] init];
    for (NSDictionary *watchDict in data) {
        NSInteger idMatch = [[watchDict objectForKey:kJSON_ID_MATCH] integerValue];
        if (![[CoreDataManager singleton] getEntity:[Match class] withId:idMatch]){
            [matchesToSearchIDs addObject:[NSNumber numberWithInteger:idMatch]];
        }
    }
}

//------------------------------------------------------------------------------
/**
 @brief Get the Class type for a request operation
 
 @discussion Get the Class type for a request operation in Metadata block, entity property
 
 @params NSDictionary       operation - The response object FavRestConsumer - getAllProviders
 
 @returns Class - Object of class type of the entity in operation
 */
//------------------------------------------------------------------------------
+(Class)getEntityForOperation:(NSDictionary *)operation {
    
    NSString *entityType = [[operation objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_ENTITY];
    Class class = NSClassFromString(entityType);
    return class;
}

@end
