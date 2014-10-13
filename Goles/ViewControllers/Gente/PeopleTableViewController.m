//
//  GenteTableViewController.m
//
//  Created by Christian Cabarrocas on 11/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "PeopleTableViewController.h"
#import "UserManager.h"
#import "FollowingCustomCell.h"
#import "ProfileViewController.h"
#import "AppDelegate.h"
#import "Conection.h"
#import "FavRestConsumer.h"
#import "User.h"
#import "Follow.h"
#import "FindFriendsTableViewController.h"
#import "PeopleLineUtilities.h"
#import "SearchManager.h"
#import "CoreDataParsing.h"
#import "Fav24Colors.h"

@interface PeopleTableViewController ()<UISearchBarDelegate, UITableViewDataSource, UITableViewDelegate> {

    BOOL moreCells;
    BOOL refreshTable;

}

@property (nonatomic,strong)                NSMutableArray  *followingUsers;
@property (nonatomic,strong)    IBOutlet    UITableView     *usersTable;
@property (nonatomic,strong)                NSIndexPath     *indexToShow;
@property (nonatomic,weak)      IBOutlet    UIBarButtonItem *btnAddFriends;
@property (nonatomic,weak)      IBOutlet    UIBarButtonItem *btnSearchFriends;
@property (nonatomic,strong)                UISearchBar     *mySearchBar;
@property (nonatomic,strong)                UILabel         *lblFooter;
@property (nonatomic,strong)                UIActivityIndicatorView     *spinner;
@property (nonatomic, assign)               CGFloat                     lastContentOffset;
@property (nonatomic,weak)      IBOutlet    UIView  *viewNotPeople;
@property (nonatomic,weak)      IBOutlet    UILabel  *lblNotPeople;

- (IBAction)addFriends:(id)sender;
- (IBAction)searchFriends:(id)sender;

@end

@implementation PeopleTableViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad{
	
    [super viewDidLoad];

	self.usersTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    
    self.followingUsers = [[[UserManager singleton] getFollowingPeopleForMe] mutableCopy];
    
    //Listen to orientation changes
    [[NSNotificationCenter defaultCenter] addObserver:self  selector:@selector(orientationChanged:) name:UIDeviceOrientationDidChangeNotification  object:nil];
    
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
    
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.title = @"People";
    [self.usersTable deselectRowAtIndexPath:self.indexToShow  animated:YES];
    #warning Used to force reload table when pushed from search
    [self.usersTable reloadData];
}

//------------------------------------------------------------------------------
- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    [self restoreInitialStateView];
}

#pragma mark - ADD PEOPLE

//------------------------------------------------------------------------------
- (IBAction)addFriends:(id)sender {
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

	[cell configureCellWithUser:user inRow:indexPath];
    [cell addTarget:self action:@selector(goProfile:)];

	return cell;
}

//------------------------------------------------------------------------------
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
 
    self.indexToShow = indexPath;
    [self pushToProfileUser:self.followingUsers[indexPath.row]];
}

//------------------------------------------------------------------------------
- (void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (!refreshTable){
        self.spinner.hidden = YES;
        
        self.lblFooter =  [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.usersTable.frame.size.width, 44)];
        self.lblFooter.text = NSLocalizedString(@"No more people", nil);
        self.lblFooter.textColor = [Fav24Colors iosSevenGray];
        self.lblFooter.textAlignment = NSTextAlignmentCenter;
        self.lblFooter.backgroundColor = [UIColor clearColor];
        self.usersTable.tableFooterView = self.lblFooter;
    }
}

//------------------------------------------------------------------------------
- (void)addLoadMoreCell{
    self.usersTable.tableFooterView = self.spinner;
    
    moreCells = NO;
    [[FavRestConsumer sharedInstance] searchPeopleWithName:self.mySearchBar.text withOffset:[NSNumber numberWithInt:self.followingUsers.count+1] withDelegate:self];
    
}

#pragma mark - Navigation
//------------------------------------------------------------------------------
- (void)goProfile:(id)sender{
    
    UIButton *btn = (UIButton *) sender;
    [self pushToProfileUser:self.followingUsers[btn.tag]];
}

//------------------------------------------------------------------------------
- (void)pushToProfileUser:(User *)user {

    [self restoreInitialStateView];

    AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
    ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
    profileVC.selectedUser = user;
    [self.navigationController pushViewController:profileVC animated:YES];
}

#pragma mark - Reload methods
//------------------------------------------------------------------------------
- (void)reloadDataAndTable {
    
    self.followingUsers = [[[UserManager singleton] getFollowingPeopleForMe] mutableCopy];
    [self.usersTable reloadData];
}

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

#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status andRefresh:(BOOL)refresh withShot:(BOOL)isShot{
    
    if (status)
        [[FavRestConsumer sharedInstance] getFollowingUsersOfUser:[[UserManager singleton] getActiveUser] withDelegate:self];

}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && !error){
        
        if ([entityClass isSubclassOfClass:[Follow class]])
            [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[User class] withDelegate:self];
        if ([entityClass isSubclassOfClass:[User class]])
            [self reloadDataAndTable];
    }
}

#pragma mark - Search Response method
//------------------------------------------------------------------------------
- (void)searchResponseWithStatus:(BOOL)status andError:(NSError *)error andUsers:(NSArray *)usersArray {
    
    if (usersArray.count > 0) {
        
        [self.followingUsers removeAllObjects];
        NSSortDescriptor *valueDescriptor = [[NSSortDescriptor alloc] initWithKey:kJSON_NAME ascending:YES];
        NSArray *descriptors = [NSArray arrayWithObject:valueDescriptor];
        NSArray *sortedArray = [usersArray sortedArrayUsingDescriptors:descriptors];
        self.followingUsers = [sortedArray mutableCopy];
        
        [self reloadTableWithAnimation];
    }else{
        self.usersTable.hidden = YES;
        self.viewNotPeople.hidden = NO;
    }
}

#pragma mark - Search methods
//------------------------------------------------------------------------------
- (IBAction)searchFriends:(id)sender {
  
    self.navigationItem.rightBarButtonItem = nil;
    self.navigationItem.leftBarButtonItem = nil;

    self.mySearchBar = [PeopleLineUtilities createSearchNavBar];
    self.mySearchBar.delegate = self;
    [self.mySearchBar becomeFirstResponder];
    [self.navigationController.navigationBar addSubview:self.mySearchBar];

}

//------------------------------------------------------------------------------
-(void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
   
    self.usersTable.hidden = NO;
    self.viewNotPeople.hidden = YES;
    
    UIBarButtonItem *addButtonItem =  [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addUser:)];
    self.navigationItem.rightBarButtonItem = addButtonItem;

    UIBarButtonItem *findButtonItem =  [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:self action:@selector(searchFriends:)];
    self.navigationItem.leftBarButtonItem = findButtonItem;
    
    [self restoreInitialStateView];
    [self reloadTableWithAnimation];
}

//------------------------------------------------------------------------------
- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    
    [[FavRestConsumer sharedInstance] searchPeopleWithName:searchBar.text withOffset:@0 withDelegate:self];
    [self.followingUsers removeAllObjects]; // First clear the filtered array.
    self.followingUsers = [[NSMutableArray alloc] initWithArray:[SearchManager searchPeopleLocal:searchBar.text]];
    [self reloadTableWithAnimation];

}


#pragma mark - HELPER METHODS
//------------------------------------------------------------------------------
- (void)restoreInitialStateView {

    [self.mySearchBar setAlpha:0.0];
    
    self.followingUsers = [[[UserManager singleton] getFollowingPeopleForMe] mutableCopy];;
    [self.mySearchBar resignFirstResponder];
    [self.mySearchBar setText:@""];
}

//------------------------------------------------------------------------------
- (void)orientationChanged:(NSNotification *)notification{
    
    CGRect screenRect = self.usersTable.frame;

    if (UIDeviceOrientationIsLandscape([[UIDevice currentDevice] orientation]))
        self.mySearchBar.frame = CGRectMake(screenRect.origin.x+12, 2, screenRect.size.height-15, 30);
    else if (UIDeviceOrientationIsPortrait([[UIDevice currentDevice] orientation]))
        self.mySearchBar.frame = CGRectMake(screenRect.origin.x+12, 6, screenRect.size.height-15, 30);

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
    }
    
    self.lastContentOffset = scrollView.contentOffset.y;
}

@end
