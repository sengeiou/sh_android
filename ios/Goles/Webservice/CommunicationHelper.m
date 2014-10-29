//
//  CommunicationHelper.m
//  Shootr
//
//  Created by Christian Cabarrocas on 28/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "CommunicationHelper.h"
#import "Services.pch"
#import "AppDelegate.h"
#import "AFNetworkActivityIndicatorManager.h"

@interface CommunicationHelper()

@property (nonatomic) BOOL       serviceActive;
@property (nonatomic, strong) AppDelegate *appDelegate;

@end


@implementation CommunicationHelper


+ (CommunicationHelper *)singleton
{
    static CommunicationHelper *sharedCommunicationHelper = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedCommunicationHelper = [[CommunicationHelper alloc] init];
        // Network activity indicator manager setup
        [[AFNetworkActivityIndicatorManager sharedManager] setEnabled:YES];
        
        // Session configuration setup
        NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];
        
        [sessionConfiguration setTimeoutIntervalForRequest:12.0];
        [sessionConfiguration setTimeoutIntervalForResource:8.0];
        
        // Initialize the Consumer
        sharedCommunicationHelper = [[CommunicationHelper alloc] initWithSessionConfiguration:sessionConfiguration];
        

    });
    return sharedCommunicationHelper;
}

//------------------------------------------------------------------------------
- (id)init {
    self = [super init];
    if (self != nil) {
        
    }
    return self;
}

//------------------------------------------------------------------------------
- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

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

-(void)postRequest:(NSDictionary *)data onCompletion:(void (^) (NSDictionary *response, NSError *error))handler{
    
    [self fetchDataWithParameters:data onCompletion:^(NSDictionary *data,NSError *error) {
        handler(data, error);
    }];
}

@end
