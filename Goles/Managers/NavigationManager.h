//
//  NavigationManager.h
//  Goles Messenger
//
//  Created by Delfin Pereiro on 02/12/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NavigationManager : NSObject

+ (NavigationManager *)singleton;
+ (NavigationManager *)sharedInstance;

//-(DetailMatchFormViewController *)pushMatchDetailViewInNavigation:(UINavigationController *)navigationController;


@end
