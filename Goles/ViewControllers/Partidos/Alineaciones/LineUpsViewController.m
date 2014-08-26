//
//  LineUpsViewController.m
//  Goles Messenger
//
//  Created by Kuro on 4/21/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "LineUpsViewController.h"
#import "Flurry.h"
#import "Services.pch"
#import "UserManager.h"
#import "CoreDataParsing.h"
#import "CoreDataManager.h"
#import "Fav24Colors.h"
#import <Social/Social.h>
#import "Team.h"
#import "LineUp.h"
#import "Device.h"
#import "UIImageView+AFNetworking.h"

#define K_LINEUPS_IMAGES_URI            @"http://backoffice.igoles.es/GUI/iGolesBackoffice/images/teams_lineups/"
#define K_PLAYER_NAME_LABEL_MAX_WIDTH   70

@interface LineUpsViewController ()

@property (nonatomic,strong) NSArray               *y_pos_local;
@property (nonatomic,strong) NSArray               *y_pos_visitor;
@property (nonatomic,strong) NSArray               *x_pos;

@property (nonatomic,strong) UITableViewCell       *mTwitterCell;
@property (nonatomic,strong) UIView                *mFieldContainerView;
@property (nonatomic,strong) UILabel               *mNoVisitingTeamLineupLabel;
@property (nonatomic,strong) UILabel               *mNoLocalTeamLineupLabel;

@end

@implementation LineUpsViewController


#pragma mark - Init methods

//------------------------------------------------------------------------------
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {

        NSArray *x_pos_1_player = @[@159];
        NSArray *x_pos_2_players = @[@121, @196];
        NSArray *x_pos_3_players = @[@83,@159,@235];
        NSArray *x_pos_4_players = @[@45,@121,@196,@272];
        NSArray *x_pos_5_players = @[@45,@99,@158,@217,@272];
        
        self.y_pos_local = @[@16,@51,@86,@121,@156];
        self.y_pos_visitor = @[@352,@317,@282,@247,@212];

        self.x_pos = @[x_pos_1_player,x_pos_2_players,x_pos_3_players,x_pos_4_players,x_pos_5_players];
    }
    return self;
}

#pragma mark - View Controller Lifecycle

//------------------------------------------------------------------------------
- (void)viewDidLoad
{
    [super viewDidLoad];
    [self createTwitterCell];

    self.mFieldContainerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 367)];

    [self setMNoLocalTeamLineupLabel:[[UILabel alloc] initWithFrame:CGRectMake(0, 90, 320, 30)]];
    [[self mNoLocalTeamLineupLabel] setFont:[UIFont systemFontOfSize:14.0]];
    [[self mNoLocalTeamLineupLabel] setTextColor:[Fav24Colors tableViewHeaderTextColor]];
    [[self mNoLocalTeamLineupLabel] setTextAlignment:NSTextAlignmentCenter];

    
    [self setMNoVisitingTeamLineupLabel:[[UILabel alloc] initWithFrame:CGRectMake(0, 247, 320, 30)]];
    [[self mNoVisitingTeamLineupLabel] setFont:[UIFont systemFontOfSize:14.0]];
    [[self mNoVisitingTeamLineupLabel] setTextColor:[Fav24Colors tableViewHeaderTextColor]];
    [[self mNoVisitingTeamLineupLabel] setTextAlignment:NSTextAlignmentCenter];


    [self.mFieldContainerView addSubview:self.mNoLocalTeamLineupLabel];
    [self.mFieldContainerView addSubview:self.mNoVisitingTeamLineupLabel];

    
    //Observer to update list when returning from background
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshView) name:@"returnFromBackground" object:nil];
}

//------------------------------------------------------------------------------
-(void) viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    [self refreshView];
}

//------------------------------------------------------------------------------
-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [Flurry logEvent:K_JORNADAS_onEnter_LINEUP];
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    
    // Dispose of any resources that can be recreated.
    [[NSNotificationCenter defaultCenter] removeObserver: self];
}

#pragma mark - private methods
//------------------------------------------------------------------------------
-(void)refreshView {
    
    [[self navigationItem] setTitle:NSLocalizedString(@"_lineUpsNavigationTitle", @"")];
    self.mNoLocalTeamLineupLabel.text = [NSString stringWithFormat:@"%@ %@",self.mMatch.teamLocal.nameShort ,NSLocalizedString(@"_noTeamLineupLabelMessage", @"")];
    self.mNoVisitingTeamLineupLabel.text = [NSString stringWithFormat:@"%@ %@",self.mMatch.teamVisitor.nameShort,NSLocalizedString(@"_noTeamLineupLabelMessage", @"")];

    [[self mNoLocalTeamLineupLabel] setHidden:NO];
    [[self mNoVisitingTeamLineupLabel] setHidden:NO];
    
    [[self tableView] reloadData];
}

#pragma mark - UITableViewController delegate methods
//------------------------------------------------------------------------------
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {

    if ([self twitterCellVisible])
        return 2;
    return 1;
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if ([self twitterCellVisible] && indexPath.section == 0) {
        return 44;
    }
    else
        return 367;
//    if ((self.mMatch.matchStateValue == kMatchStateNotStarted || self.mMatch.matchStateValue == kMatchStateStarted ) && [self canTwitt] && indexPath.section == 0)
//      return 44;
//    
//    return 367;
  
}

//------------------------------------------------------------------------------
-(NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    
    if (([self twitterCellVisible] && section == 1) || (![self twitterCellVisible] && section == 0))
        return @"Once inicial";
    else
        return nil;
    
//    if ( self.mMatch.matchStateValue == kMatchStateFinished || self.mMatch.matchStateValue == kMatchStateSuspended || ![self canTwitt]){
//        if (section == 0)
//            return @"Once inicial";
//    }
//    else if (section == 1)
//        return @"Once inicial";
//    
//    return nil;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    UITableViewCell *fieldCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"FieldCell"];

    if (([self twitterCellVisible] && indexPath.section == 1) || (![self twitterCellVisible] && indexPath.section == 0))
        return [self configureFieldCell:fieldCell];
    else {
        [self configureTwittCell];
        return [self mTwitterCell];
    }
    
//    if ((self.mMatch.matchStateValue == kMatchStateNotStarted || self.mMatch.matchStateValue == kMatchStateStarted ) && [self canTwitt] && indexPath.section == 0)
//    {
//        [self configureTwittCell];      // refresh twitter cell state
//        return [self mTwitterCell];
//    }
//    
//    return [self configureFieldCell:fieldCell];
}

//------------------------------------------------------------------------------
- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section{
    
    NSMutableString *message = [[NSMutableString alloc] init];
    NSString *localExtendedData = self.mMatch.lineUpLocal.reserve;
    NSString *visitorExtendedData = self.mMatch.lineUpVisitor.reserve;
    
    if ( [localExtendedData length]>0 )
        [message appendString:localExtendedData];
    
    if ( [visitorExtendedData length]>0 ){
        if ( localExtendedData )    [message appendString:@"\n\n"];
        [message appendString:visitorExtendedData];
    }
    
    if ([message length]>0) {

        if (([self twitterCellVisible] && section == 1) || (![self twitterCellVisible] && section == 0))
            return message;
//        if ( section == 0 && ![self canTwitt] )
//            return message;
//        else if(section == 1)
//            return message;
    }
    return nil;
}

//------------------------------------------------------------------------------
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    //More than 5 minutes since match began. Can't Tweet lineups
    if (![self matchElapsedTimeLessThanFiveMinutes] && self.mMatch.matchStateValue == kCoreDataMatchStateStarted) {
        UIAlertView *alertTwitter = [[UIAlertView alloc] initWithTitle:nil
                                                               message:NSLocalizedString(@"_lineUpsfiveMinutesAlert", nil)
                                                              delegate:self
                                                     cancelButtonTitle:NSLocalizedString(@"_ok", nil)
                                                     otherButtonTitles:nil, nil];
        [alertTwitter show];
    }
    
    //More than 1 lineup remaining for tweet
    else if ([self remainingTuits] >1){
        UIActionSheet *teamSelector = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:NSLocalizedString(@"_cancel", nil) destructiveButtonTitle:nil otherButtonTitles:self.mMatch.teamLocal.nameShort,self.mMatch.teamVisitor.nameShort, nil];
        [teamSelector showInView:self.view];
        
    }

    //Only 1 lineup remaining for tweet
    else if (self.mMatch.lineUpLocal && !self.mMatch.twitterLocalValue){
        NSString *lineUpToTwitt = [self parseLineUpForTwitt:self.mMatch.lineUpLocal];
        [self twittLineUp:lineUpToTwitt andTeamType:@"local"];
    }
    else if (self.mMatch.lineUpVisitor && !self.mMatch.twitterVisitorValue){
        NSString *lineUpToTwitt = [self parseLineUpForTwitt:self.mMatch.lineUpVisitor];
        [self twittLineUp:lineUpToTwitt andTeamType:@"visitor"];
    }
    
}

//------------------------------------------------------------------------------
- (BOOL)matchElapsedTimeLessThanFiveMinutes {
    
    return self.mMatch.elapsedMinutesValue < 5;
}

#pragma mark - Twitter cell
//------------------------------------------------------------------------------
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {

    switch (buttonIndex) {
        case  0: // Local team
        {
            NSString *message = [NSString stringWithFormat:@"%@",[self parseLineUpForTwitt:self.mMatch.lineUpLocal]];
            [self twittLineUp:message andTeamType:@"local"];
        }
        break;
            
        case  1: // Visitor team
        {
            NSString *message = [NSString stringWithFormat:@"%@",[self parseLineUpForTwitt:self.mMatch.lineUpVisitor]];
            [self twittLineUp:message andTeamType:@"visitor"];
        }
        break;
            
        default:
            break;
    }

}

//------------------------------------------------------------------------------
-(NSString *)parseLineUpForTwitt:(LineUp *)lineUpToParse {
    
    NSString *newString = @"";
    
    newString = [newString stringByAppendingString:[NSString stringWithFormat:@"%@: ",lineUpToParse.team.nameShort]];
    if (lineUpToParse.goalkeeper)       newString = [newString stringByAppendingString:[NSString stringWithFormat:@"%@",lineUpToParse.goalkeeper]];
    if (lineUpToParse.defenders)        newString = [newString stringByAppendingString:[NSString stringWithFormat:@"; %@",lineUpToParse.defenders]];
    if (lineUpToParse.midfielder01)     newString = [newString stringByAppendingString:[NSString stringWithFormat:@"; %@",lineUpToParse.midfielder01]];
    if (lineUpToParse.midfielder02)     newString = [newString stringByAppendingString:[NSString stringWithFormat:@"; %@",lineUpToParse.midfielder02]];
    if (lineUpToParse.striker)          newString = [newString stringByAppendingString:[NSString stringWithFormat:@"; %@",lineUpToParse.striker]];

    return newString;
}

//------------------------------------------------------------------------------
- (void)createTwitterCell {
    
    static NSString *cellId = @"twitterCell";
    UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellId];
    [[cell textLabel] setTextColor:[Fav24Colors iosSevenBlue]];
    [[cell textLabel] setText:@"Tuitear alineación..."];
    [self setMTwitterCell: cell];
}

//------------------------------------------------------------------------------
- (void)configureTwittCell {
    
    if ([self canTwitt]) {
        self.mTwitterCell.textLabel.alpha = 1;
        if ([self remainingTuits] == 1) {
            
            NSMutableArray *lineUpsArray = [[NSMutableArray alloc] initWithCapacity:2];
            if (self.mMatch.lineUpLocal)        [lineUpsArray addObject:self.mMatch.lineUpLocal];
            if (self.mMatch.lineUpVisitor)      [lineUpsArray addObject:self.mMatch.lineUpVisitor];
            
            NSString *teamName;

            if ([lineUpsArray count] == 1) {
                if (self.mMatch.lineUpLocal)
                    teamName = self.mMatch.teamLocal.nameShort;
                else
                    teamName = self.mMatch.teamVisitor.nameShort;
            }
            else if ([lineUpsArray count] > 1) {
                if (self.mMatch.twitterLocalValue)
                    teamName = self.mMatch.teamVisitor.nameShort;
                else
                    teamName = self.mMatch.teamLocal.nameShort;
            }else {
                teamName = nil;
            }
            
            self.mTwitterCell.detailTextLabel.text = teamName;
            self.mTwitterCell.detailTextLabel.textColor = [Fav24Colors iosSevenBlue];
        }
        
    }else {
        self.mTwitterCell.textLabel.alpha = 0.4;
        self.mTwitterCell.detailTextLabel.text = @"";
    }
}

//------------------------------------------------------------------------------
- (void)twittLineUp:(NSString *)lineUp andTeamType:(NSString *)teamType{
    
    if ([SLComposeViewController isAvailableForServiceType:SLServiceTypeTwitter])
    {
        SLComposeViewController *tweetSheet = [SLComposeViewController composeViewControllerForServiceType:SLServiceTypeTwitter];
        
        NSString *message = [NSString stringWithFormat:@"%@  \n %@", lineUp, K_TWITTER_MENTION];
        
        [tweetSheet setInitialText:message];
        [self presentViewController:tweetSheet animated:YES completion:nil];
        
        [tweetSheet setCompletionHandler:^(SLComposeViewControllerResult result) {
            
            NSMutableDictionary *logParams = [[NSMutableDictionary alloc] initWithObjectsAndKeys:[[[UserManager singleton] getDevice] idDevice],kJSON_ID_DEVICE, nil];
            
            NSNumber *idPlayer = [[[UserManager singleton] mUser] idPlayer];
            if (!idPlayer)      idPlayer = @-1;

            [logParams setValue:idPlayer forKey:kJSON_ID_PLAYER];
            
            switch (result) {
                case SLComposeViewControllerResultCancelled:
                    [Flurry logEvent:K_JORNADAS_TWITT_LINEUP_CANCELLED withParameters:logParams];
                    break;
                case SLComposeViewControllerResultDone:
                    [Flurry logEvent:K_JORNADAS_TWITT_LINEUP_DONE withParameters:logParams];
                    [self updateMatchWithTeamTwitt:teamType];
                    [self configureTwittCell];
                    [self.tableView reloadData];
                    break;
                    
                default:
                    break;
            }
            
            [self dismissViewControllerAnimated:YES completion:nil];
            
        }];
    }
    else {
        UIAlertView *alertTwitter = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"_warning", nil) message:NSLocalizedString(@"_noTweetAccount", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"Aceptar", nil) otherButtonTitles:nil, nil];
        [alertTwitter show];
    }
}

//------------------------------------------------------------------------------
- (BOOL)twitterCellVisible {
    
    if (self.mMatch.matchStateValue != kCoreDataMatchStateFinished && self.mMatch.matchStateValue != kCoreDataMatchStateSuspended && [self remainingTuits] > 0)
        return YES;
    else
        return NO;
            
}

//------------------------------------------------------------------------------
- (BOOL)canTwitt {

    if ((self.mMatch.matchStateValue == kCoreDataMatchStateNotStarted ||
        (self.mMatch.matchStateValue == kCoreDataMatchStateStarted && [self matchElapsedTimeLessThanFiveMinutes])) &&
        [self remainingTuits] > 0)
        return YES;
    
    return NO;
}

//------------------------------------------------------------------------------
- (NSUInteger)remainingTuits {

    int tuitsDone = 0;
    if (self.mMatch.twitterLocalValue)   ++tuitsDone;
    if (self.mMatch.twitterVisitorValue) ++tuitsDone;
    
    NSMutableArray *lineUpsArray = [[NSMutableArray alloc] initWithCapacity:2];
    if (self.mMatch.lineUpLocal)        [lineUpsArray addObject:self.mMatch.lineUpLocal];
    if (self.mMatch.lineUpVisitor)      [lineUpsArray addObject:self.mMatch.lineUpVisitor];
    
    return [lineUpsArray count] - tuitsDone;
}

//------------------------------------------------------------------------------
- (void)updateMatchWithTeamTwitt:(NSString *)team {

    if ([team isEqualToString:@"local"])
        [self.mMatch setTwitterLocal:@1];
    else if ([team isEqualToString:@"visitor"])
        [self.mMatch setTwitterVisitor:@1];
    
    [[CoreDataManager singleton] saveContext];
}

#pragma mark - Setup for field view cell

//------------------------------------------------------------------------------
- (UITableViewCell *)configureFieldCell:(UITableViewCell *)cell {
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.userInteractionEnabled = NO;
    cell.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Court_iOS.png"]];
    
    [cell.contentView addSubview:[self loadTeamBadges]];

    BOOL lineUpLocal = self.mMatch.lineUpLocal!=nil;
    [[self mNoLocalTeamLineupLabel] setHidden:lineUpLocal];
    if (lineUpLocal)
        [self setupLineUpsView:self.mMatch.lineUpLocal];
        
    BOOL lineUpVisitor = self.mMatch.lineUpVisitor!=nil;
    [[self mNoVisitingTeamLineupLabel] setHidden:lineUpVisitor];
    if (lineUpVisitor)
        [self setupLineUpsView:self.mMatch.lineUpVisitor];
    
    [cell.contentView addSubview:self.mFieldContainerView];
    
    return cell;
}

//------------------------------------------------------------------------------
- (UIView *)loadTeamBadges{

    UIView *badgesView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 367)];
    
    if ( self.mMatch.teamLocal.urlImage ){
        NSString *urlLocalBadge = [NSString stringWithFormat:@"%@%@%@",
                                   K_LINEUPS_IMAGES_URI,
                                   [self.mMatch.teamLocal.urlImage stringByDeletingPathExtension],
                                   @"_BIG.png"];
        UIImageView *localTeamBadgeImage = [[UIImageView alloc] initWithFrame:CGRectMake(85, 22, 150, 150)];
        
        [localTeamBadgeImage setImageWithURL:[NSURL URLWithString: urlLocalBadge] placeholderImage:nil];
        
        localTeamBadgeImage.alpha = 0.1f;
        [badgesView addSubview:localTeamBadgeImage];
    }
    
    if ( self.mMatch.teamVisitor.urlImage ){
        NSString *urlVisitorBadge = [NSString stringWithFormat:@"%@%@%@",
                                   K_LINEUPS_IMAGES_URI,
                                   [self.mMatch.teamVisitor.urlImage stringByDeletingPathExtension],
                                   @"_BIG.png"];
        UIImageView *visitorTeamBadgeImage = [[UIImageView alloc] initWithFrame:CGRectMake(85, 198, 150, 150)];
        [visitorTeamBadgeImage setImageWithURL:[NSURL URLWithString:urlVisitorBadge] placeholderImage:nil];
        visitorTeamBadgeImage.alpha = 0.1f;
        [badgesView addSubview:visitorTeamBadgeImage];
    }

    return badgesView;
}

//------------------------------------------------------------------------------
- (void)setupLineUpsView:(LineUp *)lineup{
    
    NSMutableArray *teamArray = [[NSMutableArray alloc] initWithCapacity:5];
    if (lineup.goalkeeper)         [teamArray addObject:lineup.goalkeeper];
    if (lineup.defenders)          [teamArray addObject:lineup.defenders];
    if (lineup.midfielder01)       [teamArray addObject:lineup.midfielder01];
    if (lineup.midfielder02)       [teamArray addObject:lineup.midfielder02];
    if (lineup.striker)            [teamArray addObject:lineup.striker];

    NSArray *yPosition;
    if (lineup.idLineUpValue == self.mMatch.lineUpLocal.idLineUpValue)
        yPosition = self.y_pos_local;
    else
        yPosition = self.y_pos_visitor;
    
    for (int line=0; line<[teamArray count]; ++line) {
        NSArray *playersArray = [[teamArray objectAtIndex:line] componentsSeparatedByString:@","];
        for (int player=0; player<[playersArray count]; ++player) {
            CGPoint playerPosition = CGPointMake([[[self.x_pos objectAtIndex:[playersArray count]-1] objectAtIndex:player] integerValue],
                                                 [[yPosition objectAtIndex:line] integerValue]);
            [self placeLabelOfPlayer:[playersArray objectAtIndex:player]  inPosition:playerPosition];
        }
    }
    
}


////------------------------------------------------------------------------------
- (void)placeLabelOfPlayer:(NSString *)playerData inPosition:(CGPoint)position{
    
    UILabel *playerNameLabel = [[UILabel alloc] initWithFrame:CGRectMake(position.x, position.y, 800, 20.0)];
    [playerNameLabel setContentMode:UIViewContentModeScaleAspectFit];    
    [playerNameLabel setText:playerData];
    [playerNameLabel setFont:[UIFont systemFontOfSize:12]];
    [playerNameLabel setBackgroundColor:[UIColor clearColor]];
    [playerNameLabel setTextColor:[UIColor darkGrayColor]];
    [playerNameLabel setTextAlignment:NSTextAlignmentCenter];
    [playerNameLabel setNumberOfLines:0];
    [playerNameLabel setLineBreakMode:NSLineBreakByTruncatingTail];
    [playerNameLabel sizeToFit];
    
    //Set maximum width of the player labels
    CGRect frame = playerNameLabel.frame;
    if ( frame.size.width > K_PLAYER_NAME_LABEL_MAX_WIDTH ){
        frame.size.width = K_PLAYER_NAME_LABEL_MAX_WIDTH;
        playerNameLabel.frame = frame;
    }
    
    //If player is goalkeeper set the width larger
    if (position.y < 20 || position.y > 350) {
        frame.size.width = 200;
        playerNameLabel.frame = frame;
    }

    [[playerNameLabel layer] setAnchorPoint:CGPointMake(1, 1)];
    
    [[self mFieldContainerView] addSubview:playerNameLabel];
    
}



@end
