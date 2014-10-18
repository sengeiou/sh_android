//
//  Followbutton.m
//  Shootr
//
//  Created by Christian Cabarrocas on 17/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "Followbutton.h"
#import "Fav24Colors.h"
#import "Utils.h"

@implementation Followbutton

-(id) initWithCoder:(NSCoder *)aDecoder {
    
    if ((self = [super initWithCoder:aDecoder])) {
        [self setImage:[UIImage imageNamed:@"Icon_NotFollowing"] forState:UIControlStateNormal];

//        self.backgroundColor = [UIColor whiteColor];
//        [self setAttributedTitle:[Utils formatTitle:NSLocalizedString(@"+ FOLLOW", nil)] forState:UIControlStateNormal];
//        [self setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
//        self.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
//        self.layer.borderWidth = 1.0f;
//        self.layer.masksToBounds = YES;
//
    }
    return self;
}


- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        [self setImage:[UIImage imageNamed:@"Icon_NotFollowing_Pressed"] forState:UIControlStateHighlighted];
//        self.backgroundColor = [Fav24Colors iosSevenBlue];
//        [self setAttributedTitle:[Utils formatTitle:NSLocalizedString(@"+ FOLLOW", nil)] forState:UIControlStateHighlighted];
//        self.titleLabel.textColor = [UIColor whiteColor];
////        [self setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
////        self.layer.borderWidth = 1.0f;
//        self.layer.masksToBounds = YES;
    }else {
        [self setImage:[UIImage imageNamed:@"Icon_NotFollowing"] forState:UIControlStateNormal];
//        self.backgroundColor = [UIColor whiteColor];
//        [self setAttributedTitle:[Utils formatTitle:NSLocalizedString(@"+ FOLLOW", nil)] forState:UIControlStateNormal];
//        [self setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
//        self.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
//        self.layer.borderWidth = 1.0f;
//        self.layer.masksToBounds = YES;
    }
    
}

@end
