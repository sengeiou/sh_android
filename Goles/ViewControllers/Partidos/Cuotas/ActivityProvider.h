//
//  ActivityProvider.h
//  Goles Messenger
//
//  Created by Luis Rollon on 24/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProviderBetViewController.h"

@interface ActivityProvider : UIActivityItemProvider <UIActivityItemSource>

@property(nonatomic, strong) Provider *mProvider;

@end

@interface APActivityIcon : UIActivity
@end
