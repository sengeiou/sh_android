//
//  LoginViewController.m
//  Goles Messenger
//
//  Created by Maria Teresa Bañuls on 01/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "LoginViewController.h"
#import "UserManager.h"
#import "CoreDataManager.h"

@interface LoginViewController ()


@end

@implementation LoginViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    
    [super viewDidLoad];
 
    self.navigationController.navigationBarHidden = YES;
}

- (void)didReceiveMemoryWarning {
    
    [super didReceiveMemoryWarning];
}

@end
