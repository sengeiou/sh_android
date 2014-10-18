//
//  Followingbutton.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 17/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "Followingbutton.h"


@implementation Followingbutton

-(id) initWithCoder:(NSCoder *)aDecoder {
    
    if ((self = [super initWithCoder:aDecoder])) {
        
        UIImage *unSeletedImage = [UIImage imageNamed:@"Icon_Following"];
        unSeletedImage = [unSeletedImage imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
        [self setImage:unSeletedImage forState:UIControlStateNormal];
    }
    
    return self;
}


- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        
        UIImage *unSeletedImage = [UIImage imageNamed:@"Icon_Following_Pressed"];
        unSeletedImage = [unSeletedImage imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
        [self setImage:unSeletedImage forState:UIControlStateHighlighted];

    }  else{

        UIImage *unSeletedImage = [UIImage imageNamed:@"Icon_Following"];
        unSeletedImage = [unSeletedImage imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
        [self setImage:unSeletedImage forState:UIControlStateNormal];

    }  
}


@end
