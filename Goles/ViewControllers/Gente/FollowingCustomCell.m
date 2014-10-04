//
//  FollowingCustomCellTableViewCell.m
//  Shootr
//
//  Created by Christian Cabarrocas on 03/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FollowingCustomCell.h"
#import "UIImageView+AFNetworking.h"
#import "Fav24Colors.h"
#import "UserManager.h"
#import "CoreDataManager.h"
#import "Follow.h"
#import <QuartzCore/QuartzCore.h>

@implementation FollowingCustomCell

- (void)awakeFromNib {
    // Initialization code
}

-(void)configureCellWithUser:(User *)user {

    self.userName.text = user.name;
    self.nickName.text = user.userName;
	
    self.actionButton.layer.cornerRadius = 5.0f;
	
    if ([self checkIfImFollowingUser:user])
		[self configureFollowingButton];
		
    else
		[self configureFollowButton];
	
    if (user.idUser == [[UserManager singleton] getUserId])
        self.actionButton.hidden = YES;
    
    NSURLRequest *urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:user.photo] cachePolicy:NSURLRequestReturnCacheDataElseLoad timeoutInterval:30.0f];
    UIImage *image = [[UIImageView sharedImageCache] cachedImageForRequest:urlRequest];
    
    if (image == nil) {
        
        [self.imgPhoto setImageWithURLRequest:urlRequest placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            self.imgPhoto.image = image;
            self.imgPhoto.layer.cornerRadius = self.imgPhoto.frame.size.width / 2;
            self.imgPhoto.clipsToBounds = YES;
            [[UIImageView sharedImageCache] cacheImage:image forRequest:urlRequest];
            NSLog(@"success: %@", NSStringFromCGSize([image size]));
        } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
            NSLog(@"%@", response);
        }];
    }else{
        self.imgPhoto.image = image;
        self.imgPhoto.layer.cornerRadius = self.imgPhoto.frame.size.width / 2;
        self.imgPhoto.clipsToBounds = YES;
    }

}

- (void)configureFollowButton {

	[self.actionButton setTitle:@"+ Follow" forState:UIControlStateNormal];
	self.actionButton.backgroundColor = [Fav24Colors iosSevenBlue];
}

- (void)configureFollowingButton {
	
	[self.actionButton setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
	[self.actionButton setTitle:@"Following" forState:UIControlStateNormal];
	self.actionButton.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
	self.actionButton.backgroundColor = [UIColor whiteColor];
	self.actionButton.layer.borderWidth = 1.5f;
	self.actionButton.layer.masksToBounds = YES;
}

- (BOOL)checkIfImFollowingUser:(User *)user {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser == %@",[[UserManager singleton] getUserId]];
    NSArray *follows = [[CoreDataManager singleton] getAllEntities:[Follow class] withPredicate:predicate];
    for (Follow *follow in follows) {
        if (follow.idUserFollowed == user.idUser)
            return YES;
    }
    return NO;
}

@end
