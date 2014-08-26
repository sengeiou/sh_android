//
//  BetWebViewController.h
//  Goles Messenger
//
//  Created by Luis Rollon on 03/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProviderBetViewController.h"

@interface BetWebViewController : UIViewController<UIAlertViewDelegate>

@property (strong, nonatomic) UITableViewController *mProviderBetVC;
@property (strong, nonatomic) NSURLRequest *mWebToShow;
//@property (strong, nonatomic) NSURL           *oddURL;
@property (strong, nonatomic) Provider *mProvider;

- (id)initWithProvider:(Provider*)provider andURL:(NSURL *)url andGoToRegistry:(BOOL)goToRegistry;
//- (id)initWithURL:(NSURL*)URL;

@end
