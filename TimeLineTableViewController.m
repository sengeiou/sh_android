//
//  TimeLineTableViewController.m
//  Shootr
//
//  Created by Christian Cabarrocas on 21/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "TimeLineTableViewController.h"
#import "ShotTableViewCell.h"
#import "Shot.h"
#import "ShotManager.h"
#import "FavRestConsumer.h"
#import "Fav24Colors.h"
#import "AppDelegate.h"
#import "TimeLineViewController.h"
#import "TimeLineUtilities.h"
#import "CoreDataParsing.h"

#define kTopTableInsets         250.0f
#define kBottomTableInsets      60.0f
#define kLateralTableInsets     16.0f
#define kLateralRightTableInsets     -16.0f
static NSString *CellIdentifier = @"shootCell";

@interface TimeLineTableViewController () <UIScrollViewDelegate, UITableViewDataSource, UITableViewDelegate>

@property (nonatomic,strong)            NSArray                     *arrayShots;
@property (nonatomic,strong)            NSMutableDictionary         *offscreenCells;
@property (nonatomic)                   BOOL                        moreCells;
@property (nonatomic)                   BOOL                        refreshTable;
@property (nonatomic,strong)            UIActivityIndicatorView     *spinner;
@property (nonatomic,strong)            UILabel                     *labelFooter;
@property (nonatomic,strong)            UIRefreshControl            *refreshControl;
@property (nonatomic,assign)            CGFloat                     lastContentOffset;
@property (nonatomic,assign)            IBOutlet    UITableView     *myTableView;

@end

@implementation TimeLineTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    [self basicSetup];
    
    [self loadData];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

//------------------------------------------------------------------------------
- (void)basicSetup {
    self.myTableView.scrollsToTop = YES;
    self.myTableView.contentInset = UIEdgeInsetsMake(60, 0, 60, 0);
    self.myTableView.rowHeight = UITableViewAutomaticDimension;
    self.myTableView.estimatedRowHeight = 127.0f;
    [self.myTableView registerClass:[ShotTableViewCell class] forCellReuseIdentifier:CellIdentifier];
}

//------------------------------------------------------------------------------
- (void)updateConstraints {
    
    //  [self.tableView autoPinEdgeToSuperviewEdge:ALEdgeTop withInset:kTopTableInsets];
    // [self.tableView autoPinEdgeToSuperviewEdge:ALEdgeBottom withInset:kBottomTableInsets];
    //    [self.tableView autoPinEdgeToSuperviewEdge:ALEdgeLeft withInset:kLateralTableInsets];
    //  [self.tableView autoPinEdgeToSuperviewEdge:ALEdgeRight withInset:kLateralRightTableInsets];
    //    [self.tableView autoPinEdge:ALEdgeTop toEdge:ALEdgeTop ofView:self.tableView.superview withOffset:200];
    
   // [self.tableView autoMatchDimension:ALDimensionWidth toDimension:ALDimensionWidth ofView:self.tableView.superview];
    
    // [super updateConstraints];
}

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)loadData {
    
    //Listen for synchro process end
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadShotsTable) name:K_NOTIF_SHOT_END object:nil];
    
    
    //Get shots from CoreData
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    self.offscreenCells = [NSMutableDictionary dictionary];
    [self initSpinner];
    [self setupTimeLineView];
    
    [self.myTableView reloadData];
}

//------------------------------------------------------------------------------
- (void)setupTimeLineView {
    
    if (self.arrayShots.count == 0)
        self.myTableView.hidden = YES;
    else
        [self hiddenViewNotShots];
    
}

#pragma mark - UITableViewDelegate
//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return self.arrayShots.count;
}

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
    
    if (!self.refreshTable && !self.moreCells){
        self.spinner.hidden = YES;
        
        self.labelFooter =  [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.myTableView.frame.size.width, 44)];
        self.labelFooter.text = NSLocalizedString(@"No more shots", nil);
        self.labelFooter.textColor = [Fav24Colors iosSevenGray];
        self.labelFooter.textAlignment = NSTextAlignmentCenter;
        self.labelFooter.backgroundColor = [UIColor clearColor];
        self.myTableView.tableFooterView = self.labelFooter;
    }else{
        self.labelFooter.hidden = YES;
    }
}

#pragma mark - Reload table View
//------------------------------------------------------------------------------
- (void)reloadShotsTable {
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(changeTitleView:)])
        [self.delegate changeTitleView:[TimeLineUtilities createTimelineTitleView]];
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
#warning Need to check if some shot is inserted or deleted in DB. Only in this case is necessary to reload
    
    if (self.arrayShots.count > 0)
        [self performSelectorOnMainThread:@selector(reloadTimeline) withObject:nil waitUntilDone:NO];
}

//------------------------------------------------------------------------------
- (void)reloadShotsTableWithAnimation:(id)sender {
    
    //First Time
    if (self.delegate && [self.delegate respondsToSelector:@selector(setHiddenViewNotshots:)])
        [self.delegate setHiddenViewNotshots:YES];
    
    self.myTableView.hidden = NO;
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShots.count > 0)
        [self performSelectorOnMainThread:@selector(animationInsertShot) withObject:nil waitUntilDone:NO];
    
}

//------------------------------------------------------------------------------
-(void)reloadTimeline{
    
    //First Time
    if (self.delegate && [self.delegate respondsToSelector:@selector(setHiddenViewNotshots:)])
        [self.delegate setHiddenViewNotshots:YES];
    self.myTableView.hidden = NO;
    
    [self.myTableView reloadData];
}

//------------------------------------------------------------------------------
- (void)animationInsertShot{
    
    [self.myTableView reloadData];
    
    //    [self.timelineTableView beginUpdates];
    //    NSIndexPath *iPath = [NSIndexPath indexPathForRow:0 inSection:0];
    //    [self.timelineTableView insertRowsAtIndexPaths:@[iPath] withRowAnimation:UITableViewRowAnimationTop];
    //    [self.timelineTableView endUpdates];
}

#pragma mark - Pull to refresh
//------------------------------------------------------------------------------
- (void)addPullToRefresh{
    // Config pull to refresh
    if (self.refreshControl == nil) {
        self.refreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(self.myTableView.frame.origin.x, self.myTableView.frame.origin.y+100, 40, 40)];
        [self.refreshControl addTarget:self action:@selector(onPullToRefresh:) forControlEvents:UIControlEventValueChanged];
        [self.myTableView addSubview:self.refreshControl];
    }
}
//------------------------------------------------------------------------------
- (void)onPullToRefresh:(UIRefreshControl *)refreshControl {
    
    //For Beta version only in iphone 4
    //    [UIView animateWithDuration:0.25 animations:^{
    //        self.watchingMenu.alpha = 0.0;
    //    }];
    
    [self.refreshControl endRefreshing];
}

//------------------------------------------------------------------------------
-(void) removePullToRefresh{
    [self.refreshControl endRefreshing];
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
        if (maximumOffset - currentOffset <= 200.0 && self.moreCells)
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

#pragma mark - Button to Profile ViewController
//------------------------------------------------------------------------------
- (void)goProfile:(id)sender{
    
    UIButton *btn = (UIButton *) sender;
    
    if ([self.delegate respondsToSelector:@selector(pushToProfile:)])
        [self.delegate pushToProfile:self.arrayShots[btn.tag]];
}

//------------------------------------------------------------------------------
- (void)addLoadMoreCell{
    self.myTableView.tableFooterView = self.spinner;
    
    self.moreCells = NO;
    [[FavRestConsumer sharedInstance] getOldShotsWithDelegate:self];
}

//------------------------------------------------------------------------------
- (void)initSpinner{
    self.moreCells = YES;
    self.refreshTable = YES;
    
    self.spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    [self.spinner startAnimating];
    self.spinner.frame = CGRectMake(0, 0, 320, 44);
}

//------------------------------------------------------------------------------
-(void)hiddenViewNotShots{
    
    [self addPullToRefresh];
    self.myTableView.hidden = NO;
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(setHiddenViewNotshots:)])
        [self.delegate setHiddenViewNotshots:YES];
}

#pragma mark - PUBLIC METHODS
//------------------------------------------------------------------------------
- (void)setTableInvisible {
    self.myTableView.hidden = YES;
}

//------------------------------------------------------------------------------
- (void)setTableVisible {
    self.myTableView.hidden = NO;
}

//------------------------------------------------------------------------------
- (void)setFooterInvisible {
    self.labelFooter.hidden = YES;
}

//------------------------------------------------------------------------------
- (void)setFooterVisible {
    self.labelFooter.hidden = NO;
}

//------------------------------------------------------------------------------
- (void)isNecessaryMoreCells:(BOOL)moreCells{
    self.moreCells = moreCells;
}

//------------------------------------------------------------------------------
- (void)isNecessaryRefreshCells:(BOOL)refeshTableView{
    self.refreshTable = refeshTableView;
}

@end
