//
//  TimeLineViewController.m
//  Goles
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "TimeLineViewController.h"
#import "FavRestConsumer.h"
#import "ShotManager.h"
#import "User.h"
#import "Shot.h"
#import "CoreDataManager.h"
#import "ShotTableViewCell.h"
#import "Utils.h"
#import "Conection.h"
#import "Fav24Colors.h"
#import "AppDelegate.h"
#import "Constants.h"
#import "ProfileViewController.h"
#import "UIImageView+AFNetworking.h"

@interface TimeLineViewController ()<UIScrollViewDelegate, UITextViewDelegate, ConectionProtocol>{
    NSUInteger lengthTextField;
    BOOL moreCells;
    BOOL refreshTable;
}

@property (nonatomic,weak) IBOutlet UITableView    *timelineTableView;
@property (weak, nonatomic) IBOutlet UIButton *btnWatching;
@property (weak, nonatomic) IBOutlet UIButton *btnSearch;
@property (weak, nonatomic) IBOutlet UIButton *btnInfo;
@property (weak, nonatomic) IBOutlet UITextView *txtView;
@property (weak, nonatomic) IBOutlet UIButton *btnShoot;
@property (weak, nonatomic) IBOutlet UILabel *charactersLeft;
@property (weak, nonatomic) IBOutlet UIView *viewNotShots;
@property (strong, nonatomic) NSArray *arrayShots;
@property (strong, nonatomic) UIRefreshControl *refreshControl;
@property (weak, nonatomic) IBOutlet UIView *viewOptions;
@property (weak, nonatomic) IBOutlet UIView *viewTextField;
@property (nonatomic, assign) CGFloat lastContentOffset;
@property (strong, nonatomic) IBOutlet UIView *backgroundView;
@property (assign, nonatomic) int sizeKeyboard;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint *bottomViewPositionConstraint;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint *bottomViewHeightConstraint;
@property(nonatomic, strong) UIActivityIndicatorView *spinner;
@property(nonatomic, strong) UILabel *lblFooter;
@property (weak, nonatomic) IBOutlet UIButton *startShootingFirstTime;
@property (strong, nonatomic) NSString *textComment;

@end

@implementation TimeLineViewController

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self restrictRotation:NO];
    
    lengthTextField = 0;
   
    [self initSpinner];
    
    self.arrayShots = [[NSArray alloc]init];
    self.btnShoot.enabled = NO;
    self.txtView.delegate = self;

    //For Alpha version
    self.viewOptions.hidden = YES;

    [self.btnShoot addTarget:self action:@selector(sendShot) forControlEvents:UIControlEventTouchUpInside];
    
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
    
    //Get shots from CoreData
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];

    //Listen for synchro process end
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadShotsTable:) name:K_NOTIF_SHOT_END object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardHide:) name:UIKeyboardWillHideNotification object:nil];

    
    if (self.arrayShots.count == 0)
        self.timelineTableView.hidden = YES;
    else
        [self hiddenViewNotShots];
    
    self.timelineTableView.contentInset = UIEdgeInsetsMake(0, 0, 60, 0);
	self.timelineTableView.rowHeight = UITableViewAutomaticDimension;
	self.timelineTableView.estimatedRowHeight = 80.0f;
	
    [self setNavigationBarButtons];
    [self setTextViewForShotCreation];
    
}

//------------------------------------------------------------------------------
- (void)setNavigationBarButtons {
    
    //Search button
    UIBarButtonItem *btnSearch = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"Icon_Magnifier"] style:UIBarButtonItemStyleBordered target:self action:@selector(search)];
    btnSearch.tintColor = [Fav24Colors iosSevenBlue];
    self.navigationItem.leftBarButtonItem = btnSearch;
	
    //Info button
    UIButton *button = [UIButton buttonWithType:UIButtonTypeInfoLight];
    [button addTarget:self action:@selector(infoButton) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *infoButton = [[UIBarButtonItem alloc] initWithCustomView:button];
    self.navigationItem.rightBarButtonItem = infoButton;
}

//------------------------------------------------------------------------------
- (void)setTextViewForShotCreation {

    self.txtView.clipsToBounds = YES;
    self.txtView.layer.cornerRadius = 8.0f;
	self.txtView.layer.borderWidth = 0.3;
	self.txtView.layer.borderColor = [[UIColor darkGrayColor] CGColor];

}

//------------------------------------------------------------------------------
//- (void)viewDidLayoutSubviews {
//    
//    [super viewDidLayoutSubviews];
//    self.timelineTableView.backgroundColor = [UIColor clearColor];
//}

//------------------------------------------------------------------------------
-(void) search{
    
}

-(void)initSpinner{
    moreCells = YES;
    refreshTable = YES;
    
    self.spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    [self.spinner startAnimating];
    self.spinner.frame = CGRectMake(0, 0, 320, 44);
}

//------------------------------------------------------------------------------
-(void) infoButton{
    
}

//------------------------------------------------------------------------------
-(void) watching{
    
}


//------------------------------------------------------------------------------
-(void)hiddenViewNotShots{
    
    [self addPullToRefresh];
    
    self.timelineTableView.hidden = NO;
    self.viewNotShots.hidden = YES;
    self.timelineTableView.delegate = self;
    self.timelineTableView.dataSource = self;
}

//------------------------------------------------------------------------------
-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    //self.title = @"Timeline";
    self.navigationController.navigationBar.topItem.title = @"Timeline";

    //self.navigationItem.title = @"Timeline";
}


//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


#pragma mark - Pull to refresh
//------------------------------------------------------------------------------
-(void)addPullToRefresh{
    // Config pull to refresh
    if (self.refreshControl == nil) {
        self.refreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(self.timelineTableView.frame.origin.x, self.timelineTableView.frame.origin.y+100, 40, 40)];
        [self.refreshControl addTarget:self action:@selector(onPullToRefresh:) forControlEvents:UIControlEventValueChanged];
        [self.timelineTableView addSubview:self.refreshControl];
    }  
}
//------------------------------------------------------------------------------
- (void)onPullToRefresh:(UIRefreshControl *)refreshControl {

    //For Beta version only in iphone 4
//    [UIView animateWithDuration:0.25 animations:^{
//        self.viewOptions.alpha = 0.0;
//    }];
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:NO withShot:NO];
}

#pragma mark - UITableViewDelegate
//
////------------------------------------------------------------------------------
//- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
//    return self.viewOptions.frame.size.height;
//}
//
////------------------------------------------------------------------------------
//- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
//    
//    UIView *header =  [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.timelineTableView.frame.size.width, self.viewOptions.frame.size.height)];
//    header.backgroundColor = [UIColor clearColor];
//
//    return header;
//}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return self.arrayShots.count;
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
	
    Shot *shot = self.arrayShots[indexPath.row];

    return [Utils heightForShot:shot.comment];
	
//	return [self heightForBasicCellAtIndexPath:indexPath];
}


//- (CGFloat)tableView:(UITableView *)tableView estimatedHeightForRowAtIndexPath:(NSIndexPath *)indexPath {
//	return 80.0f;
//}

//- (CGFloat)heightForBasicCellAtIndexPath:(NSIndexPath *)indexPath {
//	static ShotTableViewCell *shotCell = nil;
//	static dispatch_once_t onceToken;
//	dispatch_once(&onceToken, ^{
//		static NSString *CellIdentifier = @"shootCell";
//		shotCell = [self.timelineTableView dequeueReusableCellWithIdentifier:CellIdentifier];
//	});
// 
//	Shot *shot = self.arrayShots[indexPath.row];
//	
//	[shotCell configureBasicCellWithShot:shot andRow:indexPath.row];
//	
//	return [self calculateHeightForConfiguredSizingCell:shotCell];
//}
//
//- (CGFloat)calculateHeightForConfiguredSizingCell:(ShotTableViewCell *)shotCell {
//	
//	[shotCell setNeedsLayout];
//	[shotCell layoutIfNeeded];
// 
//	CGSize size = [shotCell.contentView systemLayoutSizeFittingSize:UILayoutFittingCompressedSize];
//	return size.height;
//}


//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"shootCell";
    ShotTableViewCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
	
    Shot *shot = self.arrayShots[indexPath.row];
	
    [cell configureBasicCellWithShot:shot andRow:indexPath.row];
    [cell addTarget:self action:@selector(goProfile:)];
  
//    cell.layer.shouldRasterize = YES;
//    cell.layer.rasterizationScale = [UIScreen mainScreen].scale;
	
    return cell;
 }


-(void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
	
    if (!refreshTable){
        self.spinner.hidden = YES;
        
        self.lblFooter =  [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.timelineTableView.frame.size.width, 44)];
        self.lblFooter.text = @"No more shots";
        self.lblFooter.textColor = [Fav24Colors iosSevenGray];
        self.lblFooter.textAlignment = NSTextAlignmentCenter;
        self.lblFooter.backgroundColor = [UIColor clearColor];
        self.timelineTableView.tableFooterView = self.lblFooter;
        
        // [[self timelineTableView]reloadSections:[NSIndexSet indexSetWithIndex:0] withRowAnimation:UITableViewRowAnimationFade];
    }
}

//------------------------------------------------------------------------------
- (void)addLoadMoreCell{
    self.timelineTableView.tableFooterView = self.spinner;
    
    moreCells = NO;
    [[FavRestConsumer sharedInstance] getOldShotsWithDelegate:self];
    
}
#pragma mark - Pass ViewController
//------------------------------------------------------------------------------
-(void)goProfile:(id)sender{
    
    UIButton *btn = (UIButton *) sender;
    
    AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
    
    ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
    
    Shot *selectedShot = self.arrayShots[btn.tag];
    
    profileVC.selectedUser = selectedShot.user;
    
    
    [self.navigationController pushViewController:profileVC animated:YES];
}


#pragma mark - Reload table View
//------------------------------------------------------------------------------
- (void)reloadShotsTable:(id)sender {

    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShots.count > 0)
        [self performSelectorOnMainThread:@selector(reloadTimeline) withObject:nil waitUntilDone:NO];
}

#pragma mark - Send shot
//------------------------------------------------------------------------------
- (void)sendShot{
    self.btnShoot.enabled = NO;
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:NO withShot:YES];
}

//------------------------------------------------------------------------------
-(BOOL) controlRepeatedShot:(NSString *)texto{
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLineBetweenHours];
    
    if ([self isShotMessageAlreadyInList:self.arrayShots withText:texto])
        return YES;
    
    return NO;
}

-(BOOL) isShotMessageAlreadyInList:(NSArray *)shots withText:(NSString *) text{
    
    for (Shot *shot in shots) {
        
        if ([shot.comment isEqualToString:text])
            return YES;
    }
    return NO;
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    self.btnShoot.enabled = YES;

    if (status && [entityClass isSubclassOfClass:[Shot class]]){
         [self reloadShotsTable:nil];
        moreCells = YES;
    }else if (!refresh){
        moreCells = NO;
        refreshTable = NO;
    }
}

#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status andRefresh:(BOOL)refresh withShot:(BOOL)isShot{

    if (isShot)
        [self shotCreated];
    else if(refresh)
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
    else
        [self performSelectorOnMainThread:@selector(showOptions) withObject:nil waitUntilDone:YES];
    
}

//------------------------------------------------------------------------------
- (void)shotCreated {
    [self controlCharactersShot];

    if (![self controlRepeatedShot:self.textComment])
        
        [[ShotManager singleton] createShotWithComment:self.textComment andDelegate:self];
    else
        [self performSelectorOnMainThread:@selector(showAlert) withObject:nil waitUntilDone:NO];
    
}

-(void)showAlert{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Shot not posted" message:@"Whoops! You already shot that." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
}

-(void)controlCharactersShot{
    
    //self.textComment = [self.txtView.text stringByReplacingOccurrencesOfString:@"\\n" withString:@""];
  //  self.textComment =[self.txtView.text stringByReplacingOccurrencesOfString:@"\r\n" withString:@""];
   self.textComment = [self.txtView.text stringByTrimmingCharactersInSet:[NSCharacterSet newlineCharacterSet]];
    
   // [self.txtView.text stringByReplacingOccurrencesOfString:@"\n\n" withString:@"\n"];
  // self.txtView.text = [[self.txtView.text componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]] componentsJoinedByString:@""];

    
    /*self.txtView.text = [self.txtView.text stringByReplacingOccurrencesOfString:@"\n"
                                                                     withString:@"\u202B\n"];*/
}

#pragma mark - Reload methods
//------------------------------------------------------------------------------
-(void)reloadTimeline{
    [self.timelineTableView reloadData];
}

//------------------------------------------------------------------------------
-(void) showOptions{
    
    [self.timelineTableView reloadData];
    [self.refreshControl endRefreshing];
}


#pragma mark - ShotCreationProtocol response
//------------------------------------------------------------------------------
- (void)createShotResponseWithStatus:(BOOL)status andError:(NSError *)error {
    
    if (status && !error){
        self.btnShoot.enabled = YES;

        [self keyboardHide:nil];
        self.txtView.text = nil;
        [self reloadShotsTable:nil];
        [self.timelineTableView setScrollsToTop:YES];
        self.btnShoot.enabled = NO;
    }
}

#pragma mark - UIScrollViewDelegate

-(void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    // For Beta version and only iphone 4

//    CGFloat currentOffset = scrollView.contentOffset.y;
//    
//   if (currentOffset == 0)
//        [UIView animateWithDuration:0.25 animations:^{
//            self.viewOptions.alpha = 1.0;
//            self.viewTextField.alpha = 1.0;
//        }];
}

//------------------------------------------------------------------------------
-(void)scrollViewDidScroll:(UIScrollView *)scrollView {
 
    
    CGFloat currentOffset = scrollView.contentOffset.y;
    CGFloat maximumOffset = scrollView.contentSize.height - scrollView.frame.size.height;
   
    if (currentOffset < 0)
        NSLog(@"");
        // For Beta version and only iphone 4
//        [UIView animateWithDuration:0.2 animations:^{
//            self.viewOptions.alpha = 0.0;
//            self.viewTextField.alpha = 0.0; // For Beta version and only iphone 4
//        }];
    else{
        if (maximumOffset - currentOffset <= 200.0 && moreCells)
            [self addLoadMoreCell];

// For Beta version and only iphone 4
//         if (self.lastContentOffset > scrollView.contentOffset.y){
//             [UIView animateWithDuration:0.25 animations:^{
//                 self.viewTextField.alpha = 1.0;
//             }];
//         
//         }else if (scrollView.contentOffset.y > self.viewOptions.frame.size.height){
//             [UIView animateWithDuration:0.2 animations:^{
//                 self.viewTextField.alpha = 0.0;
//             
//             }];
    }
  
    
     self.lastContentOffset = scrollView.contentOffset.y;
}

#pragma mark - KEYBOARD
//------------------------------------------------------------------------------
-(void)keyboardShow:(NSNotification*)notification{
    
    self.txtView.textColor = [UIColor blackColor];

    if ([self.txtView.text isEqualToString:CREATE_SHOT_PLACEHOLDER])
        self.txtView.text = nil;
    
    [self darkenBackgroundView];
    
    if (lengthTextField > 0)
         self.charactersLeft.hidden = NO;
    
    self.timelineTableView.scrollEnabled = NO;
    [UIView animateWithDuration:(double)[[[notification userInfo] valueForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue]
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseIn
                     animations:^{
                         self.bottomViewPositionConstraint.constant = [self getKeyboardHeight:notification];
                         [self.view layoutIfNeeded];
                     } completion:NULL];

}

//------------------------------------------------------------------------------
- (int)getKeyboardHeight:(NSNotification *)notification {
    
    NSDictionary* keyboardInfo = [notification userInfo];
    NSValue* keyboardFrameBegin = [keyboardInfo valueForKey:UIKeyboardFrameEndUserInfoKey];
    CGRect keyboardFrameBeginRect = [keyboardFrameBegin CGRectValue];
    self.sizeKeyboard = keyboardFrameBeginRect.size.height-50;

    return self.sizeKeyboard;
}

//------------------------------------------------------------------------------
- (void)darkenBackgroundView {
    
    self.backgroundView.hidden = NO;
    UITapGestureRecognizer *tapTapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(keyboardHide:)];
    [self.backgroundView addGestureRecognizer:tapTapRecognizer];

}

//------------------------------------------------------------------------------
-(void)keyboardHide:(NSNotification*)notification{
    
    self.backgroundView.hidden = YES;
    self.charactersLeft.hidden = YES;
    [self.timelineTableView scrollRectToVisible:CGRectMake(0, 0, 1, 1) animated:NO];
    self.timelineTableView.scrollEnabled = YES;
    
    [self.txtView resignFirstResponder];
    
    if (self.txtView.text.length == 0)
        self.txtView.text = CREATE_SHOT_PLACEHOLDER;
    
    self.txtView.textColor = [UIColor lightGrayColor];
    
    self.bottomViewHeightConstraint.constant = 75;
    self.bottomViewPositionConstraint.constant = 0.0f;
    [UIView animateWithDuration:0.25f animations:^{
        [self.view layoutIfNeeded];
    }];

}

#pragma mark TEXTVIEW

//------------------------------------------------------------------------------
- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
//    NSLog(@"%lu", (unsigned long)lengthTextField);
//    NSLog(@"%lu", self.txtView.text.length - range.length + text.length);
    
//    if (lengthTextField < self.txtView.text.length - range.length + text.length ) {
//        [self adaptViewSizeWhenWriting:textView];
//    }
    
    lengthTextField = self.txtView.text.length - range.length + text.length;
    
    if (lengthTextField >= 1 && ![textView.text isEqualToString:@"  "]){
        self.btnShoot.enabled = YES;
		float rows = round( (textView.contentSize.height - textView.textContainerInset.top - textView.textContainerInset.bottom) / textView.font.lineHeight );
		if (rows >= 3)
			self.charactersLeft.hidden = NO;
    }
    else
        self.btnShoot.enabled = NO;
	
	
	[self adaptViewSizeWhenWriting:textView];

    if (lengthTextField == 0){
		self.bottomViewHeightConstraint.constant = 75;
        self.charactersLeft.hidden = YES;
		[UIView animateWithDuration:0.25f animations:^{
			[self.view layoutIfNeeded];
		}];
    }

    self.charactersLeft.text = [self countCharacters:lengthTextField];
    return (lengthTextField > CHARACTERS_SHOT) ? NO : YES;

}

//------------------------------------------------------------------------------
- (void)adaptViewSizeWhenWriting:(UITextView *)textView {

	float rows = round( (textView.contentSize.height - textView.textContainerInset.top - textView.textContainerInset.bottom) / textView.font.lineHeight );
	if (rows > 1) {
		self.bottomViewHeightConstraint.constant = (rows*18)+75;
		[UIView animateWithDuration:0.25f animations:^{
			[self.view layoutIfNeeded];
		}];
	}

}

//------------------------------------------------------------------------------
-(NSString *) countCharacters:(NSUInteger) lenght{
    
    if (lenght <= CHARACTERS_SHOT){
        NSString *charLeft = [NSString stringWithFormat:@"%lu",CHARACTERS_SHOT - lenght];
        return charLeft;
    }
    return 0;
}

//------------------------------------------------------------------------------
- (void)dealloc {
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - Webservice response methods
-(void) restrictRotation:(BOOL) restriction
{
    AppDelegate* appDelegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    appDelegate.restrictRotation = restriction;
}


@end
