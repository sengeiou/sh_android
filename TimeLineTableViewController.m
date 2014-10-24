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
#import "CoreDataParsing.h"
#import "CoreDataManager.h"
#import "UserManager.h"

static NSString *CellIdentifier = @"shootCell";

@interface TimeLineTableViewController () <UIScrollViewDelegate, UITableViewDataSource, UITableViewDelegate,NSFetchedResultsControllerDelegate>

@property (nonatomic)                   BOOL                        moreCells;
@property (nonatomic)                   BOOL                        refreshTable;
@property (nonatomic,strong)            UIActivityIndicatorView     *spinner;
@property (nonatomic,strong)            UILabel                     *labelFooter;
@property (nonatomic,strong)            UIRefreshControl            *refreshControl;
@property (nonatomic,assign)            CGFloat                     lastContentOffset;
@property (nonatomic,assign)            IBOutlet    UITableView     *myTableView;
@property (nonatomic, retain)           NSFetchedResultsController  *fetchedResultsController;

@end

@implementation TimeLineTableViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    
    [super viewDidLoad];
    [self basicSetup];
    [self loadData];
}

//------------------------------------------------------------------------------
- (void)viewDidUnload {
    self.fetchedResultsController = nil;
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

//------------------------------------------------------------------------------
- (void)basicSetup {
    self.myTableView.scrollsToTop = YES;
    self.myTableView.contentInset = UIEdgeInsetsMake(66, 0, 70, 0);
    self.myTableView.rowHeight = UITableViewAutomaticDimension;
    self.myTableView.estimatedRowHeight = 80.0f;
    [self.myTableView registerClass:[ShotTableViewCell class] forCellReuseIdentifier:CellIdentifier];
}

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)loadData {

    [self initSpinner];
    
    NSError *error;
    if (![[self fetchedResultsController] performFetch:&error]) {
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
    
}

#pragma mark - UITableViewDelegate
//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return _fetchedResultsController.fetchedObjects.count;

}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    ShotTableViewCell *cell = (ShotTableViewCell *) [tableView dequeueReusableCellWithIdentifier:CellIdentifier];

    [self configureCell:cell atIndexPAth:indexPath];

    [cell setNeedsUpdateConstraints];
    [cell updateConstraintsIfNeeded];
    
    return cell;
}

//------------------------------------------------------------------------------
- (void)configureCell:(ShotTableViewCell *)cell atIndexPAth:(NSIndexPath *)indexPath {
    
    Shot *shot = [self.fetchedResultsController objectAtIndexPath:indexPath];
    [cell configureBasicCellWithShot:shot andRow:indexPath.row];
    [cell addTarget:self action:@selector(goProfile:)];
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
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(changeTitleView)])
        [self.delegate changeTitleView];
}


//------------------------------------------------------------------------------
-(void)reloadTimeline{
    
    //First Time
    if (self.delegate && [self.delegate respondsToSelector:@selector(setHiddenViewNotshots:)])
        [self.delegate setHiddenViewNotshots:YES];
    self.myTableView.hidden = NO;
    
    [self.myTableView reloadData];
}

#pragma mark - FETCHED_RESULTS_CONTROLLER
//------------------------------------------------------------------------------
- (NSFetchedResultsController *)fetchedResultsController {
    
    if (_fetchedResultsController != nil) {
        return _fetchedResultsController;
    }
    
    //GET THE DATA
    self.fetchedResultsController = [[ShotManager singleton] getShotsForTimeLine];
    
    self.fetchedResultsController.delegate = self;
    
    return _fetchedResultsController;
}

//------------------------------------------------------------------------------
- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    [self.myTableView beginUpdates];
}

//------------------------------------------------------------------------------
- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    
    UITableView *tableView = self.myTableView;
    
    switch(type) {
            
        case NSFetchedResultsChangeInsert:{
     
            [tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
//            NSArray *indexs = [self addIndexPath];
//            [tableView reloadRowsAtIndexPaths: indexs withRowAnimation:UITableViewRowAnimationAutomatic];
            
        }break;
            
        case NSFetchedResultsChangeDelete:
            [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
            
        case NSFetchedResultsChangeUpdate:
           
            [self configureCell:(ShotTableViewCell *)[self.myTableView cellForRowAtIndexPath:indexPath] atIndexPAth:indexPath];
//            [tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
            break;
            
        case NSFetchedResultsChangeMove:
            [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
    }
}

//------------------------------------------------------------------------------
-(NSArray *)addIndexPath{
    
    NSMutableArray *indexPaths = [NSMutableArray array];
    NSInteger rowCount = [self.tableView numberOfRowsInSection:0];
    
    for (int j = 0; j < rowCount; j++) {
        [indexPaths addObject:[NSIndexPath indexPathForRow:j inSection:0]];
    }
    
    
    return indexPaths;
}

//------------------------------------------------------------------------------
- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    
    [self.myTableView endUpdates];
//    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:0 inSection:0];
//    [self.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionTop animated:NO];
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
        else{
            self.refreshTable = NO;
            self.moreCells = NO;
        }
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
        [self.delegate pushToProfile:[self.fetchedResultsController.fetchedObjects objectAtIndex:btn.tag]];
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

//------------------------------------------------------------------------------
- (void)orientationChanged:(NSNotification *) notification{
    
//    UIDeviceOrientation deviceOrientation = [[UIDevice currentDevice] orientation];
//
//    switch (deviceOrientation) {
//        case UIDeviceOrientationPortraitUpsideDown:{
//            self.myTableView.contentInset = UIEdgeInsetsMake(66, 0, 70, 0);
//            self.myTableView.contentOffset = CGPointMake(0, -60);
//
//            break;
//        }case UIDeviceOrientationLandscapeLeft:{
//            self.myTableView.contentInset = UIEdgeInsetsMake(40, 0, 70, 0);
//            self.myTableView.contentOffset = CGPointMake(0, -40);
//
//            break;
//        } case UIDeviceOrientationLandscapeRight:{
//            self.myTableView.contentInset = UIEdgeInsetsMake(40, 0, 70, 0);
//            self.myTableView.contentOffset = CGPointMake(0, -40);
//
//            break;
//        }case UIDeviceOrientationPortrait:{
//            self.myTableView.contentInset = UIEdgeInsetsMake(66, 0, 70, 0);
//            self.myTableView.contentOffset = CGPointMake(0, -60);
//
//            break;
//        }default:{
//           
//            break;
//        }
//
//    }
}

@end
