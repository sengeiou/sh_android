//
//  CalendarFavoritesTableViewController.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 18/03/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Team.h"

@interface CalendarFavoritesTableViewController : UITableViewController

@property (nonatomic,strong)    UIImage     *backgroundImage;
@property (nonatomic,strong)    Team        *selectedTeam;

- (void)getTeamCalendarDidResponse:(NSArray *)array;

@end
