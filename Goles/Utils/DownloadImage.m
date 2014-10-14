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
    UIImage *imageCacheDefault = [[UIImageView sharedImageCache] cachedImageForName:text];

    __weak UIImageView *img = imageView;
   
    if (image == nil) {
        
        [img setImageWithURLRequest:urlRequest placeholderImage:[UIImage imageNamed:@"defaultImageCircle"] success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            img.image = image;
            img.layer.cornerRadius = imageView.frame.size.width / 2;
            img.clipsToBounds = YES;
            [[UIImageView sharedImageCache] cacheImage:image forRequest:urlRequest];
        } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
            if (imageCacheDefault == nil) {
                UIImage *imageDefault = [UIImage imageNamed:@"defaultImageCircle"];
                
                UIImage *imageCreate = [TimeLineUtilities drawText:text
                                                           inImage:imageDefault
                                                           atPoint:[TimeLineUtilities centerTextInImage:imageView] andSizeFont:80];
                img.image = imageCreate;
                [[UIImageView sharedImageCache] cacheImage:imageCreate forName: text];
            }else{
                img.image = imageCacheDefault;

            }
        }];
    }else{
        img.image = image;
        img.layer.cornerRadius = imageView.frame.size.width / 2;
        img.clipsToBounds = YES;
    }
    
    return imageView;

}


@end
