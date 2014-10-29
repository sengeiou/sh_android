//
//  EditProfileBUtton.m
//  Shootr
//
//  Created by Christian Cabarrocas on 17/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "EditProfileButton.h"
#import "Fav24Colors.h"
#import "Utils.h"

@implementation EditProfileButton

- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        self.backgroundColor = [Fav24Colors iosSevenBlue];
        [self setAttributedTitle:[Utils formatTitle:NSLocalizedString(@"+ FOLLOW", nil)] forState:UIControlStateHighlighted];
        [self setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
        self.layer.borderWidth = 1.0f;
        self.layer.masksToBounds = YES;
    }else {
        
    }
    
}

@end
