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
#import "CuotasManager.h"

#import "CalendarFavoritesTableViewController.h"

static NSArray *cuotasToDelete;

@implementation FavGeneralDAO

#pragma mark - Public Methods

//------------------------------------------------------------------------------
/**
 @brief Parse for MatchBetType.
 
 @discussion Checks for correct response from the FavRestConsumer - getOddsForMatch:withDelegate:
 and call the method that creates the request for the next object needed (BetType, BetTypeOdd and Provider).
 Also mark the current BetTypeOdds for the current match to be deleted.
 
 @params NSDictionary       data - The response object from getOddsForMatch:withDelegate:
 @params delegate           The class that's in charge for the request
 @params Match              The match of the initial search
 
 @returns None - If "data" is OK, automatically calls the next method (getCompleteInfo:andMatch:andDelegate: in FavRestConsumer)
 */
//------------------------------------------------------------------------------
+(void)cuotasParserForData:(NSDictionary *)data andMatch:(Match *)match withdelegate:(id)delegate{

    NSDictionary *status = [data objectForKey:K_WS_STATUS];
    
    //RESPONSE FROM SERVER OK
    if (status && [[status objectForKey:K_WS_STATUS_CODE] isEqualToString:K_WS_STATUS_OK]) {
        //Server response is OK
        
        [[CuotasManager singleton] markAllCuotasForMatchToDelete:match];

        //Proceed to insert new received cuotas
        NSArray *ops = [data objectForKey:K_WS_OPS];
        if ([ops count] > 0) {
            NSDictionary *operation = [[data objectForKey:K_WS_OPS] objectAtIndex:0];
            NSArray *dataBlock = [operation objectForKey:K_WS_OPS_DATA];
            NSDictionary *metadataBlock = [operation objectForKey:K_WS_OPS_METADATA];
            if ([[metadataBlock objectForKey:K_WS_OPS_ITEMS] integerValue] > 0) {
                [[FavRestConsumer sharedInstance] getCompleteInfo:dataBlock andMatch:match andDelegate:delegate];
            }else {
                [delegate getOddsForMatchResponseError];
                DLog(@"NO cuotas in server response");
            }
        }
        
    //RESPONSE FROM SERVER KO
    }else if (status && [[status objectForKey:K_WS_STATUS_MESSAGE] isEqualToString:K_WS_STATUS_KO]) {
        DLog(@"\nServer response KO\nServer message:%@ \nServer error Code:%@",[status objectForKey:K_WS_STATUS_MESSAGE],[status objectForKey:K_WS_STATUS_CODE]);
    }else
        DLog(@"\nServer response either OK or KO, strange things happening. \nServer message:%@ \nServer error Code:%@",[status objectForKey:K_WS_STATUS_MESSAGE],[status objectForKey:K_WS_STATUS_CODE]);
}

//------------------------------------------------------------------------------
/**
 @brief Parse for BetType, BetTypeOdd and Provider.
 
 @discussion Checks for correct response from the FavRestConsumer - getCompleteInfo:andMatch:andDelegate:
 and calls the insertion methods in CoreDataManager for BetType, BetTypeOdd and Provider.
 
 @params NSDictionary       dict - The response object from getCompleteInfo:andMatch:andDelegate:
 @params delegate           The class that's in charge for the request
 @params NSDictionary       The MatchBetType
 
 @returns YES - If all the process of request-parse-request-parse-insertion is Done
 @returns NO - If all the process of request-parse-request-parse-insertion fails
 */
//------------------------------------------------------------------------------
+(BOOL)providerAndBetTypeAndBetTypeOddParser:(NSDictionary *)dict forMatchBetType:(NSDictionary *)matchBetType andMatch:(Match *)match{
    
    if (dict && [[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_CODE] isEqualToString:K_WS_STATUS_OK]) {
        
        //Server response is OK
        NSArray *ops = [dict objectForKey:K_WS_OPS];
        NSMutableArray *betOdds = [[NSMutableArray alloc] init];

        Provider *provider = [Provider createTemporaryProvider];
        BetType *betType = [BetType createTemporaryBetType];
        MatchBetType *matchBT = [MatchBetType createTemporaryMatchBetType];
        
        for (NSDictionary *object in ops) {
            
            if ([[self getEntityForOperation:object] isSubclassOfClass:[Provider class]]) {
                NSDictionary *providerDict = [[object objectForKey:K_WS_OPS_DATA] objectAtIndex:0];
                provider = [Provider updateWithDictionary:providerDict];
            }

            if ([[self getEntityForOperation:object] isSubclassOfClass:[BetType class]]) {
                NSDictionary *betTypeDict = [[object objectForKey:K_WS_OPS_DATA] objectAtIndex:0];
                betType = [BetType updateWithDictionary:betTypeDict];
            }

            if ([[self getEntityForOperation:object] isSubclassOfClass:[BetTypeOdd class]]) {
                NSArray *betOddArray = [object objectForKey:K_WS_OPS_DATA];
                if ([betOddArray count] > 0) {
                    for (NSDictionary *betOdd in betOddArray) {
                        BetTypeOdd *betTypeOdd = [BetTypeOdd updateWithDictionary:betOdd];
                        if (betTypeOdd)
                            [betOdds addObject:betTypeOdd];
                    }
                }
            }

        }
        if (provider && betType) {
            NSMutableDictionary *mbt = [[NSMutableDictionary alloc] initWithDictionary:matchBetType];
            [mbt addEntriesFromDictionary:@{K_COREDATA_MATCH:match,kJSON_PROVIDER:provider,kJSON_BETTYPE:betType}];
            matchBT = [MatchBetType updateWithDictionary:mbt];
            if (!matchBT)
                return NO;
            else {
                for (BetTypeOdd *bet in betOdds)
                    [bet setMatchBetType:matchBT];
            }
        }
        
        if ([[CoreDataManager singleton] saveContext])
            return YES;
        else
            return NO;
    }
    else {
        DLog(@"\nSERVER RESPONSE STATUS KO\n SERVER MESSAGE:%@",[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_MESSAGE]);
        return NO;
    }
    return NO;
}

//Used only in favourite teams calendar. Not inserted in CoreData
//------------------------------------------------------------------------------
+(NSArray *)parseMatchesForCalendar:(NSDictionary *)dict{
    
    if (dict && [[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_CODE] isEqualToString:K_WS_STATUS_OK]) {
        
        //Server response is OK
        NSArray *ops = [dict objectForKey:K_WS_OPS];
        if  ([[[[ops objectAtIndex:0] objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_ITEMS] integerValue] > 0) {
            
            for (NSDictionary *object in ops) {
                
                NSArray *teamsArray = [object objectForKey:K_WS_OPS_DATA];
                if ([teamsArray count] > 0){
                    return teamsArray;
                }else {
                    return nil;
                    DLog(@"No data for Calendar in server response");
                }
            }
        }else {
            return nil;
            DLog(@"No data for Calendar in server response");
        }
    }
    else {
        return nil;
        DLog(@"\nSERVER RESPONSE STATUS KO\n SERVER MESSAGE:%@",[[dict objectForKey:K_WS_STATUS] objectForKey:K_WS_STATUS_MESSAGE]);
    }
    return nil;
}

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
            
            NSArray *insertedArray = [[CoreDataManager sharedInstance] updateEntities:NSClassFromString(class) WithArray:dataArray];
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
