//
//  DownloadImage.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 09/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "DownloadImage.h"
#import "UIImageView+AFNetworking.h"
#import "TimeLineUtilities.h"
#import <QuartzCore/QuartzCore.h>

@implementation DownloadImage

+(UIImageView *) downloadImageWithUrl:(NSURL *) url andUIimageView:(UIImageView *)imageView andText:(NSString *)text{
    
    __weak UIImageView *img = imageView;
    
     NSURLRequest *urlRequest = [NSURLRequest requestWithURL:url cachePolicy:NSURLRequestReturnCacheDataElseLoad timeoutInterval:30.0f];
    
    [img setImageWithURLRequest:urlRequest placeholderImage:[UIImage imageNamed:@"defaultImageCircle"] success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
        
        imageView.image = image;
//        imageView.layer.cornerRadius = imageView.frame.size.width / 2;
//        imageView.layer.masksToBounds = YES;
//        imageView.clipsToBounds = YES;
        
    } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {

            UIImage *imageDefault = [UIImage imageNamed:@"defaultImageCircle"];
            UIImage *imageCreate = [TimeLineUtilities drawText:text
                                                       inImage:imageDefault
                                                       atPoint:[TimeLineUtilities centerTextInImage:imageView] andSizeFont:80];
            imageView.image = imageCreate;
        
    }];
    
    return imageView;

}


@end
