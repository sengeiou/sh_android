//
//  FollowingViewControllerTableViewController.m
//  Shootr
//
//  Created by Christian Cabarrocas on 03/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FollowingTableViewController.h"
#import "FollowingCustomCell.h"
#import "UserManager.h"
#import "ProfileViewController.h"
#import "AppDelegate.h"
#import "Constants.h"

@interface FollowingTableViewController ()

@property (nonatomic,strong) NSArray *usersList;
@property (nonatomic,weak) IBOutlet UITableView *usersTable;

@end

@implementation FollowingTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
	
	self.usersTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
	
	if ([self.viewSelected  isEqual: FOLLOWING_SELECTED])
	    self.usersList = [[UserManager singleton] getFollowingUsersOfUser:self.selectedUser];
	else
		self.usersList = [[UserManager singleton] getFollowersOfUser:self.selectedUser];
	
	if ([self.viewSelected  isEqual: FOLLOWING_SELECTED])
		self.title = @"Following";
	else
		self.title = @"Followers";
}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.usersList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"followingCell";    
    FollowingCustomCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    User *user = [self.usersList objectAtIndex:indexPath.row];
    [cell configureCellWithUser:user inRow:indexPath];
    [cell addTarget:self action:@selector(goProfile:)];
	
    return cell;
}


#pragma mark - Navigation
//------------------------------------------------------------------------------
-(void)goProfile:(id)sender{
	
	UIButton *btn = (UIButton *) sender;
	AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
	ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
	User *selectedUser = self.usersList[btn.tag];
	profileVC.selectedUser = selectedUser;
	[self.navigationController pushViewController:profileVC animated:YES];
}


@end
