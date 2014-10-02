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
#import "NSString+CleanLinks.h"
#import "UIImageView+AFNetworking.h"

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
    self.txvText.frame = CGRectMake(self.txvText.frame.origin.x, self.txvText.frame.origin.y,self.txvText.frame.size.width, [Utils heightForShot:shot.comment]);
    self.txvText.scrollEnabled = NO;
    
    self.lblName.text = shot.user.name;
    //shot.user.imgUser = [UIImage imageNamed:@"ball"];
    
    [self.imgPhoto setImageWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:shot.user.photo]] placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
        self.imgPhoto.image = image;
        self.imgPhoto.layer.cornerRadius = self.imgPhoto.frame.size.width / 2;
        self.imgPhoto.clipsToBounds = YES;
    } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
        NSLog(@"%@", error);
    }];
    
    self.lblDate.text = [Utils getDateShot:shot.csys_birth];
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
