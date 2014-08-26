//
//  Fav24ExtendedNavigationController.m
//  Goles Messenger
//
//  Created by Delfín Pereiro on 03/09/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "Fav24ExtendedNavigationController.h"

@interface Fav24ExtendedNavigationController ()

@property (nonatomic, strong) UINavigationBar   *mPatch;
@property (nonatomic, strong) UIProgressView    *mProgress;
@property (nonatomic)         CGRect            mStardardProgressPos;
@property (nonatomic)         CGRect            mPatchedProgressPos;
@property (nonatomic)         long              mPreviousBatchSize;

-(void)hideProgressBar;
-(void)setProgress:(float)progress;

@end

@implementation Fav24ExtendedNavigationController
//------------------------------------------------------------------------------
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.

    // Add patch to navigation bar
    [self setMPatchHeight:15];
    [self setMPatch:[[UINavigationBar alloc] initWithFrame:CGRectMake(self.navigationBar.frame.origin.x,
                                                                      self.navigationBar.frame.size.height,
                                                                      self.navigationBar.frame.size.width,
                                                                      [self mPatchHeight])]];

   
    [[self navigationBar] addSubview:[self mPatch]];
    // Add progress bar to navigation bar
    [self setMStardardProgressPos:CGRectMake(self.navigationBar.frame.origin.x,
                                             self.navigationBar.frame.size.height -2.0f,
                                             self.navigationBar.frame.size.width, 0)];
    
    [self setMPatchedProgressPos:CGRectMake(self.navigationBar.frame.origin.x,
                                            self.navigationBar.frame.size.height +[self mPatchHeight] -2.0f,
                                            self.navigationBar.frame.size.width, 0)];
    
    [self setMProgress:[[UIProgressView alloc] initWithFrame:[self mStardardProgressPos]]];
    [self setMPreviousBatchSize:0];
    [[self navigationBar] addSubview:[self mProgress]];
    [[self mProgress] setHidden:YES];
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Public Methods
//------------------------------------------------------------------------------
//-(Fav24ExtendedNavigationController *)navigationController {
//    return (Fav24ExtendedNavigationController *)[super navigationController];
//}

//------------------------------------------------------------------------------
-(void)hidePatch:(BOOL)hide {
    if ( hide )     [[self mProgress] setFrame:[self mStardardProgressPos]];
    else            [[self mProgress] setFrame:[self mPatchedProgressPos]];

    [[self mPatch] setHidden:hide];
}

//------------------------------------------------------------------------------
-(void)setPatchView:(UIView *)view {
    [[self mPatch] addSubview:view];
}

//------------------------------------------------------------------------------
-(void)showProgressBar:(BOOL)show {
    if ( show ){
        [[self mProgress] setAlpha:1.0f];
        [[self mProgress] setHidden:NO];
    }
    else
        [self hideProgressBar];
}

//------------------------------------------------------------------------------
-(void)endProgressbar {
    [self setProgress:1.0f];
}

#pragma mark - Private Methods
//------------------------------------------------------------------------------
-(void)hideProgressBar {
    [UIView animateWithDuration:0.3 animations:^{
        [[self mProgress] setAlpha:0.0];
    } completion:^(BOOL finished) {
        [[self mProgress] setHidden:YES];
        [[self mProgress] setProgress:0.0f];
    }];
}

//------------------------------------------------------------------------------
// CACTUS: OLD ASIHHTP REQUEST DELEGATE
//------------------------------------------------------------------------------
- (void)setProgress:(float)progress
{
    [[self mProgress] setProgress:progress animated:YES];

    if ( progress >= 1.0f ){
        [self hideProgressBar];
    }
}

#pragma mark - Rest Consumer progress bar delegate method
//------------------------------------------------------------------------------
- (void)updateProgressBar:(long long)totalBytesRead totalBytesExpectedToRead:(long long)totalBytesExpectedToRead{
    
    float progress = totalBytesRead/(float)totalBytesExpectedToRead;
    [[self mProgress] setProgress:progress animated:YES];
//        DLog(@"Sent %lld of %lld bytes", totalBytesRead, totalBytesExpectedToRead);
//        DLog(@"Progress: %f", progress);
    
}

@end
