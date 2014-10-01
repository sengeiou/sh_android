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
#import "Constants.h"


@interface TimeLineViewController ()<ConectionProtocol, UIScrollViewDelegate, UITextViewDelegate, ConectionProtocol>{
    NSUInteger lengthTextField;
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

    if (self.arrayShots.count == 0)
        self.timelineTableView.hidden = YES;
    else
        [self hiddenViewNotShots];
    
    self.originalFrame = self.tabBarController.tabBar.frame;
    
    UIBarButtonItem *btnSearch = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"Icon_Magnifier"] style:UIBarButtonItemStyleBordered target:self action:@selector(search)];
    btnSearch.tintColor = [Fav24Colors iosSevenBlue];
    self.navigationItem.rightBarButtonItem = btnSearch;
    
//    [self.timelineTableView setContentInset:UIEdgeInsetsMake(0, 0, 37, 0)];
    
    [self createConstraintForBottomView];
    
}

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
//- (void)addLoadMoreCellWithCompletion:(void (^)(BOOL status,NSError *error))completionBlock{
- (void)addLoadMoreCell{

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

//------------------------------------------------------------------------------
-(void) showOptions{
    
    [self.timelineTableView reloadData];
    [self.refreshControl endRefreshing];

}


#pragma mark - ShotCreationProtocol response
//------------------------------------------------------------------------------
- (void)createShotResponseWithStatus:(BOOL)status andError:(NSError *)error {
    
    if (status && !error){
        if (isVisible)
            [self keyboardHide:nil];
        [self reloadShotsTable:nil];
        [self.timelineTableView scrollRectToVisible:CGRectMake(0, 0, 1, 1) animated:NO];
        [self.txtField setText:nil];
        self.btnShoot.enabled = NO;
    }
}

#pragma mark - UIScrollViewDelegate

-(void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
   
    CGFloat currentOffset = scrollView.contentOffset.y;
    
   if (currentOffset == 0)
        [UIView animateWithDuration:0.25 animations:^{
            
            self.viewOptions.alpha = 1.0;
            self.viewTextField.alpha = 1.0;
        }];
}

//------------------------------------------------------------------------------
-(void)scrollViewDidScroll:(UIScrollView *)scrollView {
 
    
    CGFloat currentOffset = scrollView.contentOffset.y;
    CGFloat maximumOffset = scrollView.contentSize.height - scrollView.frame.size.height;
   
    if (currentOffset < 0)
        
        [UIView animateWithDuration:0.2 animations:^{
            self.viewOptions.alpha = 0.0;
            self.viewTextField.alpha = 0.0;
        }];
    else{
       /* __block BOOL finish = NO;
        
        // Change 200.0 to adjust the distance from bottom
        if (maximumOffset - currentOffset <= 200.0 && finish)
            [self addLoadMoreCellWithCompletion:^(BOOL status, NSError *error) {
                if (status) finish = YES;
                 
            }];*/
        
        if (maximumOffset - currentOffset <= 200.0)
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
    }
    
     self.lastContentOffset = scrollView.contentOffset.y;
}

#pragma mark - KEYBOARD
//------------------------------------------------------------------------------
-(void)keyboardShow:(NSNotification*)notification{
    
    NSDictionary* keyboardInfo = [notification userInfo];
    NSValue* keyboardFrameBegin = [keyboardInfo valueForKey:UIKeyboardFrameEndUserInfoKey];
    CGRect keyboardFrameBeginRect = [keyboardFrameBegin CGRectValue];
    
    self.sizeKeyboard = keyboardFrameBeginRect.size.height;
    
    if (self.backgroundView == nil) {
        self.backgroundView = [[UIView alloc] initWithFrame:CGRectMake(self.timelineTableView.frame.origin.x, 0, self.timelineTableView.frame.size.width, self.timelineTableView.frame.size.height)];
        self.backgroundView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.5];
        UITapGestureRecognizer *tapTapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(keyboardHide:)];
        [self.backgroundView addGestureRecognizer:tapTapRecognizer];
    }
   
    [self.timelineTableView addSubview:self.backgroundView];
    self.timelineTableView.scrollEnabled = NO;
    
    [UIView animateWithDuration:0.25f animations:^{
        self.viewOptions.alpha = 0.0;
    }];

    isVisible = YES;

    double animationDuration = [[keyboardInfo valueForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];

    [UIView animateWithDuration:animationDuration
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseIn
                     animations:^{
                         self.bottomViewConstraint.constant = - self.sizeKeyboard;
                         [self.view layoutIfNeeded];
                     } completion:NULL];

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
   
    //self.txtField.text = [self.txtField.text stringByTrimmingCharactersInSet: [NSCharacterSet whitespaceCharacterSet]];
    
        if ([string isEqualToString:@" "])
            self.btnShoot.enabled = NO;
        else if (lengthTextField >= 1 && ![textField.text isEqualToString:@"  "])
            self.btnShoot.enabled = YES;
        else
            self.btnShoot.enabled = NO;

    NSLog(@"Caracteres que quedan= %lu", (unsigned long)[self countCharacters:lengthTextField]);
    
    return (lengthTextField > CHARACTERS_SHOT) ? NO : YES;
}

//------------------------------------------------------------------------------
-(NSUInteger) countCharacters:(NSUInteger) lenght{
    
    if (lenght <= CHARACTERS_SHOT)
        return CHARACTERS_SHOT - lenght;
    
    return 0;
}

//------------------------------------------------------------------------------
- (void)dealloc {
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
