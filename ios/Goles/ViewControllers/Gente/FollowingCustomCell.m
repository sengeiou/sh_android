//
//  FollowingCustomCellTableViewCell.m
//  Shootr
//
//  Created by Christian Cabarrocas on 03/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FollowingCustomCell.h"
#import "UserManager.h"
#import "DownloadImage.h"

@implementation FollowingCustomCell

- (void)awakeFromNib {
    // Initialization code
}

//------------------------------------------------------------------------------
-(void)configurePeopleCellWithUser:(User *)user inRow:(NSIndexPath *)indexPath whileSearching:(BOOL)searching inPeople:(BOOL)peopleTable{
    
    self.lblNickName.text = user.userName;
    self.lblfavouriteTeamName.text = user.favoriteTeamName;

    if  (searching || !peopleTable){
        if ([[UserManager singleton] checkIfImFollowingUser:user])
            [self configureFollowingButton];
        else if (user.idUser == [[UserManager singleton] getUserId]){
            self.follow.hidden = YES;
            self.following.hidden = YES;
        }else
            [self configureFollowButton];
    }
    else{
        self.following.hidden = YES;
        self.follow.hidden = YES;
    }
    self.following.tag = indexPath.row;
    self.follow.tag = indexPath.row;

    
    self.imgPhoto = [DownloadImage downloadImageWithUrl:[NSURL URLWithString:user.photo] andUIimageView:self.imgPhoto andText:[user.name substringToIndex:1]];
    self.photobutton.tag = indexPath.row;
}

//------------------------------------------------------------------------------
- (void)addTarget:(id)target action:(SEL)action {
	
	[self.photobutton addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
}

//------------------------------------------------------------------------------
- (void)addTargetBtnFollow:(id)target action:(SEL)action {
    
    [self.follow addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];

}
//------------------------------------------------------------------------------
- (void)addTargetBtnFollowing:(id)target action:(SEL)action {
    
    [self.following addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
}

//------------------------------------------------------------------------------
- (void)configureFollowButton {
    
    self.follow.hidden = NO;
    self.following.hidden = YES;
}

//------------------------------------------------------------------------------
- (void)configureFollowingButton {
   
    self.following.hidden = NO;
    self.follow.hidden = YES;
}


@end
