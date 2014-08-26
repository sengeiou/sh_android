//
//  LandingPageViewController.m
//  Goles Messenger
//
//  Created by Luis Rollon on 03/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "LandingPageViewController.h"
#import "BetWebViewController.h"
#import "CoreDataParsing.h"
#import "Flurry.h"
#import "Services.pch"
#import "Fav24Colors.h"

@interface LandingPageViewController ()

//White line to cover navigationBar bottom line
@property (nonatomic, strong) UIView *whiteLine;
@property (weak, nonatomic) IBOutlet UIScrollView *scroll;

@end

@implementation LandingPageViewController


#pragma mark - View lifecycle

//------------------------------------------------------------------------------
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    NSString *textAlreadyRegisteredButton = [NSString stringWithFormat:@"Ya estoy registrado en %@", self.provider.name];
    self.mAlreadyRegisteredButton.titleLabel.text = textAlreadyRegisteredButton;
    self.mAlreadyRegisteredButton.titleLabel.textAlignment = NSTextAlignmentCenter;

    [self.navigationController.navigationBar setBackgroundImage:nil forBarMetrics:UIBarMetricsDefault];
    
    [self setHidesBottomBarWhenPushed:YES];
    
    self.navigationController.navigationBarHidden = NO;
    self.mProviderName.text = self.provider.name;
    self.mOfferDescription.text = self.provider.comment;
    
}


//------------------------------------------------------------------------------
/**
 @brief We hide the navigationBar botton line adding a whiteLine
 */
//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    //Line to make scrolleable the view
    self.scroll.contentSize = CGSizeMake(360.0, 505.0);
    
    [self.navigationController.navigationBar setBarTintColor:[UIColor whiteColor]];

    self.whiteLine = [[UIView alloc] initWithFrame:CGRectMake(0, 44, 320, 1)];
    [self.whiteLine setBackgroundColor:[UIColor whiteColor]];
    [self.navigationController.navigationBar addSubview:self.whiteLine];

    if(self.mComesFromBack){
        [self dismissViewControllerAnimated:NO completion:nil];
        self.mComesFromBack = NO;
    }

    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"_back", nil)
                                                                   style:UIBarButtonItemStyleBordered
                                                                  target:nil
                                                                  action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    self.mAlreadyRegisteredButton.titleLabel.textColor = [Fav24Colors iosSevenBlue];
    self.mDoRegisterNow.titleLabel.textColor = [Fav24Colors iosSevenBlue];
}

- (void)viewWillDisappear:(BOOL)animated{
    [self.navigationController.navigationBar setBarTintColor:nil];
    [self.whiteLine removeFromSuperview];
}


#pragma mark - Private methods

//------------------------------------------------------------------------------
- (IBAction)alreadyRegistered:(id)sender {

    [[self navigationController]setNavigationBarHidden:NO];

    BetWebViewController *betWebVC = [[BetWebViewController alloc] initWithProvider:self.provider andURL: self.oddURL andGoToRegistry:NO];

    
    [betWebVC setMProvider:self.provider];
    
    [self userTapOnButton];
    
    //Flurry Log
    [Flurry logEvent:K_CUOTAS_Landing_AlreadyRegisteredClick];

    [[self navigationController] pushViewController:betWebVC animated:YES];

}


- (IBAction)registerNow:(id)sender {
    
    [[self navigationController]setNavigationBarHidden:NO];
    
    BetWebViewController *betWebVC = [[BetWebViewController alloc] initWithProvider:self.provider andURL:[NSURL URLWithString:self.provider.registryURL] andGoToRegistry:YES];

    [betWebVC setMProvider:self.provider];
    [[self navigationController] pushViewController:betWebVC animated:YES];
}


//------------------------------------------------------------------------------
-(void)userTapOnButton{
    NSUserDefaults *localStorage = [NSUserDefaults standardUserDefaults];
    [localStorage setBool:YES forKey:kCUOTAS_USERCLICK];
}

@end
