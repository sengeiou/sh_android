//
//  TimeLineUtilities.h
//  Shootr
//
//  Created by Christian Cabarrocas on 08/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TimeLineUtilities : NSObject

+ (NSString *)getDateShot:(NSNumber *) dateShot;
+ (UIImage*)drawText:(NSString*)text inImage:(UIImage*)image atPoint:(CGPoint)point andSizeFont:(CGFloat)sizeFont;
+ (CGPoint)centerTextInImage:(UIImageView *)imageView;
+ (NSMutableAttributedString *)filterLinkWithContent:(NSString *)content;
+ (CGSize)commentSizeForText:(NSString *)text;

@end
