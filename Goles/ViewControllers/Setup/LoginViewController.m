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
@property (weak, nonatomic) IBOutlet UIImageView *imgFacebook;

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
        
        self.imgShootr.frame = CGRectMake(self.imgShootr.frame.origin.x+40, self.imgShootr.frame.origin.y+40,  170, 170);
        self.imgShootr.image = [UIImage imageNamed:@"Shootr_i6.png"];
        self.btnTitle.frame = CGRectMake(self.btnTitle.frame.origin.x+50, self.btnTitle.frame.origin.y+50, self.btnTitle.frame.size.width, self.btnTitle.frame.size.height+20);
        self.btnTitle.font = [UIFont fontWithName:@"HelveticaNeue-Thin" size:50.0f];
        self.btnFacebook.frame = CGRectMake(self.btnFacebook.frame.origin.x+53, self.btnFacebook.frame.origin.y+130, self.btnFacebook.frame.size.width, self.btnFacebook.frame.size.height);
        self.imgFacebook.frame = CGRectMake(self.imgFacebook.frame.origin.x+28, self.imgFacebook.frame.origin.y+130, 250, 50);
        self.imgFacebook.image = [UIImage imageNamed:@"Logo_f_i6.png"];
        self.btnRegister.frame = CGRectMake(self.btnRegister.frame.origin.x+50, self.btnRegister.frame.origin.y+150, self.btnRegister.frame.size.width, self.btnRegister.frame.size.height);
        self.btnRegister.titleLabel.font = [UIFont systemFontOfSize:17.0];
        self.btnSignIn.frame = CGRectMake(self.btnSignIn.frame.origin.x+50, self.btnSignIn.frame.origin.y+170, self.btnSignIn.frame.size.width, self.btnSignIn.frame.size.height);
        self.btnSignIn.titleLabel.font = [UIFont systemFontOfSize:17.0];
        
    }else if ([Utils getIphone:self.view.frame.size.height] == 6){
        
        self.imgShootr.frame = CGRectMake(self.imgShootr.frame.origin.x+20, self.imgShootr.frame.origin.y+30, 170, 170);
        self.imgShootr.image = [UIImage imageNamed:@"Shootr_i6.png"];
        self.btnTitle.frame = CGRectMake(self.btnTitle.frame.origin.x+30, self.btnTitle.frame.origin.y+30, self.btnTitle.frame.size.width, self.btnTitle.frame.size.height+20);
        self.btnTitle.font = [UIFont fontWithName:@"HelveticaNeue-Thin" size:50.0f];
        self.btnFacebook.frame = CGRectMake(self.btnFacebook.frame.origin.x+35, self.btnFacebook.frame.origin.y+95, self.btnFacebook.frame.size.width, self.btnFacebook.frame.size.height);
        self.imgFacebook.frame = CGRectMake(self.imgFacebook.frame.origin.x+12, self.imgFacebook.frame.origin.y+95, 250, 50);
        self.imgFacebook.image = [UIImage imageNamed:@"Logo_f_i6.png"];
        self.btnRegister.frame = CGRectMake(self.btnRegister.frame.origin.x+30, self.btnRegister.frame.origin.y+110, self.btnRegister.frame.size.width, self.btnRegister.frame.size.height);
        self.btnRegister.titleLabel.font = [UIFont systemFontOfSize:17.0];
        self.btnSignIn.frame = CGRectMake(self.btnSignIn.frame.origin.x+30, self.btnSignIn.frame.origin.y+120, self.btnSignIn.frame.size.width, self.btnSignIn.frame.size.height);
        self.btnSignIn.titleLabel.font = [UIFont systemFontOfSize:17.0];
        
    }else if ([Utils getIphone:self.view.frame.size.height] == 5){
        
        self.imgShootr.frame = CGRectMake(self.imgShootr.frame.origin.x, self.imgShootr.frame.origin.y+10, self.imgShootr.frame.size.width, self.imgShootr.frame.size.height);
        self.btnTitle.frame = CGRectMake(self.btnTitle.frame.origin.x, self.btnTitle.frame.origin.y-5, self.btnTitle.frame.size.width, self.btnTitle.frame.size.height);
        
        self.btnFacebook.frame = CGRectMake(self.btnFacebook.frame.origin.x+2, self.btnFacebook.frame.origin.y+50, self.btnFacebook.frame.size.width, 45);
        self.btnFacebook.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16.0f];
        self.imgFacebook.frame = CGRectMake(self.imgFacebook.frame.origin.x-5, self.imgFacebook.frame.origin.y+50, 225, 45);
        self.imgFacebook.image = [UIImage imageNamed:@"Logo_f_i5.png"];
        
        self.btnRegister.frame = CGRectMake(self.btnRegister.frame.origin.x, self.btnRegister.frame.origin.y+55, self.btnRegister.frame.size.width, self.btnRegister.frame.size.height);
        self.btnSignIn.frame = CGRectMake(self.btnSignIn.frame.origin.x, self.btnSignIn.frame.origin.y+65, self.btnSignIn.frame.size.width, self.btnSignIn.frame.size.height);
        
    }else if ([Utils getIphone:self.view.frame.size.height] == 4){
       
        self.btnTitle.frame = CGRectMake(self.btnTitle.frame.origin.x, self.btnTitle.frame.origin.y-5, self.btnTitle.frame.size.width, self.btnTitle.frame.size.height);

        
        self.btnFacebook.titleLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16.0f];
        self.btnFacebook.frame = CGRectMake(self.btnFacebook.frame.origin.x+10, self.btnFacebook.frame.origin.y, self.btnFacebook.frame.size.width, 45);

        self.imgFacebook.frame = CGRectMake(self.imgFacebook.frame.origin.x, self.imgFacebook.frame.origin.y, 225, 45);
        self.imgFacebook.image = [UIImage imageNamed:@"Logo_f_i5.png"];
        
        
        self.btnSignIn.frame = CGRectMake(self.btnSignIn.frame.origin.x, self.btnSignIn.frame.origin.y, self.btnSignIn.frame.size.width, self.btnSignIn.frame.size.height);
        self.btnRegister.frame = CGRectMake(self.btnRegister.frame.origin.x, self.btnRegister.frame.origin.y, self.btnRegister.frame.size.width, self.btnRegister.frame.size.height);

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
