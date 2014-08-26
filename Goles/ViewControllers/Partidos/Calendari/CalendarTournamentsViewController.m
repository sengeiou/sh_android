//
//  TournamentsViewController.m
//  iGoles
//
//  Created by Administrador on 27/08/10.
//  Copyright 2012 Opentrends. All rights reserved.
//

#import "CalendarTournamentsViewController.h"
#import "Fav24ExtendedNavigationController.h"
#import "CalendarFixtureViewController.h"
#import "CalendarFavoritesTableViewController.h"
#import "Tournament.h"
#import "Constants.h"
#import "Flurry.h"
#import "LeaguesManager.h"
#import "CoreDataManager.h"
#import "UIImage+ImageEffects.h"
#import "Team.h"
#import "Fav24Colors.h"

@interface CalendarTournamentsViewController()

@property (nonatomic,weak) IBOutlet     UITableView                     *tableView;
@property (nonatomic,strong)            NSArray                         *mSuscribedTeams;
@property (nonatomic,strong)            NSArray                         *mTournamentsArray;
@property (nonatomic,strong)            UIActivityIndicatorView         *activity;

@end


@implementation CalendarTournamentsViewController

#pragma mark - View lifecycle

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    [self createActivity];
    
//    client = [[WSClient alloc] init];
//    client.delegate = self;
    
    self.navigationItem.title = @"Calendario";
    [self createNavigationButtons];
    self.mSuscribedTeams = [self getSuscribedTeamsForMode];
    self.mTournamentsArray = [self getTournamentsForMode];
}

//------------------------------------------------------------------------------
- (void)viewDidAppear:(BOOL)animated {
    
    [super viewDidAppear:animated];

}

//------------------------------------------------------------------------------
-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [Flurry logEvent:K_CALENDAR_onEnter_CALENDAR];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    
    //Background blur image under the modalview
    self.tableView.backgroundView = [[UIImageView alloc] initWithImage:[self.backgroundImage applyBlurWithRadius:24
                                                                                                       tintColor:[UIColor colorWithWhite:1 alpha:0.5]
                                                                                           saturationDeltaFactor:1.8
                                                                                                       maskImage:nil]];
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
- (NSArray *)getTournamentsForMode {
    
    NSNumber *selectedMode = [[LeaguesManager sharedInstance] getSelectedMode].idMode;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"visibleApp == 1 AND ANY modes.idMode == %@",selectedMode];
    NSArray *tournaments = [[CoreDataManager singleton] getAllEntities:[Tournament class] orderedByKey:@"orderBy" ascending:YES withPredicate:predicate];
    if (tournaments.count > 0)
        [self stopActivity];
    return tournaments;
}

//------------------------------------------------------------------------------
- (NSArray *)getSuscribedTeamsForMode {
    
    NSMutableArray *selectedTeamsInSelectedMode = [[NSMutableArray alloc] init];
    
    NSArray *allSuscribedTeams = [[LeaguesManager singleton] getSubscribedTeams];
    int selectedMode = [[[LeaguesManager sharedInstance] getSelectedMode].idMode intValue];
    
    for (Team *team in allSuscribedTeams) {
        if (team.mode.idModeValue == selectedMode)
            [selectedTeamsInSelectedMode addObject:team];
    }
    if (selectedTeamsInSelectedMode.count > 0)
        [self stopActivity];
        
    return selectedTeamsInSelectedMode;
}

//------------------------------------------------------------------------------
- (void)stopActivity {
    [self.activity stopAnimating];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
}

//------------------------------------------------------------------------------
- (void)createNavigationButtons {
    
    UIBarButtonItem *closeButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"_close", @"") style:UIBarButtonItemStylePlain target:self action:@selector(onCloseButtonClick:)];
    [[self navigationItem] setRightBarButtonItem:closeButton];
}

//------------------------------------------------------------------------------
- (void)onCloseButtonClick:(id) sender {
    
    [[self navigationController] dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - Table view data source

//------------------------------------------------------------------------------
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
	return 53;
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {

    if (section == 0)
        return self.mSuscribedTeams.count;
    else if (section == 1)
        return self.mTournamentsArray.count;
    return 0;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
	static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
	if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
	
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    cell.backgroundColor = [UIColor clearColor];
    
    if (indexPath.section == 0) {
        cell.textLabel.textColor = [Fav24Colors iosSevenBlue];
        Team *currentTeam = [self.mSuscribedTeams objectAtIndex:indexPath.row];
        cell.textLabel.text = currentTeam.nameShort;
    }else if (indexPath.section == 1) {
        Tournament *tournament = [self.mTournamentsArray objectAtIndex:indexPath.row];
        cell.textLabel.text = tournament.name;
        cell.textLabel.textColor = [UIColor blackColor];
    }
    return cell;
}

//------------------------------------------------------------------------------
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (indexPath.section == 0) {
        CalendarFavoritesTableViewController *fav = [[CalendarFavoritesTableViewController alloc] initWithNibName:@"CalendarFavoritesTableViewController" bundle:nil];
        fav.backgroundImage = self.backgroundImage;
        fav.selectedTeam = [self.mSuscribedTeams objectAtIndex:indexPath.row];
        [self.navigationController pushViewController:fav animated:YES];
    }
    else {
        CalendarFixtureViewController *fixtureVC = [[CalendarFixtureViewController alloc] initWithNibName:@"CalendarFixtureViewController" bundle:nil];
        fixtureVC.mTournament = [self.mTournamentsArray objectAtIndex:indexPath.row];
        fixtureVC.backgroundImage = self.backgroundImage;
        [self.navigationController pushViewController:fixtureVC animated:YES];
    }
}

@end

