//
//  Followingbutton.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 17/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "Followingbutton.h"
#import "Fav24Colors.h"

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
        self.titleLabel.textColor = [Fav24Colors iosSevenBlue];

    }
    
}

@end
