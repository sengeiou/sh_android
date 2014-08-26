//
//  InformationToBetViewController.h
//  Goles Messenger
//
//  Created by Luis Rollon on 20/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MessageUI/MessageUI.h>
#import <MessageUI/MFMailComposeViewController.h>
#import "ProviderBetViewController.h"

@interface InformationToBetViewController : UIViewController <MFMailComposeViewControllerDelegate>
@property (strong, nonatomic)         IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic)           IBOutlet UITextView *firstText;
@property (weak, nonatomic)           IBOutlet UITextView *secondText;

@property (nonatomic, strong)         Provider *mProvider;

@end
