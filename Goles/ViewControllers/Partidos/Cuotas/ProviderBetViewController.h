//
//  NewProviderBetViewController.h
//  Goles Messenger
//
//  Created by Luis Rollon on 06/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FAVFormContainerViewController.h"
#import "Match.h"
#import "BetDescriptionCell.h"
#import "MoneyBetCell.h"
#import "FavRestConsumer.h"

@interface ProviderBetViewController : FAVFormContainerViewController <UIPickerViewDataSource, UIPickerViewDelegate, UITableViewDataSource, UITableViewDelegate,FavRestConsumerCuotasProtocol, UIAlertViewDelegate>

@property (nonatomic, strong) Match                  *mMatch;
@property (nonatomic, strong) Provider               *mProvider;
@property (nonatomic, strong) NSArray                *mCuotasArray;

@end
