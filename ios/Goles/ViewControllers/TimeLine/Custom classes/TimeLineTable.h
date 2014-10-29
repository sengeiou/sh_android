//
//  TimeLineTable.h
//  Shootr
//
//  Created by Christian Cabarrocas on 20/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TimeLineTable : UITableViewController

- (void)setTableInvisible;
- (void)setTableVisible;

- (void)setFooterInvisible;
- (void)setFooterVisible;

- (void)reloadShotsTable:(id)sender;

@end
