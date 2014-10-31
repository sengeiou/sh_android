 //
//  FavRestConsumer.m
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
#import "ShotManager.h"
#import "Follow.h"
#import "Shot.h"
#import "Watch.h"
#import "CommunicationHelper.h"

@interface FavRestConsumer ()

@property (nonatomic) BOOL       serviceActive;

@property (nonatomic, strong) AppDelegate *appDelegate;
@property (nonatomic, strong) CommunicationHelper *communicationHelper;

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

        [sessionConfiguration setTimeoutIntervalForRequest:12.0];
        [sessionConfiguration setTimeoutIntervalForResource:8.0];
        
        // Initialize the Consumer
        _sharedInstance = [[FavRestConsumer alloc] initWithSessionConfiguration:sessionConfiguration];
        
        _sharedInstance.communicationHelper = [CommunicationHelper singleton];
        
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
    self.securityPolicy = [self ShootrSecurityPolicy];
 
    self.appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    return self;
}

//------------------------------------------------------------------------------
- (AFSecurityPolicy *)ShootrSecurityPolicy {
    
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

#pragma mark - RETRIEVE GENERIC

//------------------------------------------------------------------------------
- (void)getAllEntitiesFromClass:(Class)entityClass withDelegate:(id)delegate {
    
    //Create Alias block
    NSString *alias = [[FavRestConsumerHelper singleton] getAliasForEntity:entityClass];
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [[FavRestConsumerHelper singleton] createMetadataForOperation:K_OP_RETREAVE
                                                                     andEntity:NSStringFromClass(entityClass)
                                                                     withItems:@700
                                                                     withOffSet:@0
                                                                     andFilter:[FilterCreation getFilterForEntity:entityClass]];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:entityClass]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
      //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};

       [self.communicationHelper postRequest:serverCall onCompletion:^(NSDictionary *response, NSError *error) {
            [self validateAllEntitiesFromClass:entityClass withData:response withError:error andDelegate:delegate];
        }];
        
    }else
        DLog(@"No valid req structure created for class %@",NSStringFromClass(entityClass));
}

//------------------------------------------------------------------------------
-(void)validateAllEntitiesFromClass:(Class)entityClass withData:(NSDictionary *)data withError:(NSError *)error andDelegate:(id)delegate{
    //Check if delegate has protocol "ParserProtocol" implemented
    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)];
    

    if (!error){
        if ([entityClass isSubclassOfClass:[Shot class]]) {
            [FavGeneralDAO shotParser:data onCompletion:^(BOOL status, NSError *error, BOOL refresh) {
                if (!error && status && delegateRespondsToProtocol)
                    [delegate parserResponseForClass:entityClass status:YES andError:nil  andRefresh:refresh];
                else if (delegateRespondsToProtocol)
                    [delegate parserResponseForClass:entityClass status:NO andError:error andRefresh:refresh];
            }];
        }else{
            [FavGeneralDAO genericParser:data onCompletion:^(BOOL status, NSError *error, BOOL refresh) {
                if (!error && status && delegateRespondsToProtocol)
                    [delegate parserResponseForClass:entityClass status:YES andError:nil  andRefresh:refresh];
                else if (delegateRespondsToProtocol)
                    [delegate parserResponseForClass:entityClass status:NO andError:error andRefresh:refresh];
            }];
        }
        
    }else if (delegateRespondsToProtocol){
        
        [delegate parserResponseForClass:entityClass status:NO andError:error andRefresh:NO];
        DLog(@"Request error:%@",error);
    }

}

//------------------------------------------------------------------------------
- (void)getEntityFromClass:(Class)entityClass withKey:(NSDictionary *)key withDelegate:(id)delegate {
    
    //Create Alias block
    NSString *alias = [[FavRestConsumerHelper singleton] getAliasForEntity:entityClass];
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [[FavRestConsumerHelper singleton] createMetadataForOperation:K_OP_RETREAVE
                                                                                 andEntity:NSStringFromClass(entityClass)
                                                                                 withItems:@1
                                                                                withOffSet:@0
                                                                                    andKey:key];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:entityClass]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Check if delegate has protocol "ParserProtocol" implemented
    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error && delegateRespondsToProtocol)
                [FavGeneralDAO genericParser:data onCompletion:^(BOOL status,NSError *error, BOOL refresh){
                    
                    if (!error && status)
                        [delegate parserResponseForClass:entityClass status:YES andError:nil andRefresh:refresh];
                    else
                        [delegate parserResponseForClass:entityClass status:NO andError:error andRefresh:refresh];
                }];
            else if (delegateRespondsToProtocol){
                
                [delegate parserResponseForClass:entityClass status:NO andError:error andRefresh:NO];
                DLog(@"Request error:%@",error);
            }
        }];
    }else if (delegateRespondsToProtocol){
        
        NSError *reqError = [NSError errorWithDomain:@"Request error" code:1 userInfo:operation];
        [delegate parserResponseForClass:entityClass status:NO andError:reqError andRefresh:NO];
        DLog(@"No valid req structure created");
    }
}


#pragma mark - RETRIEVE NEXT MATCH

//------------------------------------------------------------------------------
- (void)getUserNextMatchWithDelegate:(id)delegate {
    
    //Create Alias block
    NSString *alias = kALIAS_USER_NEXT_MATCH;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [[FavRestConsumerHelper singleton] createMetadataForOperation:K_OP_RETREAVE
                                                                                 andEntity:K_COREDATA_MATCH
                                                                                 withItems:@1
                                                                                withOffSet:@0
                                                                                 andFilter:[FilterCreation getFilterForNextMatchOfMyTeam]];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:[Match class]]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        
        [self.communicationHelper postRequest:serverCall onCompletion:^(NSDictionary *response, NSError *error) {
            [self validateUserNextMatchWithData:response withError:error andDelegate:delegate];
        }];
        
    }else
        DLog(@"No valid req structure created for Match request");
}

//------------------------------------------------------------------------------
-(void)validateUserNextMatchWithData:(NSDictionary *)data withError:(NSError *)error andDelegate:(id)delegate{
    
    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)];
    
    
    if (!error){
        
        [FavGeneralDAO genericParser:data onCompletion:^(BOOL status, NSError *error, BOOL refresh) {
            if (!error && status && delegateRespondsToProtocol)
                [delegate parserResponseForClass:[Match class] status:YES andError:nil  andRefresh:refresh];
            else if (delegateRespondsToProtocol)
                [delegate parserResponseForClass:[Match class] status:NO andError:error andRefresh:refresh];
        }];
        
        
    }else if (delegateRespondsToProtocol){
        
        [delegate parserResponseForClass:[Watch class] status:NO andError:error andRefresh:NO];
        DLog(@"Request error:%@",error);
    }
    
}


#pragma mark - RETRIEVE WATCH

//------------------------------------------------------------------------------
- (void)getAllWatchWithDelegate:(id)delegate {
    
    //Create Alias block
    NSString *alias = kALIAS_FOLLOWING_WATCHES;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [[FavRestConsumerHelper singleton] createMetadataForOperation:K_OP_RETREAVE
                                                                                 andEntity:K_COREDATA_WATCH
                                                                                 withItems:@1000
                                                                                withOffSet:@0
                                                                                 andFilter:[FilterCreation getFilterForWatches]];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:[Watch class]]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        
        [self.communicationHelper postRequest:serverCall onCompletion:^(NSDictionary *response, NSError *error) {
            [self validateWatchWithData:response withError:error andDelegate:delegate];
        }];
        
    }else
        DLog(@"No valid req structure created for Watch request");
}

//------------------------------------------------------------------------------
-(void)validateWatchWithData:(NSDictionary *)data withError:(NSError *)error andDelegate:(id)delegate{

    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)];
    
    
    if (!error){

        [FavGeneralDAO watchParser:data onCompletion:^(BOOL status, NSError *error, BOOL refresh) {
            if (!error && status && delegateRespondsToProtocol)
                [delegate parserResponseForClass:[Watch class] status:YES andError:nil  andRefresh:refresh];
            else if (delegateRespondsToProtocol)
                [delegate parserResponseForClass:[Watch class] status:NO andError:error andRefresh:refresh];
        }];
        
        
    }else if (delegateRespondsToProtocol){
        
        [delegate parserResponseForClass:[Watch class] status:NO andError:error andRefresh:NO];
        DLog(@"Request error:%@",error);
    }
    
}

#pragma mark - RETRIEVE USERS

//------------------------------------------------------------------------------
- (void)getUsersFromUser:(User *)user withDelegate:(id)delegate  withTypeOfUsers:(NSNumber *) type; {
    
    //Create Alias block
    NSString *alias = [[FavRestConsumerHelper singleton] getAliasForEntity:[User class]];
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [[FavRestConsumerHelper singleton] createMetadataForOperation:K_OP_RETREAVE
                                                                     andEntity:NSStringFromClass([User class])
                                                                     withItems:@1000
                                                                    withOffSet:@0
                                                                     andFilter:[FilterCreation getFilterForUser:user withTypeOfUser: type]];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:[User class]]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Check if delegate has protocol "ParserProtocol" implemented
    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error)
                [FavGeneralDAO genericParser:data onCompletion:^(BOOL status,NSError *error, BOOL refresh){
                    
                    if (!error && status && delegateRespondsToProtocol)
                        [delegate parserResponseForClass:[User class] status:YES andError:nil  andRefresh:refresh];
                    else if (delegateRespondsToProtocol)
                        [delegate parserResponseForClass:[User class] status:NO andError:error andRefresh:refresh];
                }];
            
            else if (delegateRespondsToProtocol){
                
                [delegate parserResponseForClass:[User class] status:NO andError:error andRefresh:NO];
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created for class %@",NSStringFromClass([User class]));
}

#pragma mark - RETRIEVE FOLLOWING OF USER
//------------------------------------------------------------------------------
- (void)getFollowingUsersOfUser:(User *)user withDelegate:(id)delegate {
    
    //Create Alias block
    NSString *alias = kALIAS_FOLLOW;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [[FavRestConsumerHelper singleton] createMetadataForOperation:K_OP_RETREAVE
                                                                                 andEntity:K_COREDATA_FOLLOW
                                                                                 withItems:@300
                                                                                withOffSet:@0
                                                                                 andFilter:[FilterCreation getFilterForFollowingsOfUser:user]];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:[Follow class]]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Check if delegate has protocol "ParserProtocol" implemented
    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error)
                [FavGeneralDAO genericParser:data onCompletion:^(BOOL status,NSError *error, BOOL refresh){
                    
                    if (!error && status && delegateRespondsToProtocol)
                        [delegate parserResponseForClass:[Follow class] status:YES andError:nil  andRefresh:refresh];
                    else
                        [delegate parserResponseForClass:[Follow class] status:NO andError:error andRefresh:refresh];
                }];
            else if (delegateRespondsToProtocol){
                
                [delegate parserResponseForClass:[Follow class] status:NO andError:error andRefresh:NO];
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created for class %@",NSStringFromClass([Follow class]));
}

#pragma mark - RETRIEVE FOLLOWERS OF USER
//------------------------------------------------------------------------------
- (void)getFollowersOfUser:(User *)user withDelegate:(id)delegate {
    
    //Create Alias block
    NSString *alias = kALIAS_FOLLOW;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [[FavRestConsumerHelper singleton] createMetadataForOperation:K_OP_RETREAVE
                                                                                 andEntity:K_COREDATA_FOLLOW
                                                                                 withItems:@300
                                                                                withOffSet:@0
                                                                                 andFilter:[FilterCreation getFilterForFollowersOfUser:user]];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:[Follow class]]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Check if delegate has protocol "ParserProtocol" implemented
    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error)
                [FavGeneralDAO genericParser:data onCompletion:^(BOOL status,NSError *error, BOOL refresh){
                    
                    if (!error && status && delegateRespondsToProtocol)
                        [delegate parserResponseForClass:[Follow class] status:YES andError:nil  andRefresh:refresh];
                    else
                        [delegate parserResponseForClass:[Follow class] status:NO andError:error andRefresh:refresh];
                }];
            else if (delegateRespondsToProtocol){
                
                [delegate parserResponseForClass:[Follow class] status:NO andError:error andRefresh:NO];
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created for class %@",NSStringFromClass([Follow class]));
}


#pragma mark - RETRIEVE OLD SHOTS

//------------------------------------------------------------------------------
- (void)getOldShotsWithDelegate:(id)delegate{
    
    //Create Alias block
    NSString *alias = kALIAS_OLDER_SHOTS;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [[FavRestConsumerHelper singleton] createMetadataForOperation:K_OP_RETREAVE
                                                                     andEntity:K_COREDATA_SHOT
                                                                     withItems:@20
                                                                    withOffSet:@0
                                                                     andFilter:[FilterCreation getFilterForOldShots]];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:NSClassFromString(K_COREDATA_SHOT)]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Check if delegate has protocol "ParserProtocol" implemented
    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error && delegateRespondsToProtocol)

                [FavGeneralDAO shotParser:data onCompletion:^(BOOL status,NSError *error, BOOL refresh){
                    
                    if (!error && status)
                        [delegate parserResponseForClass:NSClassFromString(K_COREDATA_SHOT) status:YES andError:nil andRefresh:refresh];
                    else
                        [delegate parserResponseForClass:NSClassFromString(K_COREDATA_SHOT) status:NO andError:error andRefresh:refresh];
                }];
            else if (delegateRespondsToProtocol){
                
                [delegate parserResponseForClass:NSClassFromString(K_COREDATA_SHOT) status:NO andError:error andRefresh:NO];
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created for class %@",K_COREDATA_SHOT);
}



#pragma mark - CREATE
//------------------------------------------------------------------------------
- (void)createEntity:(NSString *)entity withData:(NSArray *)dictArray andKey:(NSDictionary *)key andDelegate:(id)delegate withOperation:(NSString *)operation{
    
    //Create Alias block
    
    NSString *alias;
    if ([operation isEqualToString:K_OP_DELETE])
        alias = [NSString stringWithFormat:@"DELETE_%@",entity.uppercaseString];
    else
        alias = [NSString stringWithFormat:@"CREATE_%@",entity.uppercaseString];
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create 'metadata' block
    NSDictionary *metadata = @{K_WS_OPS_OPERATION:operation,K_WS_OPS_KEY:key,K_WS_OPS_ENTITY:entity,K_WS_OPS_ITEMS:@1};
    
    //Create 'ops' block
    NSDictionary *opsDictionary = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:dictArray};
    
    //Join all 'ops' blocks
    NSArray *ops = @[opsDictionary];
    
    //Create full data structure
    if (req && ops.count > 0) {
        
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error){
                
                if ([entity isEqualToString:K_COREDATA_SHOT]) {
                    [FavGeneralDAO shotParser:data onCompletion:^(BOOL status, NSError *error, BOOL refresh) {
                        if ([delegate respondsToSelector:@selector(createShotResponseWithStatus:andError:)])
                            [delegate createShotResponseWithStatus:YES andError:nil];
                    }];
                }else{
                    [FavGeneralDAO genericParser:data onCompletion:^(BOOL status, NSError *error, BOOL refresh) {
                        if ([delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)])
                            [delegate parserResponseForClass:NSClassFromString(entity) status:YES andError:nil andRefresh:NO];
                    }];
                }
            }else {
                DLog(@"Request error:%@",error);
                if ([delegate respondsToSelector:@selector(createShotResponseWithStatus:andError:)])
                    [delegate createShotResponseWithStatus:NO andError:error];
            }
        }];
    }else
        DLog(@"No valid req structure created");
}

//------------------------------------------------------------------------------
- (void)createEntity:(NSString *)entity withData:(NSArray *)dictArray andDelegate:(id)delegate withOperation:(NSString *)operation{
    
    //Create Alias block
    NSString *alias = [NSString stringWithFormat:@"CREATE_%@",entity.uppercaseString];
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create 'metadata' block
    NSDictionary *metadata = @{K_WS_OPS_OPERATION:operation,K_WS_OPS_FILTER:[FilterCreation getFilterForEntity:NSClassFromString(entity)], K_WS_OPS_ENTITY:entity,K_WS_OPS_ITEMS:[NSNull null]};
    
    //Create 'ops' block
    NSDictionary *opsDictionary = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:dictArray};
    
    //Join all 'ops' blocks
    NSArray *ops = @[opsDictionary];
    
    //Create full data structure
    if (req && ops.count > 0) {
        
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error){
                
                if ([entity isEqualToString:K_COREDATA_SHOT]) {
                    [FavGeneralDAO shotParser:data onCompletion:^(BOOL status, NSError *error, BOOL refresh) {
                        if ([delegate respondsToSelector:@selector(createShotResponseWithStatus:andError:)])
                            [delegate createShotResponseWithStatus:YES andError:nil];
                    }];
                }else{
                    [FavGeneralDAO genericParser:data onCompletion:^(BOOL status, NSError *error, BOOL refresh) {
                        if ([delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)])
                            [delegate parserResponseForClass:NSClassFromString(entity) status:YES andError:nil andRefresh:nil];
                    }];
                }
            }else {
                DLog(@"Request error:%@",error);
                if ([delegate respondsToSelector:@selector(createShotResponseWithStatus:andError:)])
                    [delegate createShotResponseWithStatus:NO andError:error];
            }
        }];
    }else
        DLog(@"No valid req structure created");
}



#pragma mark - DELETE
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
                [FavGeneralDAO genericParser:responseData onCompletion:^(BOOL status, NSError *error, BOOL refresh) {
                    
                    NSLog(@"%@ BORRADA", entity);
                }];
            else {
                DLog(@"Request error:%@",error);
            }
        }];
    }else
        DLog(@"No valid req structure created");
}

#pragma mark - LOGIN
//------------------------------------------------------------------------------
- (void)userLoginWithKey:(NSDictionary *)key withDelegate:(id)delegate {
    
    //Create Alias block
    NSString *alias = kALIAS_LOGIN;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = [[FavRestConsumerHelper singleton] createREQ];
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [[FavRestConsumerHelper singleton] createMetadataForOperation:K_OP_RETREAVE
                                                                     andEntity:kJSON_LOGIN
                                                                     withItems:@1
                                                                    withOffSet:@0
                                                                        andKey:key];
    
    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForLogin]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Check if delegate has protocol "ParserProtocol" implemented
    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(parserResponseForClass:status:andError:andRefresh:)];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error && delegateRespondsToProtocol)
                [FavGeneralDAO genericParser:data onCompletion:^(BOOL status,NSError *error, BOOL refresh){
                    
                    if (!error && status)
                        [delegate parserResponseFromLoginWithStatus:YES andError:nil];
                    else
                        [delegate parserResponseFromLoginWithStatus:NO andError:error];
                }];
            else if (delegateRespondsToProtocol){
                [delegate parserResponseFromLoginWithStatus:NO andError:error];
                DLog(@"Request error:%@",error);
            }
        }];
    }else if (delegateRespondsToProtocol){
        
        NSError *reqError = [NSError errorWithDomain:@"Request error" code:1 userInfo:operation];
        [delegate parserResponseFromLoginWithStatus:NO andError:reqError];
        DLog(@"No valid req structure created");
    }
}

#pragma mark - SEARCH
//------------------------------------------------------------------------------
- (void)searchPeopleWithName:(NSString *)textToSearch withOffset:(NSNumber *)offset withDelegate:(id)delegate {
    
    //Create Alias block
    NSString *alias = kALIAS_FIND_FRIENDS;
    
    //Create Staus block
    NSDictionary *status = @{K_WS_STATUS_CODE: [NSNull null],K_WS_STATUS_MESSAGE:[NSNull null]};
    
    //Create 'req' block
    NSArray *req = self.appDelegate.request;
    
    //Create Provider 'metadata' block
    NSDictionary *metadata = [[FavRestConsumerHelper singleton] createMetadataForSearchPeopleWithItems:@25 withOffSet:offset andFilter:[FilterCreation getFilterForPeopleSearch:textToSearch]];

    //Create playerProvider 'ops' block
    NSDictionary *operation = @{K_WS_OPS_METADATA:metadata,K_WS_OPS_DATA:@[[FavEntityDescriptor createPropertyListForEntity:[User class]]]};
    
    //Create 'ops' block
    NSArray *ops = @[operation];
    
    //Check if delegate has protocol "ParserProtocol" implemented
    BOOL delegateRespondsToProtocol = [delegate respondsToSelector:@selector(searchResponseWithStatus:andError:andUsers:needToPaginate:)];
    
    //Create full data structure
    if (req && ops) {
        NSDictionary *serverCall = @{K_WS_ALIAS:alias,K_WS_STATUS:status,K_WS_REQ: req,K_WS_OPS:ops};
        [self fetchDataWithParameters:serverCall onCompletion:^(NSDictionary *data,NSError *error) {
            
            if (!error && delegateRespondsToProtocol)
                [FavGeneralDAO searchParser:data onCompletion:^(BOOL status, NSError *error, NSArray *data,int needToPaginate) {
                    
                    if (!error && status && data.count > 0)
                        [delegate searchResponseWithStatus:YES andError:nil andUsers:data needToPaginate:needToPaginate];
                    else
                        [delegate searchResponseWithStatus:YES andError:error andUsers:nil needToPaginate:needToPaginate];
                }];
            else if (delegateRespondsToProtocol){
                [delegate searchResponseWithStatus:NO andError:error andUsers:nil needToPaginate:nil];
                DLog(@"Request error:%@",error);
            }
        }];
    }else if (delegateRespondsToProtocol){
        
        NSError *reqError = [NSError errorWithDomain:@"Request error" code:1 userInfo:operation];
        [delegate searchResponseWithStatus:NO andError:reqError andUsers:nil needToPaginate:nil];
        DLog(@"No valid req structure created");
    }
    
}

@end
