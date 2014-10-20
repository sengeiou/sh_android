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
//    
//    [self.follow setAttributedTitle:[Utils formatTitle:NSLocalizedString(@"+ FOLLOW", nil)] forState:UIControlStateNormal];
//    self.follow.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
//    self.follow.layer.borderWidth = 1.0f;
//    self.follow.layer.masksToBounds = YES;
}

//------------------------------------------------------------------------------
- (void)configureFollowingButton {
   
    self.following.hidden = NO;
    self.follow.hidden = YES;
    
//    [self.following setImage:[UIImage imageNamed:@"Icon_Following"] forState:UIControlStateNormal];
//    [self.following setImage:[UIImage imageNamed:@"Icon_Following_Pressed"] forState:UIControlStateHighlighted];
//    self.following.tintColor = [Fav24Colors iosSevenBlue];

//    [self.following setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
//   
//    NSMutableAttributedString *buttonString = [[NSMutableAttributedString alloc] initWithString:NSLocalizedString(@" FOLLOWING", nil)];
//    [buttonString addAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:13], NSForegroundColorAttributeName:[UIColor whiteColor]} range:NSMakeRange(0, buttonString.length)];
//    [self.following setAttributedTitle:buttonString forState:UIControlStateNormal];
//   
//    self.following.layer.borderWidth = 0.0f;
//    self.following.layer.masksToBounds = NO;
//    
//    [self.following setImage:[UIImage imageNamed:@"checkWhite"] forState:UIControlStateNormal];
}


@end
