//
//  UILabel+Resize.h
//  Goles Messenger
//
//  Created by Delfín Pereiro on 15/07/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UILabel (Resize)

-(CGRect)resizeWidthToFitContentWithMaxWidth:(NSInteger)maxWidth;
-(CGRect)resizeWidthToFitContentWithMaxWidth:(NSInteger)maxWidth withMargin:(NSUInteger)margin;

@end
