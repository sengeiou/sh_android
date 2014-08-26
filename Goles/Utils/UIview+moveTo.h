//
//  UIview+moveTo.h
//  Goles Messenger
//
//  Created by Delfín Pereiro on 26/09/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UIView( moveTo )

typedef enum {
    INSERT_POSITION_LEFT,
    INSERT_POSITION_RIGHT
}INSERT_POSITION;

typedef enum {
    ALIGN_LEFT_EDGE,
    ALIGN_CENTERS,
    ALIGN_RIGHT_EDGE
}ALIGN_REFERENCE;

-(CGRect)moveLabelTO:(INSERT_POSITION)position ofView:(UIView *)view withMargin:(NSInteger)margin;
-(CGRect)alignHorizontallyWithView:(UIView *)view from:(ALIGN_REFERENCE)alignRef;

@end
