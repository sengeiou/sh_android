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

@implementation DownloadImage

+(UIImageView *) downloadImageWithUrl:(NSURL *) url andUIimageView:(UIImageView *)imageView andText:(NSString *)text{
    
    NSURLRequest *urlRequest = [NSURLRequest requestWithURL:url cachePolicy:NSURLRequestReturnCacheDataElseLoad timeoutInterval:30.0f];
    UIImage *image = [[UIImageView sharedImageCache] cachedImageForRequest:urlRequest];
  
    //__weak UIImageView* imgView = nil;
   
    if (image == nil) {
        
        [imageView setImageWithURLRequest:urlRequest placeholderImage:[UIImage imageNamed:@"defaultImageCircle"] success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            imageView.image = image;
            imageView.layer.cornerRadius = imageView.frame.size.width / 2;
            imageView.clipsToBounds = YES;
            [[UIImageView sharedImageCache] cacheImage:image forRequest:urlRequest];
        } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
            
            UIImage *imageDefault = [UIImage imageNamed:@"defaultImageCircle"];
            
            UIImage *img = [TimeLineUtilities drawText:text
                                               inImage:imageDefault
                                               atPoint:[TimeLineUtilities centerTextInImage:imageView] andSizeFont:80];
            
            imageView.image = img;
        }];
    }else{
        imageView.image = image;
        imageView.layer.cornerRadius = imageView.frame.size.width / 2;
        imageView.clipsToBounds = YES;
    }
    
    return imageView;

}


@end
