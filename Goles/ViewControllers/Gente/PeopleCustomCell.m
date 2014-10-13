//
//  PeopleCustomCell.m
//  Shootr
//
//  Created by Christian Cabarrocas on 06/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "PeopleCustomCell.h"
//#import "UIImageView+AFNetworking.h"
//#import "Fav24Colors.h"
//#import <QuartzCore/QuartzCore.h>
//#import "TimeLineUtilities.h"
#import "DownloadImage.h"

@implementation PeopleCustomCell

- (void)awakeFromNib {
    // Initialization code
}

//------------------------------------------------------------------------------
-(void)configureCellWithUser:(User *)user inRow:(NSIndexPath *)indexPath{
    
    self.userName.text = user.name;
    self.nickName.text = user.favouriteTeamName;

    self.imgPhoto = [DownloadImage downloadImageWithUrl:[NSURL URLWithString:user.photo] andUIimageView:self.imgPhoto andText:[user.name substringToIndex:1]];

    self.photobutton.tag = indexPath.row;

}
//------------------------------------------------------------------------------
- (void)addTarget:(id)target action:(SEL)action {
    
    [self.photobutton addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
}

@end
