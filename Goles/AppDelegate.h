//
//  AppDelegate.h
//  Goles
//
//  Created by Christian Cabarrocas on 11/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Appirater.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate,UITabBarControllerDelegate>

@property (strong, nonatomic) UIWindow *window;

//STORYBOARDS
@property (strong, nonatomic) UIStoryboard *peopleSB;
@property (strong, nonatomic) UIStoryboard *timelineSB;
@property (strong, nonatomic) UIStoryboard *meSB;
@property (strong, nonatomic) UIStoryboard *setupSB;

+ (void)removeAllCache;
- (void)registerAPNS;
- (void) setTabBarItems;

@end
