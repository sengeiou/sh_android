//
//  InfoCustomCellTableViewCell.m
//  Shootr
//
//  Created by Christian Cabarrocas on 11/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "InfoCustomCell.h"
#import "DownloadImage.h"
#import "CoreDataManager.h"

@implementation InfoCustomCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)configureInfoCellWithUser:(User *)user inRow:(NSIndexPath *)indexPath  {
    
    self.userName.text = user.userName;
    //self.lblFavouriteTeamName.text = user.favoriteTeamName;
   
    self.wacthing.text = @"Watching";
    
    self.imgPhoto = [DownloadImage downloadImageWithUrl:[NSURL URLWithString:user.photo] andUIimageView:self.imgPhoto andText:[user.name substringToIndex:1]];
    self.photobutton.tag = indexPath.row;
    
    
}

@end
