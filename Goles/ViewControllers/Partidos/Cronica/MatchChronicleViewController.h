//
//  MatchChronicleViewController.h
//  Goles Messenger
//
//  Created by Delfín Pereiro on 27/09/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Match.h"

@interface MatchChronicleViewController : UITableViewController 

- (void)updateEventsMatchList:(Match *)match;

@property (nonatomic,strong) Match                  *mMatch;

@end
