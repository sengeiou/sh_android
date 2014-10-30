//
//  LandingViewController.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 03/07/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "LandingViewController.h"
#import "FavRestConsumer.h"
#import "AppDelegate.h"
#import "Services.pch"
#import "LoginViewController.h"
#import "UserManager.h"

#define kNUMBER_OF_RETRIES  3

@interface LandingViewController ()

@property (nonatomic) int   retryCounter;
@property (nonatomic,strong) IBOutlet UIActivityIndicatorView *activity;
@property (nonatomic,strong) NSDate *requestDate;
@property (nonatomic) int timeToCheck;
@property (nonatomic,strong) IBOutlet UIImageView *backImage;

@end

@implementation LandingViewController

//------------------------------------------------------------------------------
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        UIImage *background;
        CGFloat screenHeight = [UIScreen mainScreen].bounds.size.height;
        if ([UIScreen mainScreen].scale == 2.f && screenHeight == 568.0f) {
            background = [UIImage imageNamed:@"Default-568@2x.png"];
        } else {
            background = [UIImage imageNamed:@"DefaultSmall@2x.png"];
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            self.backImage.frame = [UIScreen mainScreen].bounds;
            self.backImage.image = background;
        });
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    
    [super viewDidLoad];

    self.activity.hidden = YES;
   
    [self getServerTime:nil];
    
    //cuando terminemos de obtener los friends de facebook pasamos dentro de la app
    /*[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(getServerTime:) name:@"enterOfApp" object:nil];

    if ([UserManager sharedInstance].mUser == nil)
        [[UserManager singleton] retrieveUserFromPreviousPlayerId];
    
    if ([UserManager sharedInstance].mUser.sessionFacebook) {
        [self getServerTime:nil];
    }else{
        //SHOW LOGIN
        [self showLogin];
    }*/
}

//------------------------------------------------------------------------------
-(void)showLogin {
    //Show second splashscreen to control internet connection
    LoginViewController *loginVC = [[LoginViewController alloc] initWithNibName:@"LoginViewController" bundle:nil];
    
    UIWindow *topWindow = [[UIApplication sharedApplication].windows firstObject];
        
    topWindow.rootViewController = loginVC;
}

//------------------------------------------------------------------------------
- (void)getServerTime:(id)sender {
    
    self.timeToCheck += 1;
    NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];
    
    sessionConfiguration.timeoutIntervalForResource = self.timeToCheck;

    NSURLSession *session = [NSURLSession sessionWithConfiguration:sessionConfiguration];
    
    self.requestDate = [NSDate date];
    [[session dataTaskWithURL:[NSURL URLWithString:SERVERTIME]
            completionHandler:^(NSData *data,
                                NSURLResponse *response,
                                NSError *error) {
                if (!error)
                    [self conectionOkToContinue];
                else {
                    NSTimeInterval time1 = -[self.requestDate timeIntervalSinceNow];
                    if (time1 < self.timeToCheck)
                        [NSThread sleepForTimeInterval:((time1+0.5)-self.timeToCheck)*-1];
                    [self retryConnectionWithError:error];
                }
                
            }] resume];

}

//------------------------------------------------------------------------------
- (void)retryConnectionWithError:(NSError *)error {

    if (self.retryCounter <= kNUMBER_OF_RETRIES) {
        self.retryCounter +=1;
        dispatch_async(dispatch_get_main_queue(), ^{
            self.activity.hidden = NO;
            [self.activity startAnimating];
        });
        [self getServerTime:nil];
    }
    else {
        
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
    }
}

//------------------------------------------------------------------------------
- (void)conectionOkToContinue {
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.activity stopAnimating];
//        AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
//        appDelegate.window.rootViewController = appDelegate.tabBarController;
//        [appDelegate showWelcomeMessage];
    });
}

//------------------------------------------------------------------------------
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    dispatch_async(dispatch_get_main_queue(), ^{
        self.timeToCheck = 0;
        self.retryCounter = 0;
        [self retryConnectionWithError:nil];
    });
}

@end
