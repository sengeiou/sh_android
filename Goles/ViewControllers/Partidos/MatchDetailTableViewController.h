//
//  MatchDetailTableViewController.h
//  Goles
//
//  Created by Christian Cabarrocas on 12/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Match.h"
#import "FavRestConsumer.h"

@interface MatchDetailTableViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, FavRestConsumerEventsProtocol>

@property (nonatomic, strong) Match *selectedMacth;


-(void)updateEventsMatchList:(Match *)match;

@end
