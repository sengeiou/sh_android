//
//  FixtureViewController.h
//  iGoles
//
//  Created by Administrador on 27/08/10.
//  Copyright 2010 Fav24. All rights reserved.
//

#import "Tournament.h"

@class WSClient;

@interface CalendarFixtureViewController : UITableViewController  {
    
    WSClient *client;
}

@property (nonatomic, strong)       Tournament      *mTournament;
@property (nonatomic,strong)        UIImage         *backgroundImage;

@end

