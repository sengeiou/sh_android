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

- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        self.backgroundColor = [Fav24Colors iosSevenBlue];
    }
    
}

@end
