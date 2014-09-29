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
#import "Utils.h"

@interface LoginViewController ()
@property (weak, nonatomic) IBOutlet UIButton *btnSignIn;

@property (weak, nonatomic) IBOutlet UILabel *btnTitle;
@property (weak, nonatomic) IBOutlet UIButton *btnFacebook;
@property (weak, nonatomic) IBOutlet UIButton *btnRegister;
@property (weak, nonatomic) IBOutlet UIImageView *imgShootr;

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
   
    [self adjustFrame];
}

-(void)viewWillAppear:(BOOL)animated{
    self.navigationController.navigationBarHidden = YES;
}

-(void)adjustFrame{
    
    if ([Utils getIphone:self.view.frame.size.height] == 7) {
        
        self.imgShootr.frame = CGRectMake(self.imgShootr.frame.origin.x+50, self.imgShootr.frame.origin.y+50, self.imgShootr.frame.size.width, self.imgShootr.frame.size.height);
        self.btnTitle.frame = CGRectMake(self.btnTitle.frame.origin.x+50, self.btnTitle.frame.origin.y+50, self.btnTitle.frame.size.width, self.btnTitle.frame.size.height);
        self.btnFacebook.frame = CGRectMake(self.btnFacebook.frame.origin.x+50, self.btnFacebook.frame.origin.y+50, self.btnFacebook.frame.size.width, self.btnFacebook.frame.size.height);
        self.btnRegister.frame = CGRectMake(self.btnRegister.frame.origin.x+50, self.btnRegister.frame.origin.y+100, self.btnRegister.frame.size.width, self.btnRegister.frame.size.height);
        self.btnRegister.titleLabel.font = [UIFont systemFontOfSize:17.0];
        self.btnSignIn.frame = CGRectMake(self.btnSignIn.frame.origin.x+50, self.btnSignIn.frame.origin.y+100, self.btnSignIn.frame.size.width, self.btnSignIn.frame.size.height);
        self.btnSignIn.titleLabel.font = [UIFont systemFontOfSize:17.0];
        
        
    }else if ([Utils getIphone:self.view.frame.size.height] == 6){
        
        self.imgShootr.frame = CGRectMake(self.imgShootr.frame.origin.x+30, self.imgShootr.frame.origin.y+30, self.imgShootr.frame.size.width, self.imgShootr.frame.size.height);
        self.btnTitle.frame = CGRectMake(self.btnTitle.frame.origin.x+30, self.btnTitle.frame.origin.y+30, self.btnTitle.frame.size.width, self.btnTitle.frame.size.height);
        self.btnFacebook.frame = CGRectMake(self.btnFacebook.frame.origin.x+30, self.btnFacebook.frame.origin.y+30, self.btnFacebook.frame.size.width, self.btnFacebook.frame.size.height);
        self.btnRegister.frame = CGRectMake(self.btnRegister.frame.origin.x+30, self.btnRegister.frame.origin.y+80, self.btnRegister.frame.size.width, self.btnRegister.frame.size.height);
        self.btnRegister.titleLabel.font = [UIFont systemFontOfSize:17.0];
        self.btnSignIn.frame = CGRectMake(self.btnSignIn.frame.origin.x+30, self.btnSignIn.frame.origin.y+80, self.btnSignIn.frame.size.width, self.btnSignIn.frame.size.height);
        self.btnSignIn.titleLabel.font = [UIFont systemFontOfSize:17.0];
        
    }else if ([Utils getIphone:self.view.frame.size.height] == 5){
        
        self.imgShootr.frame = CGRectMake(self.imgShootr.frame.origin.x, self.imgShootr.frame.origin.y+10, self.imgShootr.frame.size.width, self.imgShootr.frame.size.height);
        self.btnTitle.frame = CGRectMake(self.btnTitle.frame.origin.x, self.btnTitle.frame.origin.y+10, self.btnTitle.frame.size.width, self.btnTitle.frame.size.height);
        self.btnFacebook.frame = CGRectMake(self.btnFacebook.frame.origin.x, self.btnFacebook.frame.origin.y+10, self.btnFacebook.frame.size.width, self.btnFacebook.frame.size.height);
        self.btnRegister.frame = CGRectMake(self.btnRegister.frame.origin.x, self.btnRegister.frame.origin.y+50, self.btnRegister.frame.size.width, self.btnRegister.frame.size.height);
        self.btnSignIn.frame = CGRectMake(self.btnSignIn.frame.origin.x, self.btnSignIn.frame.origin.y+50, self.btnSignIn.frame.size.width, self.btnSignIn.frame.size.height);
        
    }
}

-(BOOL)prefersStatusBarHidden {
    return YES;
}
- (void)didReceiveMemoryWarning {
    
    [super didReceiveMemoryWarning];
}

- (IBAction)signInFacebook:(id)sender {
}

- (IBAction)registerEmail:(id)sender {
}
@end
