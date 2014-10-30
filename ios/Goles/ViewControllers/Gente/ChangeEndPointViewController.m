//
//  ChangeEndPointViewController.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 21/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ChangeEndPointViewController.h"
#import "Services.pch"
#import "Utils.h"
#import "Constants.h"
#import "UserManager.h"
#import "Device.h"

@interface ChangeEndPointViewController ()<UITextFieldDelegate>

@property (nonatomic, weak) IBOutlet UITextField *txtFieldEndPoint;
@property (nonatomic, weak) IBOutlet UILabel *lblUserId;
@property (nonatomic, weak) IBOutlet UILabel *lblDeviceId;
@property (nonatomic, weak) IBOutlet UITextField *txtFieldToken;

@property (nonatomic,strong) NSString *changeURL;

@end

@implementation ChangeEndPointViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.lblDeviceId.text = [NSString stringWithFormat:@"DEVIDE_ID: %@",[[UserManager sharedInstance] getIdDevice]];
    self.lblUserId.text = [NSString stringWithFormat:@"USER_ID: %@",[[UserManager sharedInstance] getUserId]];
    
    Device *device = [[UserManager sharedInstance] getDevice];
    
    self.txtFieldToken.text = device.token;
    [self.txtFieldToken sizeToFit];
    self.txtFieldToken.selected = YES;
    self.txtFieldToken.delegate = self;
    
    if (K_DEBUG_MODE) {
        NSString * changeURL =[Utils getValueFromUserDefaultsFromKey:@"newUrl"];
        
        if (changeURL != nil)
            self.txtFieldEndPoint.text = changeURL;
        else
            self.txtFieldEndPoint.text = K_ENDPOINT_DEVELOPER;
    }
    
    self.txtFieldEndPoint.delegate = self;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

//-(BOOL)textFieldShouldBeginEditing:(UITextField *)textField{
//    
//    if ([textField isEqual:self.txtFieldToken])
//         [self.txtFieldToken resignFirstResponder];
//        
//    return YES;
//}

-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string{
    
    if ([textField isEqual:self.txtFieldToken])
         return NO;
    
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    
    self.changeURL = textField.text;

    [Utils setValueToUserDefaults:self.changeURL ToKey:@"newUrl"];
    
    exit(0);
    
    return YES;
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
