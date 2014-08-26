//
//  FAVFormContainerViewController.h
//  RefactorTest
//
//  Created by Delfín Pereiro on 06/05/13.
//  Copyright (c) 2013 fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FAVFormContainerViewController : UIViewController

@property (nonatomic, assign)           CGFloat             mInsertionHeight;
@property (nonatomic, retain) IBOutlet  UIScrollView        *mScrollView;

//******************************************************************************
// Add a New view controller to the view controllers container pool
// and set its parent container to self
//******************************************************************************
-(void)AddFormOption:(UIViewController *)viewController toView:(UIView *)view;

//******************************************************************************
// Remove a view Controller from the view controllers container pool
// and remove its view from its superview
// CACTUS: have to be tested....
//******************************************************************************
-(void)RemoveFormOption:(UIViewController *)viewController fromView:(UIView *)view;

//******************************************************************************
// In case one of the view controllers had changed  its frame size since it was inserted
// this method reposition all the view controllers view present in the view 
// controllers pool and adapts the scroll view content Size
//******************************************************************************
-(void)UpdateContainerContentSize;

@end
