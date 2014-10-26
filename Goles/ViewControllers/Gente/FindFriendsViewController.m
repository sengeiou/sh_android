//
//  FindFriendsViewController.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 14/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FindFriendsViewController.h"
#import "PeopleLineUtilities.h"
#import "FollowingCustomCell.h"
#import "FavRestConsumer.h"
#import "AppDelegate.h"
#import "Fav24Colors.h"
#import "ProfileViewController.h"
#import "UserManager.h"
#import "CoreDataParsing.h"
#import "SearchManager.h"
#import "Conection.h"
#import "Follow.h"
#import "UIImage+ImageEffects.h"
#import "SyncManager.h"

@interface FindFriendsViewController ()<UISearchBarDelegate, UITableViewDataSource, UITableViewDelegate, UIActionSheetDelegate>{
    BOOL search;
    BOOL moreCells;
    BOOL refreshTable;
}

@property (nonatomic,strong)                UISearchBar                 *mySearchBar;
@property (nonatomic, strong)               NSMutableArray              *usersSearch;
@property (nonatomic,strong)                UILabel                     *lblFooter;
@property (nonatomic,strong)                UIActivityIndicatorView     *spinner;
@property (nonatomic,strong)                NSMutableArray              *followingUsers;
@property (nonatomic,strong)    IBOutlet    UITableView                 *usersTable;
@property (nonatomic,strong)                NSIndexPath                 *indexToShow;
@property (nonatomic, assign)               CGFloat                     lastContentOffset;
@property (nonatomic,weak)      IBOutlet    UIView                      *viewNotPeople;
@property (nonatomic,weak)      IBOutlet    UILabel                     *lblNotPeople;
@property (nonatomic,strong)                NSString                    *textInSearchBar;

@property (nonatomic,assign)       BOOL   followActionSuccess;


@end

@implementation FindFriendsViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    //Listen to orientation changes
    //[[NSNotificationCenter defaultCenter] addObserver:self  selector:@selector(orientationChanged:) name:UIDeviceOrientationDidChangeNotification  object:nil];
    
    // Do any additional setup after loading the view.
   
    [self initSpinner];
    self.usersSearch = [[NSMutableArray alloc]init];
    search = NO;
  
    self.usersTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
}

//------------------------------------------------------------------------------
-(void)viewWillAppear:(BOOL)animated{
	
	[super viewWillAppear:animated];
	
    [self addSearchNavBar];
    
    self.lblNotPeople.text =  NSLocalizedString(@"No Results", nil);
    //Background blur image under the modalview
    self.usersTable.backgroundView = [[UIImageView alloc] initWithImage:[self.backgroundImage applyBlurWithRadius:20
                                                                                                       tintColor:[UIColor colorWithWhite:1 alpha:0.5]
                                                                                           saturationDeltaFactor:1.8
                                                                                                       maskImage:nil]];
    
    [self.usersTable deselectRowAtIndexPath:self.indexToShow  animated:YES];

    [self.usersTable reloadData];
}

//------------------------------------------------------------------------------
- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];

    [self restoreInitialStateView];
}

//------------------------------------------------------------------------------
-(void)addSearchNavBar{
    self.mySearchBar = [PeopleLineUtilities createSearchNavBar];
    self.mySearchBar.placeholder = NSLocalizedString(@"Search People", nil);
    self.mySearchBar.delegate = self;
    [self.mySearchBar becomeFirstResponder];
    
    self.mySearchBar.text = self.textInSearchBar;

    [self.navigationController.navigationBar addSubview:self.mySearchBar];
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - TABLE VIEW

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 50;
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.followingUsers.count;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"followingCell";
    FollowingCustomCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    User *user = [self.followingUsers objectAtIndex:indexPath.row];
    
    [cell configurePeopleCellWithUser:user inRow:indexPath whileSearching:YES inPeople:NO];
    [cell addTarget:self action:@selector(goProfile:)];
    [cell addTargetBtnFollow:self action:@selector(followAndUnFollowUser:)];
    [cell addTargetBtnFollowing:self action:@selector(followAndUnFollowUser:)];

    return cell;
}

//------------------------------------------------------------------------------
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    self.indexToShow = indexPath;
    [self pushToProfileUser:self.usersSearch[indexPath.row]];
}

//------------------------------------------------------------------------------
- (void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (!refreshTable && search){
        
        self.spinner.hidden = YES;
        
        self.lblFooter =  [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.usersTable.frame.size.width, 44)];
        self.lblFooter.text = NSLocalizedString(@"No more people", nil);
        self.lblFooter.textColor = [Fav24Colors iosSevenGray];
        self.lblFooter.textAlignment = NSTextAlignmentCenter;
        self.lblFooter.backgroundColor = [UIColor clearColor];
        self.usersTable.tableFooterView = self.lblFooter;
        
    }else if (!search)
        self.lblFooter.text = NSLocalizedString(@"", nil);
}

#pragma mark - TableView Pagination
//------------------------------------------------------------------------------
- (void)addLoadMoreCell{
    if (search) {
        self.usersTable.tableFooterView = self.spinner;
        moreCells = NO;
        [[FavRestConsumer sharedInstance] searchPeopleWithName:self.mySearchBar.text withOffset:[NSNumber numberWithInteger:self.followingUsers.count] withDelegate:self];
    }
}

#pragma mark - FOLLOW AND UNFOLLOW
//------------------------------------------------------------------------------
- (void)followAndUnFollowUser:(id)sender{
    UIButton *btn = (UIButton *) sender;

    User *user = self.followingUsers[btn.tag];
    
    if ([btn.titleLabel.text isEqualToString:NSLocalizedString(@"+ FOLLOW", nil)]){
        self.followActionSuccess = [[UserManager singleton] startFollowingUser:user];
       
        if (self.followActionSuccess) {
            [[SyncManager sharedInstance] sendUpdatesToServerWithDelegate:self necessaryDownload:NO];
            [self reloadTable];
        }

    }else
        [self unfollow:user];
}

//------------------------------------------------------------------------------
-(void)unfollow:(User *)userUnfollow{

    UIAlertController *alertController = [UIAlertController
                                          alertControllerWithTitle:userUnfollow.userName
                                          message:nil
                                          preferredStyle:UIAlertControllerStyleActionSheet];
    
    
    UIAlertAction *unfollow = [UIAlertAction
                         actionWithTitle:NSLocalizedString(@"Unfollow", nil)
                         style:UIAlertActionStyleDestructive
                         handler:^(UIAlertAction * action)
                         {
                             
                             self.followActionSuccess = [[UserManager singleton] stopFollowingUser:userUnfollow];
                             if (self.followActionSuccess) {
                                 [[SyncManager sharedInstance] sendUpdatesToServerWithDelegate:self necessaryDownload:NO];
                                 
                                 [self performSelectorOnMainThread:@selector(reloadTable) withObject:nil waitUntilDone:NO];
                             }
                             
                             [alertController dismissViewControllerAnimated:YES completion:nil];
                             
                         }];
    
    UIAlertAction *cancel = [UIAlertAction
                         actionWithTitle:NSLocalizedString(@"Cancel", nil)
                         style:UIAlertActionStyleCancel
                         handler:^(UIAlertAction * action)
                         {
                             [alertController dismissViewControllerAnimated:YES completion:nil];
                             
                         }];

    [alertController addAction:unfollow];
    [alertController addAction:cancel];

    [self presentViewController:alertController animated:YES completion:nil];
}

//------------------------------------------------------------------------------
-(void)reloadTable{
    [self.usersTable reloadData];
}

#pragma mark - Navigation
//------------------------------------------------------------------------------
- (void)goProfile:(id)sender{
    
    UIButton *btn = (UIButton *) sender;
    [self pushToProfileUser:self.usersSearch[btn.tag]];
}

//------------------------------------------------------------------------------
- (void)pushToProfileUser:(User *)user {
    
    AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
    ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
    profileVC.selectedUser = user;
    [self.navigationController pushViewController:profileVC animated:YES];
}

#pragma mark - Search methods
//------------------------------------------------------------------------------
-(void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{

    self.textInSearchBar = nil;

    [self dismissViewControllerAnimated:YES completion:^{
        NSLog(@"vuelvo");
    }];
    
    search = NO;
}

//------------------------------------------------------------------------------
- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    
    search = YES;
    [self.usersSearch removeAllObjects];
    
    [[FavRestConsumer sharedInstance] searchPeopleWithName:searchBar.text withOffset:@0 withDelegate:self];
    self.followingUsers = [[NSMutableArray alloc] initWithArray:[SearchManager searchPeopleLocal:searchBar.text]];
    [self.mySearchBar resignFirstResponder];
    [self enableCancelButton];
    [self reloadTableWithAnimation];
    
    self.textInSearchBar = searchBar.text;
}

//------------------------------------------------------------------------------
-(void)enableCancelButton{
   
    for (UIView *view in self.mySearchBar.subviews)
    {
        for (id subview in view.subviews)
        {
            if ( [subview isKindOfClass:[UIButton class]] )
            {
                [subview setEnabled:YES];
                return;
            }
        }
    }
}

#pragma mark - Search Response method

//------------------------------------------------------------------------------
- (void)searchResponseWithStatus:(BOOL)status andError:(NSError *)error andUsers:(NSArray *)usersArray needToPaginate:(BOOL)pagination{
    
    if (usersArray.count > 0) {
        
        refreshTable = YES;
        
        [self constructArraysFromResponse:usersArray];
        
        if (pagination)
            [self addLoadMoreCell];
        else
            self.spinner.hidden = YES;
        
        [self reloadTableWithAnimation];
        
    }else if (!error && usersArray.count == 0) {
        refreshTable = YES;
        if (!self.usersSearch.count > 0){
            [self.followingUsers removeAllObjects];
            self.spinner.hidden = YES;
        }
    }else{
        refreshTable = NO;
    }
    
    [self checkIfNeedToShowNotPeopleView];
}


//------------------------------------------------------------------------------
- (void)constructArraysFromResponse:(NSArray *)usersArray {
    
    [self.usersSearch addObjectsFromArray:usersArray];
    
    [self.followingUsers removeAllObjects];
    
    [self.followingUsers addObjectsFromArray:self.usersSearch];
    
    [self sortArray];
}

//------------------------------------------------------------------------------
- (void)sortArray {
    
    NSSortDescriptor *valueDescriptor = [[NSSortDescriptor alloc] initWithKey:kJSON_NAME ascending:YES];
    NSArray *descriptors = [NSArray arrayWithObject:valueDescriptor];
    NSArray *sortedArray = [self.followingUsers  sortedArrayUsingDescriptors:descriptors];
    self.followingUsers = [sortedArray mutableCopy];
}

//------------------------------------------------------------------------------
- (void)checkIfNeedToShowNotPeopleView {
    
    if (self.followingUsers.count == 0){
    
        self.usersTable.hidden = YES;
        self.viewNotPeople.hidden = NO;
        
    }else{
        
        self.spinner.hidden = YES;
        self.usersTable.hidden = NO;
        self.viewNotPeople.hidden = YES;
        self.spinner.hidden = YES;
    }
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && !error){
        
        if ([entityClass isSubclassOfClass:[Follow class]])
            [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[User class] withDelegate:self];
        if ([entityClass isSubclassOfClass:[User class]]){
            if (search)
                [self performSelectorOnMainThread:@selector(reloadTable) withObject:nil waitUntilDone:NO];
        }
    }
}
#pragma mark - Reload methods

//------------------------------------------------------------------------------
- (void)reloadTableWithAnimation {
    
    [UIView transitionWithView: self.usersTable
                      duration: 0.25f
                       options: UIViewAnimationOptionTransitionCrossDissolve
                    animations: ^(void)
     {
         [self.usersTable reloadData];
     }
                    completion: ^(BOOL isFinished)
     {
     }];
}

#pragma mark - HELPER METHODS
//------------------------------------------------------------------------------
- (void)restoreInitialStateView {
    
    [self.mySearchBar setAlpha:0.0];
    
    [self.mySearchBar resignFirstResponder];
    [self.mySearchBar setText:@""];
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
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {

    CGFloat currentOffset = scrollView.contentOffset.y;
    CGFloat maximumOffset = scrollView.contentSize.height - scrollView.frame.size.height;
    
    if (currentOffset < 0)
        NSLog(@"");
    
    else{
        if (maximumOffset - currentOffset <= 200.0 && moreCells)
            [self addLoadMoreCell];
        
         if (self.lastContentOffset > scrollView.contentOffset.y){
             [UIView animateWithDuration:0.25 animations:^{
                 [self.mySearchBar resignFirstResponder];
                 [self enableCancelButton];
            }];
         }
    }
    
    self.lastContentOffset = scrollView.contentOffset.y;
}

//------------------------------------------------------------------------------
- (void)orientationChanged:(NSNotification *)notification{
        
//    CGRect screenRect = [[UIScreen mainScreen] applicationFrame];
//    
//    if (UIDeviceOrientationIsLandscape([[UIDevice currentDevice] orientation])){
//        if (screenRect.size.height > screenRect.size.width)
//            self.mySearchBar.frame = CGRectMake(screenRect.origin.x+12, 2, screenRect.size.height, 30);
//        else
//            self.mySearchBar.frame = CGRectMake(screenRect.origin.x+12, 2, screenRect.size.width-15, 30);
//    }
//    else if ([[UIDevice currentDevice] orientation] == 1){
//        if (screenRect.size.height > screenRect.size.width){
//            self.mySearchBar.frame = CGRectMake(screenRect.origin.x+12, 6, screenRect.size.width-15, 30);
//        }else{
//            self.mySearchBar.frame = CGRectMake(screenRect.origin.x+12, 2, screenRect.size.height-15, 30);
//            
//        }
//    }
}

@end
