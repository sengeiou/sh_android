//
//  WritingText.m
//  Shootr
//
//  Created by Christian Cabarrocas on 18/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "WritingText.h"
#import "Fav24Colors.h"

@implementation WritingText

#pragma mark - INIT
//------------------------------------------------------------------------------
- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self basicSetup];
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)basicSetup {
    
    self.scrollsToTop = NO;
    self.clipsToBounds = YES;
    self.layer.cornerRadius = 8.0f;
    self.layer.borderWidth = 0.3;
    self.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.textContainerInset = UIEdgeInsetsMake(2, 2, 0, 0);
    
    [self addPlaceholderInTextView];
    
    [self setTextViewForShotCreation];
}

- (NSString *) getTextComment{
    return self.textComment;
}


//------------------------------------------------------------------------------
-(void)addPlaceholderInTextView{
    self.lengthTextField = 0;
    [self setPlaceholder:NSLocalizedString (@"Comment", nil)];
}

//------------------------------------------------------------------------------
- (NSInteger) getNumberOfCharacters{
    
   return self.lengthTextField;
}

//------------------------------------------------------------------------------
- (void)setTextViewForShotCreation {
        
#warning self.textComment is always 0 on viewDidLoad, isn't it? So maybe we can move the enablesReturnKeyAutomatically to the WritingTExt class init
    if (self.textComment.length == 0)
        self.enablesReturnKeyAutomatically = YES;
}

#pragma mark - PUBLIC METHODS
//------------------------------------------------------------------------------
- (void)setPlaceholder:(NSString *)placeholder {
    
    NSString *placeHolder = NSLocalizedString (@"Comment", nil);
    self.text = placeHolder;
    self.textColor = [Fav24Colors textWhatsUpViewSendShot];
    self.backgroundColor = [UIColor whiteColor];
}

#pragma mark - PUBLIC METHODS
//------------------------------------------------------------------------------
- (void)setWritingTextViewWhenCancelTouched {

    self.backgroundColor = [UIColor whiteColor];
    NSString *placeHolder = NSLocalizedString (@"Comment", nil);
    
    if ([self.text isEqualToString:placeHolder])
        self.textColor = [Fav24Colors textWhatsUpViewSendShot];
    else
        self.textColor = [UIColor blackColor];

}

//------------------------------------------------------------------------------
- (void)setWritingTextviewWhenKeyboardShown {
    
    self.textColor = [UIColor blackColor];
    self.backgroundColor = [UIColor whiteColor];
    
    NSString *placeHolder = NSLocalizedString (@"Comment", nil);
    
    if ([self.text isEqualToString:placeHolder])
        self.text = nil;
}

//------------------------------------------------------------------------------
- (void)setWritingTextViewWhenShotRepeated {
    
    self.backgroundColor = [UIColor whiteColor];
    self.textColor = [UIColor blackColor];
}

//------------------------------------------------------------------------------
- (void)setWritingTextViewWhenSendShot {
    
    self.backgroundColor = [Fav24Colors backgroundTextViewSendShot];
    [self resignFirstResponder];
    self.textColor = [Fav24Colors textTextViewSendShot];
}

@end
