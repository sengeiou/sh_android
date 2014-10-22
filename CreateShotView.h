//
//  CreateShotView.h
//  Shootr
//
//  Created by Maria Teresa Bañuls on 22/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WritingText.h"
#import "TimeLineTableViewController.h"

@interface CreateShotView : UIView <UITextViewDelegate>


@property (nonatomic,weak)      IBOutlet    WritingText                     *writingTextBox;
@property (nonatomic,weak)      IBOutlet    UIButton                        *btnShoot;
@property (nonatomic,weak)      IBOutlet    UILabel                         *charactersLeft;
@property (nonatomic,weak)      IBOutlet    UIView                          *viewToDisableTextField;

@property (nonatomic,strong)    IBOutlet    NSLayoutConstraint              *bottomViewPositionConstraint;
@property (nonatomic,strong)    IBOutlet    NSLayoutConstraint              *bottomViewHeightConstraint;

@property (nonatomic,strong)                NSString                        *textComment;
@property (nonatomic,assign)                NSUInteger                      lengthTextField;
@property (nonatomic,assign)                float                           textViewWrittenRows;
@property (nonatomic,assign)                CGRect                          previousRect;
@property (nonatomic, assign)               int                             sizeKeyboard;

@property (nonatomic,strong)                TimeLineTableViewController     *timelineTableView;

//Funcionality
- (BOOL) controlRepeatedShot:(NSString *)texto;
- (BOOL) isShotMessageAlreadyInList:(NSArray *)shots withText:(NSString *) text;
- (void)sendShot;
- (NSString *)controlCharactersShot:(NSString *)text;
- (NSString *)countCharacters:(NSUInteger) lenght;
- (void)startSendShot;
- (void)shotCreated;

//Settings
- (void) appearKeyboard;
- (void)stateInitial;
- (void)setupUIWhenCancelOrNotConnectionOrRepeat;
- (void)keyboardHide:(NSNotification*)notification;
- (void)keyboardShow:(NSNotification*)notification;
- (NSUInteger) numberOfCharactersInTextView;
- (void)addPlaceHolder;

@end
