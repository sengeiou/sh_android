//
//  ShotTableViewCell.h
//
//  Created by Maria Teresa Bañuls on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Shot.h"
#import "CustomTextView.h"

@interface ShotTableViewCell : UITableViewCell

//@property (retain, nonatomic) IBOutlet CustomTextView *txvText;
@property (retain, nonatomic) IBOutlet UILabel *txvText;
@property (retain, nonatomic) IBOutlet UILabel *lblName;
@property (retain, nonatomic) IBOutlet UILabel *lblDate;
@property (retain, nonatomic) IBOutlet UIImageView *imgPhoto;
@property (retain, nonatomic) IBOutlet UIButton *btnPhoto;

- (void)configureBasicCellWithShot:(Shot *)shot andRow:(NSInteger)row;
- (void)addTarget:(id)target action:(SEL)action;

@end
