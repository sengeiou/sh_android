//
//  TimeLineViewController.m
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "TimeLineViewController.h"
#import "FavRestConsumer.h"
#import "ShotManager.h"
#import "User.h"
#import "Shot.h"
#import "CoreDataManager.h"
#import "ShotTableViewCell.h"
#import "Utils.h"
#import "Conection.h"
#import "Fav24Colors.h"
#import "AppDelegate.h"
#import "Constants.h"
#import "ProfileViewController.h"
#import "UIImageView+AFNetworking.h"
#import <CoreText/CoreText.h>
#import "TimeLineUtilities.h"
#import "InfoTableViewController.h"
#import "WritingText.h"
#import "WatchingMenu.h"
#import "ViewNotShots.h"
#import "TimeLineTableViewController.h"

@interface TimeLineViewController ()<UITextViewDelegate, ConectionProtocol, TimeLineTableViewControllerDelegate>{
   
    NSUInteger lengthTextField;
    BOOL returningFromBackground;
    float textViewWrittenRows;
    CGRect previousRect;

    UITapGestureRecognizer *tapTapRecognizer;
}

@property (nonatomic,strong)      TimeLineTableViewController               *timelineTableView;

@property (nonatomic,weak)      IBOutlet    WritingText                 *writingTextBox;
@property (nonatomic,weak)      IBOutlet    UIButton                    *btnShoot;
@property (nonatomic,weak)      IBOutlet    UILabel                     *charactersLeft;
@property (nonatomic,weak)      IBOutlet    ViewNotShots                *viewNotShots;
@property (nonatomic,weak)      IBOutlet    UIView                      *viewToDisableTextField;
@property (nonatomic,weak)      IBOutlet    WatchingMenu                *watchingMenu;
@property (nonatomic,weak)      IBOutlet    UIView                      *viewTextField;
@property (nonatomic,strong)    IBOutlet    UIView                      *backgroundView;
@property (nonatomic,strong)    IBOutlet    NSLayoutConstraint          *bottomViewPositionConstraint;
@property (nonatomic,strong)    IBOutlet    NSLayoutConstraint          *bottomViewHeightConstraint;
@property (nonatomic, assign)               int                         sizeKeyboard;
@property (nonatomic,strong)                NSString                    *textComment;


@end

@implementation TimeLineViewController

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = NSLocalizedString(@"Timeline", nil);
    
    self.timelineTableView =  self.childViewControllers.firstObject;
    self.timelineTableView.delegate = self;
    
    //For Alpha version
    self.watchingMenu.hidden = YES;
    
    [self.btnShoot setTitle:NSLocalizedString(@"Shoot", nil) forState:UIControlStateNormal];
    [self.viewNotShots addTargetSendShot:self action:@selector(startSendShot)];
    
    [self.writingTextBox setPlaceholder:NSLocalizedString (@"Comment", nil)];
    
    [self miscelaneousSetup];
    
    [self setNavigationBarButtons];
    [self setTextViewForShotCreation];
 
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
}

//------------------------------------------------------------------------------
- (void)returnBackground{

    //Get ping from server
    returningFromBackground = YES;
    
    [self getTimeLastSyncornized];
    
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
}

//------------------------------------------------------------------------------
-(void)getTimeLastSyncornized{
    
    NSNumber *lastSync = [[CoreDataManager singleton] getLastSyncroTime];
    double lastTime = [lastSync doubleValue];
    NSDate* date = [[NSDate dateWithTimeIntervalSince1970:lastTime] dateByAddingTimeInterval:300];
   
    if ([date compare:[NSDate date]] == NSOrderedAscending)
        self.navigationItem.titleView = [TimeLineUtilities createConectandoTitleView];
}

#pragma mark - General setup on ViewDidLoad
//------------------------------------------------------------------------------
- (void)miscelaneousSetup {
    
    lengthTextField = 0;
    previousRect = CGRectZero;
    
    [self.btnShoot addTarget:self action:@selector(sendShot) forControlEvents:UIControlEventTouchUpInside];
    
    self.btnShoot.enabled = NO;
}

//------------------------------------------------------------------------------
- (void)setLocalNotificationObservers {
    
    //Listen for show conecting process
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(returnBackground) name:k_NOTIF_BACKGROUND object:nil];
    
      //Listen to orientation changes
    [[NSNotificationCenter defaultCenter] addObserver:self  selector:@selector(orientationChanged:)    name:UIDeviceOrientationDidChangeNotification  object:nil];
    
    //Listen for keyboard process open
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardShow:) name:UIKeyboardWillShowNotification object:nil];
    
    //Listen for keyboard process close
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardHide:) name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide) name:UIKeyboardWillHideNotification object:nil];
}

//------------------------------------------------------------------------------
-(void)viewWillAppear:(BOOL)animated{

    [super viewWillAppear:animated];

    [self updateCurrentTitleView];

     self.navigationItem.titleView.hidden = YES;
    
    [self setLocalNotificationObservers];
}

//------------------------------------------------------------------------------
-(void)viewDidAppear:(BOOL)animated {

    self.navigationItem.titleView.hidden = NO;
}

//------------------------------------------------------------------------------
-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

//------------------------------------------------------------------------------
- (void)setNavigationBarButtons {

    UIBarButtonItem *btnSearch = [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:self action:@selector(search)];
    self.navigationItem.leftBarButtonItem = btnSearch;
    
    //Info button
    UIButton *button = [UIButton buttonWithType:UIButtonTypeInfoLight];
    [button addTarget:self action:@selector(infoButton) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *infoButton = [[UIBarButtonItem alloc] initWithCustomView:button];
    self.navigationItem.rightBarButtonItem = infoButton;
}

//------------------------------------------------------------------------------
- (void)setTextViewForShotCreation {

    self.writingTextBox.delegate = self;
#warning self.textComment is always 0 on viewDidLoad, isn't it? So maybe we can move the enablesReturnKeyAutomatically to the WritingTExt class init
    if (self.textComment.length == 0)
        self.writingTextBox.enablesReturnKeyAutomatically = YES;

}

#pragma mark - FUTURE METHODS
//------------------------------------------------------------------------------
- (void) search{
    
}

//------------------------------------------------------------------------------
- (void) infoButton{
    
//    InfoTableViewController *infoTVC = [[InfoTableViewController alloc] init];
//    [self.navigationController pushViewController:infoTVC animated:YES];
    
}

//------------------------------------------------------------------------------
- (void) watching{
    
}

#pragma mark - Send shot
//------------------------------------------------------------------------------
- (void)sendShot{

    [self.writingTextBox setWritingTextViewWhenSendShot];
    self.viewToDisableTextField.hidden = NO;

    self.orientation = NO;
    [self keyboardHide:nil];

    self.charactersLeft.hidden = YES;
    self.btnShoot.enabled = NO;
    self.navigationItem.titleView = [TimeLineUtilities createEnviandoTitleView];
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:YES];
}

#pragma mark - Start Send Shot
//------------------------------------------------------------------------------
-(void)startSendShot{
    
    [self keyboardShow:nil];
    [self.writingTextBox becomeFirstResponder];
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

#pragma mark - Change NavigationBar
//------------------------------------------------------------------------------
-(void)changeStateViewNavBar{
    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
    [self.timelineTableView setFooterInvisible];
}

//------------------------------------------------------------------------------
-(void)changeStateActualizandoViewNavBar{
    self.navigationItem.titleView = [TimeLineUtilities createActualizandoTitleView];
    [self.timelineTableView setFooterInvisible];
}

#pragma mark - RESPONSE METHODS
#pragma mark -
#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status andRefresh:(BOOL)refresh withShot:(BOOL)isShot{
    
    if (status & !isShot)
        [self performSelectorOnMainThread:@selector(changeStateActualizandoViewNavBar) withObject:nil waitUntilDone:NO];
    if (isShot){
        self.orientation = NO;
        [self shotCreated];
        [self performSelectorOnMainThread:@selector(changecolortextview) withObject:nil waitUntilDone:NO];
    }else if(refresh)
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
    else if(!status && !refresh && !isShot && !returningFromBackground){
        //        self.orientation = NO;
        [self performSelectorOnMainThread:@selector(cleanViewWhenNotConnection) withObject:nil waitUntilDone:YES];
    }
//    } else
//        [self performSelectorOnMainThread:@selector(removePullToRefresh) withObject:nil waitUntilDone:YES];
    
    returningFromBackground = NO;
    
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && [entityClass isSubclassOfClass:[Shot class]]){
        [self performSelectorOnMainThread:@selector(callReloadTable) withObject:nil waitUntilDone:NO];
        [self.timelineTableView isNecessaryMoreCells:YES];
        //moreCells = YES;
    }else if (!refresh){
       // moreCells = NO;
       // refreshTable = NO;
        [self.timelineTableView isNecessaryMoreCells:NO];
        [self.timelineTableView isNecessaryRefreshCells:NO];
    }
    [self performSelectorOnMainThread:@selector(changeViewTitleMainThread) withObject:nil waitUntilDone:NO];
}

-(void)callReloadTable{
    [self.timelineTableView reloadShotsTable];
}

-(void)changeViewTitleMainThread{
    [self performSelector:@selector(changeStateViewNavBar) withObject:nil afterDelay:0.5];
}

//------------------------------------------------------------------------------
-(void)changecolortextview{
    self.writingTextBox.textColor = [UIColor lightGrayColor];
}

#pragma mark - ShotCreationProtocol response
//------------------------------------------------------------------------------
- (void)createShotResponseWithStatus:(BOOL)status andError:(NSError *)error {
    
    if (status && !error){
        self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
        lengthTextField = 0;
        [self.writingTextBox setPlaceholder:NSLocalizedString (@"Comment", nil)];
        textViewWrittenRows = 0;
        self.charactersLeft.hidden = YES;
        [self performSelectorOnMainThread:@selector(callReloadTable) withObject:nil waitUntilDone:NO];

        self.btnShoot.enabled = NO;
        [self keyboardHide:nil];
        self.viewToDisableTextField.hidden = YES;

    }else if (error){
        [self performSelectorOnMainThread:@selector(showAlertcanNotCreateShot) withObject:nil waitUntilDone:NO];
    }
}

#pragma mark - Response utilities methods
//------------------------------------------------------------------------------
-(void)showAlertcanNotCreateShot{

    UIAlertController * alert=   [UIAlertController
                                  alertControllerWithTitle:NSLocalizedString(@"Shot Not Posted", nil)
                                  message:NSLocalizedString(@"Connection timed out.", nil)
                                  preferredStyle:UIAlertControllerStyleAlert];

    UIAlertAction* retry = [UIAlertAction
                         actionWithTitle:NSLocalizedString(@"Retry", nil)
                         style:UIAlertActionStyleDefault
                         handler:^(UIAlertAction * action)
                         {
                             [alert dismissViewControllerAnimated:YES completion:nil];
                             [self sendShot];
                         }];
    
    UIAlertAction* cancel = [UIAlertAction
                             actionWithTitle:NSLocalizedString(@"Cancel", nil)
                             style:UIAlertActionStyleDefault
                             handler:^(UIAlertAction * action)
                             {
                                 [alert dismissViewControllerAnimated:YES completion:nil];
                                  [self setupUIWhenCancelOrNotConnection];
                             }];
    [alert addAction:retry];
    [alert addAction:cancel];

    [self presentViewController:alert animated:YES completion:nil];
    
    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
}

//------------------------------------------------------------------------------
- (void)cleanViewWhenNotConnection{
    
//    [self keyboardHide:nil];
    [self setupUIWhenCancelOrNotConnection];
    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
    
}

#pragma mark - Shot creation
//------------------------------------------------------------------------------
- (void)shotCreated {
    
    [self controlCharactersShot:self.writingTextBox.text];

    if (![self controlRepeatedShot:self.textComment])
        
        [[ShotManager singleton] createShotWithComment:self.textComment andDelegate:self];
    else
        [self performSelectorOnMainThread:@selector(showAlert) withObject:nil waitUntilDone:NO];
    
}

//------------------------------------------------------------------------------
- (void)showAlert{
    UIAlertController * alert=   [UIAlertController
                                  alertControllerWithTitle:NSLocalizedString(@"Shot Not Posted", nil)
                                  message:NSLocalizedString(@"Whoops! You already shot that.", nil)
                                  preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* ok = [UIAlertAction
                            actionWithTitle:NSLocalizedString(@"OK", nil)
                            style:UIAlertActionStyleDefault
                            handler:^(UIAlertAction * action)
                            {
                                [alert dismissViewControllerAnimated:YES completion:nil];
                                [self.writingTextBox setWritingTextViewWhenShotRepeated];
                                self.btnShoot.enabled = YES;
                                self.viewToDisableTextField.hidden = YES;
                            }];
    
    [alert addAction:ok];
    
    [self presentViewController:alert animated:YES completion:nil];
}

//------------------------------------------------------------------------------
- (NSString *)controlCharactersShot:(NSString *)text{
    
    NSRange range = [text rangeOfString:@"^\\s*" options:NSRegularExpressionSearch];
    text = [text stringByReplacingCharactersInRange:range withString:@""];
    
    self.textComment = [text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    self.textComment = [text stringByTrimmingCharactersInSet:[NSCharacterSet newlineCharacterSet]];
    
    return self.textComment;
}

#pragma mark - Reload methods
//------------------------------------------------------------------------------
- (void)setupUIWhenCancelOrNotConnection {

    self.btnShoot.enabled = YES;
    self.viewToDisableTextField.hidden = YES;
    self.orientation = NO;
    
    [self.writingTextBox setWritingTextViewWhenCancelTouched];

}

#pragma mark - KEYBOARD
//------------------------------------------------------------------------------
-(void)keyboardShow:(NSNotification*)notification{
    
    [self.writingTextBox setWritingTextviewWhenKeyboardShown];
    
    [self darkenBackgroundView];


    self.timelineTableView.tableView.scrollEnabled = NO;

    [UIView animateWithDuration:(double)[[[notification userInfo] valueForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue]
                              delay:0.0
                            options:UIViewAnimationOptionCurveEaseIn
                         animations:^{
                             self.bottomViewPositionConstraint.constant = [self getKeyboardHeight:notification];
                             [self.view layoutIfNeeded];
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
- (void)darkenBackgroundView {
    
    self.backgroundView.hidden = NO;
    
    if (tapTapRecognizer == nil){
        tapTapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(keyboardHide:)];
        [self.backgroundView addGestureRecognizer:tapTapRecognizer];
        self.orientation = NO;
    }
}

//------------------------------------------------------------------------------
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    
    if (lengthTextField == 0){
         [self.writingTextBox setPlaceholder:NSLocalizedString (@"Comment", nil)];
    }
    self.orientation = NO;
}

//------------------------------------------------------------------------------
- (void)keyboardWillHide {

    [self keyboardHide:nil];
}

//------------------------------------------------------------------------------
- (void)keyboardHide:(NSNotification*)notification{

    
    if (!self.orientation){

        [self.writingTextBox resignFirstResponder];

        
        self.backgroundView.hidden = YES;
        
        [self.timelineTableView.tableView scrollRectToVisible:CGRectMake(0, 0, 1, 1) animated:NO];
        self.timelineTableView.tableView.scrollEnabled = YES;
            
        if (lengthTextField == 0){
            [self.writingTextBox setPlaceholder:NSLocalizedString (@"Comment", nil)];
            
            textViewWrittenRows = 0;
            self.charactersLeft.hidden = YES;
        }else
            self.writingTextBox.textColor = [UIColor blackColor];

        if (textViewWrittenRows <= 2) {
            self.bottomViewHeightConstraint.constant = 75;
            self.bottomViewPositionConstraint.constant = 0.0f;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        }else{
            self.bottomViewHeightConstraint.constant = ((textViewWrittenRows-2)*self.writingTextBox.font.lineHeight)+75;
            self.bottomViewPositionConstraint.constant = 0.0f;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        
        }
    }
}

#pragma mark - TEXTVIEW

//------------------------------------------------------------------------------
- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    //NSString* result = [self controlCharactersShot:self.txtView.text];

    lengthTextField = textView.text.length - range.length + text.length;
    self.charactersLeft.hidden = NO;

//    if (![result isEqualToString:@""] && lengthTextField >= 1)
    if (lengthTextField >= 1)
        self.btnShoot.enabled = YES;
    else
        self.btnShoot.enabled = NO;

    [self adaptViewSizeWhenWriting:textView withCharacter:text];

    if (lengthTextField == 0){
		self.bottomViewHeightConstraint.constant = 75;
		[UIView animateWithDuration:0.25f animations:^{
			[self.view layoutIfNeeded];
		}];
    }
    
    self.charactersLeft.text = [self countCharacters:lengthTextField];
    return (lengthTextField > CHARACTERS_SHOT) ? NO : YES;
}

//------------------------------------------------------------------------------
- (void)textViewDidChange:(UITextView *)textView{
    
    UITextPosition* pos = textView.endOfDocument;//explore others like beginningOfDocument if you want to customize the behaviour
    CGRect currentRect = [textView caretRectForPosition:pos];

    if (currentRect.origin.y < previousRect.origin.y)
        [self adaptViewSizeWhenDeleting:textView];
   
    previousRect = currentRect;
    
}

//------------------------------------------------------------------------------
- (void)adaptViewSizeWhenWriting:(UITextView *)textView withCharacter:(NSString *)character{

	textViewWrittenRows = round( (textView.contentSize.height - textView.textContainerInset.top - textView.textContainerInset.bottom) / textView.font.lineHeight);

    if (self.viewTextField.frame.origin.y > self.navigationController.navigationBar.frame.size.height+25){
        if (textViewWrittenRows > 2 && ![character isEqualToString:@"\n"] && ![character isEqualToString:@""]) {
            self.bottomViewHeightConstraint.constant = ((textViewWrittenRows-2)*textView.font.lineHeight)+75;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        }else if(textViewWrittenRows > 1 && [character isEqualToString:@"\n"]){
            self.bottomViewHeightConstraint.constant = ((textViewWrittenRows-1)*textView.font.lineHeight)+75;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        }
    }
}

//------------------------------------------------------------------------------
- (void)adaptViewSizeWhenDeleting:(UITextView *)textView{
    
    if (textViewWrittenRows > 2) {
        textViewWrittenRows = textViewWrittenRows-3;
        self.bottomViewHeightConstraint.constant = (textViewWrittenRows*textView.font.lineHeight)+75;
        [UIView animateWithDuration:0.25f animations:^{
            [self.view layoutIfNeeded];
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

#pragma mark - Orientation methods

//------------------------------------------------------------------------------
- (void)restrictRotation:(BOOL) restriction {
    
    AppDelegate* appDelegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    appDelegate.restrictRotation = restriction;
    self.orientation = YES;
}

//------------------------------------------------------------------------------
- (void)orientationChanged:(NSNotification *)notification{

    [self updateCurrentTitleView];
    
    [self restrictRotation:NO];
    
    [self.timelineTableView orientationChanged:notification];
}

#pragma mark - Title View Get
//------------------------------------------------------------------------------
- (void)updateCurrentTitleView {

    if (self.navigationItem.titleView.subviews.count > 1) {
        UILabel *actualLabel = [self.navigationItem.titleView.subviews objectAtIndex:1];
        self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleViewWithText:actualLabel.text];
        
    }else
        self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
}
#pragma mark - Protocol Timeline

//------------------------------------------------------------------------------
- (void)changeTitleView:(UIView *) viewTitleView{
    self.navigationItem.titleView = viewTitleView;
}

//------------------------------------------------------------------------------
- (void)setHiddenViewNotshots:(BOOL) isNecessaryHidden{
    
    if (isNecessaryHidden)
        self.viewNotShots.hidden = YES;
}

//------------------------------------------------------------------------------
- (void) pushToProfile:(Shot *)shotSelected{
   
    AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
    
    ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
    
    profileVC.selectedUser = shotSelected.user;
    
    [self.navigationController pushViewController:profileVC animated:YES];
}

@end
