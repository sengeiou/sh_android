//
//  ProviderBetViewController.m
//  Goles Messenger
//
//  Created by Luis Rollon on 06/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ProviderBetViewController.h"
#import "Utils.h"
#import "Team.h"
#import "Fav24ExtendedNavigationController.h"
#import "LandingPageViewController.h"
#import "Fav24Fonts.h"
#import "CuotasManager.h"
#import "NSDate+Utils.h"
#import "BetType.h"
#import "BetWebViewController.h"
#import "InformationToBetViewController.h"
#import "CoreDataParsing.h"
#import "Services.pch"
#import "Flurry.h"
#import "InformationBetWithFriendsViewController.h"

@interface ProviderBetViewController ()

@property (nonatomic, strong) UIRefreshControl              *mRefreshControl;
@property (nonatomic, strong) NSNumber                      *mBetMoney;
@property (nonatomic, strong) UIPickerView                  *mPickerQuantity;
@property (nonatomic, strong) UILabel                       *mLabelTextEuros;
@property (nonatomic, strong) IBOutlet UITableView          *mTableViewBets;
@property (nonatomic, strong) NSArray                       *mItemsPickerView;
@property (nonatomic, strong) MoneyBetCell                  *mMoneyBetCell;
@property (nonatomic, strong) UILabel                       *mNoCuotasLabel;

@end

@implementation ProviderBetViewController

#pragma mark - View Lifecycle

//------------------------------------------------------------------------------
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    
    [super viewDidLoad];
    [self pickerSetup];
    [self pickerView:self.mPickerQuantity didSelectRow:0 inComponent:1];
    [self configPullToRefresh];
    
    Fav24ExtendedNavigationController *nav = (Fav24ExtendedNavigationController *)[self navigationController];
    [nav hidePatch:YES];
    
    self.mTableViewBets.tableHeaderView = [Utils createPickerFooterView:NSLocalizedString(@"_providerBetPickerFooter", nil)];
    
    //Observer to update cuotas when returning from background
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onReturnFromBackground) name:@"returnFromBackground" object:nil];
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    self.title = @"Cuotas";
    
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"_back", nil)
                                                                   style:UIBarButtonItemStyleBordered
                                                                  target:nil
                                                                  action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    
    UIBarButtonItem *registerButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"Registrarse", nil)
                                                                       style:UIBarButtonItemStyleBordered
                                                                      target:self
                                                                      action:@selector(registerNow)];
    self.navigationItem.rightBarButtonItem = registerButton;
    
    
    //Set middleView label with "no cuotas"
    if ([self numberOfSectionsInTableView:self.mTableViewBets] == 0) {
        self.mNoCuotasLabel.frame = CGRectMake(0, self.view.frame.size.height/2-30, 320, 30);
        self.mNoCuotasLabel.text = @"No hay cuotas disponibles";
    }
    
}

//------------------------------------------------------------------------------
-(void)viewWillDisappear:(BOOL)animated {
    
    [super viewWillDisappear:animated];
    Fav24ExtendedNavigationController *nav = (Fav24ExtendedNavigationController *)[self navigationController];
    [nav showProgressBar:NO];
    [nav endProgressbar];
}


#pragma mark - UIPickerView Methods

//------------------------------------------------------------------------------
- (void)pickerSetup {
    
    self.mPickerQuantity = [[UIPickerView alloc] initWithFrame:CGRectMake(135,-18,50,320)];
    self.mPickerQuantity.backgroundColor = [[UIColor whiteColor] colorWithAlphaComponent:0.98f];
    [self.view addSubview:self.mPickerQuantity];
    
    UIView *line = [[UIView alloc] initWithFrame:CGRectMake(0,128,320,0.5)];
    line.backgroundColor = [UIColor lightGrayColor];
    [self.view addSubview:line];
    
    self.mLabelTextEuros = [[UILabel alloc] initWithFrame:CGRectMake(0,102,320,15)];
    self.mLabelTextEuros.textAlignment = NSTextAlignmentCenter;
    self.mLabelTextEuros.font = [UIFont systemFontOfSize:10];
    self.mLabelTextEuros.text = @"euro";
    [self.view addSubview:self.mLabelTextEuros];
    
    CGAffineTransform rotate = CGAffineTransformMakeRotation(-3.141592/2);
    rotate = CGAffineTransformScale(rotate, 1.5, 1.5);
    [self.mPickerQuantity setTransform:rotate];
    
    self.mItemsPickerView = [[NSArray alloc] initWithObjects:@"1", @"2", @"3", @"4", @"5", @"6", @"7", @"8", @"9", @"10", @"12", @"15", @"20", @"25", @"30", @"35", @"40", @"45", @"50", @"60", @"70", @"80", @"90", @"100", @"125", @"150", @"175", @"200", @"225",  @"250", @"275", @"300", @"350", @"400", @"450", @"500", @"600", @"700", @"800", @"900", @"1000", nil];
    
    self.mPickerQuantity.delegate = self;
    self.mPickerQuantity.dataSource = self;
}

//------------------------------------------------------------------------------
- (UIView *)pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView *)view{
    CGRect rect = CGRectMake(0, 0, 140, 50);
    
    UILabel *label = [[UILabel alloc]initWithFrame:rect];
    CGAffineTransform rotate = CGAffineTransformMakeRotation(3.141592/2);
    rotate = CGAffineTransformScale(rotate, 0.25, 0.25);
    [label setTransform:rotate];
    label.text = [self.mItemsPickerView objectAtIndex:row];
    label.font = [UIFont systemFontOfSize:63.0];
    label.textAlignment = NSTextAlignmentCenter;
    
    return label ;
}

//------------------------------------------------------------------------------
/**
 @brief Returns the number of 'columns' to display.
 */
//------------------------------------------------------------------------------
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView {
    return 1;
}

//------------------------------------------------------------------------------
/**
 @brief returns the # of rows in each component.
 */
//------------------------------------------------------------------------------
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
    return [self.mItemsPickerView count];
    
}

//------------------------------------------------------------------------------
/**
 @ brief Bigger return value -> Bigger width elements picker.NSString
 */
//------------------------------------------------------------------------------
- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component{
    return 34.0;
}

//------------------------------------------------------------------------------
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component{
    return [self.mItemsPickerView objectAtIndex:row];
}

//------------------------------------------------------------------------------
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    if(row == 0)
        self.mLabelTextEuros.text = @"euro";
    else
        self.mLabelTextEuros.text = @"euros";
    
    NSNumberFormatter * f = [NSNumberFormatter new];
    [f setNumberStyle:NSNumberFormatterDecimalStyle];
    NSNumber *bet = [f numberFromString:[self.mItemsPickerView objectAtIndex:row]];
    if ( bet && bet > 0 ){
        [self setMBetMoney:bet];
        [[[self mMoneyBetCell] mQuantity] resignFirstResponder];
        [[self mTableViewBets] reloadData];
    }
    
}

#pragma mark - Table view data source
//------------------------------------------------------------------------------
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [self.mCuotasArray count];
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([[[self.mCuotasArray objectAtIndex:section] objectForKey:@"odds"] count] > 0)
        return [[[self.mCuotasArray objectAtIndex:section] objectForKey:@"odds"] count];
    return 1;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"MoneyBetCell";
    id cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    cell = [[BetDescriptionCell alloc]init];
    if ([[[self.mCuotasArray objectAtIndex:indexPath.section] objectForKey:@"odds"] count] ==  0){
        [cell configBetCellWithoutOdds];
        [cell setUserInteractionEnabled:NO];
    }
    else if(indexPath.section == [self numberOfSectionsInTableView:self.mTableViewBets]-1){
        UITableViewCell *defaultCell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"defaultCell"];
        BetTypeOdd *betTypeOdd = [[[self.mCuotasArray objectAtIndex:indexPath.section] objectForKey:@"odds"] objectAtIndex:indexPath.row];
        defaultCell.textLabel.text = betTypeOdd.name;
        defaultCell.textLabel.font = [UIFont systemFontOfSize:17.0];
        defaultCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        return defaultCell;
    }
    else {
        [cell configBetCellWithBetTypeOdd:[[[self.mCuotasArray objectAtIndex:indexPath.section] objectForKey:@"odds"] objectAtIndex:indexPath.row] andEuros:self.mBetMoney];
        [cell setUserInteractionEnabled:YES];
    }
    return cell;
}

//------------------------------------------------------------------------------
-(UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    
    BetType *bettype = [[self.mCuotasArray objectAtIndex:section] objectForKey:@"betType"];
    if ((bettype)&&(!([bettype.name length] == 0)))
        return [Utils createTableHeaderWithText:bettype.name andRightDetail:bettype.title withPenalties:NO];
    else
        //        return [[UIView alloc]initWithFrame:CGRectMake(0, 0, 320, 36)];
        return nil;
    
}

//------------------------------------------------------------------------------
-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    
    BetType *bettype = [[self.mCuotasArray objectAtIndex:section] objectForKey:@"betType"];
    
    if(section == 0){
        return 55.0f;
    }else if ((bettype)&&([bettype.name length] == 0))
        return 36.0f;
    return 55.0f;
    
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    
    if(section == [self numberOfSectionsInTableView:self.mTableViewBets]-1)
        return 150;
    else
        return 1;
}

//------------------------------------------------------------------------------
-(UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    
    if (section == [self.mCuotasArray count]-1)
//        return [Utils createTableHeaderView:NSLocalizedString(@"_providerBetViewFooter", @"") withLink:@"Cómo funciona" andMethod:@selector(openInformationView:) withDelegate:self];
        return [Utils createTableFooterView:NSLocalizedString(@"_providerBetViewFooter", @"") withFirstLink:@"Cómo funciona" andSecondLink:@"Apostar con amigos" inBottomPosition:YES andFirstMethod:@selector(openInformationView:) andSecondMethod:@selector(openBetWithFriendsView:) withDelegate:self];
    
    return nil;
}

//------------------------------------------------------------------------------
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    BOOL didUserTapOnButton = [userDefaults boolForKey:kCUOTAS_USERCLICK];
    
    [self.mTableViewBets deselectRowAtIndexPath:indexPath animated:YES];
    
    BetTypeOdd *odd = [[[self.mCuotasArray objectAtIndex:indexPath.section] objectForKey:@"odds"] objectAtIndex:indexPath.row];
    NSURL *oddURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@",odd.url,self.mBetMoney]];
    
    NSURL *registryURL = [NSURL URLWithString:self.mProvider.registryURL];
    
    NSString *match = [self.mMatch.teamLocal.name stringByAppendingString:[NSString stringWithFormat:@"-%@",self.mMatch.teamVisitor.name]];
    
    
    if(didUserTapOnButton)
    {
        BetWebViewController *vcToShow = [[BetWebViewController alloc]initWithProvider:self.mProvider andURL:oddURL andGoToRegistry:NO];
        [vcToShow setMProvider:self.mProvider];
        
        //Flurry Log
        [Flurry logEvent:K_CUOTAS_onClickToProvider_BetSlip withParameters:@{@"Match":match,@"Stack":self.mBetMoney}];
        
        [self.navigationController pushViewController:vcToShow animated:YES];
    }
    else
    {
        LandingPageViewController *vcToShow = [[LandingPageViewController alloc] initWithNibName:@"LandingPageViewController" bundle:nil];
        vcToShow.oddURL = oddURL;
        vcToShow.registryURL = registryURL;
        vcToShow.provider = self.mProvider;
        
        //Flurry Log
        [Flurry logEvent:K_CUOTAS_onEnterLandingPage withParameters:@{@"Match":match}];
        
        [self.navigationController pushViewController:vcToShow animated:YES];
    }
}

#pragma mark - Helper Methods

//------------------------------------------------------------------------------
- (void)openInformationView:(id)sender
{
    InformationToBetViewController *infoToBetVC = [[InformationToBetViewController alloc] initWithNibName:nil bundle:nil];
    [infoToBetVC setMProvider:self.mProvider];
    
    //Flurry Log
    [Flurry logEvent:K_CUOTAS_onEnterInformacionUtil];
    
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:infoToBetVC];
    [self.navigationController presentViewController:nav animated:YES completion:nil];
}

//------------------------------------------------------------------------------
- (void)openBetWithFriendsView:(id)sender
{
    InformationBetWithFriendsViewController *infoBetWithFriendsVC = [[InformationBetWithFriendsViewController alloc]initWithNibName:nil bundle:nil];
    [infoBetWithFriendsVC setMProvider:[[CuotasManager singleton] getProviderForCurrentOddsForMatch:self.mMatch]];
    
    // Flurry Log
    [Flurry logEvent:K_CUOTAS_ApostarConAmigos withParameters:@{@"Team Local":self.mMatch.teamLocal.name,@"Team Visitor":self.mMatch.teamVisitor.name}];
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:infoBetWithFriendsVC];
    [self.navigationController presentViewController:nav animated:YES completion:nil];
}

#pragma mark - Pull to refresh and return from background refresh

//------------------------------------------------------------------------------
- (void)configPullToRefresh {
    
    self.mRefreshControl = [[UIRefreshControl alloc] init];
    [self.mRefreshControl addTarget:self action:@selector(onPullToRefresh:) forControlEvents:UIControlEventValueChanged];
    [self.mTableViewBets addSubview:self.mRefreshControl];
}

//------------------------------------------------------------------------------
- (void)onPullToRefresh:(UIRefreshControl *)refreshControl {
    
    Fav24ExtendedNavigationController *nav = (Fav24ExtendedNavigationController *)[self navigationController];
    [nav showProgressBar:YES];
    [[FavRestConsumer sharedInstance] getOddsForMatch:self.mMatch withDelegate:self];
    
}

//------------------------------------------------------------------------------
- (void)onReturnFromBackground {
    
    Fav24ExtendedNavigationController *nav = (Fav24ExtendedNavigationController *)[self navigationController];
    [nav showProgressBar:YES];
    [[FavRestConsumer sharedInstance] getOddsForMatch:self.mMatch withDelegate:self];
}

#pragma mark - Private methods

//------------------------------------------------------------------------------
- (void)registerNow {
    
    if (self.mProvider.registryURL) {
        
        NSString *match = [self.mMatch.teamLocal.name stringByAppendingString:[NSString stringWithFormat:@"-%@",self.mMatch.teamVisitor.name]];
        [Flurry logEvent:K_CUOTAS_RegisterFromMatch withParameters:@{@"Match":match}];
        
        BetWebViewController *betWebVC = [[BetWebViewController alloc] initWithProvider:self.mProvider andURL:[NSURL URLWithString:self.mProvider.registryURL] andGoToRegistry:YES];
        [[self navigationController] pushViewController:betWebVC animated:YES];
    }
}

#pragma mark - Service response methods

//------------------------------------------------------------------------------
- (void)getOddsForMatchResponse:(BOOL)betOddsForMatchCorrect {
    self.mCuotasArray = [[CuotasManager singleton] getCompleteOddsForMatch:self.mMatch];
    [self.mTableViewBets reloadData];
    [self.mRefreshControl endRefreshing];
    [(Fav24ExtendedNavigationController *)[self navigationController] endProgressbar];
}

//------------------------------------------------------------------------------
- (void)getOddsForMatchResponseError {
    [self.mTableViewBets reloadData];
    [self.mRefreshControl endRefreshing];
    [(Fav24ExtendedNavigationController *)[self navigationController] endProgressbar];
}

@end
