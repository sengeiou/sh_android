//
//  Conection.m
//  Goles
//
//  Created by Maria Teresa Bañuls on 16/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "Conection.h"
#import "Services.pch"

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

- (void)getServerTime {
    
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
                    [self conectionOkToContinue];
               
                } else {
                    self.isConection = NO;
                    
                    /*NSTimeInterval time1 = -[self.requestDate timeIntervalSinceNow];
                    if (time1 < self.timeToCheck)
                        [NSThread sleepForTimeInterval:((time1+0.5)-self.timeToCheck)*-1];*/
                    [self retryConnectionWithError:error];
                }
                
            }] resume];
    
}

//------------------------------------------------------------------------------
- (void)retryConnectionWithError:(NSError *)error {
    
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
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"No se puede acceder a Goles" message:@"Revisa tu conexión a Internet." delegate:self cancelButtonTitle:nil otherButtonTitles:@"Reintentar", nil];
                [alert show];
            });
        }
        else {
            dispatch_async(dispatch_get_main_queue(), ^{
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"No se puede acceder a Goles" message:@"Estamos trabajando para solucionarlo lo antes posible." delegate:self cancelButtonTitle:nil otherButtonTitles:@"Reintentar", nil];
                [alert show];
            });
        }
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
