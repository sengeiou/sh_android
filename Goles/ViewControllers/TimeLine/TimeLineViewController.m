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

@interface TimeLineViewController ()<UIScrollViewDelegate, UITextViewDelegate, ConectionProtocol>{
    NSUInteger lengthTextField;
    BOOL moreCells;
    BOOL refreshTable;
    BOOL returningFromBackground;
    float rows;
    float rowsOLD;
    CGRect previousRect;

    UITapGestureRecognizer *tapTapRecognizer;
}

@property (nonatomic,weak)      IBOutlet    UITableView                 *timelineTableView;
@property (nonatomic,weak)      IBOutlet    WritingText                 *writingTextBox;
@property (nonatomic,weak)      IBOutlet    UIButton                    *btnShoot;
@property (nonatomic,weak)      IBOutlet    UILabel                     *charactersLeft;
@property (nonatomic,weak)      IBOutlet    UIView                      *viewNotShots;
@property (nonatomic,weak)      IBOutlet    UIView                      *viewToDisableTextField;
@property (nonatomic,weak)      IBOutlet    WatchingMenu                *watchingMenu;
@property (nonatomic,weak)      IBOutlet    UIView                      *viewTextField;
@property (nonatomic,strong)    IBOutlet    UIView                      *backgroundView;
@property (nonatomic,strong)    IBOutlet    NSLayoutConstraint          *bottomViewPositionConstraint;
@property (nonatomic,strong)    IBOutlet    NSLayoutConstraint          *bottomViewHeightConstraint;
@property (nonatomic,weak)      IBOutlet    UIButton                    *startShootingFirstTime;
@property (nonatomic,strong)                NSArray                     *arrayShots;
@property (nonatomic,strong)                UIRefreshControl            *refreshControl;
@property (nonatomic, assign)               CGFloat                     lastContentOffset;
@property (nonatomic, assign)               int                         sizeKeyboard;
@property (nonatomic,strong)                UIActivityIndicatorView     *spinner;
@property (nonatomic,strong)                UILabel                     *lblFooter;
@property (nonatomic,strong)                NSString                    *textComment;

@property (weak, nonatomic) IBOutlet UILabel *lblNoShots;
@property (weak, nonatomic) IBOutlet UILabel *lblShare;

@property (strong, nonatomic) NSMutableDictionary *offscreenCells;

-(IBAction)startSendShot:(id)sender;

@end

static NSString *CellIdentifier = @"shootCell";

@implementation TimeLineViewController

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
        
    //For Alpha version
    self.watchingMenu.hidden = YES;
    
    [self textLocalizableLabels];
    [self.writingTextBox setPlaceholder:NSLocalizedString (@"Comment", nil)];
    
    //Set titleView
    self.navigationItem.titleView = [TimeLineUtilities createConectandoTitleView];
    
    [self miscelaneousSetup];
    
    [self setNavigationBarButtons];
    [self setTextViewForShotCreation];
 
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
	
    [self setupTimeLineTableView];
    
}


#pragma mark - Localizable Strings
//------------------------------------------------------------------------------
-(void)textLocalizableLabels{
    
    [self.btnShoot setTitle:NSLocalizedString(@"Shoot", nil) forState:UIControlStateNormal];
    [self.startShootingFirstTime setTitle:NSLocalizedString(@"Start Shooting", nil) forState:UIControlStateNormal];
    self.lblNoShots.text =  NSLocalizedString(@"No Shots", nil);
    self.lblShare.text = NSLocalizedString (@"Share with friends about football.", nil);
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
    
    [self initSpinner];
    
    self.arrayShots = [[NSArray alloc]init];
    self.btnShoot.enabled = NO;
}

//------------------------------------------------------------------------------
- (void)setupTimeLineTableView {
    
	self.offscreenCells = [NSMutableDictionary dictionary];
	[self.timelineTableView registerClass:[ShotTableViewCell class] forCellReuseIdentifier:CellIdentifier];
	
    //Get shots from CoreData
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShots.count == 0)
        self.timelineTableView.hidden = YES;
    else
        [self hiddenViewNotShots];
    
    self.timelineTableView.scrollsToTop = YES;
    self.timelineTableView.contentInset = UIEdgeInsetsMake(0, 0, 60, 0);
    self.timelineTableView.rowHeight = UITableViewAutomaticDimension;
    self.timelineTableView.estimatedRowHeight = 80.0f;

}

//------------------------------------------------------------------------------
- (void)setLocalNotificationObservers {
    
    //Listen for show conecting process
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(returnBackground) name:k_NOTIF_BACKGROUND object:nil];
    
    //Listen for synchro process end
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadShotsTable:) name:K_NOTIF_SHOT_END object:nil];

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

   // [UIBarButtonItem.appearance setBackButtonTitlePositionAdjustment:UIOffsetMake(0, -64) forBarMetrics:UIBarMetricsDefault];
    [self.navigationController.navigationBar setTitleVerticalPositionAdjustment:0 forBarMetrics:UIBarMetricsDefault];

    self.navigationItem.titleView.hidden = YES;
    
    [self setLocalNotificationObservers];
}
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

//------------------------------------------------------------------------------
- (void)initSpinner{
    moreCells = YES;
    refreshTable = YES;
    
    self.spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    [self.spinner startAnimating];
    self.spinner.frame = CGRectMake(0, 0, 320, 44);
}

//------------------------------------------------------------------------------
-(void)hiddenViewNotShots{
    
    [self addPullToRefresh];
    
    self.timelineTableView.hidden = NO;
    self.viewNotShots.hidden = YES;
    self.timelineTableView.delegate = self;
    self.timelineTableView.dataSource = self;
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


#pragma mark - Pull to refresh
//------------------------------------------------------------------------------
- (void)addPullToRefresh{
    // Config pull to refresh
    if (self.refreshControl == nil) {
        self.refreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(self.timelineTableView.frame.origin.x, self.timelineTableView.frame.origin.y+100, 40, 40)];
        [self.refreshControl addTarget:self action:@selector(onPullToRefresh:) forControlEvents:UIControlEventValueChanged];
        [self.timelineTableView addSubview:self.refreshControl];
    }  
}
//------------------------------------------------------------------------------
- (void)onPullToRefresh:(UIRefreshControl *)refreshControl {

    //For Beta version only in iphone 4
//    [UIView animateWithDuration:0.25 animations:^{
//        self.watchingMenu.alpha = 0.0;
//    }];
    //[[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:NO withShot:NO];
    
    [self.refreshControl endRefreshing];

}

#pragma mark - UITableViewDelegate
//
////------------------------------------------------------------------------------
//- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
//    return self.watchingMenu.frame.size.height;
//}
//
////------------------------------------------------------------------------------
//- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
//    
//    UIView *header =  [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.timelineTableView.frame.size.width, self.watchingMenu.frame.size.height)];
//    header.backgroundColor = [UIColor clearColor];
//
//    return header;
//}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return self.arrayShots.count;
}

////------------------------------------------------------------------------------
//- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{

//    Shot *shot = self.arrayShots[indexPath.row];
//    
//    NSString *reuseIdentifier = CellIdentifier;
//    ShotTableViewCell *cell = [self.offscreenCells objectForKey:reuseIdentifier];
//    if (!cell) {
//        cell = [[ShotTableViewCell alloc] init];
//        [self.offscreenCells setObject:cell forKey:reuseIdentifier];
//    }
//    [cell configureBasicCellWithShot:shot andRow:indexPath.row];
//    [cell addTarget:self action:@selector(goProfile:)];
//
//    [cell setNeedsUpdateConstraints];
//    [cell updateConstraintsIfNeeded];
//    
//    cell.bounds = CGRectMake(0.0f, 0.0f, CGRectGetWidth(tableView.bounds), CGRectGetHeight(cell.bounds));
// 
//    [cell setNeedsLayout];
//    [cell layoutIfNeeded];
//    
//     CGFloat height = [cell.contentView systemLayoutSizeFittingSize:UILayoutFittingCompressedSize].height;
//    
//    height += 1;
//    
//    return height;

//}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    ShotTableViewCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier];

    Shot *shot = self.arrayShots[indexPath.row];
	
    [cell configureBasicCellWithShot:shot andRow:indexPath.row];
    [cell addTarget:self action:@selector(goProfile:)];

    [cell setNeedsUpdateConstraints];
    [cell updateConstraintsIfNeeded];
    
    return cell;
 }

//------------------------------------------------------------------------------
- (void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
	
    if (!refreshTable && !moreCells){
        self.spinner.hidden = YES;
        
        self.lblFooter =  [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.timelineTableView.frame.size.width, 44)];
        self.lblFooter.text = NSLocalizedString(@"No more shots", nil);
        self.lblFooter.textColor = [Fav24Colors iosSevenGray];
        self.lblFooter.textAlignment = NSTextAlignmentCenter;
        self.lblFooter.backgroundColor = [UIColor clearColor];
        self.timelineTableView.tableFooterView = self.lblFooter;
    }else{
        self.lblFooter.hidden = YES;
    }
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView estimatedHeightForRowAtIndexPath:(NSIndexPath *)indexPath {
//    // If you are just returning a constant value from this method, you should instead just set the table view's
//    // estimatedRowHeight property (in viewDidLoad or similar), which is even faster as the table view won't
//    // have to call this method for every row in the table view.
//    //
//    // Only implement this method if you have row heights that vary by extreme amounts and you notice the scroll indicator
//    // "jumping" as you scroll the table view when using a constant estimatedRowHeight. If you do implement this method,
//    // be sure to do as little work as possible to get a reasonably-accurate estimate.
//    
//    // NOTE for iOS 7.0.x ONLY, this bug has been fixed by Apple as of iOS 7.1:
//    // A constraint exception will be thrown if the estimated row height for an inserted row is greater
//    // than the actual height for that row. In order to work around this, we need to return the actual
//    // height for the the row when inserting into the table view - uncomment the below 3 lines of code.
//    // See: https://github.com/caoimghgin/TableViewCellWithAutoLayout/issues/6
//    //    if (self.isInsertingRow) {
//    //        return [self tableView:tableView heightForRowAtIndexPath:indexPath];
//    //    }
     return UITableViewAutomaticDimension;

//	return 80;

}

//------------------------------------------------------------------------------
- (void)addLoadMoreCell{
    self.timelineTableView.tableFooterView = self.spinner;
    
    moreCells = NO;
    [[FavRestConsumer sharedInstance] getOldShotsWithDelegate:self];
    
}

#pragma mark - Button to Profile ViewController
//------------------------------------------------------------------------------
- (void)goProfile:(id)sender{
    
    UIButton *btn = (UIButton *) sender;
    
    AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
    
    ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
    
    Shot *selectedShot = self.arrayShots[btn.tag];
    
    profileVC.selectedUser = selectedShot.user;
    
    
    [self.navigationController pushViewController:profileVC animated:YES];
}

#pragma mark - Reload table View
//------------------------------------------------------------------------------
- (void)reloadShotsTable:(id)sender {

    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
#warning Need to check if some shot is inserted or deleted in DB. Only in this case is necessaary to reload
    
    if (self.arrayShots.count > 0)
        [self performSelectorOnMainThread:@selector(reloadTimeline) withObject:nil waitUntilDone:NO];
}

//------------------------------------------------------------------------------
- (void)reloadShotsTableWithAnimation:(id)sender {
    
    //PAra la primera vez
    self.viewNotShots.hidden = YES;
    self.timelineTableView.hidden = NO;
    self.timelineTableView.dataSource = self;
    self.timelineTableView.delegate = self;
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShots.count > 0)
        [self performSelectorOnMainThread:@selector(animationInsertShot) withObject:nil waitUntilDone:NO];
    
}

//------------------------------------------------------------------------------
- (void)animationInsertShot{
    
    [self.timelineTableView reloadData];
    
//    [self.timelineTableView beginUpdates];
//    NSIndexPath *iPath = [NSIndexPath indexPathForRow:0 inSection:0];
//    [self.timelineTableView insertRowsAtIndexPaths:@[iPath] withRowAnimation:UITableViewRowAnimationTop];
//    [self.timelineTableView endUpdates];
}

#pragma mark - Send shot
//------------------------------------------------------------------------------
- (void)sendShot{
//    self.writingTextBox.backgroundColor = [Fav24Colors backgroundTextViewSendShot];
    [self.writingTextBox setWritingTextViewWhenSendShot];
    self.viewToDisableTextField.hidden = NO;

    //self.txtView.userInteractionEnabled= NO;
//    [self.writingTextBox resignFirstResponder];
    self.orientation = NO;
    [self keyboardHide:nil];
//    self.writingTextBox.textColor = [Fav24Colors textTextViewSendShot];
    self.charactersLeft.hidden = YES;
    self.btnShoot.enabled = NO;
    self.navigationItem.titleView = [TimeLineUtilities createEnviandoTitleView];
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:NO withShot:YES];
}

#pragma mark - Start Send Shot
//------------------------------------------------------------------------------
-(IBAction)startSendShot:(id)sender{
    
    [self keyboardShow:nil];
    [self.writingTextBox becomeFirstResponder];
}


//------------------------------------------------------------------------------
- (BOOL) controlRepeatedShot:(NSString *)texto{
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLineBetweenHours];
    
    if ([self isShotMessageAlreadyInList:self.arrayShots withText:texto])
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
    self.lblFooter.hidden = YES;

}
//------------------------------------------------------------------------------
-(void)changeStateActualizandoViewNavBar{
    self.navigationItem.titleView = [TimeLineUtilities createActualizandoTitleView];
    self.lblFooter.hidden = YES;
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
        [self performSelectorOnMainThread:@selector(reloadShotsTable:) withObject:nil waitUntilDone:NO];
        moreCells = YES;
    }else if (!refresh){
        moreCells = NO;
        refreshTable = NO;
    }
    [self performSelectorOnMainThread:@selector(changeViewTitleMainThread) withObject:nil waitUntilDone:NO];
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
        rows = 0;
        self.charactersLeft.hidden = YES;
        [self performSelectorOnMainThread:@selector(reloadShotsTable:) withObject:nil waitUntilDone:NO];

       // [self reloadShotsTableWithAnimation:nil];
        [self.timelineTableView setScrollsToTop:YES];
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
-(void)reloadTimeline{
   
    [self.timelineTableView reloadData];
}

//------------------------------------------------------------------------------
-(void) removePullToRefresh{
    
   // self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
   // [self.timelineTableView reloadData];
    [self.refreshControl endRefreshing];
}

//------------------------------------------------------------------------------
- (void)setupUIWhenCancelOrNotConnection {

    self.btnShoot.enabled = YES;
    self.viewToDisableTextField.hidden = YES;
    self.orientation = NO;
    
    [self.writingTextBox setWritingTextViewWhenCancelTouched];

}

#pragma mark - UIScrollViewDelegate
//------------------------------------------------------------------------------
//- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{

    // For Beta version and only iphone 4

//    CGFloat currentOffset = scrollView.contentOffset.y;
//    
//   if (currentOffset == 0)
//        [UIView animateWithDuration:0.25 animations:^{
//            self.watchingMenu.alpha = 1.0;
//            self.viewTextField.alpha = 1.0;
//        }];
//}

//------------------------------------------------------------------------------
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
 
    
    CGFloat currentOffset = scrollView.contentOffset.y;
    CGFloat maximumOffset = scrollView.contentSize.height - scrollView.frame.size.height;
   
    if (currentOffset < 0)
        NSLog(@"");
        // For Beta version and only iphone 4
//        [UIView animateWithDuration:0.2 animations:^{
//            self.watchingMenu.alpha = 0.0;
//            self.viewTextField.alpha = 0.0; // For Beta version and only iphone 4
//        }];
    else{
        if (maximumOffset - currentOffset <= 200.0 && moreCells)
            [self addLoadMoreCell];

// For Beta version and only iphone 4
//         if (self.lastContentOffset > scrollView.contentOffset.y){
//             [UIView animateWithDuration:0.25 animations:^{
//                 self.viewTextField.alpha = 1.0;
//             }];
//         
//         }else if (scrollView.contentOffset.y > self.watchingMenu.frame.size.height){
//             [UIView animateWithDuration:0.2 animations:^{
//                 self.viewTextField.alpha = 0.0;
//             
//             }];
    }
  
    
     self.lastContentOffset = scrollView.contentOffset.y;
}

#pragma mark - KEYBOARD
//------------------------------------------------------------------------------
-(void)keyboardShow:(NSNotification*)notification{
    
    [self.writingTextBox setWritingTextviewWhenKeyboardShown];
    
    [self darkenBackgroundView];


    self.timelineTableView.scrollEnabled = NO;

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
        
        [self.timelineTableView scrollRectToVisible:CGRectMake(0, 0, 1, 1) animated:NO];
        self.timelineTableView.scrollEnabled = YES;
            
        if (lengthTextField == 0){
            [self.writingTextBox setPlaceholder:NSLocalizedString (@"Comment", nil)];
            
            rows = 0;
            self.charactersLeft.hidden = YES;
        }else
            self.writingTextBox.textColor = [UIColor blackColor];

        if (rows <= 2) {
            self.bottomViewHeightConstraint.constant = 75;
            self.bottomViewPositionConstraint.constant = 0.0f;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        }else{
            self.bottomViewHeightConstraint.constant = ((rows-2)*self.writingTextBox.font.lineHeight)+75;
//            self.bottomViewHeightConstraint.constant = (rows*18)+75;
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

	rows = round( (textView.contentSize.height - textView.textContainerInset.top - textView.textContainerInset.bottom) / textView.font.lineHeight);

    if (self.viewTextField.frame.origin.y > self.navigationController.navigationBar.frame.size.height+25){
        if (rows > 2 && ![character isEqualToString:@"\n"] && ![character isEqualToString:@""]) {
            self.bottomViewHeightConstraint.constant = ((rows-2)*textView.font.lineHeight)+75;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        }else if(rows > 1 && [character isEqualToString:@"\n"]){
            self.bottomViewHeightConstraint.constant = ((rows-1)*textView.font.lineHeight)+75;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        }
    }
}

//------------------------------------------------------------------------------
- (void)adaptViewSizeWhenDeleting:(UITextView *)textView{
    
    if (rows > 2) {
        rows = rows-3;
        self.bottomViewHeightConstraint.constant = (rows*textView.font.lineHeight)+75;
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

@end
