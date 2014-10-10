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
#import "SearchManager.h"

@interface PeopleTableViewController ()

@property (nonatomic,strong) NSArray *followingUsers;
@property (nonatomic,strong) IBOutlet UITableView *usersTable;
@property (nonatomic, strong)       NSIndexPath         *indexToShow;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *btnAddFriends;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *btnSearchFriends;

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
    
#warning TESTING PURPOSES
    [[FavRestConsumer sharedInstance] searchPeopleWithName:@"A" withDelegate:self];
    

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
	
    return self.followingUsers.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	
	static NSString *CellIdentifier = @"peopleCell";
	PeopleCustomCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
	User *user = [self.followingUsers objectAtIndex:indexPath.row];
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
    
    FindFriendsTableViewController *findFriendsVC = [self.storyboard instantiateViewControllerWithIdentifier:@"findFriendsVC"];
    findFriendsVC.followingArray = self.followingUsers;
    
    [self presentViewController:findFriendsVC animated:YES completion:^{
        NSLog(@"a buscaaar");
    }];
}
@end
