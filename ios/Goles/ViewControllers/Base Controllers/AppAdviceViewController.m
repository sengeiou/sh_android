//
//  AdviceFriendsViewController.m
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "AppAdviceViewController.h"
#import "CoreDataParsing.h"
#import "Fav24Colors.h"


@interface AppAdviceViewController ()

@property (weak, nonatomic) IBOutlet UIWebView *webView;
@property (weak, nonatomic) IBOutlet UIScrollView *scroll;
@property (weak, nonatomic) IBOutlet UIButton *button;

@end

@implementation AppAdviceViewController

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {

    }
    return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    
    [super viewDidLoad];
    NSString *messageText = self.message.message;
    [self.webView loadHTMLString:messageText baseURL:nil];
//    [self.scroll setScrollEnabled:YES];
//    [self.scroll setContentSize:CGSizeMake(320, 600)];
    [self createButton];
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated {
    
    [super viewWillAppear:animated];
}

-(void)viewDidLayoutSubviews
{
    [self.scroll setContentSize:CGSizeMake(320, 600)];
}

#pragma mark - Private methods
//------------------------------------------------------------------------------
- (void)createButton {
    
    [self.button setTitle:self.buttonText forState:UIControlStateNormal];
    [self.button addTarget:self action:@selector(closeModal:) forControlEvents:UIControlEventTouchUpInside];
    self.button.hidden = !self.advice.visibleButtonValue;

}

//------------------------------------------------------------------------------
- (void)closeModal:(id)sender{

    if (!([self.advice.buttonAction rangeOfString:@"continue"].location == NSNotFound)) {
        if (!([self.advice.buttonAction rangeOfString:@"url"].location == NSNotFound) && self.advice.buttonData.length > 0) {
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:self.advice.buttonData]];
            [self dismissViewControllerAnimated:YES completion:nil];
        }else
            [self dismissViewControllerAnimated:YES completion:nil];
    }else if (!([self.advice.buttonAction rangeOfString:@"url"].location == NSNotFound) && self.advice.buttonData.length > 0)
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:self.advice.buttonData]];

}


@end
