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

@interface TimeLineViewController ()<ConectionProtocol, UIScrollViewDelegate>{
    int lengthTextField;
    BOOL move;
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

@end

@implementation TimeLineViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    lengthTextField = 0;
    self.arrayShots = [[NSArray alloc]init];
    self.btnShoot.enabled = NO;
    self.txtField.delegate = self;
    
    [[Conection sharedInstance]getServerTimewithDelegate:self];
    
    self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];

    if (self.arrayShots.count == 0)
        self.timelineTableView.hidden = YES;
    else
        [self hiddenViewNotShots];
    
    
    [self customButtonSearch];
    
    self.originalFrame = self.tabBarController.tabBar.frame;
    
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    //self.timelineTableView.contentSize = self.viewOptions.bounds.size;
    self.timelineTableView.backgroundColor = [UIColor clearColor];
}

//------------------------------------------------------------------------------
-(void)customButtonSearch{
    
    UIImage *image = [[UIImage imageNamed:@"Icon_Magnifier"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    [self.btnSearch setImage:image forState:UIControlStateNormal];
    self.btnSearch.tintColor = [Fav24Colors iosSevenBlue];
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
    self.navigationController.navigationBarHidden = YES;
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - Pull to refresh and return from background refresh
//------------------------------------------------------------------------------
-(void)addPullToRefresh{
    // Config pull to refresh
    if (self.refreshControl == nil) {
        self.refreshControl = [[UIRefreshControl alloc] init];
        [self.refreshControl addTarget:self action:@selector(onPullToRefresh:) forControlEvents:UIControlEventValueChanged];
        [self.timelineTableView addSubview:self.refreshControl];
    }  
}
//------------------------------------------------------------------------------
- (void)onPullToRefresh:(UIRefreshControl *)refreshControl {
    
    [[Conection sharedInstance]getServerTimewithDelegate:self];
}

#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)conectionResponseForStatus:(BOOL)status{
    
    [self.refreshControl endRefreshing];
}

#pragma mark - UITableViewDelegate
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return 60;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    UIView *header =  [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 60)];
    header.backgroundColor = [UIColor clearColor];
    
    return header;
}
//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return self.arrayShots.count;

}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    //return 90;
    Shot *shot = self.arrayShots[indexPath.row];

    return [Utils heightForShot:shot.comment];
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    Shot *shot = self.arrayShots[indexPath.row];
    
    ShotTableViewCell *cell = (ShotTableViewCell *)[tableView dequeueReusableCellWithIdentifier:@"shootCell" forIndexPath:indexPath];
    
    cell.txvText.text = shot.comment;
    
    cell.lblName.text = shot.user.name;
    [cell.imgPhoto fadeInFromURL:[NSURL URLWithString:shot.user.photo] withOuterMatte:NO andInnerBorder:NO];
    cell.lblDate.text = [Utils getDateShot:shot.csys_birth];

    return cell;
 }

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error {
    
    if (status && [entityClass isSubclassOfClass:[Shot class]]){

        self.arrayShots = [[ShotManager singleton] getShotsForTimeLine];
        
        if (self.arrayShots.count > 0) {
            [self hiddenViewNotShots];
            [self.timelineTableView reloadData];

        }
    }
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

#pragma mark - UITableViewDelegate

/*typedef enum ScrollDirection {
    ScrollDirectionNone,
    ScrollDirectionRight,
    ScrollDirectionLeft,
    ScrollDirectionUp,
    ScrollDirectionDown,
    ScrollDirectionCrazy,
} ScrollDirection;

- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    ScrollDirection scrollDirection;
    if (self.lastContentOffset+20 > sender.contentOffset.y && move){
       
        scrollDirection = ScrollDirectionDown;
        [self showViewOptions];
        NSLog(@"down");
        move = NO;
        
    } else if (self.lastContentOffset+20 < sender.contentOffset.y && !move){
 
        scrollDirection = ScrollDirectionUp;
        [self hiddenViewOptions];
        NSLog(@"up");
        move = YES;
    }
    
//    if (sender.contentOffset.y == 0 && move) {
//        [self showViewOptions];
//        NSLog(@"init");
//         move = NO;
//        
//    }
    
    self.lastContentOffset = sender.contentOffset.y;
    
    // do whatever you need to with scrollDirection here.
}*/


-(void)hiddenViewOptions{
    if (!move)
        [UIView animateWithDuration:0.3
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseOut
                     animations:^{
                         NSLog(@"oculto");
                         if (self.timelineTableView.frame.origin.y == 117) {
                              self.timelineTableView.frame = CGRectMake(self.timelineTableView.frame.origin.x, self.timelineTableView.frame.origin.y - self.viewOptions.frame.size.height, self.timelineTableView.frame.size.width,  self.timelineTableView.frame.size.height+self.viewOptions.frame.size.height);
                         }
                        
                     }
                     completion:^(BOOL finished) {
                         
                         self.viewOptions.alpha = 0.0f;

                     }];
}

-(void)showViewOptions{
    
    if (move)
        [UIView animateWithDuration:0.3
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseOut
                     animations:^{
                          NSLog(@"muestro");
                         if (self.timelineTableView.frame.origin.y == 117 - self.viewOptions.frame.size.height) {
                          self.timelineTableView.frame = CGRectMake(self.timelineTableView.frame.origin.x, self.timelineTableView.frame.origin.y +self.viewOptions.frame.size.height, self.timelineTableView.frame.size.width,  self.timelineTableView.frame.size.height-self.viewOptions.frame.size.height);
                         }
                        
                    }
                     completion:^(BOOL finished) {
                         
                          self.viewOptions.alpha = 1.0f;
                     }];

}
-(void)scrollViewDidScroll:(UIScrollView *)scrollView {
   
    /*UITabBar *tb = self.tabBarController.tabBar;
    NSInteger yOffset = scrollView.contentOffset.y;
    if (yOffset > 0) {
        tb.frame = CGRectMake(tb.frame.origin.x, self.originalFrame.origin.y + yOffset, tb.frame.size.width, tb.frame.size.height);
    }
    if (yOffset < 1) tb.frame = self.originalFrame;*/
    NSLog(@"antiguo %f",self.lastContentOffset );
    NSLog(@"nuevo %f",scrollView.contentOffset.y );

    
   if (self.lastContentOffset > scrollView.contentOffset.y){
        NSLog(@"entroooo");
        self.viewOptions.alpha = 1.0;
   }else if (scrollView.contentOffset.y > 60) {
       
       NSLog(@"ssss");
       self.viewOptions.alpha =0.0;
   }
    
     self.lastContentOffset = scrollView.contentOffset.y;
}

@end
