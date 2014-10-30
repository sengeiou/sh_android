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
@property (nonatomic, retain)               NSFetchedResultsController  *fetchedResultsController;
@property (nonatomic)                       NSInteger                   tableTypeOfChange;

@end

@implementation InfoTableViewController

#pragma mark - View Lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    [self loadData];

}

//------------------------------------------------------------------------------
- (void)viewDidUnload {
    self.fetchedResultsController = nil;
}

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)loadData {
    
    NSError *error;
//    if (![[self fetchedResultsController] performFetch:&error]) {
//        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
//    }
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

//    if (self.fetchedResultsController.fetchedObjects.count > 0)
//        [self addPullToRefresh];
//    
//    return self.fetchedResultsController.fetchedObjects.count;

    
    return 3;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"infoCell";
    InfoCustomCell *cell = (id) [self.infoTableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    if (indexPath.row != 0)
        cell.btnEdit.hidden = YES;

    
    //User *user = self.usersArray[indexPath.row];
    User *user = [[UserManager sharedInstance]getActiveUser];
    
    
    [cell configureInfoCellWithUser:user inRow:indexPath];
//    [cell addTarget:self action:@selector(goProfile:)];
    
    return cell;
}

#pragma mark - FETCHED_RESULTS_CONTROLLER
//------------------------------------------------------------------------------
- (NSFetchedResultsController *)fetchedResultsController {
    
    if (_fetchedResultsController != nil) {
        return _fetchedResultsController;
    }
    
    //GET THE DATA
    //self.fetchedResultsController = [[ShotManager singleton] getShotsForTimeLine];
    self.fetchedResultsController.delegate = self;
    
    return _fetchedResultsController;
}

//------------------------------------------------------------------------------
- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller {
    //[self.tableView beginUpdates];
}

//------------------------------------------------------------------------------
- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type newIndexPath:(NSIndexPath *)newIndexPath {
    
//    UITableView *tableView = self.tableView;
//    
//    switch(type) {
//            
//        case NSFetchedResultsChangeInsert:{
//            self.tableTypeOfChange = k_Insert;
//            [tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
//            break;
//        }
//            
//        case NSFetchedResultsChangeDelete:{
//            self.tableTypeOfChange = k_Delete;
//            [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
//            break;
//        }
//        case NSFetchedResultsChangeUpdate:{
//            self.tableTypeOfChange = k_Update;
//            break;
//        }
//        case NSFetchedResultsChangeMove:{
//            self.tableTypeOfChange = k_Move;
//            [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
//            break;
//        }
//    }
}

-(void)controller:(NSFetchedResultsController *)controller didChangeSection:(id<NSFetchedResultsSectionInfo>)sectionInfo atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type{
    
    
}

//------------------------------------------------------------------------------
- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller {
    
//    if (self.tableTypeOfChange == k_Insert) {
//        NSArray *indexs = [self addIndexPath];
//        [self.tableView reloadRowsAtIndexPaths: indexs withRowAnimation:UITableViewRowAnimationNone];
//        Shot *lastShot = self.fetchedResultsController.fetchedObjects.firstObject;
//        NSInteger lastShotUserID = lastShot.user.idUserValue;
//        if (lastShotUserID == [[[UserManager singleton] getUserId] integerValue] )
//            [self.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:YES];
//        
//        self.tableTypeOfChange = k_NoChange;
//    }
//    
//    [self.tableView endUpdates];
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
