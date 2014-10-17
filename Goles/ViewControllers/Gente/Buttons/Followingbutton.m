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

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        self.backgroundColor = [UIColor whiteColor];
        
        [self setAttributedTitle:[Utils formatTitle:NSLocalizedString(@"+ FOLLOW", nil)] forState:UIControlStateNormal];
        
        self.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
        self.layer.borderWidth = 1.0f;
        self.layer.masksToBounds = YES;
        [self setImage:[UIImage imageNamed:@" "] forState:UIControlStateNormal];

    }  else{
        [self setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        
        NSMutableAttributedString *buttonString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedString(@" FOLLOWING", nil)];
        [buttonString addAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:13], NSForegroundColorAttributeName:[UIColor whiteColor]} range:NSMakeRange(0, buttonString.length)];
        [self setAttributedTitle:buttonString forState:UIControlStateNormal];
        
        self.layer.borderWidth = 0.0f;
        self.layer.masksToBounds = NO;
        
        [self setImage:[UIImage imageNamed:@"checkWhite"] forState:UIControlStateNormal];

    }
  
}


@end
