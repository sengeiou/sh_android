//
//  ConfigTeamViewController.h
//  iGoles
//
//  Created by mac II on 09/07/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "Team.h"

@interface ConfigTeamViewController : UITableViewController {}

@property (nonatomic, strong) Team *mOriginalTeamData;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil passedData:(Team *)dataArgs titleButton:(NSString *)ttB;


@end
