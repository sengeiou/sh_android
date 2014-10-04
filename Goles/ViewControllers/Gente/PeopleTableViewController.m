//
//  GenteTableViewController.m
//  Goles
//
//  Created by Christian Cabarrocas on 11/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "PeopleTableViewController.h"
#import "UserManager.h"
#import "FollowingCustomCell.h"
#import "ProfileViewController.h"
#import "AppDelegate.h"

@interface PeopleTableViewController ()

@property (nonatomic,strong) NSArray *followingUsers;
@property (nonatomic,strong) IBOutlet UITableView *usersTable;

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
    [self restrictRotation:YES];

	self.usersTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    self.followingUsers = [[UserManager singleton] getFollowingUsersOfUser:[[UserManager singleton] getActiveUser]];

}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	
    return self.followingUsers.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	
	static NSString *CellIdentifier = @"followingCell";
	FollowingCustomCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
	User *user = [self.followingUsers objectAtIndex:indexPath.row];
	[cell configureCellWithUser:user inRow:indexPath];
	[cell addTarget:self action:@selector(goProfile:)];
	
	return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
	return 60;
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

#pragma mark - Webservice response methods
-(void) restrictRotation:(BOOL) restriction
{
    AppDelegate* appDelegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    appDelegate.restrictRotation = restriction;
}

@end
