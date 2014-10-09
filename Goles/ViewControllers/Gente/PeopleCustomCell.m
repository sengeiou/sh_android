//
//  PeopleCustomCell.m
//  Shootr
//
//  Created by Christian Cabarrocas on 06/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "PeopleCustomCell.h"
#import "UIImageView+AFNetworking.h"
#import "Fav24Colors.h"
#import <QuartzCore/QuartzCore.h>
#import "TimeLineUtilities.h"

@implementation PeopleCustomCell

- (void)awakeFromNib {
    // Initialization code
}

//------------------------------------------------------------------------------
-(void)configureCellWithUser:(User *)user inRow:(NSIndexPath *)indexPath{
    
    self.userName.text = user.name;
    self.nickName.text = user.userName;

    NSURLRequest *urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:user.photo] cachePolicy:NSURLRequestReturnCacheDataElseLoad timeoutInterval:30.0f];
    UIImage *image = [[UIImageView sharedImageCache] cachedImageForRequest:urlRequest];
    
    if (image == nil) {
        
        
        
        [self.imgPhoto setImageWithURLRequest:urlRequest placeholderImage:[UIImage imageNamed:@"defaultImageCircle"] success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            self.imgPhoto.image = image;
            self.imgPhoto.layer.cornerRadius = self.imgPhoto.frame.size.width / 2;
            self.imgPhoto.clipsToBounds = YES;
            [[UIImageView sharedImageCache] cacheImage:image forRequest:urlRequest];
        } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
            
            UIImage *imageDefault = [UIImage imageNamed:@"defaultImageCircle"];
            
            UIImage *img = [TimeLineUtilities drawText:[user.name substringToIndex:1]
                                   inImage:imageDefault
                                   atPoint:[TimeLineUtilities centerTextInImage:self.imgPhoto] andSizeFont:80];
            
            self.imgPhoto.image = img;
            NSLog(@"%@", response);
        }];
    }else{
        self.imgPhoto.image = image;
        self.imgPhoto.layer.cornerRadius = self.imgPhoto.frame.size.width / 2;
        self.imgPhoto.clipsToBounds = YES;
    }
    
    self.photobutton.tag = indexPath.row;

}
//------------------------------------------------------------------------------
- (void)addTarget:(id)target action:(SEL)action {
    
    [self.photobutton addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
}

@end
