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
#import "PeopleLineUtilities.h"
#import "FindFriendsViewController.h"
#import "Utils.h"

@interface PeopleTableViewController ()<UITableViewDataSource, UITableViewDelegate>

@property (nonatomic,strong)                NSMutableArray  *followingUsers;
@property (nonatomic,strong)    IBOutlet    UITableView     *usersTable;
@property (nonatomic,strong)                NSIndexPath     *indexToShow;
@property (nonatomic,weak)      IBOutlet    UIBarButtonItem *btnAddFriends;
@property (nonatomic,weak)      IBOutlet    UIBarButtonItem *btnSearchFriends;

- (IBAction)addFriends:(id)sender;
- (IBAction)searchFriends:(id)sender;

@end

@implementation PeopleTableViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad{
	
    [super viewDidLoad];

    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated {
    
    [super viewWillAppear:animated];
    self.title = NSLocalizedString(@"People", nil);
    
    [self addButtonsItem];
    
    self.followingUsers = [[[UserManager singleton] getFollowingPeopleForMe] mutableCopy];

    [self.usersTable reloadData];

    [self.usersTable deselectRowAtIndexPath:self.indexToShow  animated:YES];
}

//------------------------------------------------------------------------------
-(void)addButtonsItem {
    
    UIBarButtonItem *addButtonItem =  [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addFriends:)];
    self.navigationItem.rightBarButtonItem = addButtonItem;
    
    UIBarButtonItem *findButtonItem =  [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:self action:@selector(searchFriends:)];
    self.navigationItem.leftBarButtonItem = findButtonItem;
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

    [cell configurePeopleCellWithUser:user inRow:indexPath whileSearching:NO inPeople:YES];
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
        if ([entityClass isSubclassOfClass:[User class]]){
            [self performSelectorOnMainThread:@selector(reloadDataAndTable) withObject:nil waitUntilDone:NO];
        }
    }
}


#pragma mark - SEARCH PEOPLE
//------------------------------------------------------------------------------
- (IBAction)searchFriends:(id)sender {

    FindFriendsViewController *findFriendsVC = [self.storyboard instantiateViewControllerWithIdentifier:@"findFriendsVC"];
    UINavigationController *navFindFriendsVC = [[UINavigationController alloc]initWithRootViewController:findFriendsVC];
    findFriendsVC.backgroundImage = [Utils getUIImageFromUIView:self.view];
    [self presentViewController:navFindFriendsVC animated:YES completion:^{
    }];

}


#pragma mark - ADD PEOPLE
//------------------------------------------------------------------------------
- (IBAction)addFriends:(id)sender {
}
@end
