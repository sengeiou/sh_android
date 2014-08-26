//
//  BetDescriptionCell.m
//  Goles Messenger
//
//  Created by Delfin Pereiro on 19/12/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "BetDescriptionCell.h"
#import "Match.h"
#import "Utils.h"
#import "ProviderBetViewController.h"

@implementation BetDescriptionCell

//------------------------------------------------------------------------------
- (id)init
{
    self = [super initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"betDescriptionCell"];
    if (self) {
        [self setAccessoryType:UITableViewCellAccessoryDisclosureIndicator];
        
        // Initialization code mValueLabel
        CGFloat labelWidth = 100;
        CGFloat insertionPoint = self.contentView.bounds.size.width - labelWidth - 33;
        CGRect frame = CGRectMake(insertionPoint, 0.0, labelWidth, self.contentView.bounds.size.height);

        self.mValueLabel = [[UILabel alloc] initWithFrame:frame];
        self.mValueLabel.font = [UIFont systemFontOfSize:17.0];
        self.mValueLabel.lineBreakMode = NSLineBreakByWordWrapping;
        self.mValueLabel.textAlignment = NSTextAlignmentRight;
        self.mValueLabel.textColor = [UIColor lightGrayColor];
        
        [[self contentView] addSubview:self.mValueLabel];
    
        self.mCuotaLabel = [[UILabel alloc] initWithFrame:frame];
        self.mCuotaLabel.font = [UIFont systemFontOfSize:17.0];
        self.mCuotaLabel.lineBreakMode = NSLineBreakByWordWrapping;
        self.mCuotaLabel.textAlignment = NSTextAlignmentRight;
        self.mCuotaLabel.textColor = [UIColor lightGrayColor];
        
        self.textLabel.font = [UIFont systemFontOfSize:17.0];
        
        [[self detailTextLabel] setTextColor:[UIColor lightGrayColor]];

        [[self contentView] addSubview:self.mCuotaLabel];
        
    }
    return self;
}

//------------------------------------------------------------------------------
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [self init];
    return self;
}

//------------------------------------------------------------------------------
- (void)layoutSubviews {
    [super layoutSubviews];
    [self formatLabelCell];
}

//------------------------------------------------------------------------------
/**
 @brief Method called from layoutSubviews to change cell textlabel width. We need
 this method because when the quantity to win is very long, the cell textlabel
 overlap this text. To fix this, we set the textlabe cell width to 165.0 and put
 his adjustsFontSizeToFitWidth property to yes.
 */
//------------------------------------------------------------------------------
-(void)formatLabelCell
{
    CGRect frame = CGRectMake(self.textLabel.frame.origin.x, self.textLabel.frame.origin.y, 165.0, self.textLabel.frame.size.height);
    self.textLabel.frame =  frame;
}

//------------------------------------------------------------------------------
- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated {
    [super setHighlighted:highlighted animated:animated];
    
    if (highlighted) {
        
        self.mValueLabel.hidden = YES;
        self.mCuotaLabel.hidden = NO;

    }else{
        self.mCuotaLabel.hidden = YES;
        self.mValueLabel.hidden = NO;

    }
}

//------------------------------------------------------------------------------
- (void)configBetCellWithBetTypeOdd:(BetTypeOdd *)betTypeOdd andEuros:(NSNumber *)euros {
    
    self.textLabel.text = betTypeOdd.name;

    if (betTypeOdd.valueValue > 0) {
        CGFloat result = betTypeOdd.valueValue * [euros floatValue];
        self.mValueLabel.text  = [Utils changeQuantityToEURFormat:result];
        //self.mValueLabel.text = [[NSNumber numberWithFloat:result] stringValue];
        self.mCuotaLabel.text = [Utils setTwoDecimalsToBetOdd:betTypeOdd.valueValue];
    }else {
        self.mValueLabel.text  = nil;
    }

}

//------------------------------------------------------------------------------
- (void)configBetCellWithoutOdds {
    
    self.textLabel.text = @"No disponible";
    self.textLabel.textColor = [UIColor lightGrayColor];
}


@end
