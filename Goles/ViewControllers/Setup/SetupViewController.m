//
//  SetupViewController.m
//  Goles
//
//  Created by Maria Teresa Bañuls on 15/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "SetupViewController.h"
#import "AppDelegate.h"
#import "Conection.h"
#import "FavRestConsumer.h"
#import "CoreDataParsing.h"
#import "Encryption.h"
#import "User.h"
#import "Utils.h"
#import "Fav24Colors.h"

@interface SetupViewController ()<UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate, ConectionProtocol>{
    
     UITextField *txtFieldName;
     UITextField *txtFieldPwd;
    
    int lengthName;
    
    int lengthPwd;
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
    // Do any additional setup after loading the view.
    self.btnEnter.enabled = NO;
    
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(dismissKeyboard)];
    
    [self.view addGestureRecognizer:tap];
    
    [self addTextFields];
}

-(void)addTextFields{
    txtFieldName = [[UITextField alloc] initWithFrame:CGRectMake(180,3,self.view.frame.size.width-185,40)];
    txtFieldName.delegate = self;
    txtFieldName.clearButtonMode = YES;
    [txtFieldName setReturnKeyType:UIReturnKeyNext];
    txtFieldName.placeholder = @"Required";
    
    txtFieldPwd = [[UITextField alloc] initWithFrame:CGRectMake(180,3,self.view.frame.size.width-185,40)];
    txtFieldPwd.delegate = self;
    txtFieldPwd.clearButtonMode = YES;
    [txtFieldPwd setReturnKeyType:UIReturnKeyGo];
    txtFieldPwd.placeholder = @"Required";
    txtFieldPwd.secureTextEntry = YES;
    txtFieldPwd.autocapitalizationType = UITextAutocapitalizationTypeNone;
}

-(void)viewWillAppear:(BOOL)animated{
    self.navigationItem.backBarButtonItem.title = @"Back";
    self.navigationController.navigationBarHidden = NO;
    [self.navigationController.navigationBar setBackgroundImage:[UIImage new]
                             forBarMetrics:UIBarMetricsDefault];
    self.navigationController.navigationBar.shadowImage = [UIImage new];
    self.navigationController.navigationBar.translucent = YES;
    [self.btnEnter setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
                                           [UIFont boldSystemFontOfSize:17.0], NSFontAttributeName,
                                           [Fav24Colors iosSevenGray], NSForegroundColorAttributeName,
                                           nil]
                                 forState:UIControlStateNormal];

}

-(void)viewDidDisappear:(BOOL)animated{
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

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 2;
}


 - (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
 {
     UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cellSetup"  forIndexPath:indexPath];
     
     
     switch (indexPath.row) {
         case 0:
         {
            cell.textLabel.text = @"Email or Username";
             txtFieldName.frame = CGRectMake(180,3,self.view.frame.size.width-210,40);
            [cell addSubview:txtFieldName];
         }
             break;
         case 1:
         {
             cell.textLabel.text = @"Password";
             txtFieldPwd.frame = CGRectMake(180,3,self.view.frame.size.width-210,40);

             [cell addSubview:txtFieldPwd];
         }
            
            break;
         default:
             break;
     }
     
     return cell;
}

- (IBAction)passEnter:(id)sender {

    if (lengthName > 2 && lengthPwd >  5)
        [[Conection sharedInstance]getServerTimewithDelegate:self];
    else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"You can not access Shootr" message:@"Id or Password are not valid." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alert show];
    }

}


#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status{
    
    if (status){
        NSString *result =  [[Encryption sharedInstance] getPassword:txtFieldPwd.text];
        
        if ([[Conection sharedInstance]isConection]) {
            NSDictionary *key;
           
            if ([Utils NSStringIsValidEmail:txtFieldName.text])
                key   = @{kJSON_EMAIL:txtFieldName.text , kJSON_PASSWORD:result};
            else
                key   = @{kJSON_USERNAME:txtFieldName.text, kJSON_PASSWORD:result};

            [[FavRestConsumer sharedInstance] getEntityFromClass:[User class] withKey:key withDelegate:self];
            
        };
    }
}


#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error {
    
    if (status){
        AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];

        [appDelegate setTabBarItems];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"You can not access Shootr" message:@"Id or Password are not valid." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alert show];    }
}

#pragma mark - UITextField response methods
//------------------------------------------------------------------------------
-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
   

    if ([textField isEqual:txtFieldPwd]) {
        lengthPwd = txtFieldPwd.text.length - range.length + string.length;
    }else{
        lengthName = txtFieldName.text.length - range.length + string.length;
    }
    
    
    if (lengthName >= 1 && lengthPwd >=  1) {
        
        self.btnEnter.enabled = YES;
        
        [self.btnEnter setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
                                               [UIFont boldSystemFontOfSize:17.0], NSFontAttributeName,
                                               [Fav24Colors iosSevenBlue], NSForegroundColorAttributeName,
                                               nil]
                                     forState:UIControlStateNormal];
        
    } else {
        self.btnEnter.enabled = NO;
        
        [self.btnEnter setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
                                               [UIFont boldSystemFontOfSize:17.0], NSFontAttributeName,
                                               [Fav24Colors iosSevenGray], NSForegroundColorAttributeName,
                                               nil]
                                     forState:UIControlStateNormal];
    }
    return YES;
}

//------------------------------------------------------------------------------
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField == txtFieldName)
        [txtFieldPwd becomeFirstResponder];
    
    else if (textField == txtFieldPwd && ![txtFieldName.text isEqualToString:@""]){

      if (lengthName > 2 && lengthPwd >  5)
        [self passEnter:nil];
      else{
          UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"You can not access Shootr" message:@"Id or Password are not valid." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
          [alert show];
      }
    }
    

    return true;
}

/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */
#pragma mark - Rotation response methods
//------------------------------------------------------------------------------
-(void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration{
    
    [self.mtableView reloadData];
    [self.mtableView setNeedsDisplay];

}

@end
