//
//  FollowingButtonProfile.m
//  Shootr
//
//  Created by Christian Cabarrocas on 18/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FollowingButtonProfile.h"
#import "Fav24Colors.h"

@implementation FollowingButtonProfile

-(id) initWithCoder:(NSCoder *)aDecoder {
    
    if ((self = [super initWithCoder:aDecoder])) {

         [self setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        self.backgroundColor = [Fav24Colors iosSevenBlue];
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 5;
        self.clipsToBounds = YES;
        [self setImage:[UIImage imageNamed:@"checkWhite"] forState:UIControlStateNormal];
        [self setImageEdgeInsets:UIEdgeInsetsMake(0, 0, 0, 10)];
    }
    
    return self;
}

- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        
        self.backgroundColor = [UIColor whiteColor];
        [self setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateHighlighted];

        self.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
        self.layer.borderWidth = 1.0f;
        self.layer.masksToBounds = YES;
        [self setImage:[UIImage imageNamed:@"Icon_CheckBlue"] forState:UIControlStateHighlighted];

    }  else{

        self.backgroundColor = [Fav24Colors iosSevenBlue];
        self.layer.masksToBounds = YES;
    }
}


@end
