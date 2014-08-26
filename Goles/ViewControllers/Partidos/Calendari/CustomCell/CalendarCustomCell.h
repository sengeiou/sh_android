//
//  CalendarCustomCell.h
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 20/03/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CalendarCustomCell : UITableViewCell

- (void)configCellWithData:(NSDictionary *)matchData forLocalTeam:(BOOL)isLocal;

@end
