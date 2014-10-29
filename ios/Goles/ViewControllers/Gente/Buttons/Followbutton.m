//
//  Followbutton.m
//  Shootr
//
//  Created by Christian Cabarrocas on 17/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "Followbutton.h"
#import "Fav24Colors.h"

@implementation Followbutton

- (id) initWithCoder:(NSCoder *)aDecoder {
    
    if ((self = [super initWithCoder:aDecoder])) {
        
        UIImage *unSeletedImage = [UIImage imageNamed:@"Icon_NotFollowing"];
        unSeletedImage = [unSeletedImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
        [self setImage:unSeletedImage forState:UIControlStateNormal];
    }
    
    return self;
}


- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        
        UIImage *unSeletedImage = [UIImage imageNamed:@"Icon_NotFollowing_Pressed"];
        unSeletedImage = [unSeletedImage imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
        [self setImage:unSeletedImage forState:UIControlStateHighlighted];
        self.tintColor = [Fav24Colors iosSevenBlue];

    }else {
     
        UIImage *unSeletedImage = [UIImage imageNamed:@"Icon_NotFollowing"];
        unSeletedImage = [unSeletedImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
        [self setImage:unSeletedImage forState:UIControlStateNormal];
    }
}

@end
