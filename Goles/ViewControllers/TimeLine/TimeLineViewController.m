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
#import "UIImageView+FadeIn.h"
#import "Utils.h"
#import "Conection.h"
#import "Fav24Colors.h"
#import "AppDelegate.h"
#import "Constants.h"
#import "ProfileViewController.h"

@interface TimeLineViewController ()<ConectionProtocol, UIScrollViewDelegate, UITextViewDelegate, ConectionProtocol>{
    NSUInteger lengthTextField;
    BOOL moreCells;
    BOOL refreshTable;
}

@property (nonatomic,weak) IBOutlet UITableView    *timelineTableView;
@property (weak, nonatomic) IBOutlet UIButton *btnWatching;
@property (weak, nonatomic) IBOutlet UIButton *btnSearch;
@property (weak, nonatomic) IBOutlet UIButton *btnInfo;
@property (weak, nonatomic) IBOutlet UITextView *txtField;
@property (weak, nonatomic) IBOutlet UIButton *btnShoot;
@property (weak, nonatomic) IBOutlet UILabel *charactersLeft;
@property (weak, nonatomic) IBOutlet UIView *viewNotShots;
@property (strong, nonatomic) NSArray *arrayShots;
@property (strong, nonatomic) UIRefreshControl *refreshControl;
@property (weak, nonatomic) IBOutlet UIView *viewOptions;
@property (weak, nonatomic) IBOutlet UIView *viewTextField;
@property (nonatomic, assign) CGFloat lastContentOffset;
@property (nonatomic, assign) CGRect originalFrame;
@property (strong, nonatomic) IBOutlet UIView *backgroundView;
@property (assign, nonatomic) int sizeKeyboard;
@property (nonatomic, strong) IBOutlet NSLayoutConstraint *bottomViewConstraint;
@property(nonatomic, strong) UIActivityIndicatorView *spinner;
@property(nonatomic, strong) UILabel *lblFooter;

@end

@implementation TimeLineViewController

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    lengthTextField = 0;
   
    [self initSpinner];
    
    self.arrayShots = [[NSArray alloc]init];
    self.btnShoot.enabled = NO;
    self.txtField.delegate = self;

    //For Alpha version
    self.viewOptions.hidden = YES;

    [self.btnShoot addTarget:self action:@selector(sendShot) forControlEvents:UIControlEventTouchUpInside];
    
    //Get shots from server
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES];
    
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
    
    self.originalFrame = self.tabBarController.tabBar.frame;
    
    self.timelineTableView.contentInset = UIEdgeInsetsMake(0, 0, 84, 0);
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
    UIBarButtonItem *btnInfo = [[UIBarButtonItem alloc] initWithTitle:@"Info" style:UIBarButtonItemStyleBordered target:self action:@selector(infoButton)];
    
    //Glasses button
    UIBarButtonItem *btnWatch = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"Icon_Magnifier"] style:UIBarButtonItemStyleBordered target:self action:@selector(watching)];
//    btnInfo.tintColor = [Fav24Colors iosSevenBlue];
    
    [self.navigationItem setRightBarButtonItems:[NSArray arrayWithObjects:btnInfo,btnWatch, nil]];
}

//------------------------------------------------------------------------------
- (void)setTextViewForShotCreation {

    self.txtField.clipsToBounds = YES;
    self.txtField.layer.cornerRadius = 8.0f;

}

//------------------------------------------------------------------------------
- (void)viewDidLayoutSubviews {
    
    [super viewDidLayoutSubviews];
    self.timelineTableView.backgroundColor = [UIColor clearColor];
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
    self.title = @"Timeline";
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
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:NO];
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
- (void)addLoadMoreCell{
    self.timelineTableView.tableFooterView = self.spinner;

    moreCells = NO;
    [[FavRestConsumer sharedInstance] getOldShotsWithDelegate:self];

}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"shootCell";
    ShotTableViewCell *cell = (id) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
   
   
    Shot *shot = self.arrayShots[indexPath.row];
    [cell configureBasicCellWithShot:shot];
    [cell addTarget:self action:@selector(goProfile)];
    
    return cell;
 }

-(void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
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
#pragma mark - Pass ViewController
//------------------------------------------------------------------------------
-(void)goProfile{
    AppDelegate *delegate =(AppDelegate *) [[UIApplication sharedApplication]delegate];
    
    ProfileViewController *profileVC = [delegate.peopleSB instantiateViewControllerWithIdentifier:@"profileVC"];
    [self.navigationController pushViewController:profileVC animated:YES];
}


#pragma mark - Reload table View
//------------------------------------------------------------------------------
- (void)reloadShotsTable:(id)sender {
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShots.count > 0) {
        [self hiddenViewNotShots];
        [self.timelineTableView reloadData];
    }
}

#pragma mark - Send shot
//------------------------------------------------------------------------------
- (void)sendShot{
    [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:NO];
}

//------------------------------------------------------------------------------
-(BOOL) controlRepeatedShot:(NSString *)texto{
    
     self.arrayShots = [[ShotManager singleton] getShotsForTimeLineBetweenHours];
    
    for (Shot *shot in self.arrayShots) {
        
        if ([shot.comment isEqualToString:texto])
            return YES;
            
    }

    return NO;
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
}

#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status andRefresh:(BOOL)refresh{

    if (self.txtField.text.length >= 1 && ![self.txtField.text isEqualToString:@"What's Up?"]) {
        
        if ([[Conection sharedInstance] isConection]) {
            
            if (![self controlRepeatedShot:self.txtField.text])
                [[ShotManager singleton] createShotWithComment:self.txtField.text andDelegate:self];
            else{
                
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Shot not posted" message:@"Whoops! You already shot that." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
                [alert show];
            }
        }
    }else if(refresh){
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
    }else{
        [self performSelectorOnMainThread:@selector(showOptions) withObject:nil waitUntilDone:YES];
    }
}

#pragma mark - Reload methods
//------------------------------------------------------------------------------
-(void)reloadData{
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
        [self keyboardHide:nil];
        [self reloadShotsTable:nil];
        [self.timelineTableView setScrollsToTop:YES];
        [self.txtField setText:nil];
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
    
    self.txtField.text = nil;
    NSDictionary* keyboardInfo = [notification userInfo];
    NSValue* keyboardFrameBegin = [keyboardInfo valueForKey:UIKeyboardFrameEndUserInfoKey];
    CGRect keyboardFrameBeginRect = [keyboardFrameBegin CGRectValue];
    self.sizeKeyboard = keyboardFrameBeginRect.size.height-50;
    
    //Darken background view
    self.backgroundView.hidden = NO;
    UITapGestureRecognizer *tapTapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(keyboardHide:)];
    [self.backgroundView addGestureRecognizer:tapTapRecognizer];
    
    self.timelineTableView.scrollEnabled = NO;

    double animationDuration = [[keyboardInfo valueForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];

    [UIView animateWithDuration:animationDuration
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseIn
                     animations:^{
                         self.bottomViewConstraint.constant = self.sizeKeyboard;

                         [self.view layoutIfNeeded];
                     } completion:NULL];

}


//------------------------------------------------------------------------------
-(void)keyboardHide:(NSNotification*)notification{
    
    self.backgroundView.hidden = YES;
    [self.timelineTableView setScrollsToTop:YES];
    self.timelineTableView.scrollEnabled = YES;
    
    
    //move writing field
    self.bottomViewConstraint.constant = 0.0f;
    [UIView animateWithDuration:0.25f animations:^{
        [self.view layoutIfNeeded];
    }];

}

#pragma mark TEXTVIEW

//------------------------------------------------------------------------------
- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    lengthTextField = self.txtField.text.length - range.length + text.length;
    
    if ([text isEqualToString:@" "])
        self.btnShoot.enabled = NO;
    else if (lengthTextField >= 1 && ![textView.text isEqualToString:@"  "])
        self.btnShoot.enabled = YES;
    else
        self.btnShoot.enabled = NO;
    
    self.charactersLeft.text = [self countCharacters:lengthTextField];
    return (lengthTextField > CHARACTERS_SHOT) ? NO : YES;
    
    [self adaptViewSizeWhenWriting:textView];

}

- (void)adaptViewSizeWhenWriting:(UITextView *)textView {
    
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

@end
