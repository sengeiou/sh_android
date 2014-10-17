//
//  Followingbutton.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 17/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "Followingbutton.h"
#import "Fav24Colors.h"
#import "Utils.h"

@implementation Followingbutton

-(id) initWithCoder:(NSCoder *)aDecoder {
    
    if ((self = [super initWithCoder:aDecoder])) {

        NSMutableAttributedString *buttonString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedString(@" FOLLOWING", nil)];
        [buttonString addAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:13], NSForegroundColorAttributeName:[UIColor whiteColor]} range:NSMakeRange(0, buttonString.length)];
        [self setAttributedTitle:buttonString forState:UIControlStateNormal];
        
        self.layer.borderWidth = 0.0f;
        self.layer.masksToBounds = YES;
        
        [self setImage:[UIImage imageNamed:@"checkWhite"] forState:UIControlStateNormal];
        
    }
    return self;
}


- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        self.backgroundColor = [UIColor whiteColor];
        
        NSMutableAttributedString *buttonString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedString(@" FOLLOWING", nil)];
        [buttonString addAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:13], NSForegroundColorAttributeName:[Fav24Colors iosSevenBlue]} range:NSMakeRange(0, buttonString.length)];
        [self setAttributedTitle:buttonString forState:UIControlStateHighlighted];

        self.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
        self.layer.borderWidth = 1.0f;
        self.layer.masksToBounds = YES;
        [self setImage:[UIImage imageNamed:@"checkWhite"] forState:UIControlStateHighlighted];

    }  else{
        
        NSMutableAttributedString *buttonString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedString(@" FOLLOWING", nil)];
        [buttonString addAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:13], NSForegroundColorAttributeName:[UIColor whiteColor]} range:NSMakeRange(0, buttonString.length)];
        [self setAttributedTitle:buttonString forState:UIControlStateNormal];
        
        self.layer.borderWidth = 0.0f;
        self.layer.masksToBounds = YES;
        
        [self setImage:[UIImage imageNamed:@"checkWhite"] forState:UIControlStateNormal];

    }
  
}


@end
