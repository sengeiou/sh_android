//
//  TimeLineViewController.m
//  Goles
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
#import "UIImageView+FadeIn.h"
#import "Utils.h"
#import "Conection.h"
#import "Fav24Colors.h"
#import "AppDelegate.h"


@interface TimeLineViewController ()<ConectionProtocol, UIScrollViewDelegate, UITextViewDelegate, ConectionProtocol>{
    int lengthTextField;
    BOOL isVisible;
}

@property (nonatomic,strong) IBOutlet UITableView    *timelineTableView;
@property (weak, nonatomic) IBOutlet UIButton *btnWatching;
@property (weak, nonatomic) IBOutlet UIButton *btnSearch;
@property (weak, nonatomic) IBOutlet UIButton *btnInfo;
@property (weak, nonatomic) IBOutlet UITextField *txtField;
@property (weak, nonatomic) IBOutlet UIButton *btnShoot;
@property (weak, nonatomic) IBOutlet UIView *viewNotShots;
@property (strong, nonatomic) NSArray *arrayShots;
@property (strong, nonatomic) UIRefreshControl *refreshControl;
@property (weak, nonatomic) IBOutlet UIView *viewOptions;
@property (weak, nonatomic) IBOutlet UIView *viewTextField;
@property (nonatomic, assign) CGFloat lastContentOffset;
@property (nonatomic, assign) CGRect originalFrame;
@property (weak, nonatomic) IBOutlet UIImageView *line1;
@property (weak, nonatomic) IBOutlet UIImageView *line2;
@property (strong, nonatomic) UIView *backgroundView;
@property (assign, nonatomic) int sizeKeyboard;
@property (weak, nonatomic) IBOutlet UITextField *txtViewWrite;
@property (nonatomic, strong) NSLayoutConstraint *bottomViewConstraint;
@property (nonatomic, strong) NSTimer *mTimer;

@end

@implementation TimeLineViewController

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    lengthTextField = 0;
    self.arrayShots = [[NSArray alloc]init];
    self.btnShoot.enabled = NO;
    self.txtField.delegate = self;
    
   // [self setTimer];
    
    [self.btnShoot addTarget:self action:@selector(sendShot) forControlEvents:UIControlEventTouchUpInside];
    
    //Get shots from server
    [[Conection sharedInstance]getServerTimewithDelegate:self];
    
    //Get shots from CoreData
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];

    //Listen for synchro process end
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadShotsTable:) name:K_NOTIF_SHOT_END object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardHide:) name:UIKeyboardWillHideNotification object:nil];
//    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardChange:) name:UIKeyboardWillChangeFrameNotification object:nil];
    if (self.arrayShots.count == 0)
        self.timelineTableView.hidden = YES;
    else
        [self hiddenViewNotShots];
    
    self.originalFrame = self.tabBarController.tabBar.frame;
    
    
    UIBarButtonItem *btnSearch = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"Icon_Magnifier"] style:UIBarButtonItemStyleBordered target:self action:@selector(search)];
    btnSearch.tintColor = [Fav24Colors iosSevenBlue];
    self.navigationItem.rightBarButtonItem = btnSearch;
    
    [self.timelineTableView setContentInset:UIEdgeInsetsMake(0, 0, 47, 0)];
    self.line1.frame = CGRectMake(self.line1.frame.origin.x, self.line1.frame.origin.y, self.line1.frame.size.width, 0.5);
    self.line2.frame = CGRectMake(self.line2.frame.origin.x, self.line2.frame.origin.y, self.line2.frame.size.width, 0.5);
    
    [self createConstraintForBottomView];
    
}
//
////------------------------------------------------------------------------------
//-(void)setTimer
//{
//    self.mTimer = [NSTimer scheduledTimerWithTimeInterval:5.0 target:self
//                                                 selector:@selector(btnNotPullToRefresh) userInfo:nil repeats:NO];
//}

//------------------------------------------------------------------------------
- (void)viewDidLayoutSubviews {
    
    [super viewDidLayoutSubviews];
    self.timelineTableView.backgroundColor = [UIColor clearColor];
}

//------------------------------------------------------------------------------
- (void)createConstraintForBottomView {
    
    self.bottomViewConstraint = [NSLayoutConstraint constraintWithItem:self.viewTextField
                                                             attribute:NSLayoutAttributeBottom
                                                             relatedBy:NSLayoutRelationEqual
                                                                toItem:self.view
                                                             attribute:NSLayoutAttributeBottom
                                                            multiplier:1.0f constant:-47.0f];
    [self.view addConstraint:self.bottomViewConstraint];
}

//------------------------------------------------------------------------------
-(void) search{
    
}

//------------------------------------------------------------------------------
-(void) loadTextField{
    
    self.txtViewWrite.backgroundColor = [UIColor whiteColor];
    self.txtViewWrite.placeholder = @"what's Up?";
}

//------------------------------------------------------------------------------
-(void)hiddenViewNotShots{
    
    [self addPullToRefresh];
    
    self.timelineTableView.hidden = NO;
    self.viewNotShots.hidden = YES;
    self.timelineTableView.delegate = self;
    self.timelineTableView.dataSource = self;
}

//------------------------------------------------------------------------------
-(void)viewWillAppear:(BOOL)animated{
    self.title = @"Timeline";
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - Pull to refresh
//------------------------------------------------------------------------------
-(void)addPullToRefresh{
    // Config pull to refresh
    if (self.refreshControl == nil) {
        self.refreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(self.timelineTableView.frame.origin.x, self.timelineTableView.frame.origin.y+100, 40, 40)];
        [self.refreshControl addTarget:self action:@selector(onPullToRefresh:) forControlEvents:UIControlEventValueChanged];
        [self.timelineTableView addSubview:self.refreshControl];
    }  
}
//------------------------------------------------------------------------------
- (void)onPullToRefresh:(UIRefreshControl *)refreshControl {
    
    [UIView animateWithDuration:0.25 animations:^{
        self.viewOptions.alpha = 0.0;
    }];
    
    [[Conection sharedInstance]getServerTimewithDelegate:self];
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return self.viewOptions.frame.size.height;
}

//------------------------------------------------------------------------------
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    UIView *header =  [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.timelineTableView.frame.size.width, self.viewOptions.frame.size.height)];
    header.backgroundColor = [UIColor clearColor];

    return header;
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if (isVisible)
        [UIView animateWithDuration:0.25 animations:^{
            self.viewOptions.alpha = 1.0;
        }];
    return self.arrayShots.count;

}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{

    Shot *shot = self.arrayShots[indexPath.row];

   return [Utils heightForShot:shot.comment];
}

//------------------------------------------------------------------------------
- (void)addLoadMoreCell {

    [[FavRestConsumer sharedInstance] getOldShotsWithDelegate:self];
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"shootCell";
    ShotTableViewCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
   
    Shot *shot = self.arrayShots[indexPath.row];
    [cell configureBasicCellWithShot:shot];

    return cell;
 }

#pragma mark - Reload table View
//------------------------------------------------------------------------------
- (void)reloadShotsTable:(id)sender {
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShots.count > 0) {
        [self hiddenViewNotShots];
        [self.timelineTableView reloadData];
        
    }
}

#pragma mark - Send shot
//------------------------------------------------------------------------------
- (void)sendShot{
    [[Conection sharedInstance]getServerTimewithDelegate:self];
}


//------------------------------------------------------------------------------
-(BOOL) controlRepeatedShot:(NSString *)texto{
    
     self.arrayShots = [[ShotManager singleton] getShotsForTimeLineBetweenHours];
    
    for (Shot *shot in self.arrayShots) {
        
        if ([shot.comment isEqualToString:texto])
            return YES;
            
    }

    return NO;
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error {
    
    if (status && [entityClass isSubclassOfClass:[Shot class]])
        [self reloadShotsTable:nil];
}

#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status{

    if (self.txtField.text.length >= 1) {
        
        if ([[Conection sharedInstance] isConection]) {
            
            if (![self controlRepeatedShot:self.txtField.text])
                [[ShotManager singleton] createShotWithComment:self.txtField.text andDelegate:self];
            else{
                
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Shot not posted" message:@"Whoops! You already shot that." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
                [alert show];
            }
        }
    }else
        [self performSelectorOnMainThread:@selector(showOptions) withObject:nil waitUntilDone:YES];
    
}

-(void) showOptions{
    //Show OptionsView
    [UIView animateWithDuration:0.25 animations:^{
        self.viewOptions.alpha = 1.0;
    }];
    [self.timelineTableView reloadData];
    [self.refreshControl endRefreshing];

    //isVisible = YES;
}


#pragma mark - ShotCreationProtocol response
//------------------------------------------------------------------------------
- (void)createShotResponseWithStatus:(BOOL)status andError:(NSError *)error {
    
    if (status && !error){
        if (isVisible)
            [self keyboardHide:nil];
        [self reloadShotsTable:nil];
        [self.txtField setText:nil];
        self.btnShoot.enabled = NO;
    }
}


#pragma mark - UIScrollViewDelegate
//------------------------------------------------------------------------------
-(void)scrollViewDidScroll:(UIScrollView *)scrollView {
   
    //Hide tabbar like safari
    /*UITabBar *tb = self.tabBarController.tabBar;
    NSInteger yOffset = scrollView.contentOffset.y;
    if (yOffset > 0) {
        tb.frame = CGRectMake(tb.frame.origin.x, self.originalFrame.origin.y + yOffset, tb.frame.size.width, tb.frame.size.height);
    }
    if (yOffset < 1) tb.frame = self.originalFrame;*/

    /*NSLog(@"content: %f", scrollView.contentOffset.y);
    NSLog(@"resta: %f", scrollView.contentSize.height - scrollView.frame.size.height - self.viewTextField.frame.size.height);*/

    if (scrollView.contentOffset.y == scrollView.contentSize.height - scrollView.frame.size.height)
        [self addLoadMoreCell];
    
    if (self.lastContentOffset > scrollView.contentOffset.y){
        [UIView animateWithDuration:0.25 animations:^{
            
            self.viewOptions.alpha = 1.0;
            self.viewTextField.alpha = 1.0;
        }];
        
    }else if (scrollView.contentOffset.y > self.viewOptions.frame.size.height){
        [UIView animateWithDuration:0.2 animations:^{
            self.viewOptions.alpha = 0.0;
            self.viewTextField.alpha = 0.0;

        }];
    }
   
    
     self.lastContentOffset = scrollView.contentOffset.y;
}

#pragma mark - KEYBOARD
//------------------------------------------------------------------------------
-(void)keyboardShow:(NSNotification*)notification{
    
    NSDictionary* keyboardInfo = [notification userInfo];
    NSValue* keyboardFrameBegin = [keyboardInfo valueForKey:UIKeyboardFrameEndUserInfoKey];
    CGRect keyboardFrameBeginRect = [keyboardFrameBegin CGRectValue];
    
    self.sizeKeyboard = keyboardFrameBeginRect.size.height;//- self.viewTextField.frame.size.height;
    
    if (self.backgroundView == nil) {
        self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(self.timelineTableView.frame.origin.x, 0, self.timelineTableView.frame.size.width, self.timelineTableView.frame.size.height)];
        self.backgroundView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.5];
    }
   
    [self.timelineTableView addSubview:self.backgroundView];
    self.timelineTableView.scrollEnabled = NO;
    
    [UIView animateWithDuration:0.25f animations:^{
        self.viewOptions.alpha = 0.0;
    }];

    isVisible = YES;

    [UIView animateWithDuration:0.25f animations:^{
        self.bottomViewConstraint.constant = - self.sizeKeyboard;
        [self.view layoutIfNeeded];
    }];
    
}


//------------------------------------------------------------------------------
-(void)keyboardHide:(NSNotification*)notification{
    
    [self.backgroundView removeFromSuperview];
    
    [UIView animateWithDuration:0.25f animations:^{
        self.viewOptions.alpha = 1.0;
    }];
    
    [self.timelineTableView setScrollsToTop:YES];
    self.timelineTableView.scrollEnabled = YES;

    isVisible = NO;
    
    [self textFieldShouldReturn:self.txtField];
    
    self.bottomViewConstraint.constant = -47;
    
    [UIView animateWithDuration:0.25f animations:^{
        [self.view layoutIfNeeded];
    }];

}

//------------------------------------------------------------------------------

//-(void)keyboardChange:(NSNotification*)notification{
//    
//    NSDictionary* keyboardInfo = [notification userInfo];
//    NSValue* keyboardFrameBegin = [keyboardInfo valueForKey:UIKeyboardFrameBeginUserInfoKey];
//    CGRect keyboardFrameBeginRect = [keyboardFrameBegin CGRectValue];
//    
//    self.sizeKeyboard = keyboardFrameBeginRect.size.height  -47;//- self.viewTextField.frame.size.height;
//    
//    if (self.backgroundView == nil) {
//        self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(self.timelineTableView.frame.origin.x, 0, self.timelineTableView.frame.size.width, self.timelineTableView.frame.size.height)];
//        self.backgroundView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.5];
//    }
//    
//    [self.timelineTableView addSubview:self.backgroundView];
//    self.timelineTableView.scrollEnabled = NO;
//    
//    [UIView animateWithDuration:0.25 animations:^{
//        self.viewOptions.alpha = 0.0;
//    }];
//    
//    isVisible = YES;
//    
//    self.bottomViewConstraint.constant = self.sizeKeyboard;
//    
//    [UIView animateWithDuration:0.5f animations:^{
//        [self.view layoutIfNeeded];
//    }];
//    
//}

#pragma mark TextFieldDelegate

//------------------------------------------------------------------------------
- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    
    [textField resignFirstResponder];
    return YES;
}


#pragma mark  UITextField response methods
//------------------------------------------------------------------------------
-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    
    lengthTextField = self.txtField.text.length - range.length + string.length;
    
    if (lengthTextField >= 1)
        self.btnShoot.enabled = YES;
    else
        self.btnShoot.enabled = NO;
    
    return YES;
}

//------------------------------------------------------------------------------
- (void)dealloc {
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
