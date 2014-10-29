//
//  DownloadImage.h
//  Shootr
//
//  Created by Maria Teresa Bañuls on 09/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DownloadImage : NSObject

+(UIImageView *) downloadImageWithUrl:(NSURL *) url andUIimageView:(UIImageView *)imageView andText:(NSString *)text;
+(UIImageView *) downloadImageForTimeLineWithUrl:(NSURL *) url andUIimageView:(UIImageView *)imageView andText:(NSString *)text;

@end
