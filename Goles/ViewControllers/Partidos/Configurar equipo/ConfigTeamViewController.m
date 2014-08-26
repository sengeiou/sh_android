//
//  ConfigTeamViewController.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 31/03/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ConfigTeamViewController.h"
#import "Constants.h"
#import "ConfigTeamGoalTextViewController.h"
#import "ConfigTeamGoalSoundViewController.h"
#import "Fav24ExtendedNavigationController.h"
#import "UserManager.h"
#import "SubscriptionManager.h"
#import "Flurry.h"
#import "SML.h"
#import "Utils.h"
#import "Device.h"
#import "UserManager.h"
#import "CoreDataManager.h"
#import "CoreDataparsing.h"
#import "CuotasManager.h"

@interface ConfigTeamViewController()

typedef enum {
    kSWITCH_TAG_INTENSO = 0,
    kSWITCH_TAG_GOLES,
    kSWITCH_TAG_INICIO_FIN,
    kSWITCH_TAG_EXPULSIONES,
    kSWITCH_TAG_1HORA_ANTES,
    kSWITCH_TAG_DESCANSO,
    kSWITCH_TAG_ALINEACION,
    kSWITCH_TAG_PENALTIES,
    kSWITCH_TAG_AMARILLA,
    kSWITCH_TAG_CAMBIOS,
    kSWITCH_TAG_OFERTA_PARTIDO,
    kSWITCH_TAG_CUOTAS
} kSWITCH_TAGS;

@property (nonatomic) int   securityIDAllEvents;

@end


@implementation ConfigTeamViewController

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (id) initWithNibName:(NSString *)nibNameOrNil
                bundle:(NSBundle *)nibBundleOrNil
            passedData:(Team *)team
           titleButton:(NSString *)ttB {
    
	if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {
		// Custom initialization
        self.mOriginalTeamData = team;
        self.navigationItem.title = self.mOriginalTeamData.name;
	}
	return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
}

//------------------------------------------------------------------------------
-(void)viewWillAppear:(BOOL)animated{

    self.securityIDAllEvents = 0;
    
    [super viewWillAppear:animated];
    NSIndexPath *selected = [[self tableView] indexPathForSelectedRow];
    
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"_back", nil)
                                                                   style:UIBarButtonItemStyleBordered
                                                                  target:nil
                                                                  action:nil];
    self.navigationItem.backBarButtonItem = backButton;
    
    [self.tableView reloadData];
    
    if ( selected ) {
        [[self tableView] selectRowAtIndexPath:selected animated:NO scrollPosition:UITableViewScrollPositionNone];
        [[self tableView] deselectRowAtIndexPath:selected animated:YES];
    }
}

//------------------------------------------------------------------------------
- (void)viewDidAppear:(BOOL)animated {
    
}

//------------------------------------------------------------------------------
- (void)viewWillDisappear:(BOOL)animated {
    
    [super viewWillDisappear:animated];
    [self subscriptionUpdate];
}

#pragma mark - Private methods

//------------------------------------------------------------------------------
- (void)subscriptionUpdate{
    
//    if (self.mOriginalTeamData.subscription)
//        [[RestConsumer sharedInstance] setSubscription:nil team:self.mOriginalTeamData withDelegate:self];
}

//------------------------------------------------------------------------------
- (void)switchTouched:(UISwitch *)sender {
    
    
    BOOL switchState = [sender isOn];
    int32_t subscriptionValue = 0;
    
    Subscription *subscription = self.mOriginalTeamData.subscription;
    
    if (!subscription) {
        subscription = [Subscription insertWithDictionary:@{kJSON_ID_TEAM:self.mOriginalTeamData.idTeam,kJSON_ID_SML:@"2",kJSON_ID_ALL_EVENTS:@0}];
    }else {
        subscriptionValue = subscription.idAllEventsValue;
    }

    switch ([sender tag]) {
        case kSWITCH_TAG_GOLES:
            if (switchState && ![subscription isOnSubscription:goles]){
                [subscription setIdAllEventsValue:subscriptionValue+goles];
            }
            else if ([subscription isOnSubscription:goles])
                [subscription setIdAllEventsValue:subscriptionValue-goles];
            break;
        case kSWITCH_TAG_INICIO_FIN:
            if (switchState && ![subscription isInicioBlockOn:subscription])
                 [subscription setIdAllEventsValue:subscriptionValue+inicio+fin+suspendido+endmin90+inicioProrroga+finalProrroga+inicioPenalties];
            else if ([subscription isInicioBlockOn:subscription])
                [subscription setIdAllEventsValue:subscriptionValue-inicio-fin-suspendido-endmin90-inicioProrroga-finalProrroga-inicioPenalties];
            break;
        case kSWITCH_TAG_EXPULSIONES:
            if (switchState && ![subscription isOnSubscription:expulsiones])
                [subscription setIdAllEventsValue:subscriptionValue+expulsiones];
            else if ([subscription isOnSubscription:expulsiones])
                [subscription setIdAllEventsValue:subscriptionValue-expulsiones];
            break;
        case kSWITCH_TAG_1HORA_ANTES:
            if (switchState && ![subscription isOnSubscription:recordatoriounahoraantes])
                [subscription setIdAllEventsValue:subscriptionValue+recordatoriounahoraantes];
            else if ([subscription isOnSubscription:recordatoriounahoraantes])
                [subscription setIdAllEventsValue:subscriptionValue-recordatoriounahoraantes];
            break;
        case kSWITCH_TAG_DESCANSO:
            if (switchState && ![subscription isOnSubscription:descanso] && ![subscription isOnSubscription:finDescanso])
                [subscription setIdAllEventsValue:subscriptionValue+descanso+finDescanso];
            else if ([subscription isOnSubscription:descanso] && [subscription isOnSubscription:finDescanso])
                [subscription setIdAllEventsValue:subscriptionValue-descanso-finDescanso];
            break;
        case kSWITCH_TAG_ALINEACION:
            if (switchState && ![subscription isOnSubscription:alineacion])
                [subscription setIdAllEventsValue:subscriptionValue+alineacion];
            else if ([subscription isOnSubscription:alineacion])
                [subscription setIdAllEventsValue:subscriptionValue-alineacion];
            break;
        case kSWITCH_TAG_PENALTIES:
            if (switchState &&
                ![subscription isOnSubscription:penaltis] &&
                ![subscription isOnSubscription:penaltiFallado] &&
                ![subscription isOnSubscription:penaltiAmarilla] &&
                ![subscription isOnSubscription:penaltiRoja])
                [subscription setIdAllEventsValue:subscriptionValue+penaltis+penaltiFallado+penaltiAmarilla+penaltiRoja];
            else if ([subscription isOnSubscription:penaltis] &&
                     [subscription isOnSubscription:penaltiFallado] &&
                     [subscription isOnSubscription:penaltiAmarilla] &&
                     [subscription isOnSubscription:penaltiRoja])
                [subscription setIdAllEventsValue:subscriptionValue-penaltis-penaltiFallado-penaltiAmarilla-penaltiRoja];
            break;
        case kSWITCH_TAG_AMARILLA:
            if (switchState && ![subscription isOnSubscription:amarilla])
                [subscription setIdAllEventsValue:subscriptionValue+amarilla];
            else if ([subscription isOnSubscription:amarilla])
                [subscription setIdAllEventsValue:subscriptionValue-amarilla];
            break;
        case kSWITCH_TAG_CAMBIOS:
            if (switchState && ![subscription isOnSubscription:cambios])
                [subscription setIdAllEventsValue:subscriptionValue+cambios];
            else if ([subscription isOnSubscription:cambios])
                [subscription setIdAllEventsValue:subscriptionValue-cambios];
            break;
        case kSWITCH_TAG_OFERTA_PARTIDO:
            if (switchState && ![subscription isOnSubscription:ofertadelpartido])
                [subscription setIdAllEventsValue:subscriptionValue+ofertadelpartido];
            else if ([subscription isOnSubscription:ofertadelpartido])
                [subscription setIdAllEventsValue:subscriptionValue-ofertadelpartido];
            break;
        case kSWITCH_TAG_CUOTAS:
            if (switchState && ![subscription isOnSubscription:cuotas])
                [subscription setIdAllEventsValue:subscriptionValue+cuotas];
            else if ([subscription isOnSubscription:cuotas])
                [subscription setIdAllEventsValue:subscriptionValue-cuotas];
            break;
        case kSWITCH_TAG_INTENSO:
            if (switchState){
                int newIdAllEventValue = 0;
                
                if ([subscription isOnSubscription:goles])
                    newIdAllEventValue = newIdAllEventValue + goles;
                if ([subscription isOnSubscription:inicio])
                    newIdAllEventValue = newIdAllEventValue + inicio+fin+suspendido+endmin90+inicioProrroga+finalProrroga+inicioPenalties;
                if ([subscription isOnSubscription:expulsiones])
                    newIdAllEventValue = newIdAllEventValue + expulsiones;
                
                newIdAllEventValue = newIdAllEventValue + recordatoriounahoraantes+descanso+finDescanso+alineacion+penaltis+penaltiAmarilla+penaltiRoja+penaltiFallado+amarilla+cambios+ofertadelpartido+cuotas;
       
                [subscription setIdAllEventsValue:newIdAllEventValue];
            
            }else{
                int newIdAllEventValue = subscription.idAllEventsValue;
                if ([subscription isOnSubscription:recordatoriounahoraantes])   newIdAllEventValue = newIdAllEventValue - recordatoriounahoraantes;
                if ([subscription isOnSubscription:descanso])                   newIdAllEventValue = newIdAllEventValue - descanso-finDescanso;
                if ([subscription isOnSubscription:alineacion])                 newIdAllEventValue = newIdAllEventValue - alineacion;
                if ([subscription isOnSubscription:penaltis])                   newIdAllEventValue = newIdAllEventValue - penaltis-penaltiAmarilla-penaltiRoja-penaltiFallado;
                if ([subscription isOnSubscription:amarilla])                   newIdAllEventValue = newIdAllEventValue - amarilla;
                if ([subscription isOnSubscription:cambios])                    newIdAllEventValue = newIdAllEventValue - cambios;
                if ([subscription isOnSubscription:ofertadelpartido])           newIdAllEventValue = newIdAllEventValue - ofertadelpartido;
                if ([subscription isOnSubscription:cuotas])                     newIdAllEventValue = newIdAllEventValue - cuotas;

                [subscription setIdAllEventsValue:newIdAllEventValue];
            }
            break;
        default:
            break;
    }
    subscription.csys_syncronized = kJSON_SYNCRO_UPDATED;
    [[CoreDataManager sharedInstance] saveContext];
    [self.tableView reloadData];
    
}

#pragma mark - Table view data source
//------------------------------------------------------------------------------
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    
    return 2;
}

//------------------------------------------------------------------------------
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (section == 0)
        return  NSLocalizedString(@"_notificacionesBasicas",nil);
    else if (section == 1)
        return  NSLocalizedString(@"_notificacionesAdicionales",nil);

    return nil;
}

//------------------------------------------------------------------------------
- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section{
    
    if(section == 1){
        if ([[CuotasManager singleton] isProviderActive])
            return [NSString stringWithFormat: NSLocalizedString(@"_ConfigTeamViewFooterCuotas", nil)];
        else
            return [NSString stringWithFormat: NSLocalizedString(@"_ConfigTeamViewFooterNoCuotas", nil)];
    }
    else
        return nil;
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {

    if (section == 0)
        return 5;
    else if (section == 1){
        if ([[CuotasManager singleton] isProviderActive])
            return 8;
        else
            return 7;
    }
    return 0;
}

// Customize the appearance of table view cells.
//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    
    Subscription *subscription = [self.mOriginalTeamData subscription];

    static NSString *CellIdentifier = @"Cell";
    
    //    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    UITableViewCell *cell = nil;
    
    if (cell == nil)
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CellIdentifier];
    
    // Configure the cell...
    [cell setUserInteractionEnabled:YES];
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    
    if (indexPath.section == 0 ) {
        
        UISwitch *basicoSwitch = [[UISwitch alloc]initWithFrame:CGRectZero];
        [basicoSwitch addTarget:self action:@selector(switchTouched:) forControlEvents:UIControlEventValueChanged];
        SML *sml = self.mOriginalTeamData.subscription.sml;

        switch (indexPath.row) {

            case 0:
                cell.accessoryView = basicoSwitch;
                basicoSwitch.tag = kSWITCH_TAG_GOLES;
                [basicoSwitch setOn:[subscription isOnSubscription:goles]];
                cell.textLabel.text = NSLocalizedString(@"Goles",nil);
                break;
            case 1:
                cell.selectionStyle = UITableViewCellSelectionStyleGray;
                cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                cell.textLabel.text = NSLocalizedString(@"Mensaje de gol",nil);
                cell.detailTextLabel.textColor = [UIColor lightGrayColor];
                cell.detailTextLabel.text = [sml getMessageString];
                if ([subscription isOnSubscription:goles])
                    cell.textLabel.textColor = [UIColor blackColor];
                else {
                    cell.textLabel.textColor = [UIColor lightGrayColor];
                    cell.userInteractionEnabled = NO;
                }
                break;
            case 2:
                cell.selectionStyle = UITableViewCellSelectionStyleGray;
                cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                cell.textLabel.text = NSLocalizedString(@"Sonido de gol",nil);
                cell.detailTextLabel.textColor = [UIColor lightGrayColor];
                cell.detailTextLabel.text = [sml getSoundMessageString];
                if ([subscription isOnSubscription:goles])
                    cell.textLabel.textColor = [UIColor blackColor];
                else {
                    cell.textLabel.textColor = [UIColor lightGrayColor];
                    cell.userInteractionEnabled = NO;
                }
                break;
            case 3:
                cell.accessoryView = basicoSwitch;
                basicoSwitch.tag = kSWITCH_TAG_INICIO_FIN;
                [basicoSwitch setOn:[subscription isInicioBlockOn:subscription]];
                cell.textLabel.text = NSLocalizedString(@"Inicio y fin partido",nil);
                break;
            case 4:
                cell.accessoryView = basicoSwitch;
                basicoSwitch.tag = kSWITCH_TAG_EXPULSIONES;
                [basicoSwitch setOn:[subscription isOnSubscription:expulsiones]];
                cell.textLabel.text = NSLocalizedString(@"Expulsiones",nil);
            default:
                break;
      
        }

    } else if (indexPath.section == 1) {

        UISwitch *intenseSwitch = [[UISwitch alloc]initWithFrame:CGRectZero];
        [intenseSwitch addTarget:self action:@selector(switchTouched:) forControlEvents:UIControlEventValueChanged];
        cell.accessoryView = intenseSwitch;
        
        switch (indexPath.row) {
            
            case 0:
                [intenseSwitch setOn:[subscription isAdicionalBlockOn:subscription]];
                intenseSwitch.onTintColor = [UIColor orangeColor];
                intenseSwitch.tag = kSWITCH_TAG_INTENSO;
                cell.textLabel.text = NSLocalizedString(@"_activarTodas",nil);
                cell.textLabel.textColor = [UIColor orangeColor];
                break;
            case 1:
                [intenseSwitch setOn:[subscription isOnSubscription:recordatoriounahoraantes]];
                intenseSwitch.tag = kSWITCH_TAG_1HORA_ANTES;
                cell.textLabel.text = NSLocalizedString(@"Recordatorio 1h antes",nil);
                break;
            case 2:
                [intenseSwitch setOn:([subscription isOnSubscription:descanso] && [subscription isOnSubscription:finDescanso])];
                intenseSwitch.tag = kSWITCH_TAG_DESCANSO;
                cell.textLabel.text = NSLocalizedString(@"Descanso",nil);
                break;
            case 3:
                [intenseSwitch setOn:[subscription isOnSubscription:alineacion]];
                intenseSwitch.tag = kSWITCH_TAG_ALINEACION;
                cell.textLabel.text = NSLocalizedString(@"_TeamDetailIntenseModeLineupsLabel",nil);
                break;
            case 4:
                [intenseSwitch setOn:([subscription isOnSubscription:penaltis] &&
                 [subscription isOnSubscription:penaltiFallado] &&
                 [subscription isOnSubscription:penaltiAmarilla] &&
                 [subscription isOnSubscription:penaltiRoja])];
                intenseSwitch.tag = kSWITCH_TAG_PENALTIES;
                cell.textLabel.text = NSLocalizedString(@"_TeamDetailIntenseModePenaltiesLabel",nil);
                break;
            case 5:
                [intenseSwitch setOn:[subscription isOnSubscription:amarilla]];
                intenseSwitch.tag = kSWITCH_TAG_AMARILLA;
                cell.textLabel.text = NSLocalizedString(@"_TeamDetailIntenseModeYellowCardsLabel",nil);
                break;
            case 6:
                [intenseSwitch setOn:[subscription isOnSubscription:cambios]];
                intenseSwitch.tag = kSWITCH_TAG_CAMBIOS;
                cell.textLabel.text = NSLocalizedString(@"_TeamDetailIntenseModeChangedPlayersLabel",nil);
                break;
            case 7:
                [intenseSwitch setOn:[subscription isOnSubscription:cuotas]];
                intenseSwitch.tag = kSWITCH_TAG_CUOTAS;
                cell.textLabel.text = NSLocalizedString(@"_TeamDetailIntenseModeCuotas", nil);

        }
    }
	
    return cell;
}

//------------------------------------------------------------------------------
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (indexPath.section == 0) {
        if (indexPath.row == 1)
        {
            ConfigTeamGoalTextViewController *cftgtVC = [[ConfigTeamGoalTextViewController alloc]
                                                         initWithNibName:@"ConfigTeamGoalTextViewController" bundle:nil];
            [cftgtVC setMOriginalTeamData:[self mOriginalTeamData]];
            [self.navigationController pushViewController:cftgtVC animated:YES];
        }
        else if(indexPath.row == 2)
        {
            ConfigTeamGoalSoundViewController *cftgsVC = [[ConfigTeamGoalSoundViewController alloc]
                                                          initWithNibName:@"ConfigTeamGoalSoundViewController" bundle:nil];
            [cftgsVC setMOriginalTeamData:[self mOriginalTeamData]];
            [self.navigationController pushViewController:cftgsVC animated:YES];
        }
    }
}

#pragma mark - REST METHODS

//------------------------------------------------------------------------------
-(void)setSubscriptionDidResponse:(NSDictionary *)responseObject {
    
    self.mOriginalTeamData.subscription.csys_syncronized = kJSON_SYNCRO_SYNCRONIZED;
    [[CoreDataManager sharedInstance] saveContext];
}

@end

