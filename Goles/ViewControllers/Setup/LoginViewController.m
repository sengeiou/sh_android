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
@property (weak, nonatomic) IBOutlet UIButton *btnSignIn;

@property (weak, nonatomic) IBOutlet UILabel *btnTitle;
@property (weak, nonatomic) IBOutlet UIButton *btnFacebook;
@property (weak, nonatomic) IBOutlet UIButton *btnRegister;

- (IBAction)signInFacebook:(id)sender;
- (IBAction)registerEmail:(id)sender;

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
}

-(void)viewDidAppear:(BOOL)animated{
    self.navigationController.navigationBarHidden = YES;

}

- (void)didReceiveMemoryWarning {
    
    [super didReceiveMemoryWarning];
}

- (IBAction)signInFacebook:(id)sender {
}

- (IBAction)registerEmail:(id)sender {
}
@end
