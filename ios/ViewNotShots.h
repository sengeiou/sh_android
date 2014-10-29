//
//  ViewNotShots.h
//  Shootr
//
//  Created by bamate on 20/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewNotShots : UIView

@property (nonatomic,weak)      IBOutlet    UIButton                    *startShootingFirstTime;

- (void)addTargetSendShot:(id)target action:(SEL)action;
- (void)setNoShotsViewInvisible;
- (void)setNoShotsViewVisible;

@end
