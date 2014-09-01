//
//  PartidosTableViewController.m
//  Goles
//
//  Created by Christian Cabarrocas on 11/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "PartidosTableViewController.h"
#import "FavRestConsumer.h"
#import "CoreDataManager.h"
#import "PartidosTableViewCell.h"
#import "NSDate+Utils.h"
#import "OptionsPartidosTableViewCell.h"
#import "Fav24ExtendedNavigationController.h"
#import "FavRestConsumer.h"


@interface PartidosTableViewController () <ParserProtocol>

@property (nonatomic,strong) NSArray *matchesArray;
@property (nonatomic,strong) IBOutlet  UITableView *tableView;

@end

@implementation PartidosTableViewController

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
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

    [self refreshMatchList];
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Match class] withDelegate:self];
   
    

}


//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - Table view data source
//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return self.matchesArray.count+1;
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0)
        return 44;
    else
        return 61;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (indexPath.row == 0) {
        OptionsPartidosTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"optionsPartidosCell" forIndexPath:indexPath];

        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.accessoryType = UITableViewCellAccessoryNone;
        
        return cell;

    }else{
        PartidosTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"partidosCell" forIndexPath:indexPath];
        
        Match *match = self.matchesArray[indexPath.row-1];
        
        cell.lblTeamLocal.text = match.teamLocal.nameShort;
        cell.lblTeamVisitor.text = match.teamVisitor.nameShort;
        
        /*NSDate *matchDate = [NSDate dateWithTimeIntervalSince1970:[match.matchDate doubleValue]/1000.0];
        NSLog(@"%@", [NSString stringWithFormat:@"%@",[matchDate getFormattedDateMatch:NO]]);*/
        
        // Config match minute Label
        if (match.matchStateValue == kCoreDataMatchStateStarted) {
            switch ([match matchSubstateValue]) {
                case 1:
                    [cell.lblDateStart setText:NSLocalizedString(@"_roundMatchesListMatchHalf-time", nil)];
                    break;
                case 2:
                    [cell.lblDateStart setText:[NSString stringWithFormat:NSLocalizedString(@"%@ %@'",nil),NSLocalizedString(@"_roundMatchesListMatchExtraTime", nil),match.elapsedMinutes]];
                    break;
                case 3:
                    [cell.lblDateStart setText:NSLocalizedString(@"_roundMatchesListMatchPenatyKicks", nil)];
                    break;
                default:
                    [cell.lblDateStart setText:[NSString stringWithFormat:NSLocalizedString(@"%@'",nil),match.elapsedMinutes]];
                    break;
            }
        }
        else if (match.matchStateValue == kCoreDataMatchStateSuspended) {
            [cell.lblDateStart setText:NSLocalizedString(@"_roundMatchesListMatchSuspended", nil)];
        }
        else if (match.matchStateValue == kCoreDataMatchStateFinished) {
            [cell.lblDateStart setText:NSLocalizedString(@"_roundMatchesListMatchEnded", nil)];
        } else {
            NSDate *matchDate = [NSDate dateWithTimeIntervalSince1970:[[match matchDate] doubleValue]/1000.0];
            [cell.lblDateStart setText:[[matchDate getFormattedDateMatch:[match notConfirmedMatchDateValue]] lowercaseString]];
        }

        [[[cell lblTV] layer] setCornerRadius:3.5f];
        
        return cell;

    }
}

#pragma mark - Refresh data
//------------------------------------------------------------------------------
- (void)refreshMatchList {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"teamLocal.idTeam == 3 OR teamVisitor.idTeam == 3"];
    self.matchesArray = [[CoreDataManager sharedInstance] getAllEntities:[Match class] orderedByKey:@"matchDate" ascending:YES withPredicate:predicate];
    
    if (self.matchesArray.count > 0)
        [self.tableView reloadData];
}

#pragma mark - Navigation
//------------------------------------------------------------------------------
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.

    if ([segue.identifier isEqualToString:kSEGUE_MATCH_DETAIL]) {
       
    }
}

#pragma mark - Delegate Methods from Webservice
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error {
    
    if ([entityClass isSubclassOfClass:[Match class]] && status && !error)
        [self refreshMatchList];
}

@end
