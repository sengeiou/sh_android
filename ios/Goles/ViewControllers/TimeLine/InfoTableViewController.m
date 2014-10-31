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

typedef enum typesOfChange : NSUInteger {
    k_NoChange,
    k_Update,
    k_Insert,
    k_Delete,
    k_Move,
} typesOfChange;


@interface InfoTableViewController () <NSFetchedResultsControllerDelegate>

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
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
   
    if (section == 0)
        return 60;
    
    return 50;
}

//------------------------------------------------------------------------------
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    return [InfoUtilities createHeaderViewWithFrame:tableView.frame andMatch:[Match alloc] andSection:section];
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
-(NSArray *)addIndexPath{
    
    NSMutableArray *indexPaths = [NSMutableArray array];
    NSInteger rowCount = [self.tableView numberOfRowsInSection:0];
    
    for (int j = 0; j < rowCount; j++) {
        [indexPaths addObject:[NSIndexPath indexPathForRow:j inSection:0]];
    }
    
    
    return indexPaths;
}


//------------------------------------------------------------------------------
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}


@end
