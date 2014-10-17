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

@property (nonatomic,weak) IBOutlet UILabel *lblNickName;
@property (nonatomic,weak) IBOutlet UILabel *lblfavouriteTeamName;
@property (weak, nonatomic) IBOutlet UIImageView *imgPhoto;
@property (weak, nonatomic) IBOutlet UIButton *following;
@property (weak, nonatomic) IBOutlet UIButton *follow;
@property (nonatomic,weak) IBOutlet UIButton *photobutton;

-(void)configurePeopleCellWithUser:(User *)user inRow:(NSIndexPath *)indexPath whileSearching:(BOOL)searching inPeople:(BOOL)peopleTable;
-(void)addTarget:(id)target action:(SEL)action;
-(void)addTargetBtnFollow:(id)target action:(SEL)action;

@end
