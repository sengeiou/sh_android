//
//  TimeLineViewController.m
//  Goles
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "TimeLineViewController.h"
#import "FavRestConsumer.h"
#import "User.h"
#import "Shot.h"
#import "CoreDataManager.h"
#import "ShotTableViewCell.h"
#import "UIImageView+FadeIn.h"
#import "Utils.h"
#import "ShotManager.h"
#import "Conection.h"
#import "Fav24Colors.h"

#define SYZE_KEYBOARD   167

@interface TimeLineViewController ()<ConectionProtocol, UIScrollViewDelegate, UITextViewDelegate>{
    int lengthTextField;
    BOOL isVisible;
}

@property (nonatomic,strong) IBOutlet UITableView    *timelineTableView;
@property (weak, nonatomic) IBOutlet UIButton *btnWatching;
@property (weak, nonatomic) IBOutlet UIButton *btnSearch;
@property (weak, nonatomic) IBOutlet UIButton *btnInfo;
@property (weak, nonatomic) IBOutlet UITextField *txtField;
@property (weak, nonatomic) IBOutlet UIButton *btnShoot;
@property (weak, nonatomic) IBOutlet UIView *viewNotShots;
@property (strong, nonatomic) NSArray *arrayShots;
@property (strong, nonatomic) UIRefreshControl *refreshControl;
@property (weak, nonatomic) IBOutlet UIView *viewOptions;
@property (weak, nonatomic) IBOutlet UIView *viewTextField;
@property (nonatomic, assign) CGFloat lastContentOffset;
@property (nonatomic, assign) CGRect originalFrame;
@property (weak, nonatomic) IBOutlet UIImageView *line1;
@property (weak, nonatomic) IBOutlet UIImageView *line2;

@end

@implementation TimeLineViewController

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    lengthTextField = 0;
    self.arrayShots = [[NSArray alloc]init];
    self.btnShoot.enabled = NO;
    self.txtField.delegate = self;
    
    //Get shots from server
    [[Conection sharedInstance]getServerTimewithDelegate:self];
    
    //Get shots from CoreData
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];

    //Listen for synchro process end
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadShotsTable:) name:K_NOTIF_SHOT_END object:nil];

    if (self.arrayShots.count == 0)
        self.timelineTableView.hidden = YES;
    else
        [self hiddenViewNotShots];
    
    self.originalFrame = self.tabBarController.tabBar.frame;
    
    
    UIBarButtonItem *btnSearch = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"Icon_Magnifier"] style:UIBarButtonItemStyleBordered target:self action:@selector(search)];
    btnSearch.tintColor = [Fav24Colors iosSevenBlue];
    self.navigationItem.rightBarButtonItem = btnSearch;
    
    
    self.line1.frame = CGRectMake(self.line1.frame.origin.x, self.line1.frame.origin.y, self.line1.frame.size.width, 0.5);
    self.line2.frame = CGRectMake(self.line2.frame.origin.x, self.line2.frame.origin.y, self.line2.frame.size.width, 0.5);
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    self.timelineTableView.backgroundColor = [UIColor clearColor];
}

-(void) search{
    
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
   // self.navigationController.navigationBarHidden = YES;
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
    
    self.viewOptions.alpha = 0.0;

    [[Conection sharedInstance]getServerTimewithDelegate:self];
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return self.viewOptions.frame.size.height;
}

//------------------------------------------------------------------------------

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    UIView *header =  [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.timelineTableView.frame.size.width, self.viewOptions.frame.size.height)];
    header.backgroundColor = [UIColor clearColor];

    return header;
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (isVisible)
        self.viewOptions.alpha = 1.0;
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
    ShotTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];

    Shot *shot = self.arrayShots[indexPath.row];

    cell.txvText.text = shot.comment;
    cell.txvText.frame = CGRectMake(cell.txvText.frame.origin.x, cell.txvText.frame.origin.y,cell.txvText.frame.size.width, [Utils heightForShot:shot.comment]);
    
    //cell.txvText.selectedTextRange = nil;
    //cell.txvText.delegate = self;
    //cell.txvText.selectable = NO;
    [cell.txvText setUserInteractionEnabled:NO];
    
    cell.lblName.text = shot.user.name;
    [cell.imgPhoto fadeInFromURL:[NSURL URLWithString:shot.user.photo] withOuterMatte:NO andInnerBorder:NO];
    cell.lblDate.text = [Utils getDateShot:shot.csys_birth];

    return cell;
 }
- (BOOL)canBecomeFirstResponder {
    return NO;
}
- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    /*UIMenuController *menuController = [UIMenuController sharedMenuController];
    if (menuController) {
        [UIMenuController sharedMenuController].menuVisible = NO;
    }
    return NO;*/
    
    /*switch (action) {
     case @selector(paste:):
     case @selector(copy:):
     case @selector(cut:):
     case @selector(cut:):
     case @selector(select:):
     case @selector(selectAll:):
     return NO;
     }
     return [super canPerformAction:action withSender:sender];*/
    [UIMenuController sharedMenuController].menuVisible = NO;  //do not display the menu
    [self resignFirstResponder];                      //do not allow the user to selected anything
    return NO;
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

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error {
    
    if (status && [entityClass isSubclassOfClass:[Shot class]])
        [self reloadShotsTable:nil];
}

#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status{
    
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
    [self.refreshControl endRefreshing];
}


#pragma mark - UITextField response methods
//------------------------------------------------------------------------------
-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    
    lengthTextField = self.txtField.text.length - range.length + string.length;
   
    if (lengthTextField >= 1)
        self.btnShoot.enabled = YES;
     else
        self.btnShoot.enabled = NO;
    
    return YES;
}

#pragma mark - UIScrollViewDelegate
//------------------------------------------------------------------------------
-(void)scrollViewDidScroll:(UIScrollView *)scrollView {
   
    //Hide tabbar like safari
    /*UITabBar *tb = self.tabBarController.tabBar;
    NSInteger yOffset = scrollView.contentOffset.y;
    if (yOffset > 0) {
        tb.frame = CGRectMake(tb.frame.origin.x, self.originalFrame.origin.y + yOffset, tb.frame.size.width, tb.frame.size.height);
    }
    if (yOffset < 1) tb.frame = self.originalFrame;*/
    
    
    if (self.lastContentOffset > scrollView.contentOffset.y){
        [UIView animateWithDuration:0.2 animations:^{
            
            self.viewOptions.alpha = 1.0;
            self.viewTextField.alpha = 1.0;
        }];
        
    }else if (scrollView.contentOffset.y > self.viewOptions.frame.size.height){
        [UIView animateWithDuration:0.2 animations:^{
            self.viewOptions.alpha = 0.0;
            self.viewTextField.alpha = 0.0;

        }];
    }
   
    
     self.lastContentOffset = scrollView.contentOffset.y;
}

-(void)keyboardShow{
    
    [UIView animateWithDuration:0.2 animations:^{
        self.viewOptions.alpha = 0.0;
    }];

    [self.timelineTableView scrollRectToVisible:CGRectMake(self.timelineTableView.frame.origin.x, self.timelineTableView.frame.origin.y - self.viewOptions.frame.size.height, self.timelineTableView.frame.size.width, self.timelineTableView.frame.size.height) animated:YES];
    
    //[self.timelineTableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:YES];
    
    
    isVisible = YES;
    
    CGRect rectFild = self.viewTextField.frame;
    rectFild.origin.y -= SYZE_KEYBOARD;
    
    [UIView animateWithDuration:0.25f
                     animations:^{
                         [self.viewTextField setFrame:rectFild];
            }
     ];
}

-(void)keyboardHide{
    [UIView animateWithDuration:0.2 animations:^{
        self.viewOptions.alpha = 1.0;
        
       

    }];
    
    [self.timelineTableView setScrollsToTop:YES];
    
    // [self.timelineTableView scrollRectToVisible:CGRectMake(self.timelineTableView.frame.origin.x, self.timelineTableView.frame.origin.y + self.viewOptions.frame.size.height, self.timelineTableView.frame.size.width, self.timelineTableView.frame.size.height) animated:YES];
    
    isVisible = NO;
    
    CGRect rectFild = self.viewTextField.frame;
    rectFild.origin.y += SYZE_KEYBOARD;
    
    [UIView animateWithDuration:0.25f
                     animations:^{
                         [self.viewTextField setFrame:rectFild];
                    }
     ];
}


#pragma mark -
#pragma mark TextFieldDelegate

- (void)textFieldDidBeginEditing:(UITextField *)textField{
    
    [self keyboardShow];
}

- (void)textFieldDidEndEditing:(UITextField *)textField{
    
    if (isVisible) {
        
        [self keyboardHide];
    }
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [textField setText:nil];
    
    [textField resignFirstResponder];
    return YES;
}

@end
