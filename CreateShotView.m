//
//  CreateShotView.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 22/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "CreateShotView.h"
#import "ShotManager.h"
#import "Constants.h"
#import "CoreDataParsing.h"
#import "TimeLineViewController.h"

@implementation CreateShotView

#pragma mark - INIT
//------------------------------------------------------------------------------
- (id)initWithCoder:(NSCoder *)aDecoder {
    
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self basicSetup];
    }
    return self;
}

- (void) awakeFromNib
{
    [super awakeFromNib];
    
    self.writingTextBox.delegate = self;

    [self.btnShoot setTitle:NSLocalizedString(@"Shoot", nil) forState:UIControlStateNormal];
    [self.btnShoot addTarget:self action:@selector(sendShot) forControlEvents:UIControlEventTouchUpInside];
    self.btnShoot.enabled = NO;
}

//------------------------------------------------------------------------------
-(void) basicSetup{
    
    //Listen for keyboard process close
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide) name:UIKeyboardWillHideNotification object:nil];
    
    self.previousRect = CGRectZero;
}

#pragma mark - SETTINGS
-(void)stateInitial{
    
    [self.writingTextBox addPlaceholderInTextView];
    
    self.textViewWrittenRows = 0;
    self.charactersLeft.hidden = YES;
    self.btnShoot.enabled = NO;
    self.viewToDisableTextField.hidden = YES;
}

//------------------------------------------------------------------------------
-(void)appearKeyboard{
    
    [self.writingTextBox becomeFirstResponder];
}

//------------------------------------------------------------------------------
-(void)changecolortextviewWhenSendShot{
    [self.writingTextBox setWritingTextViewWhenSendShot];
}

//------------------------------------------------------------------------------
- (void)setupUIWhenCancelOrNotConnectionOrRepeat {
    
    self.btnShoot.enabled = YES;
    self.viewToDisableTextField.hidden = YES;
    
    [self.writingTextBox setWritingTextViewWhenCancelTouched];
}

#pragma mark - KEYBOARD
//------------------------------------------------------------------------------
-(void)keyboardShow:(NSNotification*)notification{
    
    [self.writingTextBox setWritingTextviewWhenKeyboardShown];
    
    self.timelineTableView.tableView.scrollEnabled = NO;
    
    [UIView animateWithDuration:(double)[[[notification userInfo] valueForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue]
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseIn
                     animations:^{
                         self.bottomViewPositionConstraint.constant = [self getKeyboardHeight:notification];
                         [self layoutIfNeeded];
                     }completion:^(BOOL finished) {
                         
                     }];
}

//------------------------------------------------------------------------------
- (int)getKeyboardHeight:(NSNotification *)notification {
    
    NSDictionary* keyboardInfo = [notification userInfo];
    NSValue* keyboardFrameBegin = [keyboardInfo valueForKey:UIKeyboardFrameEndUserInfoKey];
    CGRect keyboardFrameBeginRect = [keyboardFrameBegin CGRectValue];
    self.sizeKeyboard = keyboardFrameBeginRect.size.height-50;
    
    return self.sizeKeyboard;
}

//------------------------------------------------------------------------------
- (void)keyboardHide:(NSNotification*)notification{
    
    [self.writingTextBox resignFirstResponder];
    [self.timelineTableView.tableView scrollRectToVisible:CGRectMake(0, 0, 1, 1) animated:NO];
    self.timelineTableView.tableView.scrollEnabled = YES;
    
    if ([self.writingTextBox getNumberOfCharacters] == 0){
        [self stateInitial];
        
    }else
        [self.writingTextBox setWritingTextViewWhenCancelTouched];
    
    if (self.textViewWrittenRows <= 2) {
        self.bottomViewHeightConstraint.constant = 75;
        self.bottomViewPositionConstraint.constant = 0.0f;
        [UIView animateWithDuration:0.25f animations:^{
            [self layoutIfNeeded];
        }];
    }else{
        self.bottomViewHeightConstraint.constant = ((self.textViewWrittenRows-2)*self.writingTextBox.font.lineHeight)+75;
        self.bottomViewPositionConstraint.constant = 0.0f;
        [UIView animateWithDuration:0.25f animations:^{
            [self layoutIfNeeded];
        }];
    }
}

//------------------------------------------------------------------------------
- (void)keyboardWillHide {
    
    [self keyboardHide:nil];
}

#pragma mark - Start Send Shot
//------------------------------------------------------------------------------
-(void)startSendShot{
    
    [self keyboardShow:nil];
    [self appearKeyboard];
}

#pragma mark - Send shot
//------------------------------------------------------------------------------
- (void)sendShot{
    
    [self.writingTextBox setWritingTextViewWhenSendShot];
    self.viewToDisableTextField.hidden = NO;
    
    [self keyboardHide:nil];

    self.charactersLeft.hidden = YES;
    self.btnShoot.enabled = NO;
    [self shotCreated];
}

//------------------------------------------------------------------------------
- (BOOL) controlRepeatedShot:(NSString *)texto{
    
    if ([self isShotMessageAlreadyInList:[[ShotManager singleton] getShotsForTimeLineBetweenHours] withText:texto])
        return YES;
    
    return NO;
}

//------------------------------------------------------------------------------
-(BOOL) isShotMessageAlreadyInList:(NSArray *)shots withText:(NSString *) text{
    
    for (Shot *shot in shots) {
        
        if ([shot.comment isEqualToString:text])
            return YES;
    }
    return NO;
}

//------------------------------------------------------------------------------
- (NSString *)controlCharactersShot:(NSString *)text{
    
    NSRange range = [text rangeOfString:@"^\\s*" options:NSRegularExpressionSearch];
    text = [text stringByReplacingCharactersInRange:range withString:@""];
    
    [self.writingTextBox setTextComment:[text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]];
    [self.writingTextBox setTextComment:[text stringByTrimmingCharactersInSet:[NSCharacterSet newlineCharacterSet]]];
    
    return [self.writingTextBox getTextComment];
}

#pragma mark - Shot creation
//------------------------------------------------------------------------------
- (void)shotCreated {
    
    if (self.writingTextBox.text != nil) {
        [self controlCharactersShot:self.writingTextBox.text];
        
        if (![self controlRepeatedShot:[self.writingTextBox getTextComment]]){
            [[NSNotificationCenter defaultCenter] postNotificationName:k_NOTIF_SHOT_SEND object:[self.writingTextBox getTextComment]];
            [self performSelectorOnMainThread:@selector(changecolortextviewWhenSendShot) withObject:nil waitUntilDone:NO];
            
        }else
            [[NSNotificationCenter defaultCenter] postNotificationName:k_NOTIF_SHOT_REPEAT object:nil];
    }
}


#pragma mark - TEXTVIEW
//------------------------------------------------------------------------------
- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    [self.writingTextBox setLengthTextField:textView.text.length - range.length +  text.length ];

    self.charactersLeft.hidden = NO;

    [self adaptViewSizeWhenWriting:textView withCharacter:text];
    
    if ([self.writingTextBox thereIsText])
        self.btnShoot.enabled = YES;
    
    else if ([self.writingTextBox thereIsText]){
        self.btnShoot.enabled = NO;
        
        self.bottomViewHeightConstraint.constant = 75;
        [UIView animateWithDuration:0.25f animations:^{
            [self layoutIfNeeded];
        }];
    }
    
    self.charactersLeft.text = [self countCharacters:[self.writingTextBox getNumberOfCharacters]];
    return ([self.writingTextBox getNumberOfCharacters] > CHARACTERS_SHOT) ? NO : YES;
}

//------------------------------------------------------------------------------
- (void)textViewDidChange:(UITextView *)textView{
    
    UITextPosition* pos = textView.endOfDocument;//explore others like beginningOfDocument if you want to customize the behaviour
    CGRect currentRect = [textView caretRectForPosition:pos];
    
    if (currentRect.origin.y < self.previousRect.origin.y)
        [self adaptViewSizeWhenDeleting:textView];
    
    self.previousRect = currentRect;
}

//------------------------------------------------------------------------------
- (void)adaptViewSizeWhenWriting:(UITextView *)textView withCharacter:(NSString *)character{
    
    self.textViewWrittenRows = round( (textView.contentSize.height - textView.textContainerInset.top - textView.textContainerInset.bottom) / textView.font.lineHeight);
    
    if (self.frame.origin.y > 64+25){ //self.navigationController.navigationBar.frame.size.height
        if (self.textViewWrittenRows > 2 && ![character isEqualToString:@"\n"] && ![character isEqualToString:@""]) {
            self.bottomViewHeightConstraint.constant = ((self.textViewWrittenRows-2)*textView.font.lineHeight)+75;
            [UIView animateWithDuration:0.25f animations:^{
                [self layoutIfNeeded];
            }];
        }else if(self.textViewWrittenRows > 1 && [character isEqualToString:@"\n"]){
            self.bottomViewHeightConstraint.constant = ((self.textViewWrittenRows-1)*textView.font.lineHeight)+75;
            [UIView animateWithDuration:0.25f animations:^{
                [self layoutIfNeeded];
            }];
        }
    }
}

//------------------------------------------------------------------------------
- (void)adaptViewSizeWhenDeleting:(UITextView *)textView{
    
    if (self.textViewWrittenRows > 2) {
        self.textViewWrittenRows = self.textViewWrittenRows-3;
        self.bottomViewHeightConstraint.constant = (self.textViewWrittenRows*textView.font.lineHeight)+75;
        [UIView animateWithDuration:0.25f animations:^{
            [self layoutIfNeeded];
        }];
    }
}

//------------------------------------------------------------------------------
- (NSString *)countCharacters:(NSUInteger) lenght{
    
    if (lenght <= CHARACTERS_SHOT){
        NSString *charLeft = [NSString stringWithFormat:@"%lu",CHARACTERS_SHOT - lenght];
        return charLeft;
    }
    return @"0";
}

@end
