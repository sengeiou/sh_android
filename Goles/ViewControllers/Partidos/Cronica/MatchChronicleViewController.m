//
//  MatchChronicleViewController.m
//  Goles Messenger
//
//  Created by Delfín Pereiro on 27/09/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "MatchChronicleViewController.h"
#import "Fav24ExtendedNavigationController.h"
#import "CoreDataManager.h"
#import "EventOfMatch.h"
#import "Event.h"
#import "Flurry.h"
#import "Services.pch"
#import "Fav24Colors.h"
#import "Match.h"
#import "Team.h"

@interface MatchChronicleViewController ()

@property (nonatomic,strong) NSArray                *mEventsArray;
@property (nonatomic,strong) UIRefreshControl       *mrefreshControl;
@property (nonatomic,strong) NSMutableArray         *mTableArray;

@end

@implementation MatchChronicleViewController

//------------------------------------------------------------------------------
- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad
{
    [super viewDidLoad];
    [[self navigationItem] setTitle:NSLocalizedString(@"_MatchChronicleNavigationTitle", @"")];
    [Flurry logEvent:K_JORNADAS_onEnter_CRONICA];

    
    [self getAllEventsOfMatch:self.mMatch];
    
    //Config Navigation progress bar
    //[(Fav24ExtendedNavigationController *)[self navigationController] hidePatch:YES];
    
    // Config pull to refresh
    [self setMrefreshControl:[[UIRefreshControl alloc] init]];
    [[self mrefreshControl] addTarget:self action:@selector(onPullToRefresh:) forControlEvents:UIControlEventValueChanged];
    [[self view] addSubview:[self mrefreshControl]];
}

-(void) getAllEventsOfMatch:(Match *)match{
   
    NSPredicate *predicate = [NSPredicate predicateWithFormat:[NSString stringWithFormat:@"idMatch == %d",[match.idMatch intValue]]];
    
    self.mEventsArray = [[CoreDataManager sharedInstance] getAllEntities:[EventOfMatch class] orderedByKey:@"dateIn" ascending:NO withPredicate:predicate];
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - public methods
//------------------------------------------------------------------------------
- (void)updateEventsMatchList:(Match *)match{
    
    self.mMatch = match;
    self.mEventsArray = [match getChronicleOrderedEvents];
    [self.tableView reloadData];
}

#pragma mark - UIRefreshControl delegate
//------------------------------------------------------------------------------
- (void)onPullToRefresh:(UIRefreshControl *)refreshControl {
    [self loadData];
}

#pragma mark - Private methods
//------------------------------------------------------------------------------
-(void)loadData {
    NSLog(@"Poner ProgressBar in NavigationBar");
    [[self mrefreshControl] endRefreshing];
   // [(Fav24ExtendedNavigationController *)[self navigationController] showProgressBar:YES];
}

#pragma mark - Table view data source
//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.mEventsArray count];
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"ChronicleDetailCell";
    UITableViewCell *chronicleCell = [tableView dequeueReusableCellWithIdentifier:identifier];
    
    if ( !chronicleCell ){
        chronicleCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identifier];
        [chronicleCell setSelectionStyle:UITableViewCellSelectionStyleNone];
        [chronicleCell setUserInteractionEnabled:NO];
    }
    
    chronicleCell.textLabel.textColor = [UIColor orangeColor];
    return [self configureChronicleCell:chronicleCell withIndex:indexPath.row];
}

- (UITableViewCell *)configureChronicleCell:(UITableViewCell *)cell withIndex:(int)indexPath{
    
    //Set event Data
    EventOfMatch *matchEvent = [self.mEventsArray objectAtIndex:indexPath];

    //Set the label text
    if (matchEvent.event.idEventValue == 2 ||
        matchEvent.event.idEventValue == 4 ||
        matchEvent.event.idEventValue == 5 ||
        matchEvent.event.idEventValue == 6 ||
        matchEvent.event.idEventValue == 7 ||
        matchEvent.event.idEventValue == 8 ||
        matchEvent.event.idEventValue == 12 ||
        matchEvent.event.idEventValue == 13) {
            NSString *score = [NSString stringWithFormat:@"%d-%d", matchEvent.localScoreValue, matchEvent.visitorScoreValue];
            cell.textLabel.text = [NSString stringWithFormat:@"%@(%@)",matchEvent.actorTransmitterName,score];
    }
    else {
        //NSArray *tempArray = [matchEvent.comments componentsSeparatedByString:@"("];
        //cell.textLabel.text = [tempArray objectAtIndex:0];
    }
    
    //Set label detail text (minutes)
    cell.detailTextLabel.text = [NSString stringWithFormat:@"%@'", matchEvent.matchMinute];
    
    //Get the team of the event and set the left image tintcolor
    if (matchEvent.eventTeam.idTeamValue == matchEvent.match.teamLocal.idTeamValue)
        cell.imageView.tintColor = [Fav24Colors iosSevenBlue];
    else if (matchEvent.eventTeam.idTeamValue == matchEvent.match.teamVisitor.idTeamValue)
        cell.imageView.tintColor = cell.imageView.tintColor = [Fav24Colors iosSevenOrange];
    else
        cell.imageView.tintColor = [UIColor lightGrayColor];

    //Set the left image file
    UIImage *whistle =          [UIImage imageNamed:@"icon_whistle.png"];
    UIImage *goal =             [UIImage imageNamed:@"icon_ball.png"];
    UIImage *redCard =          [UIImage imageNamed:@"icon_redcard.png"];
    UIImage *penaltyMissed =    [UIImage imageNamed:@"PenaltyMissed.png"];
    UIImage *yellowCard =       [UIImage imageNamed:@"icon_yellowcard.png"];
    UIImage *change =           [UIImage imageNamed:@"icon_change.png"];
    
    switch (matchEvent.event.idEventValue) {
        case 1: // Comienza el partido
        case 4: // Final del partido
        case 5: // Final primera parte
        case 6: // Inicio segunda parte
        case 7: // Fin 90min (hay prórroga)
        case 8: // Final prórroga
        case 12: // Inicio prórroga
        case 13: // Inicio penalties
        case 15: // Suspendido
            cell.imageView.image = [whistle imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
            break;
            
        case 2: // Goool
            cell.imageView.image = [goal imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
            break;
            
        case 3: // Expulsión
        case 10: // Penalti y expulsión
            cell.imageView.image = [redCard imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
            break;
            
        case 11: // Penalti fallado
            cell.imageView.image = [penaltyMissed imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
            break;
        
        case 16: // Amarilla
        case 20: // Penalti y Amarilla
            cell.imageView.image = [yellowCard imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
            break;
            
        case 18: // Cambio Jugador
            cell.imageView.image = [change imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
            break;
       
        default:
            cell.imageView.image = nil;
            break;
    }
    cell.textLabel.enabled = YES;
    cell.textLabel.textColor = [UIColor blackColor];
    
    return cell;
}

//------------------------------------------------------------------------------
- (void)getMatchEventsDidResponse {
    [[self mrefreshControl] endRefreshing]; // hides pull to refresh view
    [(Fav24ExtendedNavigationController *)[self navigationController] endProgressbar];
    [self updateEventsMatchList:self.mMatch];
}

@end
