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

@interface TimeLineViewController ()<UIScrollViewDelegate, UITextViewDelegate, ConectionProtocol, UIAlertViewDelegate>{
    NSUInteger lengthTextField;
    BOOL moreCells;
    BOOL refreshTable;
    float rows;
    float rowsOLD;
    CGRect previousRect;

    UITapGestureRecognizer *tapTapRecognizer;
}

@property (nonatomic,weak)      IBOutlet    UITableView                 *timelineTableView;
@property (nonatomic,weak)      IBOutlet    UIButton                    *btnWatching;
@property (nonatomic,weak)      IBOutlet    UIButton                    *btnIgnore;
@property (nonatomic,weak)      IBOutlet    UITextView                  *txtView;
@property (nonatomic,weak)      IBOutlet    UIButton                    *btnShoot;
@property (nonatomic,weak)      IBOutlet    UILabel                     *charactersLeft;
@property (nonatomic,weak)      IBOutlet    UIView                      *viewNotShots;
@property (nonatomic,weak)      IBOutlet    UIView                      *viewToDisableTextField;
@property (nonatomic,weak)      IBOutlet    UIView                      *viewOptions;
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

@property (weak, nonatomic) IBOutlet UILabel *lblMatch;
@property (weak, nonatomic) IBOutlet UILabel *lblNowPlaying;
@property (weak, nonatomic) IBOutlet UILabel *lblNoShots;
@property (weak, nonatomic) IBOutlet UILabel *lblShare;


@end

@implementation TimeLineViewController

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
        
    //For Alpha version
    self.viewOptions.hidden = YES;
    
    [self textLocalizable];
    
    //Set titleView
    self.navigationItem.titleView = [TimeLineUtilities createConectandoTitleView];
    
    [self miscelaneousSetup];
    
    [self setNavigationBarButtons];
    [self setTextViewForShotCreation];
    [self setLocalNotificationObservers];
 
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
    
    [self setupTimeLineTableView];
}

#pragma mark - Localizable Strings
-(void)textLocalizable{
    
    [self.btnShoot setTitle:NSLocalizedString(@"Shoot", nil) forState:UIControlStateNormal];
    [self.btnWatching setTitle:NSLocalizedString(@"I'm Watching", nil) forState:UIControlStateNormal];
    [self.btnIgnore setTitle:NSLocalizedString(@"Ignore", nil) forState:UIControlStateNormal];
    self.lblNowPlaying.text = NSLocalizedString(@"Now Playing", nil);
    [self.startShootingFirstTime setTitle:NSLocalizedString(@"Start Shooting", nil) forState:UIControlStateNormal];
    self.lblNoShots.text =  NSLocalizedString(@"No Shots", nil);
    self.lblShare.text = NSLocalizedString (@"Share with friends about football.", nil);
}
//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
#warning This notification needs to be here or in the ViewDidLoad?
    [[NSNotificationCenter defaultCenter] addObserver:self  selector:@selector(orientationChanged:)    name:UIDeviceOrientationDidChangeNotification  object:nil];
    
    self.viewToDisableTextField.hidden = YES;
}

#warning Really neede if remove all observer in dealloc?
//------------------------------------------------------------------------------
- (void)viewWillDisappear:(BOOL)animated{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIDeviceOrientationDidChangeNotification object:nil];
}

//------------------------------------------------------------------------------
- (void)dealloc {
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

//------------------------------------------------------------------------------
- (void)returnBackground{

    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
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
    
    //Get shots from CoreData
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShots.count == 0)
        self.timelineTableView.hidden = YES;
    else
        [self hiddenViewNotShots];
    
    self.timelineTableView.contentInset = UIEdgeInsetsMake(0, 0, 60, 0);
    self.timelineTableView.rowHeight = UITableViewAutomaticDimension;
    self.timelineTableView.estimatedRowHeight = 60.0f;

}

//------------------------------------------------------------------------------
- (void)setLocalNotificationObservers {
    
    //Listen for show conecting process
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(returnBackground) name:k_NOTIF_BACKGROUND object:nil];
    
    //Listen for synchro process end
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadShotsTable:) name:K_NOTIF_SHOT_END object:nil];

    
    //Listen for keyboard process open
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardShow:) name:UIKeyboardWillShowNotification object:nil];
    
    //Listen for keyboard process close
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardHide:) name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide) name:UIKeyboardWillHideNotification object:nil];
}

//------------------------------------------------------------------------------
- (void)setNavigationBarButtons {
    
    //Search button
    UIBarButtonItem *btnSearch = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"Icon_Magnifier"] style:UIBarButtonItemStylePlain target:self action:@selector(search)];
    btnSearch.tintColor = [Fav24Colors iosSevenBlue];
    self.navigationItem.leftBarButtonItem = btnSearch;
    
    //Info button
    UIButton *button = [UIButton buttonWithType:UIButtonTypeInfoLight];
    [button addTarget:self action:@selector(infoButton) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *infoButton = [[UIBarButtonItem alloc] initWithCustomView:button];
    self.navigationItem.rightBarButtonItem = infoButton;
}

//------------------------------------------------------------------------------
- (void)setTextViewForShotCreation {

    self.txtView.delegate = self;
    
    self.txtView.clipsToBounds = YES;
    self.txtView.layer.cornerRadius = 8.0f;
	self.txtView.layer.borderWidth = 0.3;
	self.txtView.layer.borderColor = [[UIColor darkGrayColor] CGColor];

    //Disable keyboard intro key
    if (self.textComment.length == 0)
        self.txtView.enablesReturnKeyAutomatically = YES;
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
//        self.viewOptions.alpha = 0.0;
//    }];
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:NO withShot:NO];
}

#pragma mark - UITableViewDelegate
//
////------------------------------------------------------------------------------
//- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
//    return self.viewOptions.frame.size.height;
//}
//
////------------------------------------------------------------------------------
//- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
//    
//    UIView *header =  [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.timelineTableView.frame.size.width, self.viewOptions.frame.size.height)];
//    header.backgroundColor = [UIColor clearColor];
//
//    return header;
//}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return self.arrayShots.count;
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
	
    Shot *shot = self.arrayShots[indexPath.row];

    return [TimeLineUtilities heightForShot:shot.comment];

}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"shootCell";
    ShotTableViewCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
	
    Shot *shot = self.arrayShots[indexPath.row];
	
    [cell configureBasicCellWithShot:shot andRow:indexPath.row];
    [cell addTarget:self action:@selector(goProfile:)];

    
    return cell;
 }

//------------------------------------------------------------------------------
- (void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
	
    if (!refreshTable){
        self.spinner.hidden = YES;
        
        self.lblFooter =  [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.timelineTableView.frame.size.width, 44)];
        self.lblFooter.text = NSLocalizedString(@"No more shots", nil);
        self.lblFooter.textColor = [Fav24Colors iosSevenGray];
        self.lblFooter.textAlignment = NSTextAlignmentCenter;
        self.lblFooter.backgroundColor = [UIColor clearColor];
        self.timelineTableView.tableFooterView = self.lblFooter;
    }
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
    
    if (self.arrayShots.count > 0)
        [self performSelectorOnMainThread:@selector(reloadTimeline) withObject:nil waitUntilDone:NO];
}

//------------------------------------------------------------------------------
- (void)reloadShotsTableWithAnimation:(id)sender {
    
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
    self.txtView.backgroundColor = [Fav24Colors backgroundTextViewSendShot];
    self.viewToDisableTextField.hidden = NO;

    //self.txtView.userInteractionEnabled= NO;
    [self.txtView resignFirstResponder];
    self.orientation = NO;
    [self keyboardHide:nil];
    self.txtView.textColor = [Fav24Colors textTextViewSendShot];
    self.charactersLeft.hidden = YES;
    self.btnShoot.enabled = NO;
    self.navigationItem.titleView = [TimeLineUtilities createEnviandoTitleView];
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:NO withShot:YES];
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

}
//------------------------------------------------------------------------------
-(void)changeStateActualizandoViewNavBar{
    self.navigationItem.titleView = [TimeLineUtilities createActualizandoTitleView];
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
    else if(!status && !refresh && !isShot){
        //        self.orientation = NO;
        //        [self performSelectorOnMainThread:@selector(cleanViewWhenNotConnection) withObject:nil waitUntilDone:YES];
    } else
        [self performSelectorOnMainThread:@selector(removePullToRefresh) withObject:nil waitUntilDone:YES];
    
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && [entityClass isSubclassOfClass:[Shot class]]){
        [self reloadShotsTable:nil];
        moreCells = YES;
    }else if (!refresh){
        moreCells = NO;
        refreshTable = NO;
    }
    [self performSelector:@selector(changeStateViewNavBar) withObject:nil afterDelay:0.5];

}
-(void)changecolortextview{
    self.txtView.textColor = [UIColor lightGrayColor];
}

#pragma mark - ShotCreationProtocol response
//------------------------------------------------------------------------------
- (void)createShotResponseWithStatus:(BOOL)status andError:(NSError *)error {
    
    if (status && !error){
        self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
        lengthTextField = 0;
        [self addPlaceHolder];
        rows = 0;
        self.charactersLeft.hidden = YES;
        [self reloadShotsTableWithAnimation:nil];
        [self.timelineTableView setScrollsToTop:YES];
        self.btnShoot.enabled = NO;
        [self keyboardHide:nil];
        //self.txtView.userInteractionEnabled = YES;
        self.viewToDisableTextField.hidden = YES;

    }else if (error){
        [self performSelectorOnMainThread:@selector(showAlertcanNotCreateShot) withObject:nil waitUntilDone:NO];
    }
}

-(void)addPlaceHolder{
    NSString *placeHolder = NSLocalizedString (@"What's Up?", nil);
    self.txtView.text = placeHolder;
    self.txtView.textColor = [UIColor lightGrayColor];
    self.txtView.backgroundColor = [UIColor whiteColor];

}
#pragma mark - Response utilities methods
//------------------------------------------------------------------------------
-(void)showAlertcanNotCreateShot{
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Shot Not Posted", nil) message:NSLocalizedString(@"Connection timed out.", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel", nil) otherButtonTitles:NSLocalizedString(@"Retry", nil), nil];
    alertView.tag = 18;
    [alertView show];
    
    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
}

//------------------------------------------------------------------------------
- (void)cleanViewWhenNotConnection{
    
    [self keyboardHide:nil];
    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
    
}

#pragma mark - Shot creation
//------------------------------------------------------------------------------
- (void)shotCreated {
    
    [self controlCharactersShot:self.txtView.text];

    if (![self controlRepeatedShot:self.textComment])
        
        [[ShotManager singleton] createShotWithComment:self.textComment andDelegate:self];
    else
        [self performSelectorOnMainThread:@selector(showAlert) withObject:nil waitUntilDone:NO];
    
}

//------------------------------------------------------------------------------
- (void)showAlert{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Shot Not Posted", nil) message:NSLocalizedString(@"Whoops! You already shot that.", nil) delegate:self cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil];
    [alert show];
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
    
    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
    [self.timelineTableView reloadData];
    [self.refreshControl endRefreshing];
}


#pragma mark - UIAlertViewDelegate
//------------------------------------------------------------------------------
-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    if (alertView.tag == 18) {
        switch (buttonIndex) {
            case 0:{
                self.btnShoot.enabled = YES;
                //self.txtView.userInteractionEnabled = YES;
                self.viewToDisableTextField.hidden = YES;
                self.orientation = NO;
                [self keyboardHide:nil];
                self.txtView.backgroundColor = [UIColor whiteColor];
                self.txtView.textColor = [UIColor blackColor];
                
                break;
            }
            case 1:
                [self sendShot];
                
                break;
            default:
                break;
        }

    }else{
        self.txtView.backgroundColor = [UIColor whiteColor];
        self.txtView.textColor = [UIColor blackColor];
        self.btnShoot.enabled = YES;

    }
}

#pragma mark - UIScrollViewDelegate
//------------------------------------------------------------------------------
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    // For Beta version and only iphone 4

//    CGFloat currentOffset = scrollView.contentOffset.y;
//    
//   if (currentOffset == 0)
//        [UIView animateWithDuration:0.25 animations:^{
//            self.viewOptions.alpha = 1.0;
//            self.viewTextField.alpha = 1.0;
//        }];
}

//------------------------------------------------------------------------------
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
 
    
    CGFloat currentOffset = scrollView.contentOffset.y;
    CGFloat maximumOffset = scrollView.contentSize.height - scrollView.frame.size.height;
   
    if (currentOffset < 0)
        NSLog(@"");
        // For Beta version and only iphone 4
//        [UIView animateWithDuration:0.2 animations:^{
//            self.viewOptions.alpha = 0.0;
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
//         }else if (scrollView.contentOffset.y > self.viewOptions.frame.size.height){
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
    
    self.txtView.textColor = [UIColor blackColor];

    self.txtView.backgroundColor = [UIColor whiteColor];
    
//    if (rows >= 3)
//        self.charactersLeft.hidden = NO;
//    else
//        self.charactersLeft.hidden = YES;
    
    NSString *placeHolder = NSLocalizedString (@"What's Up?", nil);
    
    if ([self.txtView.text isEqualToString:placeHolder])
        self.txtView.text = nil;
    
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
    
    NSLog(@"%lu", (unsigned long)self.txtView.text.length);
    NSLog(@"%lu", (unsigned long)lengthTextField);

    if (lengthTextField == 0){
         [self addPlaceHolder];
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

        [self.txtView resignFirstResponder];

        
        self.backgroundView.hidden = YES;
        
        [self.timelineTableView scrollRectToVisible:CGRectMake(0, 0, 1, 1) animated:NO];
        self.timelineTableView.scrollEnabled = YES;
            
        if (lengthTextField == 0){
            [self addPlaceHolder];
            
            rows = 0;
            self.charactersLeft.hidden = YES;
        }else
            self.txtView.textColor = [UIColor blackColor];

        if (rows <= 2) {
            self.bottomViewHeightConstraint.constant = 75;
            self.bottomViewPositionConstraint.constant = 0.0f;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        }else{
            self.bottomViewHeightConstraint.constant = ((rows-2)*self.txtView.font.lineHeight)+75;
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

    //self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
    
    [self restrictRotation:NO];
}

@end
