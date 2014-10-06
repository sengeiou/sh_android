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
#import "FavRestConsumer.h"
#import "Follow.h"
#import "Conection.h"

@interface FollowingTableViewController ()

@property (nonatomic,strong) NSArray *usersList;
@property (nonatomic,weak) IBOutlet UITableView *usersTable;

@end

@implementation FollowingTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
	
	self.usersTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
    
    if ([self.viewSelected  isEqual: FOLLOWING_SELECTED]){
        self.title = @"Following";
	    self.usersList = [[UserManager singleton] getFollowingUsersOfUser:self.selectedUser];
        //[[FavRestConsumer sharedInstance] getFollowingUsersOfUser:self.selectedUser withDelegate:self];
    }
    else {
        self.title = @"Followers";
		self.usersList = [[UserManager singleton] getFollowersOfUser:self.selectedUser];
       // [[FavRestConsumer sharedInstance] getFollowersOfUser:self.selectedUser withDelegate:self];
    }

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

#pragma mark - Reload methods
//------------------------------------------------------------------------------
- (void)reloadDataAndTable {
    
    if ([self.viewSelected  isEqual: FOLLOWING_SELECTED]){
        self.usersList = [[UserManager singleton] getFollowingUsersOfUser:self.selectedUser];
        [self.usersTable reloadData];
    }
    else {
        self.usersList = [[UserManager singleton] getFollowersOfUser:self.selectedUser];
        [self.usersTable reloadData];
    }
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

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && !error){
        
        if ([entityClass isSubclassOfClass:[Follow class]])
            [[FavRestConsumer sharedInstance] getUsersFromUser:self.selectedUser withDelegate:self];
        if ([entityClass isSubclassOfClass:[User class]])
            [self reloadDataAndTable];
    }
}
#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status andRefresh:(BOOL)refresh withShot:(BOOL)isShot{

    if (status){
        if ([self.viewSelected  isEqual: FOLLOWING_SELECTED])
            [[FavRestConsumer sharedInstance] getFollowingUsersOfUser:self.selectedUser withDelegate:self];
        
        else
            [[FavRestConsumer sharedInstance] getFollowersOfUser:self.selectedUser withDelegate:self];
    }
}

@end
