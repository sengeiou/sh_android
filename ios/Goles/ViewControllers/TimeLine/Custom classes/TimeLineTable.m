//
//  TimeLineTable.m
//  Shootr
//
//  Created by Christian Cabarrocas on 20/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "TimeLineTable.h"
#import "ShotTableViewCell.h"
#import "Shot.h"
#import "ShotManager.h"
#import "FavRestConsumer.h"
#import "Fav24Colors.h"
#import "AppDelegate.h"
#import "ProfileViewController.h"

#define kTopTableInsets         16.0f
#define kBottomTableInsets      60.0f

@interface TimeLineTable () <UIScrollViewDelegate>

@property (nonatomic,strong)            NSArray                     *arrayShots;
@property (nonatomic,strong)            NSMutableDictionary         *offscreenCells;
@property (nonatomic)                   BOOL                        moreCells;
@property (nonatomic)                   BOOL                        refreshTable;
@property (nonatomic,strong)            UIActivityIndicatorView     *spinner;
@property (nonatomic,strong)            UILabel                     *labelFooter;
@property (nonatomic,strong)            UIRefreshControl            *refreshControl;
@property (nonatomic,assign)            CGFloat                     lastContentOffset;

@end

@implementation TimeLineTable

static NSString *CellIdentifier = @"shootCell";

#pragma mark - INIT
//------------------------------------------------------------------------------
- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    if ((self = [super initWithCoder:aDecoder])) {
        [self basicSetup];
        [self setupConstrains];
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)basicSetup {
    
    self.tableView.scrollsToTop = YES;
    self.tableView.contentInset = UIEdgeInsetsMake(0, 0, 60, 0);
    self.tableView.rowHeight = UITableViewAutomaticDimension;
    self.tableView.estimatedRowHeight = 127.0f;
    [self.tableView registerClass:[ShotTableViewCell class] forCellReuseIdentifier:CellIdentifier];
    
}

//------------------------------------------------------------------------------
- (void)setupConstrains {
    
    [self.tableView autoPinEdgeToSuperviewEdge:ALEdgeTop withInset:kTopTableInsets];
    [self.tableView autoPinEdgeToSuperviewEdge:ALEdgeBottom withInset:kBottomTableInsets];
    [self.tableView autoPinEdgeToSuperviewEdge:ALEdgeLeft withInset:kTopTableInsets];
    [self.tableView autoPinEdgeToSuperviewEdge:ALEdgeRight withInset:kTopTableInsets];
}

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    //Get shots from CoreData
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    self.offscreenCells = [NSMutableDictionary dictionary];
    [self initSpinner];
    [self setupTimeLineView];
}

//------------------------------------------------------------------------------
- (void)setupTimeLineView {
    
    if (self.arrayShots.count == 0)
        self.tableView.hidden = YES;
    else
        [self hiddenViewNotShots];
    
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
//
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
//    NSLog(@"%f", height);
//
//    height += 1;
//
//    return height;
//
//}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    ShotTableViewCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    Shot *shot = self.arrayShots[indexPath.row];
    
    [cell configureBasicCellWithShot:shot andRow:indexPath.row];
    [cell addTarget:self action:@selector(goProfile:)];
    
    //    cell.imgPhoto.layer.cornerRadius = cell.imgPhoto.frame.size.width / 2;
    //    cell.imgPhoto.clipsToBounds = YES;
    
    [cell setNeedsUpdateConstraints];
    [cell updateConstraintsIfNeeded];
    
    return cell;
}

//------------------------------------------------------------------------------
- (void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (!self.refreshTable && !self.moreCells){
        self.spinner.hidden = YES;
        
        self.labelFooter =  [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.tableView.frame.size.width, 44)];
        self.labelFooter.text = NSLocalizedString(@"No more shots", nil);
        self.labelFooter.textColor = [Fav24Colors iosSevenGray];
        self.labelFooter.textAlignment = NSTextAlignmentCenter;
        self.labelFooter.backgroundColor = [UIColor clearColor];
        self.tableView.tableFooterView = self.labelFooter;
    }else{
        self.labelFooter.hidden = YES;
    }
}

////------------------------------------------------------------------------------
//- (CGFloat)tableView:(UITableView *)tableView estimatedHeightForRowAtIndexPath:(NSIndexPath *)indexPath {
////    // If you are just returning a constant value from this method, you should instead just set the table view's
////    // estimatedRowHeight property (in viewDidLoad or similar), which is even faster as the table view won't
////    // have to call this method for every row in the table view.
////    //
////    // Only implement this method if you have row heights that vary by extreme amounts and you notice the scroll indicator
////    // "jumping" as you scroll the table view when using a constant estimatedRowHeight. If you do implement this method,
////    // be sure to do as little work as possible to get a reasonably-accurate estimate.
////
////    // NOTE for iOS 7.0.x ONLY, this bug has been fixed by Apple as of iOS 7.1:
////    // A constraint exception will be thrown if the estimated row height for an inserted row is greater
////    // than the actual height for that row. In order to work around this, we need to return the actual
////    // height for the the row when inserting into the table view - uncomment the below 3 lines of code.
////    // See: https://github.com/caoimghgin/TableViewCellWithAutoLayout/issues/6
////    //    if (self.isInsertingRow) {
////    //        return [self tableView:tableView heightForRowAtIndexPath:indexPath];
////    //    }
//     return UITableViewAutomaticDimension;
//
////	return 80;
//
//}

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
    self.tableView.hidden = NO;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShots.count > 0)
        [self performSelectorOnMainThread:@selector(animationInsertShot) withObject:nil waitUntilDone:NO];
    
}

//------------------------------------------------------------------------------
- (void)animationInsertShot{
    
    [self.tableView reloadData];
    
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
        self.refreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(self.tableView.frame.origin.x, self.tableView.frame.origin.y+100, 40, 40)];
        [self.refreshControl addTarget:self action:@selector(onPullToRefresh:) forControlEvents:UIControlEventValueChanged];
        [self.tableView addSubview:self.refreshControl];
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

//------------------------------------------------------------------------------
-(void) removePullToRefresh{
    
    // self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
    // [self.timelineTableView reloadData];
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
    
    AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
    
    ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
    
    Shot *selectedShot = self.arrayShots[btn.tag];
    
    profileVC.selectedUser = selectedShot.user;
    
    
    [self.navigationController pushViewController:profileVC animated:YES];
}

//------------------------------------------------------------------------------
- (void)addLoadMoreCell{
    self.tableView.tableFooterView = self.spinner;
    
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
    self.tableView.hidden = NO;
    self.viewNotShots.hidden = YES;
}

#pragma mark - PUBLIC METHODS
//------------------------------------------------------------------------------
- (void)setTableInvisible {
    self.tableView.hidden = YES;
}

//------------------------------------------------------------------------------
- (void)setTableVisible {
    self.tableView.hidden = NO;
}

//------------------------------------------------------------------------------
- (void)setFooterInvisible {
    self.labelFooter.hidden = YES;
}

//------------------------------------------------------------------------------
- (void)setFooterVisible {
    self.labelFooter.hidden = NO;
}

@end
