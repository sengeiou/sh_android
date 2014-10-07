//
//  AdviceFriendsViewController.h
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AppAdvice.h"
#import "Message.h"

@interface AppAdviceViewController : UIViewController

@property (nonatomic,strong)    AppAdvice       *advice;
@property (nonatomic,strong)    Message         *message;
@property (nonatomic,strong)    NSString        *sender;
@property (nonatomic,strong)    NSString        *buttonText;

@end
