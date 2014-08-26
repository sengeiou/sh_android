//
//  CuotasCell.m
//  Goles Messenger
//
//  Created by Luis Rollon on 07/04/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "CuotasCell.h"

@interface CuotasCell()

@property(nonatomic, strong) UILabel *mLabel1X2;

@end

@implementation CuotasCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.mLabel1X2 = [[UILabel alloc]initWithFrame:CGRectMake(236, 12, 60, 20)];
        self.mLabel1X2.textColor = [UIColor lightGrayColor];
        self.mLabel1X2.backgroundColor = [UIColor whiteColor];
        self.mLabel1X2.text = @"1, X, 2";
        [self addSubview:self.mLabel1X2];
        self.mLabel1X2.hidden = YES;
    }
    return self;
}

- (void)awakeFromNib
{
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

//------------------------------------------------------------------------------
- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated {
    [super setHighlighted:highlighted animated:animated];
    
    if (highlighted){
        self.mLabel1X2.hidden = NO;
        self.detailTextLabel.hidden = YES;
    }
    else{
        self.mLabel1X2.hidden = YES;
        self.detailTextLabel.hidden = NO;
    }
}



@end
