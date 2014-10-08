//
//  TimeLineViewController.m
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
#import <CoreText/CoreText.h>
#import "TimeLineUtilities.h"

@interface TimeLineViewController ()<UIScrollViewDelegate, UITextViewDelegate, ConectionProtocol, UIAlertViewDelegate>{
    NSUInteger lengthTextField;
    BOOL moreCells;
    BOOL refreshTable;
    float rows;
    float rowsOLD;
    CGRect previousRect;

    UITapGestureRecognizer *tapTapRecognizer;
}

@property (nonatomic,weak) IBOutlet UITableView    *timelineTableView;
@property (weak, nonatomic) IBOutlet UIButton *btnWatching;
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

    lengthTextField = 0;
    previousRect = CGRectZero;
    
    [self initSpinner];
    
    self.arrayShots = [[NSArray alloc]init];
    self.btnShoot.enabled = NO;
    self.txtView.delegate = self;

    //For Alpha version
    self.viewOptions.hidden = YES;

    [self.btnShoot addTarget:self action:@selector(sendShot) forControlEvents:UIControlEventTouchUpInside];
   
    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
    self.navigationItem.titleView = [TimeLineUtilities createConectandoTitleView];
    
    //Listen for show conecting process
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(returnBackground) name:k_NOTIF_BACKGROUND object:nil];
    

    //Get shots from CoreData
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];

    //Disable keyboard intro key
    if (self.textComment.length == 0)
        self.txtView.enablesReturnKeyAutomatically = YES;
  
    //Listen for synchro process end
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadShotsTable:) name:K_NOTIF_SHOT_END object:nil];
    
     //Listen for keyboard process open
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardShow:) name:UIKeyboardWillShowNotification object:nil];
   
    //Listen for keyboard process close
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardHide:) name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide) name:UIKeyboardWillHideNotification object:nil];

   
    
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
-(void)returnBackground{

    //Get ping from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
    self.navigationItem.titleView = [TimeLineUtilities createConectandoTitleView];
}

//------------------------------------------------------------------------------
- (void)setNavigationBarButtons {
    
    //Search button
    UIBarButtonItem *btnSearch = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"Icon_Magnifier"] style:UIBarButtonItemStylePlain target:self action:@selector(search)];
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
     self.title = @"Timeline";

    [[NSNotificationCenter defaultCenter] addObserver:self  selector:@selector(orientationChanged:)    name:UIDeviceOrientationDidChangeNotification  object:nil];

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
	
}



//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"shootCell";
    ShotTableViewCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
	
    Shot *shot = self.arrayShots[indexPath.row];
	
    [cell configureBasicCellWithShot:shot andRow:indexPath.row];
    [cell addTarget:self action:@selector(goProfile:)];
	
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

    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShots.count > 0)
        [self performSelectorOnMainThread:@selector(reloadTimeline) withObject:nil waitUntilDone:NO];
}

//------------------------------------------------------------------------------
- (void)reloadShotsTableWithAnimation:(id)sender {
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShots.count > 0)
        [self performSelectorOnMainThread:@selector(animationInsertShot) withObject:nil waitUntilDone:NO];
}

-(void)animationInsertShot{
    [self.timelineTableView beginUpdates];
    NSIndexPath *iPath = [NSIndexPath indexPathForRow:0 inSection:0];
    [self.timelineTableView insertRowsAtIndexPaths:@[iPath] withRowAnimation:UITableViewRowAnimationTop];
    [self.timelineTableView endUpdates];
}

#pragma mark - Send shot
//------------------------------------------------------------------------------
- (void)sendShot{
    self.txtView.backgroundColor = [UIColor colorWithRed:230.0/255.0 green:230.0/255.0 blue:230.0/255.0 alpha:1];

    //self.txtView.userInteractionEnabled= NO;
    [self.txtView resignFirstResponder];
    self.orientation = NO;
    [self keyboardHide:nil];
    self.charactersLeft.hidden = YES;
    self.btnShoot.enabled = NO;
    self.navigationItem.titleView = [TimeLineUtilities createEnviandoTitleView];
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

#pragma mark - Change NavigationBar
-(void)changeStateViewNavBar{
    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];

}
-(void)changeStateActualizandoViewNavBar{
    self.navigationItem.titleView = [TimeLineUtilities createActualizandoTitleView];
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && [entityClass isSubclassOfClass:[Shot class]]){
        [self reloadShotsTable:nil];
        moreCells = YES;
    }else if (!refresh){
        moreCells = NO;
        refreshTable = NO;
    }
    [self performSelector:@selector(changeStateViewNavBar) withObject:nil afterDelay:0.5];
}

#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status andRefresh:(BOOL)refresh withShot:(BOOL)isShot{

    if (status & !isShot)
        [self performSelectorOnMainThread:@selector(changeStateActualizandoViewNavBar) withObject:nil waitUntilDone:NO];

    
    if (isShot){
        self.orientation = NO;
        [self shotCreated];
    }else if(refresh)
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
    else if(!status && !refresh && !isShot){
//        self.orientation = NO;
//        [self performSelectorOnMainThread:@selector(cleanViewWhenNotConnection) withObject:nil waitUntilDone:YES];
    } else
        [self performSelectorOnMainThread:@selector(removePullToRefresh) withObject:nil waitUntilDone:YES];
    
}

#pragma mark - ShotCreationProtocol response
//------------------------------------------------------------------------------
- (void)createShotResponseWithStatus:(BOOL)status andError:(NSError *)error {
    
    if (status && !error){
        self.txtView.backgroundColor = [UIColor whiteColor];
        self.txtView.textColor = [UIColor blackColor];
        self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
        rows = 0;
        self.charactersLeft.hidden = YES;
        self.txtView.text = nil;
        [self reloadShotsTableWithAnimation:nil];
        [self.timelineTableView setScrollsToTop:YES];
        self.btnShoot.enabled = NO;
        //self.txtView.userInteractionEnabled = YES;
    }else if (error){
        [self performSelectorOnMainThread:@selector(showAlertcanNotCreateShot) withObject:nil waitUntilDone:NO];
    }
}

-(void)showAlertcanNotCreateShot{
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Shot Not Posted" message:@"Connection timed out." delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Retry", nil];
    alertView.tag = 18;
    [alertView show];
    
    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
}

-(void)cleanViewWhenNotConnection{
    
    [self keyboardHide:nil];
    self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
    
}

#pragma mark - Shot creation
//------------------------------------------------------------------------------
- (void)shotCreated {
    
    [self controlCharactersShot:self.txtView.text];

    if (![self controlRepeatedShot:self.textComment])
        
        [[ShotManager singleton] createShotWithComment:self.textComment andDelegate:self];
    else
        [self performSelectorOnMainThread:@selector(showAlert) withObject:nil waitUntilDone:NO];
    
}

//------------------------------------------------------------------------------
-(void)showAlert{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Shot Not Posted" message:@"Whoops! You already shot that." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
}


//------------------------------------------------------------------------------
-(NSString *)controlCharactersShot:(NSString *)text{
    
    NSRange range = [text rangeOfString:@"^\\s*" options:NSRegularExpressionSearch];
    text = [text stringByReplacingCharactersInRange:range withString:@""];
    
    self.textComment = [text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    self.textComment = [text stringByTrimmingCharactersInSet:[NSCharacterSet newlineCharacterSet]];
    
    return self.textComment;
}

#pragma mark - Reload methods
//------------------------------------------------------------------------------
-(void)reloadTimeline{
    [self.timelineTableView reloadData];
}

//------------------------------------------------------------------------------
-(void) removePullToRefresh{
    
    [self.timelineTableView reloadData];
    [self.refreshControl endRefreshing];
}


#pragma mark - UIAlertViewDelegate

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    if (alertView.tag == 18) {
        switch (buttonIndex) {
            case 0:{
                self.btnShoot.enabled = YES;
                //self.txtView.userInteractionEnabled = YES;
                self.orientation = NO;
                [self keyboardHide:nil];
                self.txtView.backgroundColor = [UIColor whiteColor];
                self.txtView.textColor = [UIColor blackColor];
                
                break;
            }
            case 1:
                [self sendShot];
                
                break;
            default:
                break;
        }

    }else{
        self.txtView.backgroundColor = [UIColor whiteColor];
        self.txtView.textColor = [UIColor blackColor];
        self.btnShoot.enabled = YES;

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
    self.txtView.backgroundColor = [UIColor whiteColor];
    
//    if (rows >= 3)
//        self.charactersLeft.hidden = NO;
//    else
//        self.charactersLeft.hidden = YES;
    
    if ([self.txtView.text isEqualToString:CREATE_SHOT_PLACEHOLDER])
        self.txtView.text = nil;
    
    [self darkenBackgroundView];


    self.timelineTableView.scrollEnabled = NO;

    [UIView animateWithDuration:(double)[[[notification userInfo] valueForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue]
                              delay:0.0
                            options:UIViewAnimationOptionCurveEaseIn
                         animations:^{
                             self.bottomViewPositionConstraint.constant = [self getKeyboardHeight:notification];
                             [self.view layoutIfNeeded];
                         }completion:^(BOOL finished) {
                            
                         }];

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
    
    if (tapTapRecognizer == nil){
        tapTapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(keyboardHide:)];
        [self.backgroundView addGestureRecognizer:tapTapRecognizer];
        self.orientation = NO;
    }
}

//------------------------------------------------------------------------------
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    self.orientation = NO;
}

//------------------------------------------------------------------------------
-(void)keyboardWillHide
{
    [self keyboardHide:nil];
}

//------------------------------------------------------------------------------
-(void)keyboardHide:(NSNotification*)notification{

    
    if (!self.orientation){

        [self.txtView resignFirstResponder];

        
        self.backgroundView.hidden = YES;
        
        [self.timelineTableView scrollRectToVisible:CGRectMake(0, 0, 1, 1) animated:NO];
        self.timelineTableView.scrollEnabled = YES;
            
        if (lengthTextField == 0){
            self.txtView.text = CREATE_SHOT_PLACEHOLDER;
            self.txtView.textColor = [UIColor lightGrayColor];
            
            rows = 0;
            self.charactersLeft.hidden = YES;
        }else
            self.txtView.textColor = [UIColor colorWithRed:137.0/255.0 green:137.0/255.0 blue:137.0/255.0 alpha:1];
        
        if (rows == 0 || rows == 1) {
            self.bottomViewHeightConstraint.constant = 75;
            self.bottomViewPositionConstraint.constant = 0.0f;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        }else{
            self.bottomViewHeightConstraint.constant = (rows*18)+75;
            self.bottomViewPositionConstraint.constant = 0.0f;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        
        }
    }
}

#pragma mark TEXTVIEW

//------------------------------------------------------------------------------
- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    NSString* result = [self controlCharactersShot:self.txtView.text];

    lengthTextField = textView.text.length - range.length + text.length;
    self.charactersLeft.hidden = NO;

//    if (![result isEqualToString:@""] && lengthTextField >= 1)
    if (lengthTextField >= 1)
        self.btnShoot.enabled = YES;
    else
        self.btnShoot.enabled = NO;

    [self adaptViewSizeWhenWriting:textView withCharacter:text];

    if (lengthTextField == 0){
		self.bottomViewHeightConstraint.constant = 75;
        //self.charactersLeft.hidden = YES;
		[UIView animateWithDuration:0.25f animations:^{
			[self.view layoutIfNeeded];
		}];
    }
    
    self.charactersLeft.text = [self countCharacters:lengthTextField];
    return (lengthTextField > CHARACTERS_SHOT) ? NO : YES;
}
//------------------------------------------------------------------------------
- (void)textViewDidChange:(UITextView *)textView{
    
    UITextPosition* pos = textView.endOfDocument;//explore others like beginningOfDocument if you want to customize the behaviour
    CGRect currentRect = [textView caretRectForPosition:pos];

    if (currentRect.origin.y < previousRect.origin.y)
        [self adaptViewSizeWhenDeleting:textView];
   
    previousRect = currentRect;
    
}

//------------------------------------------------------------------------------
- (void)adaptViewSizeWhenWriting:(UITextView *)textView withCharacter:(NSString *)character{

	rows = round( (textView.contentSize.height - textView.textContainerInset.top - textView.textContainerInset.bottom) / textView.font.lineHeight);

    if (self.viewTextField.frame.origin.y > self.navigationController.navigationBar.frame.size.height+25){
        if (rows > 2 && ![character isEqualToString:@"\n"] && ![character isEqualToString:@""]) {
            self.bottomViewHeightConstraint.constant = ((rows-2)*textView.font.lineHeight)+75;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        }else if(rows > 1 && [character isEqualToString:@"\n"]){
            self.bottomViewHeightConstraint.constant = ((rows-1)*textView.font.lineHeight)+75;
            [UIView animateWithDuration:0.25f animations:^{
                [self.view layoutIfNeeded];
            }];
        }
    }
}

//------------------------------------------------------------------------------
- (void)adaptViewSizeWhenDeleting:(UITextView *)textView{
    
    if (rows > 2) {
        rows = rows-3;
        self.bottomViewHeightConstraint.constant = (rows*textView.font.lineHeight)+75;
        [UIView animateWithDuration:0.25f animations:^{
            [self.view layoutIfNeeded];
        }];
    }
}

//------------------------------------------------------------------------------
-(NSString *)countCharacters:(NSUInteger) lenght{
    
    if (lenght <= CHARACTERS_SHOT){
        NSString *charLeft = [NSString stringWithFormat:@"%lu",CHARACTERS_SHOT - lenght];
        return charLeft;
    }
    return @"0";
}

//------------------------------------------------------------------------------
- (void)dealloc {
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - Orientation methods

//------------------------------------------------------------------------------
-(void) restrictRotation:(BOOL) restriction
{
    AppDelegate* appDelegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    appDelegate.restrictRotation = restriction;
    self.orientation = YES;
}

//------------------------------------------------------------------------------
- (void)orientationChanged:(NSNotification *)notification{
   // self.navigationItem.titleView = [TimeLineUtilities createTimelineTitleView];
    [self restrictRotation:NO];
}

//------------------------------------------------------------------------------
-(void)viewWillDisappear:(BOOL)animated{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIDeviceOrientationDidChangeNotification object:nil];
}

@end
