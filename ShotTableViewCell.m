//
//  ShotTableViewCell.m
//  Goles
//
//  Created by Maria Teresa Bañuls on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ShotTableViewCell.h"
#import "User.h"
#import "Utils.h"
#import "UIImageView+FadeIn.h"

@implementation ShotTableViewCell

- (void)awakeFromNib {
    
    
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    
    if (self) {
        
    }
    
    return self;
}

- (void)configureBasicCellWithShot:(Shot *)shot {

    self.txvText.text = [shot.comment stringByReplacingOccurrencesOfString:@"http://" withString:@""];
    self.txvText.textColor = [UIColor blackColor];
    self.txvText.frame = CGRectMake(self.txvText.frame.origin.x, self.txvText.frame.origin.y,self.txvText.frame.size.width, [Utils heightForShot:shot.comment]);
    //[self.txvText setUserInteractionEnabled:NO];
    self.txvText.scrollEnabled = NO;
    
    self.lblName.text = shot.user.name;
    [self.imgPhoto fadeInFromURL:[NSURL URLWithString:shot.user.photo] withOuterMatte:NO andInnerBorder:NO];
    self.lblDate.text = [Utils getDateShot:shot.csys_birth];

}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
