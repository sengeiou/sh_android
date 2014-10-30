//
//  Conection.h
//  Goles
//
//  Created by Maria Teresa Bañuls on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIImage (CircularClip)

- (UIImage *)circularImageToFit:(CGRect)rect;
- (UIImage *)circularMedallionToFit:(CGRect)rect withInnerBorder:(BOOL)inner;

@end
