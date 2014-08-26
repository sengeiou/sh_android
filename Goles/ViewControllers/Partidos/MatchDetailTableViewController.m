//
//  MatchDetailTableViewController.m
//  Goles
//
//  Created by Christian Cabarrocas on 12/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "MatchDetailTableViewController.h"
#import "Fav24ExtendedNavigationController.h"
#import "GenteMatchTableViewController.h"
#import "YoMatchTableViewController.h"
#import "Team.h"
#import "Constants.h"
#import "AppDelegate.h"
#import "EventOfMatch.h"
#import "FavRestConsumer.h"
#import "CoreDataManager.h"
#import "NotificationsTableViewController.h"
#import "MatchChronicleViewController.h"
#import "PartidosTableViewController.h"

@interface MatchDetailTableViewController ()<UITableViewDataSource, UITableViewDelegate>{
    
    AppDelegate *appDelegate;
    BOOL changeSegmented;
    
    
    //Custom NavigationBar
    UIView *navView;
    UILabel *lblTitle;
    UIButton *backBtn;
    
    //propertiesTableView
    NSArray *arrayCellProperties;
    NSArray *arrayImagenesCellProperties;
    
    //golesTableView
    NSArray *arrayGoles;
}

@property (nonatomic, strong)   NSArray *viewControllersSegmented;
@property (nonatomic, strong)   UISegmentedControl *segmentedControl;
@property (nonatomic, strong)   UIViewController *currentViewController;
@property (nonatomic, strong)   IBOutlet UIScrollView *mscrollView;
@property (nonatomic, strong)   UITableView *golesTableView;
@property (nonatomic, strong)   UITableView *propertiesTableView;
@end

@implementation MatchDetailTableViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    if (self.selectedMacth)
        [[FavRestConsumer sharedInstance] getAllEventsForMatch:self.selectedMacth andDelegate:self];

    appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
   
    arrayCellProperties = [NSArray arrayWithObjects:@"Notificaciones",@"Inicio",@"TV",@"Cuotas",@"Alineaciones",@"Cronica", nil];
    arrayImagenesCellProperties = [NSArray arrayWithObjects:[UIImage imageNamed:@"BellBasic"],[UIImage imageNamed:@"BellBasic"],[UIImage imageNamed:@"BellBasic"],[UIImage imageNamed:@"BellBasic"],[UIImage imageNamed:@"BellBasic"],[UIImage imageNamed:@"BellBasic"], nil];
    
    self.golesTableView = [[UITableView alloc]initWithFrame:CGRectMake(0, 0, 320, 0)];
    self.golesTableView.dataSource = self;
    self.golesTableView.delegate = self;
    [self.mscrollView addSubview:self.golesTableView];
    
    self.propertiesTableView = [[UITableView alloc]initWithFrame:CGRectMake(0, 0, 320, 0)];
    self.propertiesTableView.dataSource = self;
    self.propertiesTableView.delegate = self;
    [self.mscrollView addSubview:self.propertiesTableView];
}

-(void)viewWillAppear:(BOOL)animated{
  
    Team *teamLocal = self.selectedMacth.teamLocal;
    Team *teamVisitor = self.selectedMacth.teamVisitor;
  
    NSString *tituloCompuesto = [NSString stringWithFormat:@"%@-%@",  teamLocal.nameShort,
                                 teamVisitor.nameShort];

    
    CGRect frame = CGRectMake(0.0f, 0.0f, 320.0f, kHeightNavBarWithSegmentedControl);
    [self.navigationController.navigationBar setFrame:frame];

    
    if (![self.navigationController.navigationBar viewWithTag:18]){
        [self.navigationController.navigationBar addSubview:[self createNavigationPatchView]];
        [self customBackButtonItem];
        [self customTitleView:tituloCompuesto];
    }
   
}

- (UIView *)createNavigationPatchView {
    

    NSUInteger height = 30;
    
    navView = [[UIView alloc] initWithFrame:CGRectMake(0, 74, 320, height)];
    
    navView.tag = 18;
    
    GenteMatchTableViewController *genteMatchVC = [appDelegate.partidosSB instantiateViewControllerWithIdentifier:@"genteMatchVC"];
    YoMatchTableViewController *yoMatchVC = [appDelegate.partidosSB instantiateViewControllerWithIdentifier:@"yoMatchVC"];

    // Add A and B view controllers to the array
    self.viewControllersSegmented = [[NSArray alloc] initWithObjects:self, genteMatchVC, yoMatchVC,nil];
    
    self.segmentedControl = [[UISegmentedControl alloc]
                             initWithItems:(NSArray *)@[@"Info",@"Gente", @"Yo"]];
    
    self.segmentedControl.frame = CGRectMake(20, 0, 280, height);
    
    [self.segmentedControl addTarget:self action:@selector(indexDidChangeForSegmentedControl:) forControlEvents: UIControlEventValueChanged];
    
    // Ensure a view controller is loaded
    self.segmentedControl.selectedSegmentIndex = 0;
    [self.segmentedControl setUserInteractionEnabled:YES];
    
    [navView addSubview:self.segmentedControl];
    
    navView.userInteractionEnabled = YES;
    
    return navView;
}

//------------------------------------------------------------------------------
-(void) customBackButtonItem{
    
    self.navigationItem.hidesBackButton = YES;
    
    backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    UIImage *backBtnImage = [UIImage imageNamed:@"WebViewBackImage"];
    
    backBtn.tag = 19;
    
    [backBtn setBackgroundImage:backBtnImage forState:UIControlStateNormal];
    [backBtn addTarget:self action:@selector(goBack) forControlEvents:UIControlEventTouchUpInside];
    backBtn.frame = CGRectMake(10, 40, backBtnImage.size.width, backBtnImage.size.height);
    
    [self.navigationController.navigationBar addSubview:backBtn];
}

//------------------------------------------------------------------------------
-(void) customTitleView:(NSString *)titulo{
    
    lblTitle = [[UILabel alloc]initWithFrame:CGRectMake(50, 25, 220, 50)];
    lblTitle.textAlignment = NSTextAlignmentCenter;
    lblTitle.text = titulo;
    
    lblTitle.tag = 20;
    
    [self.navigationController.navigationBar addSubview:lblTitle];
}

//------------------------------------------------------------------------------
-(void) navBarNotCustom{
    if ([self.navigationController.navigationBar viewWithTag:18])
        [[self.navigationController.navigationBar viewWithTag:18] removeFromSuperview];
    
    
    if ([self.navigationController.navigationBar viewWithTag:19])
        [[self.navigationController.navigationBar viewWithTag:19] removeFromSuperview];
    
    
    if ([self.navigationController.navigationBar viewWithTag:20])
        [[self.navigationController.navigationBar viewWithTag:20] removeFromSuperview];
    
    CGRect frame = CGRectMake(0.0f, 0.0f, 320.0f, 64);
    [self.navigationController.navigationBar setFrame:frame];
}


//------------------------------------------------------------------------------
-(void)goBack{
    
    [self navBarNotCustom];
    
    NSArray * viewControllers = [self.navigationController viewControllers];
    
    for (int i = 0; i < viewControllers.count; i++) {
        
        UIViewController *controller = [viewControllers objectAtIndex:i];
        
        if ([controller isKindOfClass:[PartidosTableViewController class]])
            
            [self.navigationController popToViewController:controller animated:YES];
    }
}

#pragma mark - View controller switching and saving

- (void)indexDidChangeForSegmentedControl:(UISegmentedControl *)aSegmentedControl {
    
    changeSegmented = YES;
    
    self.title = @"";
    [self.navigationItem setHidesBackButton:YES animated:YES];

    NSUInteger index = aSegmentedControl.selectedSegmentIndex;
   
    UIViewController * incomingViewController = [self.viewControllersSegmented objectAtIndex:index];

    NSArray * viewControllers = [self.navigationController viewControllers];
   
    NSMutableArray * newViewControllers;
    
    if (!newViewControllers)
        newViewControllers = [[NSMutableArray alloc] init];
    else
        [newViewControllers removeAllObjects];
        
    for (int i = 0; i < viewControllers.count; i++) {
        [newViewControllers addObject:[viewControllers objectAtIndex:i]];
    }
    if ([newViewControllers containsObject:incomingViewController])
        [newViewControllers removeObject:incomingViewController];
    
    [newViewControllers addObject:incomingViewController];
    
    [self.navigationController setViewControllers:newViewControllers animated:NO];
}

//------------------------------------------------------------------------------
-(void)addTableViews{
    
    CGRect frame = self.golesTableView.frame;
    CGSize content = self.golesTableView.contentSize;
    frame.size = content;
    
    if (content.height != 0)
        frame.origin.y = -84;
    
    [[self golesTableView] setFrame:frame];
    self.golesTableView.backgroundColor = [UIColor redColor];
    self.golesTableView.scrollEnabled = NO;
    
    CGRect frame2 = self.propertiesTableView.frame;
   
    if (content.height == 0)
        frame2.origin.y = -84;
    else
        frame2.origin.y = content.height-84;
    
    CGSize content2 = self.propertiesTableView.contentSize;
    frame2.size = content2;
    [[self propertiesTableView] setFrame:frame2];

    self.propertiesTableView.scrollEnabled = NO;

   [[self mscrollView] setContentSize:CGSizeMake(320, frame2.size.height+frame2.origin.y)];
}

//------------------------------------------------------------------------------
-(void)updateEventsMatchList:(Match *)match{
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:[NSString stringWithFormat:@"idEvent == 2 AND idMatch == %d",[match.idMatch intValue]]];

    //NSArray *events = [[match getOrderedEvents] filteredArrayUsingPredicate:predicate];
    //arrayGoles = events;
    
    arrayGoles = [[CoreDataManager sharedInstance] getAllEntities:[EventOfMatch class] orderedByKey:@"dateIn" ascending:NO withPredicate:predicate];
    
    [self.golesTableView reloadData];
    [self addTableViews];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source
//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if (tableView == self.propertiesTableView)
        return arrayCellProperties.count;

    return arrayGoles.count;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (tableView == self.propertiesTableView) {
       
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"propertiesCell"];
        if ( !cell )
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"propertiesCell"];
        

              // Configure the cell...
        cell.imageView.image = arrayImagenesCellProperties[indexPath.row];
        cell.textLabel.text = arrayCellProperties[indexPath.row];
        
        if (indexPath.row == 1 || indexPath.row == 2) {
            cell.accessoryType = UITableViewCellAccessoryNone;
             cell.selectionStyle = UITableViewCellSelectionStyleNone;
        }else
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
        cell.backgroundColor = [UIColor clearColor];

        return cell;
    }else{
        UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CustomGoalCell"];
        
        if ( !cell ){
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"CustomGoalCell"];
            [cell setUserInteractionEnabled:NO];
        }
        
        if ( [arrayGoles count] > 0 )
            return [self createGoalCell:cell withIndexPathRow:indexPath.row];
        
        cell.backgroundColor = [UIColor clearColor];
    
        return cell;
    }
}

//------------------------------------------------------------------------------
- (UITableViewCell *)createGoalCell:(UITableViewCell *)cell withIndexPathRow:(int)index {
    
    EventOfMatch *event = [arrayGoles objectAtIndex:index];
    
    //Get player name from the event description
    NSString *playerName = [[event actorTransmitterName] stringByReplacingOccurrencesOfString:@"Gol de " withString:@""];
    NSString *cellText = [playerName stringByAppendingString:[NSString stringWithFormat:@" %@'",[event matchMinute]]];
    cell.textLabel.text = cellText;
    cell.textLabel.enabled = YES;
    cell.textLabel.textColor = [UIColor blackColor];
    
    NSString *detailText = [NSString stringWithFormat:@"%d-%d", [event localScoreValue], [event visitorScoreValue]];
    cell.detailTextLabel.text = detailText;
    cell.detailTextLabel.enabled = YES;
    
    //Get the team of player who scores
    /*UIImage *badge = [UIImage imageNamed:[[event eventTeam] urlImage]];
    if ( !badge )       badge = [UIImage imageNamed:@"DefaultTeamShield.png"];
    cell.imageView.image = badge;*/
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{

    if (tableView == self.propertiesTableView) {
       
        [self navBarNotCustom];
        
        CGRect frame = CGRectMake(0.0f, 0.0f, 320.0f, 64);
        [self.navigationController.navigationBar setFrame:frame];
        
        if (indexPath.row == 0) { //notificaciones
            
            NotificationsTableViewController *notificationsVC = [appDelegate.partidosSB  instantiateViewControllerWithIdentifier:@"notificationsVC"];
            notificationsVC.selectedMatch = self.selectedMacth;

            [self.navigationController pushViewController:notificationsVC animated:YES];
            
        }else if (indexPath.row == 5){ //cronica
            //cronicaVC
            MatchChronicleViewController *cronicaVC = [appDelegate.partidosSB  instantiateViewControllerWithIdentifier:@"cronicaVC"];
            cronicaVC.mMatch = self.selectedMacth;
            
            [self.navigationController pushViewController:cronicaVC animated:YES];
        }
    }
}


#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}


@end
