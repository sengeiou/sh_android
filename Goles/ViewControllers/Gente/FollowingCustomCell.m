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
#import "TimeLineUtilities.h"
#import "Utils.h"
#import "DownloadImage.h"

@implementation FollowingCustomCell

- (void)awakeFromNib {
    // Initialization code
}

//------------------------------------------------------------------------------
-(void)configureCellWithUser:(User *)user inRow:(NSIndexPath *)indexPath{

    self.userName.text = user.name;
    self.nickName.text = user.userName;
	
    self.actionButton.layer.cornerRadius = 5.0f;
	
   
    if ([self checkIfImFollowingUser:user]){
         NSLog(@"nameeeFOLLOWING :%@ ", user.name);
        [self configureFollowingButton];
    }else{
         NSLog(@"nameeeFOLLERS :%@ ", user.name);
		[self configureFollowButton];
    }
    if (user.idUser == [[UserManager singleton] getUserId])
        self.actionButton.hidden = YES;
    
    self.imgPhoto = [DownloadImage downloadImageWithUrl:[NSURL URLWithString:user.photo] andUIimageView:self.imgPhoto andText:[user.name substringToIndex:1]];
    self.photobutton.tag = indexPath.row;
}

//------------------------------------------------------------------------------
- (void)addTarget:(id)target action:(SEL)action {
	
	[self.photobutton addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
}

//------------------------------------------------------------------------------
- (void)configureFollowButton {

    [self.actionButton setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
    [self.actionButton setAttributedTitle:[Utils formatTitle:NSLocalizedString(@"+ FOLLOW", nil)] forState:UIControlStateNormal];
    [self.actionButton setTitleEdgeInsets:UIEdgeInsetsMake(-1, 0, 0, 0)];
    self.actionButton.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
    self.actionButton.backgroundColor = [UIColor whiteColor];
    self.actionButton.layer.borderWidth = 1.0f;
    self.actionButton.layer.masksToBounds = YES;
}


//------------------------------------------------------------------------------
- (void)configureFollowingButton {

    [self.actionButton setTitle:NSLocalizedString(@" FOLLOWING", nil) forState:UIControlStateNormal];
    self.actionButton.backgroundColor = [Fav24Colors iosSevenBlue];
    [self.actionButton setImage:[UIImage imageNamed:@"checkWhite"] forState:UIControlStateNormal];
}

//------------------------------------------------------------------------------
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
