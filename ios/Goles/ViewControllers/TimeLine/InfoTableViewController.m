//
//  InfoTableViewTableViewController.m
//  Shootr
//
//  Created by Christian Cabarrocas on 11/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "InfoTableViewController.h"
#import "InfoCustomCell.h"
#import "User.h"
#import "UserManager.h"
#import "InfoUtilities.h"
#import "Match.h"

@interface InfoTableViewController ()

@property (nonatomic,strong)                NSArray         *usersArray;
@property (nonatomic,strong)    IBOutlet    UITableView     *infoTableView;

@end

@implementation InfoTableViewController

#pragma mark - View Lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
}


#pragma mark - TABLE VIEW
//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 50;
}
//------------------------------------------------------------------------------
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}

//------------------------------------------------------------------------------
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {

    return @"Real Madrid - Barcelona";
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    Match *match = [[Match alloc]init];

    match.matchDate = @1414602325;
    
    return [InfoUtilities createHeaderViewWithFrame:tableView.frame andMatch:match];
    
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {

    return 3;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"infoCell";
    InfoCustomCell *cell = (id) [self.infoTableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    //User *user = self.usersArray[indexPath.row];
    User *user = [[UserManager sharedInstance]getActiveUser];
    
    
    [cell configureInfoCellWithUser:user inRow:indexPath];
//    [cell addTarget:self action:@selector(goProfile:)];
    
    return cell;
}

//------------------------------------------------------------------------------
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}


@end
