//
//  WritingText.h
//  Shootr
//
//  Created by Christian Cabarrocas on 18/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WritingText : UITextView

@property (nonatomic,strong) NSString       *placeholder;


- (void)setWritingTextViewWhenCancelTouched;
- (void)setWritingTextviewWhenKeyboardShown;
- (void)setWritingTextViewWhenShotRepeated;
- (void)setWritingTextViewWhenSendShot;

@end
