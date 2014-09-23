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
#import "AppDelegate.h"

@interface FavRestConsumer ()

@property (nonatomic) BOOL       serviceActive;

@property (nonatomic, strong) AppDelegate *appDelegate;


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
 
    self.appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
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
    NSArray *req = self.appDelegate.request;
    
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
    }else
        DLog(@"No valid req structure created for class %@",NSStringFromClass(entityClass));
}

#pragma mark - Create method
//------------------------------------------------------------------------------
- (void)createEntity:(NSString *)entity withData:(NSArray *)dictArray andKey:(NSDictionary *)key andDelegate:(id)delegate{
    
    //Create Alias block
    NSString *alias = [NSString stringWithFormat:@"Create %@",entity];
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
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
    NSArray *req =self.appDelegate.request;
    
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

//------------------------------------------------------------------------------
- (void)getEntityFromClass:(Class)entityClass withKey:(NSDictionary *)key withDelegate:(id)delegate {
    
    //Create Alias block
    NSString *alias = [FavRestConsumerHelper getAliasForEntity:entityClass];
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [FavRestConsumerHelper createMetadataForOperation:K_OP_RETREAVE
                                                                     andEntity:[FavRestConsumerHelper getEntityForClass:entityClass]
                                                                     withItems:@1
                                                                    withOffSet:@0
                                                                     andKey:key];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntityByKey:entityClass]]};
    
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




@end
