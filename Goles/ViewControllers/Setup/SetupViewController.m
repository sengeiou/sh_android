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

@interface SetupViewController ()<UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate, ConectionProtocol>{
    
     UITextField *txtFieldName;
     UITextField *txtFieldPwd;
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
}

-(void)viewWillAppear:(BOOL)animated{
    self.navigationController.navigationBarHidden = NO;
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

    [[Conection sharedInstance]getServerTimewithDelegate:self];
}


#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status{
    
    if (status){
        NSString *result =  [[Encryption sharedInstance] getPassword:txtFieldPwd.text];
        
        if ([[Conection sharedInstance]isConection]) {
            NSDictionary *key;
           
            if ([Utils NSStringIsValidEmail:txtFieldName.text])
                key   = @{kJSON_EMAIL:txtFieldName.text, kJSON_PASSWORD:result};
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
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"You can not access Shooter" message:@"Id or Password are not valid" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alert show];    }
}

-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
   
    NSUInteger length = txtFieldName.text.length - range.length + string.length;
    
    NSUInteger length1 = txtFieldPwd.text.length - range.length + string.length;

    
    if (length > 1 && length1 > 1) {
        self.btnEnter.enabled = YES;
    } else {
        self.btnEnter.enabled = NO;
    }
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField == txtFieldName)
        [txtFieldPwd becomeFirstResponder];
    
    else if (textField == txtFieldPwd && ![txtFieldName.text isEqualToString:@""])
        [self passEnter:nil];
    

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

-(void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration{
    
    [self.mtableView reloadData];
    [self.mtableView setNeedsDisplay];

}

@end
