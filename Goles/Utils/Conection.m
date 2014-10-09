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
//Conection singleton instance shared across application
+ (Conection *)sharedInstance
{
    static Conection *sharedConection = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedConection = [[Conection alloc] init];
    });
    return sharedConection;
}

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

//    if (![delegate isKindOfClass:[TimeLineViewController class]]) {
//        if (error.code < 400) {
//            dispatch_async(dispatch_get_main_queue(), ^{
//                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"The network connection was lost",nil) message:nil delegate:self cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil];
//                [alert show];
//            });
//        }
//        else {
//            dispatch_async(dispatch_get_main_queue(), ^{
//                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"We are working to fix it as soon as possible", nil) message:nil delegate:self cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil];
//                [alert show];
//            });
//        }
//
//    }else{
//        
//    }
 
    [delegate conectionResponseForStatus:NO andRefresh:NO withShot:NO];
}

@end
