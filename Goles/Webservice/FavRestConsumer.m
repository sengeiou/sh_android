 //
//  FavRestConsumer.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 13/01/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FavRestConsumer.h"
#import "FavGeneralDAO.h"
#import "Constants.h"
#import "AFNetworkActivityIndicatorManager.h"
#import "UIProgressView+AFNetworking.h"
#import "UserManager.h"
#import "CoreDataParsing.h"
#import "FavEntityDescriptor.h"
#import "FilterCreation.h"
#import "FavRestConsumerHelper.h"
#import "CoreDataManager.h"
#import "LeaguesManager.h"

@interface FavRestConsumer ()

@property (nonatomic) BOOL       serviceActive;

@end

@implementation FavRestConsumer

#pragma mark - Singleton methods
//------------------------------------------------------------------------------
+ (instancetype)sharedInstance {
    static FavRestConsumer *_sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        
        // Network activity indicator manager setup
        [[AFNetworkActivityIndicatorManager sharedManager] setEnabled:YES];
        
        // Session configuration setup
        NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];

        [sessionConfiguration setTimeoutIntervalForRequest:6.0];
        [sessionConfiguration setTimeoutIntervalForResource:8.0];
        
        // Initialize the Consumer
        _sharedInstance = [[FavRestConsumer alloc] initWithSessionConfiguration:sessionConfiguration];
    });
    
    return _sharedInstance;
}

#pragma mark - Init methods
//------------------------------------------------------------------------------
- (instancetype)initWithSessionConfiguration:(NSURLSessionConfiguration *)configuration {
    
    NSURL *baseURL = [NSURL URLWithString:golesBaseURL];
    self = [super initWithBaseURL:baseURL sessionConfiguration:configuration];
    self.serviceActive = YES;
    
    if (!self) return nil;

    [self setRequestSerializer:[AFJSONRequestSerializer serializer]];
    
    //SSL setup
    self.securityPolicy = [self GolesSecurityPolicy];

    return self;
}

//------------------------------------------------------------------------------
- (AFSecurityPolicy *)GolesSecurityPolicy {
    
    AFSecurityPolicy *securityPolicy = [[AFSecurityPolicy alloc] init];
    [securityPolicy setAllowInvalidCertificates:YES];
    [securityPolicy setSSLPinningMode:AFSSLPinningModeNone];
    return securityPolicy;
}

#pragma mark - Conection Methods

//------------------------------------------------------------------------------
/**
 @brief Creates a basic HTTP POST connection.
 
 @discussion Creates a basic HTTP POST connection with paramters and block object as response.
 
 @params NSDictionary (parameters) with the request necessary data
 @params Block with predefined data and error  - (^FetchDataCompletionBlock)(NSDictionary *data, NSError *error)
 
 @returns The response of the connection is provided inside the block.
 */
//------------------------------------------------------------------------------
- (void)fetchDataWithParameters:(NSDictionary *)parameters onCompletion:(FetchDataCompletionBlock)completionBlock;
{
    
    NSString *finalURL;
    if (self.serviceActive)
        finalURL = golesBaseURL;
    else
        finalURL = golesBaseURL;
    
        [self POST:finalURL parameters:parameters success:^(NSURLSessionDataTask *task, id responseObject) {
            NSDictionary *data = responseObject;
            completionBlock(data, nil);
            
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            if ([[[error userInfo] objectForKey:AFNetworkingOperationFailingURLResponseErrorKey] statusCode] != 200) {
                self.serviceActive = !self.serviceActive;
            }
            completionBlock(nil, error);
        }];
}

#pragma mark - Server time request
//------------------------------------------------------------------------------
- (void)getServerTime:(FetchDataCompletionBlock)completionBlock {

    [self POST:SERVERTIME parameters:nil success:^(NSURLSessionDataTask *task, id responseObject) {
        NSDictionary *data = responseObject;
        completionBlock(data, nil);
        
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        //Retry
        completionBlock(nil, error);
    }];
}

#pragma mark - Generic Request creation

//------------------------------------------------------------------------------
- (void)getAllEntitiesFromClass:(Class)entityClass withDelegate:(id)delegate {
    
    //Create Alias block
    NSString *alias = [FavRestConsumerHelper getAliasForEntity:entityClass];
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = [FavRestConsumerHelper createREQ];
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [FavRestConsumerHelper createMetadataForOperation:K_OP_RETREAVE
                                                                     andEntity:NSStringFromClass(entityClass)
                                                                     withItems:@1000
                                                                     withOffSet:@0
                                                                     andFilter:[FilterCreation getFilterForEntity:entityClass]];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:entityClass]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Check if delegate has protocol "ParserProtocol" implemented
    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(parserResponseForClass:status:andError:)];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error && delegateRespondsToProtocol)
                [FavGeneralDAO genericParser:data onCompletion:^(BOOL status,NSError *error){
                   
                   if (!error && status)
                       [delegate parserResponseForClass:entityClass status:YES andError:nil];
                    else
                       [delegate parserResponseForClass:entityClass status:NO andError:error];
                }];
            else if (delegateRespondsToProtocol){
                
                [delegate parserResponseForClass:entityClass status:NO andError:error];
                DLog(@"Request error:%@",error);
            }
        }];
    }else if (delegateRespondsToProtocol){
        
        NSError *reqError = [NSError errorWithDomain:@"Request error" code:1 userInfo:operation];
        [delegate parserResponseForClass:entityClass status:NO andError:reqError];
        DLog(@"No valid req structure created");
    }
}

#pragma mark - Request for favorite teams calendar view
//------------------------------------------------------------------------------
- (void)getAllMatchesForTeams:(NSArray *)teams andDelegate:(id)delegate{
    
    //Create Alias block
    NSString *alias = kALIAS_GET_CALENDAR;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = [FavRestConsumerHelper createREQ];
    
    //Filter creation
    
    //First filter - mode and date
    NSMutableArray *filterItemsA = [[NSMutableArray alloc] initWithCapacity:2];
    NSDictionary *modeFilter = @{K_WS_COMPARATOR:K_WS_OPS_EQ,kJSON_NAME:@"idMode",kJSON_VALUE:[[[LeaguesManager sharedInstance] getSelectedMode] idMode]};
    NSDictionary *tournamentFilter = @{K_WS_COMPARATOR:K_WS_OPS_EQ,kJSON_NAME:@"tournamentStatus",kJSON_VALUE:@1};
    [filterItemsA addObject:modeFilter];
    [filterItemsA addObject:tournamentFilter];
    
    //First filter - all desired teams

    NSMutableArray *filterItemsB = [[NSMutableArray alloc] initWithCapacity:[teams count]];
    
    for (Team *team in teams) {
        NSDictionary *firstTeamFilter = @{K_WS_COMPARATOR: K_WS_OPS_EQ,kJSON_NAME:@"idTeamLocal",kJSON_VALUE:team.idTeam};
        NSDictionary *secondTeamFilter = @{K_WS_COMPARATOR: K_WS_OPS_EQ,kJSON_NAME:@"idTeamVisitor",kJSON_VALUE:team.idTeam};
        
        [filterItemsB addObject:firstTeamFilter];
        [filterItemsB addObject:secondTeamFilter];
    }

    NSDictionary *mainTeamFilter = @{K_WS_OPS_NEXUS:K_WS_OPS_OR,K_WS_FILTERITEMS:filterItemsB,K_WS_FILTERS:@[]};
    
    NSDictionary *filter = @{K_WS_OPS_FILTER:@{K_WS_OPS_NEXUS:K_WS_OPS_AND,K_WS_FILTERITEMS:filterItemsA,K_WS_FILTERS:@[mainTeamFilter]}};
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [FavRestConsumerHelper createMetadataForOperation:K_OP_RETREAVE
                                                                     andEntity:@"TeamCalendar"
                                                                     withItems:@2000
                                                                     withOffSet:@0
                                                                     andFilter:filter];
    
    //Properties to retrieve
    
    NSDictionary *properties = @{K_WS_OPS_REVISION:[NSNull null],
                                 K_WS_OPS_BIRTH_DATE:[NSNull null],
                                 K_WS_OPS_UPDATE_DATE:[NSNull null],
                                 K_WS_OPS_DELETE_DATE:[NSNull null],
                                 kJSON_ID_TEAM_LOCAL:[NSNull null],
                                 kJSON_ID_TEAM_VISITOR:[NSNull null],
                                 @"idRound":[NSNull null],
                                 kJSON_DATE_MATCH:[NSNull null],
                                 kJSON_SCORE_LOCAL:[NSNull null],
                                 kJSON_SCORE_VISITOR:[NSNull null],
                                 kJSON_MATCH_STATE:[NSNull null],
                                 @"roundDescription":[NSNull null],
                                 @"tournamentName":[NSNull null],
                                 @"localNameShort":[NSNull null],
                                 @"dateMatchNotConfirmed":[NSNull null],
                                 @"visitorNameShort":[NSNull null],
                                 @"localNameTiny":[NSNull null],
                                 @"visitorNameTiny":[NSNull null],
                                 @"roundStartDate":[NSNull null],
                                 @"roundEndDate":[NSNull null]};

    //Create 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[properties]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error){
               // NSArray *matches = [FavGeneralDAO parseMatchesForCalendar:data];
                //[delegate getTeamCalendarDidResponse:matches];
            }
            else {
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created");
    
}

//------------------------------------------------------------------------------
- (void)getAllEventsForMatch:(Match *)match andDelegate:(id)delegate{
   
    //Create Alias block
    NSString *alias = kALIAS_GET_ALL_EVENTS_FOR_MATCH;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = [FavRestConsumerHelper createREQ];
    
    NSDictionary *metadata = [FavRestConsumerHelper createMetadataForOperation:K_OP_RETREAVE
                                                                     andEntity:K_COREDATA_EVENTOFMATCH
                                                                     withItems:@100
                                                                    withOffSet:@0
                                                                     andFilter:[FavRestConsumerHelper createFilterForParameter:kJSON_ID_MATCH andValue:match.idMatch]];
    //Create ONE 'ops' block
    NSDictionary *simpleOPS = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:[EventOfMatch class]]]};
    
    //Create 'ops' block
    NSArray *ops = @[simpleOPS];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error)
                [FavGeneralDAO genericParser:data onCompletion:^(BOOL status, NSError *error) {
                    
                    [delegate updateEventsMatchList:match];
                    
                }];
            else {
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created");

}

//------------------------------------------------------------------------------
- (void)getDeviceSuscriptions {
    
    //Create Alias block
    NSString *alias = kALIAS_GETALL_SUSCRIPTIONS;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = [FavRestConsumerHelper createREQ];
    
    //Create Suscriptions 'metadata' block
    NSNumber *idDevice = [[UserManager sharedInstance] getIdDevice];
  /*  NSDictionary *metadataSuscriptions = [FavRestConsumerHelper createMetadataForOperation:K_OP_RETREAVE
                                                                                 andEntity:K_COREDATA_SUSCRIPTIONS
                                                                                 withItems:@300
                                                                                 andFilter:[FavRestConsumerHelper createFilterForParameter:kJSON_ID_DEVICE andValue:idDevice]];*/
    NSDictionary *metadataSuscriptions = [FavRestConsumerHelper createMetadataForOperation:K_OP_RETREAVE andEntity:K_COREDATA_SUSCRIPTIONS withItems:@300 withOffSet:@0 andFilter:[FavRestConsumerHelper createFilterForParameter:kJSON_ID_DEVICE andValue:idDevice]];

    //Create Teams 'ops' block
    NSDictionary *opsSuscriptions = @{K_WS_OPS_METADATA:metadataSuscriptions,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:[Subscription class]]]};
    
    //Create 'ops' block
    NSArray *ops = @[opsSuscriptions];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error)
               // [FavGeneralDAO parseSubscriptions:data];
                
                [FavGeneralDAO genericParser:data onCompletion:^(BOOL status, NSError *error) {
                    NSLog(@"parser subscriptions hecho");
                }];

            else {
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created");
    
}

#pragma mark - Device Consumer methods
//------------------------------------------------------------------------------
// DEVICE RESTCONSUMER
//------------------------------------------------------------------------------
- (void)deviceRegistration:(Device *)device withDelegate:(id)delegate{
     
    //Create Alias block
    NSString *alias = kALIAS_REGISTER_DEVICE;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = [FavRestConsumerHelper createREQ];
    
    //Create Suscriptions 'metadata' block
    NSNumber *idDevice = [[UserManager sharedInstance] getIdDevice];
   
    NSDictionary *metadataSuscriptions = [FavRestConsumerHelper createMetadataForOperation:K_OP_RETREAVE andEntity:K_COREDATA_DEVICE withItems:@1 withOffSet:@0 andFilter:[FavRestConsumerHelper createFilterForParameter:kJSON_ID_DEVICE andValue:idDevice]];
    
    //Create Teams 'ops' block
    NSDictionary *opsSuscriptions = @{K_WS_OPS_METADATA:metadataSuscriptions,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:[Device class]]]};
    
    //Create 'ops' block
    NSArray *ops = @[opsSuscriptions];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error)
            
                [FavGeneralDAO parseDevice:data];
            else {
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created");

}

#pragma mark - Create method
//------------------------------------------------------------------------------
- (void)createEntity:(NSString *)entity withData:(NSArray *)dictArray andKey:(NSDictionary *)key andDelegate:(id)delegate{
    
    //Create Alias block
    NSString *alias = [NSString stringWithFormat:@"Create %@",entity];
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = [FavRestConsumerHelper createREQ];
    
    //Create 'metadata' block
    NSDictionary *metadata = @{K_WS_OPS_OPERATION:K_OP_RETREAVE,K_WS_OPS_KEY:key,K_WS_OPS_ENTITY:entity,K_WS_OPS_ITEMS:[NSNull null]};
    
    //Create 'ops' block
    NSDictionary *opsDictionary = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:dictArray};
    
    //Join all 'ops' blocks
    NSArray *ops = @[opsDictionary];
    
    //Create full data structure
    if (req && [ops count] > 0) {
        
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error)
                [FavGeneralDAO genericParser:data onCompletion:^(BOOL status, NSError *error) {
                    
                    NSLog(@"%@ CREADA", entity);
                }];
            else {
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created");
}

//------------------------------------------------------------------------------
- (void)deleteEntity:(NSString *)entity withKey:(NSDictionary *)key andData:(NSArray *)data andDelegate:(id)delegate{
    
    //Create Alias block
    NSString *alias = [NSString stringWithFormat:@"Delete %@",entity];
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = [FavRestConsumerHelper createREQ];
    
    //Create 'metadata' block
    
    NSDictionary *metadata = @{K_WS_OPS_OPERATION:K_OP_DELETE,K_WS_OPS_ENTITY:entity,K_WS_OPS_KEY:key,K_WS_OPS_ITEMS:[NSNull null],K_WS_OPS_TOTAL_ITEMS:[NSNull null]};
    
    //Create 'ops' block
    NSDictionary *opsDictionary = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:data};
    
    //Join all 'ops' blocks
    NSArray *ops = @[opsDictionary];
    
    //Create full data structure
    if (req && [ops count] > 0) {
        
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *responseData,NSError *error) {
            
            if (!error)
                [FavGeneralDAO genericParser:responseData onCompletion:^(BOOL status, NSError *error) {
                    
                    NSLog(@"%@ BORRADA", entity);
                }];
            else {
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created");
}

@end
