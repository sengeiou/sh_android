//
//  FollowButtonProfile.m
//  Shootr
//
//  Created by Christian Cabarrocas on 18/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FollowButtonProfile.h"
#import "Fav24Colors.h"

@implementation FollowButtonProfile

-(id) initWithCoder:(NSCoder *)aDecoder {
    
    if ((self = [super initWithCoder:aDecoder])) {
        [self setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
        self.backgroundColor = [UIColor whiteColor];
        self.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
        self.layer.borderWidth = 1.0f;
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 5;
        self.clipsToBounds = YES;
    }
    
    return self;
}


- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        [self setTitleColor:[UIColor whiteColor] forState:UIControlStateHighlighted];
        self.backgroundColor = [Fav24Colors iosSevenBlue];
    }else {
        [self setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
        self.backgroundColor = [UIColor whiteColor];
        self.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
        self.layer.borderWidth = 1.0f;
    }
}

@end
