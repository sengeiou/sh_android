//
//  TimeLineTableViewController.h
//  Shootr
//
//  Created by Christian Cabarrocas on 21/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Shot.h"

@protocol TimeLineTableViewControllerDelegate;

@interface TimeLineTableViewController : UITableViewController


@property (nonatomic, weak) id<TimeLineTableViewControllerDelegate> delegate;

- (void)setTableInvisible;
- (void)setTableVisible;

- (void)setFooterInvisible;
- (void)setFooterVisible;


- (void)isNecessaryMoreCells:(BOOL)moreCells;
- (void)isNecessaryRefreshCells:(BOOL)refeshTableView;

- (void)reloadShotsTable;

- (void)orientationChanged:(NSNotification *) notification;

@end

@protocol TimeLineTableViewControllerDelegate <NSObject>

- (void)changeTitleView:(UIView *) viewTitleView;
- (void)setHiddenViewNotshots:(BOOL) isNecessaryHidden;
- (void) pushToProfile:(Shot *)shotSelected;

@end