//
//  Conection.h
//  Goles
//
//  Created by Maria Teresa Bañuls on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//
#import "UIImage+CircularClip.h"
#include <math.h>

@implementation UIImage (CircularClip)

- (UIImage *)circularImageToFit:(CGRect)rect
{
    return [self circularImageToFit:rect withWhiteExtension:NO andInnerBorder:NO];
}

- (UIImage *)circularMedallionToFit:(CGRect)rect withInnerBorder:(BOOL)inner
{
    return [self circularImageToFit:rect withWhiteExtension:YES andInnerBorder:inner];
}

- (UIImage *)circularImageToFit:(CGRect)rect withWhiteExtension:(BOOL)extension andInnerBorder:(BOOL)inner
{
    // This function returns a newImage, based on image, that has been:
    // - scaled to fit in (CGRect) rect
    // - and cropped within a circle of radius: rectWidth/2
    
    
    //Create the bitmap graphics context
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(rect.size.width, rect.size.height), NO, 0.0);
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    //Get the width and heights
    CGFloat imageWidth = self.size.width;
    CGFloat imageHeight = self.size.height;
    CGFloat rectWidth = rect.size.width;
    CGFloat rectHeight = rect.size.height;
    
    //Calculate the centre of the circle
    CGFloat imageCentreX = rectWidth/2;
    CGFloat imageCentreY = rectHeight/2;
    
    // Create and CLIP to a CIRCULAR Path
    // (This could be replaced with any closed path if you want a different shaped clip)
    CGFloat radius = rectWidth/2;
    CGFloat compression = 1.0f;
    if (extension) {
        if (inner) {
            [[UIColor whiteColor] setFill];
            CGContextBeginPath (context);
            CGContextAddArc (context, imageCentreX, imageCentreY, radius, 0, 2*M_PI, 0);
            CGContextClosePath (context);
            CGContextFillPath(context);
            radius -= 3.0f;
            CGContextBeginPath (context);
            CGContextAddArc (context, imageCentreX, imageCentreY, radius, 0, 2*M_PI, 0);
            CGContextClosePath (context);
            CGContextSetLineWidth(context, 2.0f);
            [[UIColor darkGrayColor] setStroke];
            CGContextStrokePath(context);
            radius -= 1.0f;
            compression = 0.6f;

        } else {
            [[UIColor whiteColor] setFill];
            CGContextBeginPath (context);
            CGContextAddArc (context, imageCentreX, imageCentreY, radius, 0, 2*M_PI, 0);
            CGContextClosePath (context);
            CGContextFillPath(context);
            radius -= 2.0f;
            compression = 1.0-2.0/radius;
        }
    }
    
    CGContextBeginPath (context);
    CGContextAddArc (context, imageCentreX, imageCentreY, radius, 0, 2*M_PI, 0);
    CGContextClosePath (context);
    CGContextClip (context);
    
    //Calculate the scale factor
    CGFloat scaleFactor = MAX(rectWidth/imageWidth,rectHeight/imageHeight);
    
    //Set the SCALE factor for the graphics context
    //All future draw calls will be scaled by this factor
    CGContextScaleCTM (context, scaleFactor, scaleFactor);
    
    imageWidth = imageWidth*compression;
    imageHeight = imageHeight*compression;
    
    
    // Draw the IMAGE
    CGPoint org = CGPointMake((rectWidth/scaleFactor-imageWidth)/2.0,(rectHeight/scaleFactor-imageHeight)/2.0);
    
    CGRect myRect = CGRectMake(org.x, org.y , imageWidth, imageHeight);
    [self drawInRect:myRect];
    
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return newImage;
}

@end
