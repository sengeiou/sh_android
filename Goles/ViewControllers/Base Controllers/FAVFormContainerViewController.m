//
//  FAVFormContainerViewController.m
//  RefactorTest
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2013 fav24. All rights reserved.
//

#import "FAVFormContainerViewController.h"
#import "Constants.h"

@interface FAVFormContainerViewController ()

@end

@implementation FAVFormContainerViewController


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
    
    [self setMInsertionHeight:0];
}

//------------------------------------------------------------------------------
-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

//------------------------------------------------------------------------------
-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Public Methods
//------------------------------------------------------------------------------
-(void)AddFormOption:(UIViewController *)viewController toView:(UIView *)view{
    
    // Add controller to the pool of view controllers
    [self addChildViewController:viewController];

    // add the view at position
    CGRect frame = viewController.view.frame;
    frame.origin.y = [self mInsertionHeight];
    [[viewController view] setFrame:frame];
    [view addSubview:[viewController view]];
    
    // Update insertion point for the next option View controller
    self.mInsertionHeight += frame.size.height;
}

//------------------------------------------------------------------------------
-(void)RemoveFormOption:(UIViewController *)viewController fromView:(UIView *)view{

    [viewController removeFromParentViewController];
    [[viewController view] removeFromSuperview];
}

//------------------------------------------------------------------------------
-(void)UpdateContainerContentSize {

    CGSize originalSize = [[self mScrollView] contentSize];
    CGSize frameSize = self.mScrollView.frame.size;
    
    CGFloat newHeight = kHeightNavBarWithSegmentedControl-64;
    for (UIViewController *vc in [self childViewControllers] ){

        CGRect frame = [[vc view] frame];
        frame.origin.y = newHeight;
        [[vc view] setFrame:frame];
        newHeight += frame.size.height;
    }

    // To guarantee the horizontal scroll is working
    if ( frameSize.height > newHeight )
        newHeight = frameSize.height+1;
    
    originalSize.height = newHeight;
    [[self mScrollView] setContentSize:originalSize];
    
    // Update insertion point for the next option View controller
    self.mInsertionHeight = newHeight;
}


@end
