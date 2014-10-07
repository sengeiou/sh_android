//
//  TimeLineViewController.h
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TimeLineViewController : UIViewController <UITextViewDelegate,UITableViewDataSource,UITableViewDelegate>

-(BOOL) controlRepeatedShot:(NSString *)texto;
-(BOOL) isShotMessageAlreadyInList:(NSArray *)shots withText:(NSString *) text;
-(NSString *)countCharacters:(NSUInteger) lenght;

@property(nonatomic, assign)    BOOL orientation;

@end
