//
//  PeopleCustomCell.h
//  Shootr
//
//  Created by Christian Cabarrocas on 06/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "User.h"

@interface PeopleCustomCell : UITableViewCell

@property (nonatomic,weak) IBOutlet UILabel *lblNickName;
@property (nonatomic,weak) IBOutlet UILabel *lblFavouriteTeamName;
@property (weak, nonatomic) IBOutlet UIImageView *imgPhoto;
@property (nonatomic,weak) IBOutlet UIButton *photobutton;

-(void)configureCellWithUser:(User *)user inRow:(NSIndexPath *)indexPath;
- (void)addTarget:(id)target action:(SEL)action;

@end