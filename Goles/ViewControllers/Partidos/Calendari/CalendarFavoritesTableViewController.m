//
//  CalendarFavoritesTableViewController.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 18/03/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "CalendarFavoritesTableViewController.h"
#import "FavRestConsumer.h"
#import "LeaguesManager.h"
#import "CalendarCustomCell.h"
#import "Match.h"

@interface CalendarFavoritesTableViewController ()

@property (nonatomic,strong)            NSArray                     *teamMatches;
@property (nonatomic)                   int                         currentMatch;
@property (nonatomic,strong)            UIActivityIndicatorView     *activity;
@property (nonatomic,strong)    IBOutlet        UILabel             *noMatchesLabel;
@end


@implementation CalendarFavoritesTableViewController

#pragma mark - View lifecycle

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    
    [super viewDidLoad];
    if (self.selectedTeam)
        self.navigationItem.title = self.selectedTeam.nameShort;

    [self createActivity];
    [self createNavigationButtons];
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (self.selectedTeam)
        [[FavRestConsumer sharedInstance] getAllMatchesForTeams:@[self.selectedTeam] andDelegate:self];
}

#pragma mark - Table view data source
//------------------------------------------------------------------------------
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
	NSInteger sections = 0;
	
	if ([self.teamMatches count] <= 0) {
		sections = 0;
	}
	else {
		sections = 1;
        self.noMatchesLabel.text = nil;
        self.noMatchesLabel.enabled = NO;
    }
	return sections;
}
//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return [self.teamMatches count];
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {

    return 53;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"CalendarCustomCell";
    CalendarCustomCell *cell = [self.tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    cell = nil;
    if (cell == nil) {
        UIViewController *nib = [[UIViewController alloc] initWithNibName:@"CalendarCustomCell" bundle:nil];
        cell = (CalendarCustomCell *)[nib view];
    }
    
    cell.accessoryType = UITableViewCellAccessoryNone;
    
    NSDictionary *match = [self.teamMatches objectAtIndex:indexPath.row];

    BOOL isLocal = NO;
    if ([[match objectForKey:@"localNameShort"] isEqualToString:self.selectedTeam.nameShort])
        isLocal = YES;

    [cell configCellWithData:match forLocalTeam:isLocal];
    cell.backgroundColor = [UIColor clearColor];
    
	return cell;
}

#pragma mark - WS response methods

//------------------------------------------------------------------------------
- (void)getTeamCalendarDidResponse:(NSArray *)array {

    [self.activity stopAnimating];
    
    self.teamMatches = array;
    if (array.count == 0) {
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.noMatchesLabel.enabled = YES;
        self.noMatchesLabel.text = NSLocalizedString(@"No hay ningún partido para el evento seleccionado.",nil);
    }else
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    
    [self.tableView reloadData];
    
    [self scrollTableToFirstNotStartedMatch];
}


#pragma mark - Private methods

//------------------------------------------------------------------------------
- (void)createActivity {
    
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.activity = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    self.activity.frame = CGRectMake((self.view.frame.size.width/2)-15, (self.view.frame.size.height/2)-64, 30, 30);
    [self.tableView addSubview:self.activity];
    [self.activity startAnimating];
}

//------------------------------------------------------------------------------
- (void)scrollTableToFirstNotStartedMatch {
    
    int match = 0;
    while (match <[self.teamMatches count] && [[[self.teamMatches objectAtIndex:match] objectForKey:@"matchState"] integerValue] == kCoreDataMatchStateFinished) {
        ++match;
    }
    self.currentMatch = match;
    
    if (match < [self.teamMatches count]) {
        NSIndexPath *scrollIndexPath = [NSIndexPath indexPathForRow:self.currentMatch inSection:0];
        [self.tableView scrollToRowAtIndexPath:scrollIndexPath atScrollPosition:UITableViewScrollPositionBottom animated:NO];
        NSIndexPath *indexToScroll = [NSIndexPath indexPathForRow:match inSection:0];
        [self.tableView selectRowAtIndexPath:indexToScroll animated:NO scrollPosition:UITableViewScrollPositionMiddle];
    }

}

//------------------------------------------------------------------------------
- (void)createNavigationButtons {
    
    UIBarButtonItem *closeButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"_close", @"") style:UIBarButtonItemStylePlain target:self action:@selector(onCloseButtonClick:)];
    [self.navigationItem setRightBarButtonItem:closeButton];
}

//------------------------------------------------------------------------------
- (void)onCloseButtonClick:(id) sender {
    
    [self.navigationController dismissViewControllerAnimated:YES completion:nil];
}

@end
