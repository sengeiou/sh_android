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

@interface TimeLineViewController ()<ConectionProtocol>{
    int lengthTextField;
}

@property (nonatomic,strong) IBOutlet UITableView    *timelineTableView;
@property (weak, nonatomic) IBOutlet UIButton *btnWatching;
@property (weak, nonatomic) IBOutlet UIButton *btnSearch;
@property (weak, nonatomic) IBOutlet UIButton *btnInfo;
@property (weak, nonatomic) IBOutlet UITextField *txtField;
@property (weak, nonatomic) IBOutlet UIButton *btnShoot;
@property (weak, nonatomic) IBOutlet UIView *viewNotShoots;
@property (strong, nonatomic) NSArray *arrayShoots;
@property (weak, nonatomic) IBOutlet UIScrollView *mScrollView;
@property (strong, nonatomic) UIRefreshControl *refreshControl;
@property (weak, nonatomic) IBOutlet UIView *viewOptions;

@end

@implementation TimeLineViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.arrayShoots = [[NSArray alloc]init];
    self.btnShoot.enabled = NO;
    self.txtField.delegate = self;
    
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
    
    self.arrayShoots = [[ShotManager singleton] getShotsForTimeLine];
    
    if (self.arrayShoots.count == 0){
        self.mScrollView.scrollEnabled = YES;
        self.timelineTableView.hidden = YES;
    }else
        [self hiddenViewNotShoots];
    
    UIImage *image = [[UIImage imageNamed:@"Icon_Magnifier"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    [self.btnSearch setImage:image forState:UIControlStateNormal];
    self.btnSearch.tintColor = [UIColor colorWithRed:0.0/255.0 green:122.0/255.0 blue:255.0/255.0 alpha:1];
}

//------------------------------------------------------------------------------
-(void)hiddenViewNotShoots{
    
    [self addPullToRefresh];
    
    self.timelineTableView.hidden = NO;
    self.viewNotShoots.hidden = YES;
    self.timelineTableView.delegate = self;
    self.timelineTableView.dataSource = self;
    self.mScrollView.scrollEnabled = NO;
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

    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
    
    if (status){
        if ([[Conection sharedInstance]isConection]) {
            
            [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
            
            [self.refreshControl endRefreshing];
        };
    }else
        [self.refreshControl endRefreshing];
}

#pragma mark - UITableViewDelegate

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return self.arrayShoots.count;
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    //return 90;
    Shot *shot = self.arrayShoots[indexPath.row];

    return [Utils heightForShoot:shot.comment];
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    Shot *shot = self.arrayShoots[indexPath.row];
    
    ShotTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"shootCell" forIndexPath:indexPath];
    
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
        
        self.arrayShoots = [[ShotManager singleton] getShotsForTimeLine];
        
        if (self.arrayShoots.count > 0) {
            [self hiddenViewNotShoots];
            [self.timelineTableView reloadData];

        }
    }
}

#pragma mark - UITextField response methods
//------------------------------------------------------------------------------
-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    
    lengthTextField = self.txtField.text.length - range.length + string.length;
   
    if (lengthTextField > 1)
        self.btnShoot.enabled = YES;
     else
        self.btnShoot.enabled = NO;
    
    return YES;
}

-(void)hiddenViewOptions{
    [UIView animateWithDuration:0.3
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseOut
                     animations:^{
                        /* self.mSubMenuNavigation.frame = CGRectMake(0, kSUBMENU_INIT_POSITION, 320, kSUBMENU_HEIGHT);
                         self.bSilenceMode.alpha = 0.0f;
                         self.bBasicMode.alpha = 0.0f;
                         self.bIntenseMode.alpha = 0.0f;
                         self.lLabelSubscriptionMode.alpha = 0.0f;
                         self.blackBottomImage.alpha = 0.0f;
                         //                                 [self.whiteLine removeFromSuperview];
                         self.navigationItem.title = [self getTeamNames];*/
                     }
                     completion:^(BOOL finished) {
                        // [[self mSubMenuNavigation] setAlpha:0.0f];
                     }];
}

-(void)showViewOptions{
    [UIView animateWithDuration:0.3
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseOut
                     animations:^{
                       /*  self.mSubMenuNavigation.frame = CGRectMake(0, kSUBMENU_INIT_POSITION+self.mSubMenuNavigation.frame.size.height, 320, kSUBMENU_HEIGHT);
                         self.bSilenceMode.alpha = 1.0f;
                         self.bBasicMode.alpha = 1.0f;
                         self.bIntenseMode.alpha = 1.0f;
                         self.lLabelSubscriptionMode.alpha = 1.0f;
                         self.blackBottomImage.alpha = 0.3f;
                         //                                 self.whiteLine = [[UIView alloc] initWithFrame:CGRectMake(0, 44, 320, 1)];
                         //                                 [self.whiteLine setBackgroundColor:[UIColor whiteColor]];
                         //                                 self.whiteLine.alpha = 0.9f;
                         //                                 [self.navigationController.navigationBar addSubview:self.whiteLine];
                         self.navigationItem.title = @"Notificaciones";*/
                     }
     
                     completion:^(BOOL finished) {
                     }];

}

@end
