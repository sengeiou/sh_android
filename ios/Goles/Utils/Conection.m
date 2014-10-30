//
//  Conection.m
//
//  Created by Maria Teresa Bañuls on 16/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "Conection.h"
#import "Services.pch"
#import "TimeLineViewController.h"

@implementation Conection

#define kNUMBER_OF_RETRIES  1
//------------------------------------------------------------------------------
+ (Conection *)sharedInstance {
    
    static Conection *sharedConection = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedConection = [[Conection alloc] init];
    });
    return sharedConection;
}

//------------------------------------------------------------------------------
- (void)getServerTimewithDelegate:(id)delegate andRefresh:(BOOL) refresh withShot:(BOOL)isShot{
    
    NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];
    
    sessionConfiguration.timeoutIntervalForResource = 10;
    
    NSURLSession *session = [NSURLSession sessionWithConfiguration:sessionConfiguration];
    
    self.requestDate = [NSDate date];
    [[session dataTaskWithURL:[NSURL URLWithString:SERVERTIME]
            completionHandler:^(NSData *data,
                                NSURLResponse *response,
                                NSError *error) {
                if (!error){
                    self.isConection = YES;
                    [delegate conectionResponseForStatus:YES andRefresh:refresh withShot:isShot];
                } else {
                    self.isConection = NO;
                    [self retryConnectionWithError:error andDelegate:delegate];
                }
                
            }] resume];
    
}

//------------------------------------------------------------------------------
- (void)retryConnectionWithError:(NSError *)error andDelegate:(id)delegate{

    [delegate conectionResponseForStatus:NO andRefresh:NO withShot:NO];
}

@end
