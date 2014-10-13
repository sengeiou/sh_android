//
//  GenteTableViewController.m
//
//  Created by Christian Cabarrocas on 11/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "PeopleTableViewController.h"
#import "UserManager.h"
#import "PeopleCustomCell.h"
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

@interface PeopleTableViewController ()<UISearchBarDelegate, UITableViewDataSource, UITableViewDelegate>

@property (nonatomic,strong)                NSMutableArray  *followingUsers;
@property (nonatomic,strong)    IBOutlet    UITableView     *usersTable;
@property (nonatomic,strong)                NSIndexPath     *indexToShow;
@property (nonatomic,weak)      IBOutlet    UIBarButtonItem *btnAddFriends;
@property (nonatomic,weak)      IBOutlet    UIBarButtonItem *btnSearchFriends;
@property (nonatomic,strong)                UISearchBar     *mySearchBar;

- (IBAction)addFriends:(id)sender;
- (IBAction)searchFriends:(id)sender;

@end

@implementation PeopleTableViewController

//------------------------------------------------------------------------------
- (id)initWithStyle:(UITableViewStyle)style {
	
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad{
	
    [super viewDidLoad];

	self.usersTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    
    self.followingUsers = [[[UserManager singleton] getFollowingPeopleForMe] mutableCopy];
    
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
    
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
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
	
	static NSString *CellIdentifier = @"peopleCell";
	PeopleCustomCell *cell = (id) [self.usersTable dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
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
    
    [UIView transitionWithView: self.tableView
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
    }
}

#pragma mark - Search methods
//------------------------------------------------------------------------------
- (IBAction)searchFriends:(id)sender {
  
    //self.navigationItem.rightBarButtonItem = nil;
    
    self.mySearchBar = [PeopleLineUtilities createSearchNavBar];
    self.mySearchBar.delegate = self;
    [self.mySearchBar becomeFirstResponder];
    [self.navigationController.navigationBar addSubview:self.mySearchBar];

}

//------------------------------------------------------------------------------
-(void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
   
    UIBarButtonItem *addButtonItem =  [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addUser:)];
    self.navigationItem.rightBarButtonItem = addButtonItem;

    [self restoreInitialStateView];
    [self reloadTableWithAnimation];
}

//------------------------------------------------------------------------------
- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    
    [[FavRestConsumer sharedInstance] searchPeopleWithName:searchBar.text withOffset:@0 withDelegate:self];
    [self.followingUsers removeAllObjects]; // First clear the filtered array.
//    [self.mySearchBar resignFirstResponder];
    self.followingUsers = [[NSMutableArray alloc] initWithArray:[SearchManager searchPeopleLocal:searchBar.text]];
    [self reloadTableWithAnimation];

}

#pragma mark - HELPER METHODS
//------------------------------------------------------------------------------
- (void)restoreInitialStateView {

    self.navigationItem.rightBarButtonItem = self.btnAddFriends;
    [self.mySearchBar setAlpha:0.0];
    self.followingUsers = [[[UserManager singleton] getFollowingPeopleForMe] mutableCopy];;
    [self.mySearchBar resignFirstResponder];
    [self.mySearchBar setText:@""];
}

@end
