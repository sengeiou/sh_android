//
//  UIview+moveTo.m
//  Goles Messenger
//
//  Created by Delfín Pereiro on 26/09/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "UIview+moveTo.h"

@implementation UIView( moveTo )

//------------------------------------------------------------------------------
-(CGRect)moveLabelTO:(INSERT_POSITION)position ofView:(UIView *)view  withMargin:(NSInteger)margin{
    
    CGRect frame = [view frame];
    CGRect selfFrame = [self frame];
    if ( position == INSERT_POSITION_LEFT )         selfFrame.origin.x = frame.origin.x - (selfFrame.size.width + margin);
    else if (position == INSERT_POSITION_RIGHT )    selfFrame.origin.x = frame.origin.x + frame.size.width + margin;
    [self setFrame:selfFrame];
    return selfFrame;
}

//------------------------------------------------------------------------------
-(CGRect)alignHorizontallyWithView:(UIView *)view from:(ALIGN_REFERENCE)alignRef{
    CGRect frame = [self frame];
    CGFloat widthDifference = view.frame.size.width - self.frame.size.width;
    switch (alignRef) {
        case ALIGN_LEFT_EDGE:
            frame.origin.x = view.frame.origin.x;
            break;
            
        case ALIGN_CENTERS:
            frame.origin.x = view.frame.origin.x + (widthDifference/2.0f);
            break;
            
        case ALIGN_RIGHT_EDGE:
            frame.origin.x = view.frame.origin.x + widthDifference;
            break;
    }
    [self setFrame:frame];
    return frame;
}

@end
