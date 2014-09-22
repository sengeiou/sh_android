 //
//  FavGeneralDAO.m
//  Goles Messenger
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

static NSArray *cuotasToDelete;

@implementation FavGeneralDAO

#pragma mark - Public Methods

//------------------------------------------------------------------------------
+(void)genericParser:(NSDictionary *)dict onCompletion:(void (^)(BOOL status,NSError *error))completionBlock {

    BOOL statusOK = [[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_CODE] isEqualToString:K_WS_STATUS_OK];
    NSArray *ops = [dict objectForKey:K_WS_OPS];
    BOOL returnedItems = [[[[ops objectAtIndex:0] objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_ITEMS] integerValue] > 0;
    
    if (dict && statusOK && returnedItems) {
        
        NSArray *dataArray = [[ops objectAtIndex:0] objectForKey:K_WS_OPS_DATA];
        if (dataArray.count > 0) {
            
            //Check for entity class
            NSString *class = [[[ops objectAtIndex:0] objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_ENTITY];
            
            //+ (NSString *)getClassForString:(NSString *)entityString {

            
            NSArray *insertedArray = [[CoreDataManager sharedInstance] updateEntities:NSClassFromString([FavRestConsumerHelper getClassForString:class]) WithArray:dataArray]; //NSClassFromString(@"User")
            if (insertedArray.count > 0){
                if ([[CoreDataManager singleton] saveContext])
                    completionBlock(YES,nil);
            }
        }
    }
    else{
        
        DLog(@"\n\nSERVER RESPONSE STATUS KO\nSERVER MESSAGE:%@",[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_MESSAGE]);
        NSError *error = [NSError errorWithDomain:@"Data service error" code:0 userInfo:[dict objectForKey:K_WS_STATUS]];
        completionBlock(NO,error);
    }
}


+(BOOL)parseDevice:(NSDictionary *)deviceData {
    
    if (!deviceData || [deviceData isKindOfClass:[NSNull class]]) {
        return NO;
    }
    else {
        NSNumber *idDevice = [deviceData objectForKey:kJSON_ID_DEVICE];
        if ([idDevice isKindOfClass:[NSNumber class]]) {
            Device *device = [Device updateWithDictionary:deviceData];
            if (device)     return YES;
        }
    }
    return NO;
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

#pragma mark - Private Methods

@end
