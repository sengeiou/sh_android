//
//  FixtureViewController.m
//  iGoles
//
//  Created by Administrador on 27/08/10.
//  Copyright 2012 Opentrends. All rights reserved.
//

#import "CalendarFixtureViewController.h"
#import "CalendarMatchViewController.h"
#import "Round.h"
#import "Constants.h"
#import "NSDate+Utils.h"
#import "Flurry.h"
#import "UIImage+ImageEffects.h"

@interface CalendarFixtureViewController()

@property (nonatomic,strong)    UIActivityIndicatorView *activity;
@property (nonatomic,strong)    IBOutlet UILabel        *noDataLabel;
@property (nonatomic)           int                     indexToShow;
@property (nonatomic,strong)    Round                   *mRound;
@property (nonatomic,strong)    NSArray                 *roundData;

@end


@implementation CalendarFixtureViewController

#pragma mark - View lifecycle

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
   /* client = [[WSClient alloc] init];
    client.delegate = self;*/
    
    self.title = self.mTournament.name;
    self.noDataLabel.hidden = YES;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    [self reloadFixtureData:self.mTournament.idTournamentValue];
    [self addNavigationCloseButton];
    
    self.activity = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    self.activity.frame = CGRectMake((self.view.frame.size.width/2)-15, (self.view.frame.size.height/2)-50, 30, 30);
    [self.tableView addSubview:self.activity];
    [self.activity startAnimating];
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated {
    
    [super viewWillAppear:animated];
    
    [Flurry logEvent:K_CALENDAR_onEnter_TOURNAMENT];
    [self.tableView reloadData];

    self.tableView.backgroundView = [[UIImageView alloc] initWithImage:[self.backgroundImage applyBlurWithRadius:24
                                                                                                       tintColor:[UIColor colorWithWhite:1 alpha:0.5]
                                                                                           saturationDeltaFactor:1.8
                                                                                                       maskImage:nil]];

}

//------------------------------------------------------------------------------
- (void)viewDidAppear:(BOOL)animated {
    
    [super viewDidAppear:animated];

}

//------------------------------------------------------------------------------
- (void)reloadFixtureData:(long long)idTournament {
    
    //[client getFixtures:idTournament];
}

#pragma mark - Public methods
//------------------------------------------------------------------------------
/*- (void)fixtureAddDidResponse:(DMFixtureList*)newFixtureList {
    
    [self.activity stopAnimating];
	self.fixtureData = [NSArray arrayWithArray:newFixtureList.fixtures];
    
    [self noDataLabelSetup];
    
    [self.tableView reloadData];

}*/

#pragma mark - Private methods
//------------------------------------------------------------------------------
- (void)noDataLabelSetup {
    
    if (self.roundData.count <= 0) {
        self.noDataLabel.text = NSLocalizedString(@"No hay ningún partido para el evento seleccionado.",nil);
        self.noDataLabel.hidden = NO;
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    }else {
        self.noDataLabel.hidden = YES;
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    }
}

//------------------------------------------------------------------------------
-(void)addNavigationCloseButton{
    UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"_close", @"")
                                                                   style:UIBarButtonItemStylePlain
                                                                  target:self
                                                                  action:@selector(onCloseButtonClick)];
    [[self navigationItem] setRightBarButtonItem:doneButton];
}

//------------------------------------------------------------------------------
-(void)onCloseButtonClick{
    [self dismissViewControllerAnimated:YES completion:nil];
}

//------------------------------------------------------------------------------
- (void)popViewController:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}


#pragma mark - Table view data source
//------------------------------------------------------------------------------
- (void)selectRowAtIndexPath:(NSIndexPath *) path {
    if(path.row < [self tableView:self.tableView numberOfRowsInSection:path.section] && path.section < [self numberOfSectionsInTableView:self.tableView])
           [self.tableView selectRowAtIndexPath:path animated:NO scrollPosition:UITableViewScrollPositionMiddle ];
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	
    if (self.roundData.count <= 0)
        return 0;
  
    return self.roundData.count;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
	
	if ([self.roundData count] > 0) {
		if (cell==nil) {
			cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier];
        }
        
		cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
       /* DMFixture *foo = [self.fixtureData objectAtIndex:indexPath.row];
        
		NSString *titleFixture = [[NSString alloc] initWithFormat:@"%@",[foo getLocalizedDescription]];
	    NSDate *dateStart =foo.dateStart;
        NSDate *dateEndTemp = foo.dateEnd;
        
        // We set dateEnd to 8 hours earlier to show the right DAY
        NSDate *dateEnd = [dateEndTemp dateByAddingTimeInterval:-28800.0];
		NSString *dateStartParsed = [foo.dateStart getFormattedDate];
		NSString *dateEndParsed = [dateEnd getFormattedDate];
        NSDate *today = [[NSDate alloc] init];
        
        NSComparisonResult result = [today compare:dateEnd];
	    if([self date:today isBetweenDate:dateStart andDate:dateEnd]) {
        	self.indexToShow = indexPath.row;
        }
        
		if (result == NSOrderedAscending)
			cell.textLabel.textColor = [UIColor blackColor];
		else
			cell.textLabel.textColor = [UIColor lightGrayColor];
		cell.textLabel.text = [NSString stringWithFormat:@"%@",titleFixture];
        cell.textLabel.font = [UIFont systemFontOfSize:17];
        
        NSTimeInterval dayDif = [dateEnd timeIntervalSinceDate:dateStart];
        if (dayDif > 86400)
            cell.detailTextLabel.text = [NSString stringWithFormat:NSLocalizedString(@"del %@ al %@",nil), dateStartParsed, dateEndParsed];
        else
            cell.detailTextLabel.text = [NSString stringWithFormat:NSLocalizedString(@"%@",nil), dateStartParsed];*/
        
        cell.detailTextLabel.font = [UIFont systemFontOfSize:12];
        cell.detailTextLabel.textColor = [UIColor lightGrayColor];

	}
    cell.backgroundColor = [UIColor clearColor];
	return cell;
}

//------------------------------------------------------------------------------
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
   /* DMFixture *foo = [self.fixtureData objectAtIndex:indexPath.row];
	CalendarMatchViewController *matchViewController = [[CalendarMatchViewController alloc] initWithNibName:@"CalendarMatchViewController" bundle:nil passedData:foo];
    matchViewController.backgroundImage = self.backgroundImage;
    [self.navigationController pushViewController: matchViewController animated: YES];*/
}

#pragma mark - Methods Date
//------------------------------------------------------------------------------
- (NSDate *)NSStringToNSDate:(NSString *)dateString {
    long long interval = [dateString longLongValue]/1000;
    NSDate *dateFromInterval = [NSDate dateWithTimeIntervalSince1970:interval];
    NSDateFormatter *frm = [[NSDateFormatter alloc] init];
    [frm setTimeZone:[NSTimeZone timeZoneWithName:@"UTC"]];
    [frm setDateFormat:@"yyyy/MM/dd HH:mm:ss"];
    NSString *bar = [frm stringFromDate:dateFromInterval];
    return [frm dateFromString:bar];
}

//------------------------------------------------------------------------------
- (NSString *)NSDateToNSString:(NSDate *)date {
    NSString *dateString = [[NSString alloc] initWithFormat:@"%@",date];
    long long interval = [dateString longLongValue]/1000;
    NSDate *dateFromInterval = [NSDate dateWithTimeIntervalSince1970:interval];
    NSDateFormatter *frm = [[NSDateFormatter alloc] init];
    [frm setTimeZone:[NSTimeZone timeZoneWithName:@"UTC"]];
    [frm setDateFormat:@"yyyy/MM/dd HH:mm:ss"];
    NSString *bar = [frm stringFromDate:dateFromInterval];
    return bar;
}

//------------------------------------------------------------------------------
- (BOOL)date:(NSDate*)date isBetweenDate:(NSDate*)beginDate andDate:(NSDate*)endDate {
    if ([date compare:beginDate] == NSOrderedAscending)
        return NO;
    if ([date compare:endDate] == NSOrderedDescending) 
        return NO;
    return YES;
}

//------------------------------------------------------------------------------
- (BOOL) todayIsBetween:(NSString*)dateStart andDate:(NSString*)dateEnd {
    BOOL dateIsBetween = YES;
    NSDate *today = [[NSDate alloc] init];
    if ([today compare:[self NSStringToNSDate:dateStart]] == NSOrderedAscending)
        dateIsBetween = NO;
    
    if ([today compare:[self NSStringToNSDate:dateEnd]] == NSOrderedDescending) 
        dateIsBetween = NO;

    return dateIsBetween;
}

@end

