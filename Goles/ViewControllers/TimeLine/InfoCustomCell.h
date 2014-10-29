//
//  InfoCustomCellTableViewCell.h
//  Shootr
//
//  Created by Christian Cabarrocas on 11/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "User.h"

@interface InfoCustomCell : UITableViewCell

@property (nonatomic,weak) IBOutlet UILabel     *userName;
@property (nonatomic,weak) IBOutlet UIImageView *imgPhoto;
@property (nonatomic,weak) IBOutlet UIButton    *photobutton;
@property (nonatomic,weak) IBOutlet UILabel     *wacthing;

- (void)configureInfoCellWithUser:(User *)user inRow:(NSIndexPath *)indexPath;

@end
