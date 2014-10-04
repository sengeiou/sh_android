//
//  FollowingCustomCellTableViewCell.h
//  Shootr
//
//  Created by Christian Cabarrocas on 03/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "User.h"

@interface FollowingCustomCell : UITableViewCell

@property (nonatomic,weak) IBOutlet UILabel *userName;
@property (nonatomic,weak) IBOutlet UILabel *nickName;
@property (weak, nonatomic) IBOutlet UIImageView *imgPhoto;
@property (weak, nonatomic) IBOutlet UIButton *actionButton;
@property (nonatomic,weak) IBOutlet UIButton *photobutton;

-(void)configureCellWithUser:(User *)user;
-(void)addTarget:(id)target action:(SEL)action;

@end
