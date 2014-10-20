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
#import "CoreDataManager.h"

@interface FollowingTableViewController ()

@property (nonatomic,strong) NSArray *usersList;
@property (nonatomic,weak) IBOutlet UITableView *usersTable;
@property (nonatomic,strong)       NSIndexPath         *indexToShow;

@end

@implementation FollowingTableViewController

#pragma mark - View Lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
	
	self.usersTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
    
    [self setNavigationBarTitle];
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    
    [self reloadDataAndTable];
    
    [self.usersTable deselectRowAtIndexPath:self.indexToShow  animated:YES];
}

#pragma mark - HELPERS
//------------------------------------------------------------------------------
- (void)loadLocalDataInArrays {
    
    if ([self.viewSelected  isEqual: FOLLOWING_SELECTED])
        self.usersList = [[UserManager singleton] getFollowingUsersOfUser:self.selectedUser];
    else if ([self.viewSelected  isEqual: FOLLOWERS_SELECTED])
        self.usersList = [[UserManager singleton] getFollowersOfUser:self.selectedUser];
    
    [self.usersTable reloadData];
}

//------------------------------------------------------------------------------
- (void)setNavigationBarTitle {
    
    if ([self.viewSelected  isEqual: FOLLOWING_SELECTED])
        self.title = NSLocalizedString(@"Following", nil);
    else if ([self.viewSelected  isEqual: FOLLOWERS_SELECTED])
        self.title = NSLocalizedString(@"Followers", nil);
}

#pragma mark - TABLEVIEW
//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.usersList.count;
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 50;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"followingCell";    
    FollowingCustomCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    User *user = [self.usersList objectAtIndex:indexPath.row];
    [cell configurePeopleCellWithUser:user inRow:indexPath whileSearching:NO inPeople:NO];
    [cell addTarget:self action:@selector(goProfile:)];
    [cell addTargetBtnFollow:self action:@selector(followUser:)];
    [cell addTargetBtnFollowing:self action:@selector(followUser:)];

    return cell;
}

//------------------------------------------------------------------------------
- (void)followUser:(id)sender{
    UIButton *btn = (UIButton *) sender;
    User *userFollow = self.usersList[btn.tag];
    
    User *activeUser = [[UserManager sharedInstance] getActiveUser];
    
    BOOL followActionSuccess;
    if ([btn.titleLabel.text isEqualToString:NSLocalizedString(@"+ FOLLOW", nil)]){
        followActionSuccess = [[UserManager singleton] startFollowingUser:userFollow];
        [[UserManager sharedInstance]setNumberFollowings:[NSNumber numberWithInt:[activeUser.numFollowing intValue] + 1]];
    }else{
        followActionSuccess = [[UserManager singleton] stopFollowingUser:userFollow];
        [[UserManager sharedInstance]setNumberFollowings: [NSNumber numberWithInt:[activeUser.numFollowing intValue] - 1]];

    }
    if (followActionSuccess)
        [self.usersTable reloadData];
}

//------------------------------------------------------------------------------
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
    ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
    User *selectedUser = self.usersList[indexPath.row];
    profileVC.selectedUser = selectedUser;
    
    self.indexToShow = indexPath;

    [self.navigationController pushViewController:profileVC animated:YES];
}

#pragma mark - RELOAD
//------------------------------------------------------------------------------
- (void)reloadDataAndTable {
    
    [self loadLocalDataInArrays];
    [self.usersTable reloadData];
}

#pragma mark - NAVIGATION
//------------------------------------------------------------------------------
-(void)goProfile:(id)sender{
	
	UIButton *btn = (UIButton *) sender;
	AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
	ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
	User *selectedUser = self.usersList[btn.tag];
	profileVC.selectedUser = selectedUser;
	[self.navigationController pushViewController:profileVC animated:YES];
}

#pragma mark - DATASERVICE RESPONSE
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status andRefresh:(BOOL)refresh withShot:(BOOL)isShot{
    
    if (status){
        if ([self.viewSelected  isEqual: FOLLOWING_SELECTED])
            [[FavRestConsumer sharedInstance] getFollowingUsersOfUser:self.selectedUser withDelegate:self];
        
        else if ([self.viewSelected  isEqual: FOLLOWERS_SELECTED])
            [[FavRestConsumer sharedInstance] getFollowersOfUser:self.selectedUser withDelegate:self];
    }
}

//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && !error){
        
        if ([entityClass isSubclassOfClass:[Follow class]]){
            
            if ([self.viewSelected  isEqual: FOLLOWING_SELECTED])
                [[FavRestConsumer sharedInstance] getUsersFromUser:self.selectedUser withDelegate:self withTypeOfUsers: FOLLOWING_SELECTED];
            else
                [[FavRestConsumer sharedInstance] getUsersFromUser:self.selectedUser withDelegate:self withTypeOfUsers: FOLLOWERS_SELECTED];

        }if ([entityClass isSubclassOfClass:[User class]])
            [self performSelectorOnMainThread:@selector(reloadDataAndTable) withObject:nil waitUntilDone:NO];
    }
}

@end
