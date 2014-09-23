//
//  ShotTableViewCell.h
//  Goles
//
//  Created by Maria Teresa Bañuls on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ShotTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UITextView *txvText;
@property (weak, nonatomic) IBOutlet UILabel *lblName;
@property (weak, nonatomic) IBOutlet UILabel *lblDate;
@property (weak, nonatomic) IBOutlet UIImageView *imgPhoto;

@end
