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
#import "CoreDataManager.h"
#import "SyncManager.h"

@interface FollowingTableViewController () <UIActionSheetDelegate>

@property (nonatomic,strong) NSArray *usersList;
@property (nonatomic,weak) IBOutlet UITableView *usersTable;
@property (nonatomic,strong)       NSIndexPath         *indexToShow;
@property (nonatomic,assign)       BOOL   followActionSuccess;
@property (nonatomic,strong)       User   *userFollowUnfollow;

@end

@implementation FollowingTableViewController

#pragma mark - View Lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
	
	self.usersTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    [self reloadDataMainThread];

    [self getUsersServer];
    

    [self setNavigationBarTitle];
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    
    [self loadLocalDataInArrays];
    
    [self.usersTable deselectRowAtIndexPath:self.indexToShow  animated:YES];
}

#pragma mark - HELPERS
//------------------------------------------------------------------------------
- (void)loadLocalDataInArrays {
    
    if ([self.viewSelected  isEqual: FOLLOWING_SELECTED])
        self.usersList = [[UserManager singleton] getFollowingUsersOfUser:self.selectedUser];
    else if ([self.viewSelected  isEqual: FOLLOWERS_SELECTED])
        self.usersList = [[UserManager singleton] getFollowersOfUser:self.selectedUser];
    NSLog(@"Users list count in local DB:%lu",(unsigned long)self.usersList.count);
    
    [self performSelectorOnMainThread:@selector(reloadDataMainThread) withObject:nil waitUntilDone:NO];

}

-(void)getUsersServer{
    if ([self.viewSelected  isEqual: FOLLOWING_SELECTED])
        [[FavRestConsumer sharedInstance] getFollowingUsersOfUser:self.selectedUser withDelegate:self];
    
    else if ([self.viewSelected  isEqual: FOLLOWERS_SELECTED])
        [[FavRestConsumer sharedInstance] getFollowersOfUser:self.selectedUser withDelegate:self];
}

- (void)reloadDataMainThread{
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
    self.userFollowUnfollow = self.usersList[btn.tag];
        
    if ([btn.titleLabel.text isEqualToString:NSLocalizedString(@"+ FOLLOW", nil)]) {
        self.followActionSuccess = [[UserManager singleton] startFollowingUser:self.userFollowUnfollow];
        [self reloadTable];
    }
    else
        [self unfollow:self.userFollowUnfollow];
    
}

-(void)unfollow:(User *)userUnfollow{
//    UIAlertController * alert=   [UIAlertController
//                                  alertControllerWithTitle:userUnfollow.userName
//                                  message:nil
//                                  preferredStyle:UIAlertControllerStyleActionSheet];
//    
//    UIAlertAction* cancel = [UIAlertAction
//                         actionWithTitle:NSLocalizedString(@"Cancel", nil)
//                         style:UIAlertActionStyleDefault
//                         handler:^(UIAlertAction * action)
//                         {
//                             [alert dismissViewControllerAnimated:YES completion:nil];
//                            
//                             self.followActionSuccess = NO;
//                         }];
//    
//    UIAlertAction* unfollow = [UIAlertAction
//                             actionWithTitle:NSLocalizedString(@"Unfollow", nil)
//                             style:UIAlertActionStyleDestructive
//                             handler:^(UIAlertAction * action)
//                             {
//                                 
//                                 self.followActionSuccess = [[UserManager singleton] stopFollowingUser:userUnfollow];
//                                 [self performSelectorOnMainThread:@selector(reloadTable) withObject:nil waitUntilDone:NO];
//                                 
//                                 [alert dismissViewControllerAnimated:YES completion:nil];
//                                 
//                             }];
//
//    
//    [alert addAction:unfollow];
//
//    [alert addAction:cancel];
//
//    [self presentViewController:alert animated:YES completion:nil];
    UIActionSheet *actionShet = [[UIActionSheet alloc] initWithTitle:userUnfollow.userName delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel", nil) destructiveButtonTitle:NSLocalizedString(@"Unfollow", nil) otherButtonTitles: nil];
    actionShet.actionSheetStyle = UIActionSheetStyleDefault;
    [actionShet showInView:self.view];
    
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex{
    
    if (buttonIndex == 0) {
        self.followActionSuccess = [[UserManager singleton] stopFollowingUser:self.userFollowUnfollow];
        [self performSelectorOnMainThread:@selector(reloadTable) withObject:nil waitUntilDone:NO];
        
    }
}

-(void)reloadTable{
    if (self.followActionSuccess)
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
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && !error){
        
        if ([entityClass isSubclassOfClass:[Follow class]]){
            
            if ([self.viewSelected  isEqual: FOLLOWING_SELECTED])
                [[FavRestConsumer sharedInstance] getUsersFromUser:self.selectedUser withDelegate:self withTypeOfUsers: FOLLOWING_SELECTED];
            else
                [[FavRestConsumer sharedInstance] getUsersFromUser:self.selectedUser withDelegate:self withTypeOfUsers: FOLLOWERS_SELECTED];
            
        }if ([entityClass isSubclassOfClass:[User class]])
            [self loadLocalDataInArrays];
    }
}

@end
