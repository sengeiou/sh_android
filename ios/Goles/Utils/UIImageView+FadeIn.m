//
//  Conection.h
//  Goles
//
//  Created by Maria Teresa Bañuls on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "UIImageView+FadeIn.h"
#import "UIImage+CircularClip.h"
#import "UIImageView+AFNetworking.h"

@implementation UIImageView (FadeIn)

- (void)fadeInImage:(UIImage *)image
{
    if (image!=self.image) {
        UIImageView *overlay = [[UIImageView alloc] initWithImage:image];
        overlay.contentMode = self.contentMode;
        overlay.frame = self.frame;
        overlay.alpha = 0;
        [self.superview addSubview:overlay];
        [UIView animateWithDuration:0.25f
                         animations:^{
                             overlay.alpha = 1;
                         }
                         completion:^(BOOL finished) {
                             self.image = image;
                             [overlay removeFromSuperview];
                         }];
    }
}

- (void)fadeInFromURL:(NSURL *)url withOuterMatte:(BOOL)matte andInnerBorder:(BOOL)inner
{    
    __weak UIImageView *weakSelf = self;
    UIImage *circularPlaceholder =  nil; //[placeholder circularImageToFit:self.bounds];

    [self setImageWithURLRequest:[NSURLRequest requestWithURL:url]
                placeholderImage:circularPlaceholder
                         success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
                             UIImage *circularImage;
                             if (matte) {
                                 circularImage = [image circularMedallionToFit:weakSelf.bounds withInnerBorder:inner];
                             } else {
                                 circularImage = [image circularImageToFit:CGRectMake(0, 0, 48, 48)]; //weakSelf.bounds
                             }
                             if (response) {
                                 [weakSelf fadeInImage:circularImage];
                             } else {
                                 weakSelf.image = circularImage;
                             }
                         }
                         failure:nil];
}

@end
