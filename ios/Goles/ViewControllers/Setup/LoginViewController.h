//
//  LoginViewController.h
//
//  Created by Maria Teresa Bañuls on 01/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^usersReturnBlock)(NSArray *usersArray, NSError *error);
typedef void(^booleanReturnBlock)(BOOL success, NSError *error);

@interface LoginViewController : UIViewController

@end
