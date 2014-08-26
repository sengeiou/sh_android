//
//  Fav24ExtendedNavigationController.h
//  Goles Messenger
//
//  Created by Delfín Pereiro on 03/09/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface Fav24ExtendedNavigationController : UINavigationController
@property (nonatomic) NSUInteger mPatchHeight;

-(void)hidePatch:(BOOL)hide;
-(void)setPatchView:(UIView *)view;
-(void)showProgressBar:(BOOL)show;
-(void)endProgressbar;

@end
