//
//  ClassificationNEWViewController.m
//  Goles Messenger
//
//  Created by Delfín Pereiro on 01/10/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "ClassificationViewController.h"
#import "CoreDataManager.h"
#import "Fav24ExtendedNavigationController.h"
#import "ClassificationCell.h"
#import "LeaguesManager.h"
#import "Tournament.h"
#import "Flurry.h"
#import "Services.pch"
#import "UIImage+ImageEffects.h"
#import "UILabel+Resize.h"
#import "UIview+moveTo.h"

@interface ClassificationViewController ()

@property (nonatomic, strong) Tournament   *mClassificationTournament;
@property (nonatomic, strong) NSArray      *mClassificationsArray;
@property (nonatomic) int                   totalPossibleMatches;
@property (nonatomic, strong) IBOutlet UILabel      *mNoDataLabel;

@end

@implementation ClassificationViewController

- (id)initWithStyle:(UITableViewStyle)style {
    
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    
    [super viewDidLoad];

    [[self navigationItem] setTitle:NSLocalizedString(@"Clasificación", nil)];
    
    [self createNavigationButtons];
    
    // Config navigation patch
    UINavigationController *nav = [self navigationController];
    if ( [nav isKindOfClass:[Fav24ExtendedNavigationController class]] ) {
        [(Fav24ExtendedNavigationController *)nav setPatchView:[self createNavigationPatchView]];
        [(Fav24ExtendedNavigationController *)nav hidePatch:NO];
    }
    
    [self refreshData];
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning {
    
    [super didReceiveMemoryWarning];
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated {
    
    [super viewWillAppear:animated];
    [self refreshView];

    self.tableView.backgroundView = [[UIImageView alloc] initWithImage:[self.backgroundImage applyBlurWithRadius:24
                                                                                                       tintColor:[UIColor colorWithWhite:1 alpha:0.5]
                                                                                           saturationDeltaFactor:1.8
                                                                                                       maskImage:nil]];//CACTUS - WIP

    
    [Flurry logEvent:K_CLASIFICATION_onEnter_CLASIFICATION];
}

#pragma mark - Private methods
//------------------------------------------------------------------------------
- (void)createNavigationButtons {
    
    UIBarButtonItem *closeButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"_close", @"") style:UIBarButtonItemStylePlain target:self action:@selector(onCloseButtonClick:)];
    [[self navigationItem] setRightBarButtonItem:closeButton];
}

//------------------------------------------------------------------------------
- (void)onCloseButtonClick:(id) sender {
   
    [[self navigationController] dismissViewControllerAnimated:YES completion:nil];
}

//------------------------------------------------------------------------------
- (void)refreshView {
    
    NSArray *leagues = [[LeaguesManager singleton] getLeaguesFromCurrentMode];
    if ( leagues ) {
        // we take the first (and the only one at the moment) tournament in the array
        Tournament *league = [leagues objectAtIndex:0];

        NSSortDescriptor *descriptor = [NSSortDescriptor sortDescriptorWithKey:@"weight" ascending:YES];
        NSArray *classificationArray = [league.classifications sortedArrayUsingDescriptors:@[descriptor]];
        [self setMClassificationsArray:classificationArray];
        self.totalPossibleMatches = [self getTotalMatches:classificationArray];
        
        if (classificationArray.count == 0) {
            self.mNoDataLabel.hidden = NO;
            self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        }else {
            self.totalPossibleMatches = [self getTotalMatches:classificationArray];
            self.mNoDataLabel.hidden = YES;
            self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        }
        
        [self.tableView reloadData];

    }
}

//------------------------------------------------------------------------------
- (void)refreshData {
    
    Fav24ExtendedNavigationController *nav = (Fav24ExtendedNavigationController *)[self navigationController];
    [nav showProgressBar:YES];
    
   // [[RestConsumer sharedInstance] setProgressBarDelegate:nav];
   // [[RestConsumer sharedInstance] getTournamentsByMode:[[[LeaguesManager singleton] getSelectedMode] idMode] withDelegate:self];
}

//------------------------------------------------------------------------------
- (int)getTotalMatches:(NSArray *)teams {
    
    int maxMacthes = 0;

    for (Classification *team in teams) {
        int matchesPlayed = [team.pl integerValue] + [team.pv integerValue];
        if ( matchesPlayed > maxMacthes) {
            maxMacthes = matchesPlayed;
        }
    }
    return maxMacthes;
}

//------------------------------------------------------------------------------
- (UIView *)createNavigationPatchView {
    
    NSUInteger height = 15;
    
    UIView *navView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, height)];
    
    UILabel *ptsLabel = [[UILabel alloc] initWithFrame:CGRectMake(140, 0, 120, height)];
    [ptsLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [ptsLabel setBackgroundColor:[UIColor clearColor]];
    [ptsLabel setText:@"PTS"];
    [ptsLabel resizeWidthToFitContentWithMaxWidth:NSIntegerMax];
    [navView addSubview:ptsLabel];
    
    UILabel *pjLabel = [[UILabel alloc] initWithFrame:CGRectMake(200, 0, 120, height)];
    [pjLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [pjLabel setBackgroundColor:[UIColor clearColor]];
    [pjLabel setText:@"PJ"];
    [pjLabel resizeWidthToFitContentWithMaxWidth:NSIntegerMax];
    [pjLabel moveLabelTO:INSERT_POSITION_RIGHT ofView:ptsLabel withMargin:15];
    [navView addSubview:pjLabel];
    
    UILabel *pgLabel = [[UILabel alloc] initWithFrame:CGRectMake(200, 0, 120, height)];
    [pgLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [pgLabel setBackgroundColor:[UIColor clearColor]];
    [pgLabel setText:@"PG"];
    [pgLabel resizeWidthToFitContentWithMaxWidth:NSIntegerMax];
    [pgLabel moveLabelTO:INSERT_POSITION_RIGHT ofView:pjLabel withMargin:8];
    [navView addSubview:pgLabel];

    UILabel *peLabel = [[UILabel alloc] initWithFrame:CGRectMake(200, 0, 120, height)];
    [peLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [peLabel setBackgroundColor:[UIColor clearColor]];
    [peLabel setText:@"PE"];
    [peLabel resizeWidthToFitContentWithMaxWidth:NSIntegerMax];
    [peLabel moveLabelTO:INSERT_POSITION_RIGHT ofView:pgLabel withMargin:7];
    [navView addSubview:peLabel];

    UILabel *ppLabel = [[UILabel alloc] initWithFrame:CGRectMake(200, 0, 120, height)];
    [ppLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [ppLabel setBackgroundColor:[UIColor clearColor]];
    [ppLabel setText:@"PP"];
    [ppLabel resizeWidthToFitContentWithMaxWidth:NSIntegerMax];
    [ppLabel moveLabelTO:INSERT_POSITION_RIGHT ofView:peLabel withMargin:8];
    [navView addSubview:ppLabel];

    UILabel *gfLabel = [[UILabel alloc] initWithFrame:CGRectMake(200, 0, 120, height)];
    [gfLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [gfLabel setBackgroundColor:[UIColor clearColor]];
    [gfLabel setText:@"GF"];
    [gfLabel resizeWidthToFitContentWithMaxWidth:NSIntegerMax];
    [gfLabel moveLabelTO:INSERT_POSITION_RIGHT ofView:ppLabel withMargin:8];
    [navView addSubview:gfLabel];

    UILabel *gcLabel = [[UILabel alloc] initWithFrame:CGRectMake(200, 0, 120, height)];
    [gcLabel setFont:[UIFont systemFontOfSize:10.0f]];
    [gcLabel setBackgroundColor:[UIColor clearColor]];
    [gcLabel setText:@"GC"];
    [gcLabel resizeWidthToFitContentWithMaxWidth:NSIntegerMax];
    [gcLabel moveLabelTO:INSERT_POSITION_RIGHT ofView:gfLabel withMargin:9];
    [navView addSubview:gcLabel];
    
    return navView;
}

#pragma mark - Table view data source

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return self.mClassificationsArray.count;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    
	ClassificationCell *cell = (ClassificationCell *)[tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
	if (cell==nil) {
        UIViewController *vc = [[UIViewController alloc] initWithNibName:@"ClassificationCell" bundle:nil];
        cell = (ClassificationCell *)[vc view];
	}
	   
    Classification *classification = [self.mClassificationsArray objectAtIndex:[indexPath row]];
    [cell configCellWithClassification:classification andMaxMatches:self.totalPossibleMatches];
    return cell;
}

#pragma mark - WebService delegate
//------------------------------------------------------------------------------
-(void)getTournamentsByModeDidResponse {
        
    NSArray *leagues = [[LeaguesManager singleton] getLeaguesFromCurrentMode];
    if ( leagues ) {
        
        // we take the first (and the only one at the moment) tournament in the array
        Tournament *league = [leagues objectAtIndex:0];
        [self setMClassificationTournament:league];
        
        Fav24ExtendedNavigationController *nav = (Fav24ExtendedNavigationController *)[self navigationController];
        [nav showProgressBar:YES];
        //[[RestConsumer sharedInstance] setProgressBarDelegate:nav];
       // [[RestConsumer sharedInstance] getClassificationOfTournament:league withDelegate:self];
    }
}

//------------------------------------------------------------------------------
- (void)getClassificationOfTournamentDidResponse {
    [self refreshView];
    Fav24ExtendedNavigationController *nav = (Fav24ExtendedNavigationController *)[self navigationController];
    [nav endProgressbar];
}

@end
