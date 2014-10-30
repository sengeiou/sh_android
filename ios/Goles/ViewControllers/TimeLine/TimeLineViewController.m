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
#import "Conection.h"
#import "AppDelegate.h"
#import "ProfileViewController.h"
#import "InfoTableViewController.h"
#import "WatchingMenu.h"
#import "ViewNotShots.h"
#import "TimeLineTableViewController.h"
#import "CreateShotView.h"

@interface TimeLineViewController ()<UITextViewDelegate, ConectionProtocol, TimeLineTableViewControllerDelegate>

@property (nonatomic,weak)      IBOutlet    ViewNotShots                    *viewNotShots;
@property (nonatomic,weak)      IBOutlet    WatchingMenu                    *watchingMenu;
@property (nonatomic,weak)      IBOutlet    CreateShotView                  *viewTextField;
@property (nonatomic,strong)    IBOutlet    UIView                          *backgroundView;

@property (nonatomic,strong)                TimeLineTableViewController     *timelineTableView;
@property (nonatomic, assign)               BOOL                            returningFromBackground;
@property (nonatomic,strong)                UITapGestureRecognizer          *tapTapRecognizer;

@property (nonatomic, assign)               BOOL                            keyboardIsVisible;
@property (nonatomic, assign)               BOOL                            dragged;


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

    self.keyboardIsVisible = NO;
    
    [self.viewNotShots addTargetSendShot:self action:@selector(initSendShot)];
    
    [self setNavigationBarButtons];
 
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
    
    
    [[FavRestConsumer sharedInstance] getUserNextMatchWithDelegate:self];
    [[FavRestConsumer sharedInstance] getAllWatchWithDelegate:self];
}

//------------------------------------------------------------------------------
-(void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    
    [self updateCurrentTitleView];
    
    [self setLocalNotificationObservers];
}

//------------------------------------------------------------------------------
-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

//------------------------------------------------------------------------------
-(void)initSendShot{
    [self.viewTextField startSendShot];
}

//------------------------------------------------------------------------------
- (void)returnBackground{

    //Get ping from server
    self.returningFromBackground = YES;
    
    [self getTimeLastSyncornized];
    
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
}

//------------------------------------------------------------------------------
-(void)getTimeLastSyncornized{
    
    NSNumber *lastSync = [[CoreDataManager singleton] getLastSyncroTime];
    double lastTime = [lastSync doubleValue];
    NSDate* date = [[NSDate dateWithTimeIntervalSince1970:lastTime] dateByAddingTimeInterval:300];
   
    if ([date compare:[NSDate date]] == NSOrderedAscending){
        self.title = NSLocalizedString (@"Checking for Shots...", nil);
        self.tabBarItem.title = @"Timeline";
    }
}

//------------------------------------------------------------------------------
- (void)setLocalNotificationObservers {
    
    //Listen for keyboard process close
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hiddenKeyboard) name:UIKeyboardWillHideNotification object:nil];
   
    //Listen for keyboard process open
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showKeyboard:) name:UIKeyboardWillShowNotification object:nil];
    

    //Listen for show conecting process
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(returnBackground) name:k_NOTIF_BACKGROUND object:nil];
    
    //Listen for show alert show repeat
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showAlert) name:k_NOTIF_SHOT_REPEAT object:nil];

    //Listen for show alert show send
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(sendShotByNotification:) name:k_NOTIF_SHOT_SEND object:nil];

      //Listen to orientation changes
   // [[NSNotificationCenter defaultCenter] addObserver:self  selector:@selector(orientationUpdates:)    name:UIDeviceOrientationDidChangeNotification  object:nil];
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


#pragma mark - FUTURE METHODS
//------------------------------------------------------------------------------
- (void) search{
    
}

//------------------------------------------------------------------------------
- (void) infoButton{
    
    InfoTableViewController *infoTVC = [self.storyboard instantiateViewControllerWithIdentifier:@"infoTVC"];
    [self.navigationController pushViewController:infoTVC animated:YES];
    
}

#pragma mark - Change NavigationBar
//------------------------------------------------------------------------------
-(void)changeStateViewNavBar{
    
    [self changeTitleView];
    [self.timelineTableView setFooterInvisible];
}

//------------------------------------------------------------------------------
-(void)changeStateCheckingViewNavBar{

    self.title = NSLocalizedString (@"Checking for Shots...", nil);
    self.tabBarItem.title = @"Timeline";
    [self.timelineTableView setFooterInvisible];
}

#pragma mark - RESPONSE METHODS
#pragma mark -
#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status andRefresh:(BOOL)refresh withShot:(BOOL)isShot{
    
    if (status & !isShot)
        [self performSelectorOnMainThread:@selector(changeStateCheckingViewNavBar) withObject:nil waitUntilDone:NO];
    
    if (isShot){
        
        self.orientation = NO;
        [self.viewTextField shotCreated];
   
    }else if(refresh)
        
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];

    else if(!status && !refresh && !isShot && !self.returningFromBackground)
        
        [self performSelectorOnMainThread:@selector(cleanViewWhenNotConnection) withObject:nil waitUntilDone:YES];

    self.returningFromBackground = NO;
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && [entityClass isSubclassOfClass:[Shot class]])
        [self.timelineTableView isNecessaryMoreCells:YES];

    else if (!refresh){

        [self.timelineTableView isNecessaryMoreCells:NO];
        [self.timelineTableView isNecessaryRefreshCells:NO];
    }

    [self performSelectorOnMainThread:@selector(changeViewTitleMainThread) withObject:nil waitUntilDone:NO];
}

#pragma mark - ShotCreationProtocol response
//------------------------------------------------------------------------------
- (void)createShotResponseWithStatus:(BOOL)status andError:(NSError *)error {
    
    if (status && !error){
        [self changeTitleView];
        [self.viewTextField stateInitial];
        [self.viewTextField keyboardHide:nil];

    }else if (!status || error)
        [self performSelectorOnMainThread:@selector(showAlertcanNotCreateShot) withObject:nil waitUntilDone:NO];
    
}

#pragma mark - Response utilities methods

//------------------------------------------------------------------------------
-(void)changeViewTitleMainThread{
    
    [self performSelector:@selector(changeStateViewNavBar) withObject:nil afterDelay:0];
}

//------------------------------------------------------------------------------
-(void)showAlertcanNotCreateShot{
    
    self.orientation = NO;
    
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
                             [self retrySendShot];
                         }];
    
    UIAlertAction* cancel = [UIAlertAction
                             actionWithTitle:NSLocalizedString(@"Cancel", nil)
                             style:UIAlertActionStyleCancel
                             handler:^(UIAlertAction * action)
                             {
                                 [alert dismissViewControllerAnimated:YES completion:nil];
                                 [self.viewTextField setupUIWhenCancelOrNotConnectionOrRepeat];
                             }];
    [alert addAction:retry];
    [alert addAction:cancel];

    [self presentViewController:alert animated:YES completion:nil];
    [self changeTitleView];
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
                             [self.viewTextField setupUIWhenCancelOrNotConnectionOrRepeat];
                            
                         }];
    
    [alert addAction:ok];
    
    [self presentViewController:alert animated:YES completion:nil];
}

//------------------------------------------------------------------------------
-(void)sendShotByNotification:(NSNotification *)notification{
    
    self.orientation = NO;
    [self hiddenKeyboard];
    
    self.title = NSLocalizedString (@"Shooting...", nil);

    [[ShotManager singleton] createShotWithComment:[notification object] andDelegate:self];
}

//------------------------------------------------------------------------------
-(void)retrySendShot{
    [self.viewTextField sendShot];
    self.title = NSLocalizedString (@"Shooting...", nil);
    self.tabBarItem.title = @"Timeline";

    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:YES];
}

//------------------------------------------------------------------------------
- (void)cleanViewWhenNotConnection{
    
    [self.viewTextField setupUIWhenCancelOrNotConnectionOrRepeat];
    [self changeTitleView];
}

//------------------------------------------------------------------------------
- (void)darkenBackgroundView {

    if (!self.keyboardIsVisible) {
        self.backgroundView.hidden = NO;
        [self.backgroundView setAlpha:0.0];
        [UIView animateWithDuration:1.5
                              delay:0.0
                            options:UIViewAnimationOptionCurveEaseIn // See other options
                         animations:^{
                             [self.backgroundView setAlpha:0.5];
                         }
                         completion:^(BOOL finished) {
                             // Completion Block
                         }];
        
        if (self.tapTapRecognizer == nil){
            self.tapTapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(hiddenKeyboard)];
            [self.backgroundView addGestureRecognizer:self.tapTapRecognizer];
            self.orientation = NO;
        }
        
        self.keyboardIsVisible = YES;
    }

}

//------------------------------------------------------------------------------
-(void)hiddenKeyboard{
    
    if (!self.orientation){
        if (self.keyboardIsVisible) {
            [self.viewTextField keyboardHide:nil];
            
            [self.backgroundView setAlpha:0.5];
            [UIView animateWithDuration:0.5
                                  delay:0.0
                                options:UIViewAnimationOptionCurveEaseIn // See other options
                             animations:^{
                                 [self.backgroundView setAlpha:0.0];
                             }
                             completion:^(BOOL finished) {
                                 // Completion Block
                             }];
        self.keyboardIsVisible = NO;
       }
    }
}

//------------------------------------------------------------------------------
-(void)showKeyboard:(NSNotification *) notification{
    
    [self darkenBackgroundView];
    [self.viewTextField keyboardShow:notification];
}
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event{
   
    if (!self.dragged) {
        if ([self.viewTextField.writingTextBox getNumberOfCharacters] == 0){
            [self.viewTextField.writingTextBox addPlaceholderInTextView];
        }
        self.orientation = NO;
    }
}

//------------------------------------------------------------------------------
-(void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event{
   
    self.dragged=YES;
}

//------------------------------------------------------------------------------
-(void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event{
    if (!self.dragged) {
        if ([self.viewTextField.writingTextBox getNumberOfCharacters] == 0){
            [self.viewTextField.writingTextBox addPlaceholderInTextView];
        }
        self.orientation = NO;
    }
    self.dragged=NO;
}

#pragma mark - Orientation methods
//------------------------------------------------------------------------------
- (void)restrictRotation:(BOOL) restriction {
    
    AppDelegate* appDelegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    appDelegate.restrictRotation = restriction;
    self.orientation = YES;
}

//------------------------------------------------------------------------------
- (void)orientationUpdates:(NSNotification *)notification{

//    [self updateCurrentTitleView];
//    
//    [self restrictRotation:NO];
    
   // [self.timelineTableView orientationChanged:notification];
}

#pragma mark - Title View Get
//------------------------------------------------------------------------------
- (void)updateCurrentTitleView {

    if (self.navigationItem.titleView.subviews.count > 1) {
        NSString *nowText = self.title;
        self.title = nowText;
    }else
        [self changeTitleView];
}

#pragma mark - Protocol Timeline
//------------------------------------------------------------------------------
- (void)changeTitleView{
    self.title = NSLocalizedString (@"Timeline", nil);
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
