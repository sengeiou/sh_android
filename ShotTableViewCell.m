          //
//  ShotTableViewCell.m
//
//  Created by Maria Teresa Bañuls on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ShotTableViewCell.h"
#import "User.h"
#import "TimeLineUtilities.h"
#import "NSString+CleanLinks.h"
#import "UIImageView+AFNetworking.h"
#import "UIButton+AFNetworking.h"
#import "AFHTTPRequestOperation.h"
#import "DownloadImage.h"

@implementation ShotTableViewCell

- (void)awakeFromNib {
    
    
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    
    if (self) {
        
    }
    
    return self;
}

- (void)configureBasicCellWithShot:(Shot *)shot andRow:(NSInteger)row {

    self.txvText.text = [shot.comment cleanStringfromLinks:shot.comment];
    self.txvText.textColor = [UIColor blackColor];
    self.txvText.frame = CGRectMake(self.txvText.frame.origin.x, self.txvText.frame.origin.y,self.txvText.frame.size.width, [TimeLineUtilities heightForShot:shot.comment]);
    self.txvText.scrollEnabled = NO;

    self.lblName.text = shot.user.name;
    
    self.imgPhoto = [DownloadImage downloadImageWithUrl:[NSURL URLWithString:shot.user.photo] andUIimageView:self.imgPhoto andText:[shot.user.name substringToIndex:1]];
    
    self.lblDate.text = [TimeLineUtilities getDateShot:shot.csys_birth];
    self.btnPhoto.tag = row;
}

- (void)addTarget:(id)target action:(SEL)action
{
    [self.btnPhoto addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
