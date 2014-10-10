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


@interface PeopleTableViewController ()<UISearchBarDelegate, UISearchDisplayDelegate, UITableViewDataSource, UITableViewDelegate>

@property (nonatomic,strong) NSArray *followingUsers;
@property (nonatomic,strong) IBOutlet UITableView *usersTable;
@property (nonatomic, strong)       NSIndexPath         *indexToShow;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *btnAddFriends;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *btnSearchFriends;
@property (nonatomic, strong)       NSMutableArray             *searchResults;
//@property (nonatomic, strong) IBOutlet UISearchDisplayController *searchDisplay;
@property (nonatomic, strong) IBOutlet UISearchBar *mySearchBar;
@property (nonatomic, strong) UITableView *searchTableView;

- (IBAction)addFriends:(id)sender;
- (IBAction)searchFriends:(id)sender;

@end

@implementation PeopleTableViewController

- (id)initWithStyle:(UITableViewStyle)style {
	
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad{
	
    [super viewDidLoad];

	self.usersTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    
    self.followingUsers = [[UserManager singleton] getFollowingPeopleForMe];
    
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
    
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    [self.usersTable deselectRowAtIndexPath:self.indexToShow  animated:YES];

}
- (IBAction)addPeople:(id)sender {
}

#pragma mark - Table view data source

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 50;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if (tableView == self.searchDisplayController.searchResultsTableView)
        return [self.searchResults count];
    
    return self.followingUsers.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	
	static NSString *CellIdentifier = @"peopleCell";
	PeopleCustomCell *cell = (id) [self.usersTable dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    User *user;
    
    if (tableView == self.searchDisplayController.searchResultsTableView)
        user = [self.searchResults objectAtIndex:indexPath.row];

    else
        user = [self.followingUsers objectAtIndex:indexPath.row];


	[cell configureCellWithUser:user inRow:indexPath];
    [cell addTarget:self action:@selector(goProfile:)];

	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
 
    AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
    ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
    User *selectedUser = self.followingUsers[indexPath.row];
    profileVC.selectedUser = selectedUser;
    
    self.indexToShow = indexPath;
    [self.navigationController pushViewController:profileVC animated:YES];
}

#pragma mark - Navigation
//------------------------------------------------------------------------------
-(void)goProfile:(id)sender{
    
    UIButton *btn = (UIButton *) sender;
    AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
    ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
    User *selectedUser = self.followingUsers[btn.tag];
    profileVC.selectedUser = selectedUser;
    [self.navigationController pushViewController:profileVC animated:YES];
}

#pragma mark - Reload methods
//------------------------------------------------------------------------------
- (void)reloadDataAndTable {
    
    self.followingUsers = [[UserManager singleton] getFollowingPeopleForMe];
    [self.usersTable reloadData];
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

- (void)searchResponseWithStatus:(BOOL)status andError:(NSError *)error andUsers:(NSArray *)usersArray {
    
    if (usersArray.count > 0) {
        
    }
}

- (IBAction)addFriends:(id)sender {
}

- (IBAction)searchFriends:(id)sender {
  
//    self.navigationItem.rightBarButtonItem = nil;

   // self.mySearchBar = [PeopleLineUtilities createSearchNavBar];
    //self.mySearchBar.delegate = self;
//    [self.navigationController.navigationBar addSubview:self.searchDisplayController.searchBar];
  //  self.searchDisplay =  [[UISearchDisplayController alloc] initWithSearchBar:self.mySearchBar contentsController:self];

   // [self setSearchDisplayController:searchDisplay];
//    [self.searchDisplay setDelegate:self];
//    self.searchDisplay.searchResultsDelegate = self;
//    self.searchDisplay.searchResultsDataSource = self;
}

//-(void)searchBar:(UISearchBar*)searchBar textDidChange:(NSString*)text
//{
//    self.searchResults = [SearchManager searchPeopleLocal:text];
//}
//
//-(void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
//    
//    self.navigationController.navigationBarHidden = NO;
//    [mySearchBar setAlpha:0.0];
//
//    [SearchManager searchPeopleLocal:@""];
//    
//    [mySearchBar resignFirstResponder];
//    [mySearchBar setText:@""];
//    [self.usersTable reloadData];
//}
//
//- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar
//{
//    
//    [mySearchBar setShowsCancelButton:YES animated:YES];
//}
//
//-(void)searchBarTextDidEndEditing:(UISearchBar *)searchBar
//{
//    [mySearchBar setShowsCancelButton:NO animated:YES];
//}



#pragma mark - UISearchDisplayControllerDelegate


- (void)filterContentForSearchText:(NSString*)searchText scope:(NSString*)scope {
  
    [self.searchResults removeAllObjects]; // First clear the filtered array.
    
    self.searchResults = [[NSMutableArray alloc] initWithArray:[SearchManager searchPeopleLocal:searchText]];
}

- (BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchString:(NSString *)searchString {
    [self filterContentForSearchText:searchString scope:
     [[self.searchDisplayController.searchBar scopeButtonTitles] objectAtIndex:[self.searchDisplayController.searchBar selectedScopeButtonIndex]]];
    // Return YES to cause the search result table view to be reloaded.
    return YES;
}

- (BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchScope:(NSInteger)searchOption {
    [self filterContentForSearchText:[self.searchDisplayController.searchBar text] scope:
     [[self.searchDisplayController.searchBar scopeButtonTitles] objectAtIndex:searchOption]];
    // Return YES to cause the search result table view to be reloaded.
    return YES;
}

//- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
//    [self.searchResults removeAllObjects]; // First clear the filtered array.
//    
//    self.searchResults = [[NSMutableArray alloc] initWithArray:[SearchManager searchPeopleLocal:searchBar.text]];
//    [self.usersTable reloadData];
//
//}

- (void)searchDisplayControllerDidBeginSearch:(UISearchDisplayController *)controller {
    [controller setSearchResultsDelegate:self.usersTable.delegate];
}

- (void)searchDisplayControllerDidEndSearch:(UISearchDisplayController *)controller {

}


//-(BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchString:(NSString *)searchString
//{
//    
////  [self.searchResults delete:<#(id)#>]
//    self.searchResults = [SearchManager searchPeopleLocal:searchString];
//    
////    [self filterContentForSearchText:searchString
////                               scope:[[self.searchDisplayController.searchBar scopeButtonTitles]
////                                     objectAtIndex:[self.searchDisplayController.searchBar
////                                                     selectedScopeButtonIndex]]];
//    
//    return YES;
//}
//- (void)searchDisplayController:(UISearchDisplayController *)controller didLoadSearchResultsTableView:(UITableView *)tableView{
//    
//    self.searchTableView = self.usersTable;
//
//}
//
//- (void)searchDisplayControllerWillBeginSearch:(UISearchDisplayController *)controller {
//    //When the user taps the search bar, this means that the controller will begin searching.
//}
//
//- (void)searchDisplayControllerWillEndSearch:(UISearchDisplayController *)controller {
//    //When the user taps the Cancel Button, or anywhere aside from the view.
//}
//
//- (BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchScope:(NSInteger)searchOption{
//   
//    self.searchResults = [SearchManager searchPeopleLocal:[self.searchDisplay.searchBar text]];
//
//    return YES;
//}
//
//- (void)searchBarCancelButtonClicked:(UISearchBar *) searchBar {
//    self.navigationItem.rightBarButtonItem = self.btnAddFriends;
//
//    self.navigationController.navigationBarHidden = NO;
//    [self.mySearchBar setAlpha:0.0];
//}
//
//-(void)viewWillDisappear:(BOOL)animated{
//    self.navigationItem.rightBarButtonItem = self.btnAddFriends;
//
//    self.navigationController.navigationBarHidden = NO;
//    [self.mySearchBar setAlpha:0.0];
//}

@end
