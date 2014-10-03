//
//  FollowingViewControllerTableViewController.h
//  Shootr
//
//  Created by Christian Cabarrocas on 03/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "User.h"

@interface FollowingTableViewController : UITableViewController

@property (nonatomic,strong)    User *selectedUser;

@end
