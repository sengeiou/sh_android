//
//  LineUpsViewController.h
//  Goles Messenger
//
//  Created by Kuro on 4/21/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Match.h"

@interface LineUpsViewController : UITableViewController <UIActionSheetDelegate>

@property (nonatomic, strong) Match                 *mMatch;

@end
