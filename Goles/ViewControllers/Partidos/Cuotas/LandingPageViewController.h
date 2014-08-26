//
//  LandingPageViewController.h
//  Goles Messenger
//
//  Created by Luis Rollon on 03/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Provider.h"

@interface LandingPageViewController : UIViewController<UIAlertViewDelegate>

@property (weak, nonatomic) IBOutlet UILabel *mProviderName;
@property (weak, nonatomic) IBOutlet UILabel *mOfferDescription;
@property (weak, nonatomic) IBOutlet UIButton *mAlreadyRegisteredButton;
@property (weak, nonatomic) IBOutlet UIButton *mDoRegisterNow;
@property (strong, nonatomic) UITableViewController *mProviderBetVC;
@property (strong, nonatomic) NSURL           *registryURL;
@property (strong, nonatomic) NSURL           *oddURL;
@property (strong, nonatomic) Provider        *provider;

@property BOOL mComesFromBack;

@end
