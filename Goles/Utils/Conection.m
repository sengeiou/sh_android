//
//  Conection.m
//
//  Created by Maria Teresa Bañuls on 16/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "Conection.h"
#import "Services.pch"
#import "Reachability.h"


@interface Conection ()

@property (nonatomic) Reachability *internetReachability;

@end



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

-(void)getAirplaneMode{
    self.internetReachability = [Reachability reachabilityForInternetConnection];
    [self.internetReachability startNotifier];
    [self updateInterfaceWithReachability:self.internetReachability];

}
- (void)updateInterfaceWithReachability:(Reachability *)reachability
{
    
     if ([[Reachability reachabilityForLocalWiFi] currentReachabilityStatus] == ReachableViaWiFi) {
      
         NSLog(@"WIFI");
     }else
   
    if (reachability == self.internetReachability)
    {
        NSLog(@"%ld", [reachability currentReachabilityStatus]);
         BOOL connection = [reachability connectionRequired];
        
        NSLog(@"%@", connection);
        
    }
}

- (void)getServerTimewithDelegate:(id)delegate andRefresh:(BOOL) refresh withShot:(BOOL)isShot{
    
    [self getAirplaneMode];
    
    self.timeToCheck += 1;
    NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];
    
    //sessionConfiguration.timeoutIntervalForResource = self.timeToCheck;
    
    NSURLSession *session = [NSURLSession sessionWithConfiguration:sessionConfiguration];
    
    self.requestDate = [NSDate date];
    [[session dataTaskWithURL:[NSURL URLWithString:SERVERTIME]
            completionHandler:^(NSData *data,
                                NSURLResponse *response,
                                NSError *error) {
                if (!error){
                    
                    self.isConection = YES;
                    [delegate conectionResponseForStatus:YES andRefresh:refresh withShot:isShot];
                    
                    [self conectionOkToContinue];
               
                } else {
                    self.isConection = NO;
                    
                    /*NSTimeInterval time1 = -[self.requestDate timeIntervalSinceNow];
                    if (time1 < self.timeToCheck)
                        [NSThread sleepForTimeInterval:((time1+0.5)-self.timeToCheck)*-1];*/
                    [self retryConnectionWithError:error andDelegate:delegate];
                }
                
            }] resume];
    
}

//------------------------------------------------------------------------------
- (void)retryConnectionWithError:(NSError *)error andDelegate:(id)delegate{
    
   /* if (self.retryCounter <= kNUMBER_OF_RETRIES) {
        self.retryCounter +=1;
//        dispatch_async(dispatch_get_main_queue(), ^{
//            self.activity.hidden = NO;
//            [self.activity startAnimating];
//        });
        [self getServerTime];
    }
    else {*/
        
        if (error.code < 400) {
            dispatch_async(dispatch_get_main_queue(), ^{
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"The network connection was lost" message:nil delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
                [alert show];
            });
        }
        else {
            dispatch_async(dispatch_get_main_queue(), ^{
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"We are working to fix it as soon as possible" message:nil delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
                [alert show];
            });
        }
    
    [delegate conectionResponseForStatus:NO andRefresh:NO withShot:NO];
    
    //}
}

//------------------------------------------------------------------------------
- (void)conectionOkToContinue {
    
    dispatch_async(dispatch_get_main_queue(), ^{
        /*[self.activity stopAnimating];
        iGolesAppDelegate *appDelegate = (iGolesAppDelegate *)[[UIApplication sharedApplication] delegate];
        appDelegate.window.rootViewController = appDelegate.tabBarController;
        [appDelegate showWelcomeMessage];*/
    });
}

//------------------------------------------------------------------------------
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    /*dispatch_async(dispatch_get_main_queue(), ^{
        self.timeToCheck = 0;
        self.retryCounter = 0;
        [self retryConnectionWithError:nil];
    });*/
}

@end
