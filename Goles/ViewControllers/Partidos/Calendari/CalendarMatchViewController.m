//
//  MatchViewController.m
//  iGoles
//
//  Created by Administrador on 30/08/10.
//  Copyright 2012 Opentrends. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "CalendarMatchViewController.h"
#import "CustomCellCalendar.h"
#import "Constants.h"
#import "NSDate+Utils.h"
#import "Flurry.h"

@interface CalendarMatchViewController ()

@property (nonatomic, strong) IBOutlet UITableView *tableView;
@property (nonatomic,weak) IBOutlet UILabel *noMatchesLabel;

@end

@implementation CalendarMatchViewController

#pragma mark - View lifecycle

//- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil passedData:(DMFixture *)dataArgs {
//	if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {
//        
//        client = [[WSClient alloc] init];
//        client.delegate = self;
//        
//		self.fixtureData = dataArgs;
//        
//        [self.navigationItem setBackBarButtonItem:nil];
//	}
//	
//	return self;
//}

//------------------------------------------------------------------------------
-(void)viewDidLoad {
    [super viewDidLoad];
    
    [self addNavigationCloseButton];
//    self.navigationItem.title = [NSString stringWithFormat:@"%@",[self.fixtureData getLocalizedDescription]];
    [self loadData];
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [Flurry logEvent:K_CALENDAR_onEnter_ROUND];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
//    self.tableView.backgroundView = [[UIImageView alloc] initWithImage:[self.backgroundImage applyBlurWithRadius:24
//                                                                                                       tintColor:[UIColor colorWithWhite:1 alpha:0.5]
//                                                                                           saturationDeltaFactor:1.8
//                                                                                                       maskImage:nil]];

}

//------------------------------------------------------------------------------
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

//------------------------------------------------------------------------------
- (void)viewWillDisappear:(BOOL)animated {
	[super viewWillDisappear:animated];
}

#pragma mark - Private Methods
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
- (void)loadData {
//    [client getMatches:fixtureData.idFixture];
}


//------------------------------------------------------------------------------
- (void)popViewController:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}


#pragma mark - WSClient delegate
//------------------------------------------------------------------------------
//- (void)matchesAddDidResponse:(DMMatchList *)matches {
//   
//    [self setMatchData:matches];
//    if ([self.matchData count] > 0) {
//        self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
//        _noMatchesLabel.frame = CGRectZero;
//        _noMatchesLabel.hidden = YES;
//    }else {
//        self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
//        _noMatchesLabel.frame = CGRectMake(0, 0, 320, self.view.frame.size.height-113);
//        _noMatchesLabel.hidden = NO;
//    }
//    [self.tableView reloadData];
//}


#pragma mark - Table view data source

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
//	int len = [self.matchData count];
	return 1;
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath; {
	return 65;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {

	static NSString *CellIdentifier = @"CustomCellCalendar";
    CustomCellCalendar *cell = [self.tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    cell = nil;
    if (cell == nil) {
        UIViewController *nib = [[UIViewController alloc] initWithNibName:@"CustomCellCalendar" bundle:nil];
        cell = (CustomCellCalendar *)[nib view];
    }
    
    cell.accessoryType = UITableViewCellAccessoryNone;
    
//    DMMatch *foo = [self.matchData.matches objectAtIndex:indexPath.row];
//    [cell configCellWithMatchData:foo];
//    cell.backgroundColor = [UIColor clearColor];
   

	return cell;
}


@end
