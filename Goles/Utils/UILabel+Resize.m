//
//  UILabel+Resize.m
//  Goles Messenger
//
//  Created by Delfín Pereiro on 15/07/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "UILabel+Resize.h"

@implementation UILabel (Resize)

-(CGRect)resizeWidthToFitContentWithMaxWidth:(NSInteger)maxWidth{
    
    return [self resizeWidthToFitContentWithMaxWidth:maxWidth withMargin:0];
}

-(CGRect)resizeWidthToFitContentWithMaxWidth:(NSInteger)maxWidth withMargin:(NSUInteger)margin {
    
    CGSize maxSize = CGSizeMake(maxWidth,CGFLOAT_MAX);
    CGSize expectedSizeHeader = [[self text] sizeWithFont:[self font] constrainedToSize:maxSize lineBreakMode:[self lineBreakMode]];
    //    CGRect expectedSizeHeader = [[self text] boundingRectWithSize:maxSize options:NSStringDrawingUsesLineFragmentOrigin attributes:nil context:nil];
    CGRect frame = [self frame];
    frame.size.width =  ceilf(expectedSizeHeader.width+margin);
    [self setFrame:frame];
    return frame;
}

@end
