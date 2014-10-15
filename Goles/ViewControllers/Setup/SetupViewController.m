//
//  SetupViewController.m
//
//  Created by Maria Teresa Bañuls on 15/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "SetupViewController.h"
#import "AppDelegate.h"
#import "Conection.h"
#import "FavRestConsumer.h"
#import "FavRestConsumerHelper.h"
#import "CoreDataParsing.h"
#import "SyncManager.h"
#import "Encryption.h"
#import "User.h"
#import "Follow.h"
#import "Shot.h"
#import "Utils.h"
#import "Constants.h"
#import "Fav24Colors.h"

@interface SetupViewController ()<UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate, ConectionProtocol>{
    
     UITextField *txtFieldName;
     UITextField *txtFieldPwd;
    
    NSInteger lengthName;
    NSInteger lengthPwd;
}

@property (weak, nonatomic) IBOutlet UITableView *mtableView;
@property (weak, nonatomic) IBOutlet UIButton *btnForgotPwd;
@property (weak, nonatomic) IBOutlet UIButton *btnSignIn;
@property (weak, nonatomic) IBOutlet UILabel *lblNote;
@property (weak, nonatomic) IBOutlet UIButton *btnCreateCount;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *btnEnter;
@property (weak, nonatomic) IBOutlet UIScrollView *mScrollView;

- (IBAction)passEnter:(id)sender;

@end

@implementation SetupViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self adjustFrame];
    
    // Do any additional setup after loading the view.
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(dismissKeyboard)];
    
    [self.view addGestureRecognizer:tap];
  
    [self addTextFields];
    
    [self textLocalizable];
}

-(void)adjustFrame{
   
    self.mtableView.frame = CGRectMake(self.mtableView.frame.origin.x, 0, self.view.frame.size.width, self.mtableView.frame.size.height);
    
    if ([Utils getIphone:self.view.frame.size.height] == 7)
        self.lblNote.frame = CGRectMake(self.lblNote.frame.origin.x, self.lblNote.frame.origin.y-8, self.lblNote.frame.size.width, self.lblNote.frame.size.height);

    else if ([Utils getIphone:self.view.frame.size.height] == 6){
        
        self.lblNote.frame = CGRectMake(self.lblNote.frame.origin.x-6, self.lblNote.frame.origin.y-5, self.lblNote.frame.size.width, self.lblNote.frame.size.height);
        self.btnCreateCount.frame = CGRectMake(self.btnCreateCount.frame.origin.x-6, self.btnCreateCount.frame.origin.y, self.btnCreateCount.frame.size.width, self.btnCreateCount.frame.size.height);
        self.btnSignIn.frame = CGRectMake(self.btnSignIn.frame.origin.x-6, self.btnSignIn.frame.origin.y, self.btnSignIn.frame.size.width, self.btnSignIn.frame.size.height);
        self.btnForgotPwd.frame = CGRectMake(self.btnForgotPwd.frame.origin.x-7, self.btnForgotPwd.frame.origin.y, self.btnForgotPwd.frame.size.width, self.btnSignIn.frame.size.height);

    }else if ([Utils getIphone:self.view.frame.size.height] == 5){
        self.lblNote.frame = CGRectMake(self.lblNote.frame.origin.x-7, self.lblNote.frame.origin.y, self.lblNote.frame.size.width, self.lblNote.frame.size.height);
        self.btnCreateCount.frame = CGRectMake(self.btnCreateCount.frame.origin.x-7, self.btnCreateCount.frame.origin.y, self.btnCreateCount.frame.size.width, self.btnCreateCount.frame.size.height);
        self.btnSignIn.frame = CGRectMake(self.btnSignIn.frame.origin.x-7, self.btnSignIn.frame.origin.y, self.btnSignIn.frame.size.width, self.btnSignIn.frame.size.height);
        self.btnForgotPwd.frame = CGRectMake(self.btnForgotPwd.frame.origin.x-7, self.btnForgotPwd.frame.origin.y, self.btnForgotPwd.frame.size.width, self.btnSignIn.frame.size.height);
    }
    else if ([Utils getIphone:self.view.frame.size.height] == 4){
       
        self.lblNote.frame = CGRectMake(self.lblNote.frame.origin.x-6, self.lblNote.frame.origin.y-20, self.lblNote.frame.size.width-30, self.lblNote.frame.size.height+40);
        self.lblNote.numberOfLines = 2;
        
        self.btnCreateCount.frame = CGRectMake(self.btnCreateCount.frame.origin.x-6, self.btnCreateCount.frame.origin.y, self.btnCreateCount.frame.size.width, self.btnCreateCount.frame.size.height);
        self.btnSignIn.frame = CGRectMake(self.btnSignIn.frame.origin.x-6, self.btnSignIn.frame.origin.y, self.btnSignIn.frame.size.width, self.btnSignIn.frame.size.height);
        self.btnForgotPwd.frame = CGRectMake(self.btnForgotPwd.frame.origin.x-6, self.btnForgotPwd.frame.origin.y+10, self.btnForgotPwd.frame.size.width, self.btnSignIn.frame.size.height);

        self.mtableView.frame = CGRectMake(self.mtableView.frame.origin.x, 10, self.view.frame.size.width, self.mtableView.frame.size.height);
    }
    
    
    if ([Utils getIphone:self.view.frame.size.height] == 4)
        self.mScrollView.contentSize = CGSizeMake(self.view.frame.size.width, self.view.frame.size.height-65);
    else
        self.mScrollView.contentSize = CGSizeMake(self.view.frame.size.width, self.view.frame.size.height-100);

}


-(void)addTextFields{
    txtFieldName = [[UITextField alloc] initWithFrame:CGRectMake(180,3,self.view.frame.size.width-185,40)];
    txtFieldName.delegate = self;
    txtFieldName.clearButtonMode = YES;
    [txtFieldName setReturnKeyType:UIReturnKeyNext];
    txtFieldName.placeholder = NSLocalizedString(@"Required", nil);
    txtFieldName.autocapitalizationType = UITextAutocapitalizationTypeNone;

    txtFieldPwd = [[UITextField alloc] initWithFrame:CGRectMake(180,3,self.view.frame.size.width-185,40)];
    txtFieldPwd.delegate = self;
    txtFieldPwd.clearButtonMode = YES;
    [txtFieldPwd setReturnKeyType:UIReturnKeyGo];
    txtFieldPwd.placeholder = NSLocalizedString(@"Required", nil);
    txtFieldPwd.secureTextEntry = YES;
    txtFieldPwd.autocapitalizationType = UITextAutocapitalizationTypeNone;
}

#pragma mark - Localizable Strings
-(void)textLocalizable{
    
    [self.btnForgotPwd setTitle: NSLocalizedString(@"Forgot Password?", nil) forState:UIControlStateNormal];
    [self.btnCreateCount setTitle:NSLocalizedString(@"Create New Account", nil) forState:UIControlStateNormal];
    [self.btnSignIn setTitle:NSLocalizedString(@"Sign In with Facebook", nil) forState:UIControlStateNormal];
    [self.btnEnter setTitle:NSLocalizedString(@"Enter", nil)];
    self.lblNote.text = NSLocalizedString(@"Shootr will never post without your permission.", nil);
}

- (BOOL) shouldAutorotate {
    return NO;
}

-(void)viewWillAppear:(BOOL)animated{
   
    [super viewWillAppear:animated];

    [self modifyNavigationBar];
    
    if (self.view.frame.size.height == 480)
        self.mScrollView.contentInset = UIEdgeInsetsMake(-40.0, 0, 0, 0.0);
}

-(void)modifyNavigationBar{
    
    self.navigationItem.backBarButtonItem.title = NSLocalizedString(@"Back", nil);
    self.navigationController.navigationBarHidden = NO;
    [self.navigationController.navigationBar setBackgroundImage:[UIImage new]
                                                  forBarMetrics:UIBarMetricsDefault];
    self.navigationController.navigationBar.shadowImage = [UIImage new];
    self.navigationController.navigationBar.translucent = YES;
    [self disableButtonItem];
}

-(void)enableButtonItem{
    self.btnEnter.enabled = YES;
    
    [self.btnEnter setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
                                           [UIFont boldSystemFontOfSize:17.0], NSFontAttributeName,
                                           [Fav24Colors iosSevenBlue], NSForegroundColorAttributeName,
                                           nil]
                                 forState:UIControlStateNormal];

}
-(void)disableButtonItem{
    self.btnEnter.enabled = NO;

    [self.btnEnter setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
                                           [UIFont boldSystemFontOfSize:17.0], NSFontAttributeName,
                                           [Fav24Colors iosSevenGray], NSForegroundColorAttributeName,
                                           nil]
                                 forState:UIControlStateNormal];
}
-(void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    self.navigationController.navigationBarHidden = YES;
}

-(void)dismissKeyboard {
    [txtFieldName resignFirstResponder];

    [txtFieldPwd resignFirstResponder];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - Table view data source
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 2;
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return 44;
}

 - (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
 {
     UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cellSetup"  forIndexPath:indexPath];
     
     switch (indexPath.row) {
         case 0:
         {
            cell.textLabel.text = NSLocalizedString(@"Email or Username", nil);

             txtFieldName.frame = CGRectMake(177,3,self.view.frame.size.width-180,40);
            [cell addSubview:txtFieldName];
         }
             break;
         case 1:
         {
             cell.textLabel.text = NSLocalizedString(@"Password", nil);
             txtFieldPwd.frame = CGRectMake(177,3,self.view.frame.size.width-180,40);

             [cell addSubview:txtFieldPwd];
         }
            
            break;
         default:
             break;
     }
     
     return cell;
}

- (IBAction)passEnter:(id)sender {

    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:NO withShot:NO];
}

#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status andRefresh:(BOOL)refresh withShot:(BOOL)isShot{
    
    if (status && (lengthName > 2 && lengthPwd >  5)){
        NSString *result =  [[Encryption sharedInstance] getPassword:txtFieldPwd.text];
        
        if ([[Conection sharedInstance]isConection]) {
            NSDictionary *key;
           
            if ([Utils NSStringIsValidEmail:txtFieldName.text])
                key   = @{kJSON_EMAIL:txtFieldName.text , kJSON_PASSWORD:result};
            else
                key   = @{kJSON_USERNAME:txtFieldName.text, kJSON_PASSWORD:result};

            [[FavRestConsumer sharedInstance] userLoginWithKey:key withDelegate:self];
            
        };
    }else  if (status && (lengthName < 3 || lengthPwd <  6)){
        
        dispatch_async(dispatch_get_main_queue(), ^{
         
            [self showAlertBadText];
        });
    }else if (!status){
        dispatch_async(dispatch_get_main_queue(), ^{
            
            [self showAlertBadText];
        });
    }
}

-(void)showAlertBadText{
   
    UIAlertController * alert=   [UIAlertController
                                  alertControllerWithTitle:NSLocalizedString(@"Id or Password are not valid", nil)
                                  message:nil
                                  preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* ok = [UIAlertAction
                         actionWithTitle:NSLocalizedString(@"OK", nil)
                         style:UIAlertActionStyleDefault
                         handler:^(UIAlertAction * action)
                         {
                             [alert dismissViewControllerAnimated:YES completion:nil];
                             
                         }];
     [alert addAction:ok];
     [self presentViewController:alert animated:YES completion:nil];
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseFromLoginWithStatus:(BOOL)status andError:(NSError *)error {
    
    if (status) {
        AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
        appDelegate.request = [FavRestConsumerHelper createREQ];
        [appDelegate registerAPNS];
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Follow class] withDelegate:self];
        
    }
}

//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status){

        if (status && [entityClass isSubclassOfClass:[Follow class]]){
            [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[User class] withDelegate:self];
        }
        else if (status && [entityClass isSubclassOfClass:[User class]]){
            [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
        }
        else if (status && [entityClass isSubclassOfClass:[Shot class]]){
            //Turn on synchro process
            if (SYNCHRO_ACTIVATED)
                [[SyncManager singleton] startSyncProcess];
            AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
            [appDelegate setTabBarItems];
        }
        
    }else if(error){
        dispatch_async(dispatch_get_main_queue(), ^{
            
            [self showAlertTimeout];
        });

    }else{
        dispatch_async(dispatch_get_main_queue(), ^{
            
            [self showAlertBadText];
        });
    }
}

-(void)showAlertTimeout{
    UIAlertController * alert=   [UIAlertController
                                  alertControllerWithTitle:NSLocalizedString(@"Connection timed out.", nil)
                                  message:nil
                                  preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* ok = [UIAlertAction
                            actionWithTitle:NSLocalizedString(@"OK", nil)
                            style:UIAlertActionStyleDefault
                            handler:^(UIAlertAction * action)
                            {
                                [alert dismissViewControllerAnimated:YES completion:nil];
                            }];

    [alert addAction:ok];
    [self presentViewController:alert animated:YES completion:nil];
}

#pragma mark - UITextField response methods
//------------------------------------------------------------------------------
- (BOOL)textFieldShouldClear:(UITextField *)textField{
    
    if ([textField isEqual:txtFieldPwd])
        lengthPwd = 0;
    else
        lengthName = 0;
    
    if (lengthName >= 1 && lengthPwd >=  1)
        [self enableButtonItem];
    else
        [self disableButtonItem];

    
    return YES;
}

//------------------------------------------------------------------------------
-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
   
    
    if ([textField isEqual:txtFieldPwd]) {
        lengthPwd = txtFieldPwd.text.length - range.length + string.length;
    }else{
        lengthName = txtFieldName.text.length - range.length + string.length;
    }
    
    if (lengthName >= 1 && lengthPwd >=  1)
        
        [self enableButtonItem];
    else
        [self disableButtonItem];

    return YES;
}

//------------------------------------------------------------------------------
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField == txtFieldName)
        [txtFieldPwd becomeFirstResponder];
    
    else if (textField == txtFieldPwd){

      if (lengthName > 2 && lengthPwd >  5)
        [self passEnter:nil];
      else{
          [self showAlertBadText];
      }
    }
    
    return true;
}


@end
