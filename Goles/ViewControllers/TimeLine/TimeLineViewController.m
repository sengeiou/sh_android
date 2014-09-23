//
//  TimeLineViewController.m
//  Goles
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "TimeLineViewController.h"
#import "FavRestConsumer.h"
#import "Follow.h"
#import "User.h"
#import "Shot.h"
#import "CoreDataManager.h"
#import "ShotTableViewCell.h"
#import "UIImageView+FadeIn.h"
#import "Utils.h"

@interface TimeLineViewController (){
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

@end

@implementation TimeLineViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.arrayShoots = [[NSArray alloc]init];
    self.btnShoot.enabled = NO;
    self.txtField.delegate = self;
    
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Follow class] withDelegate:self];
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
    
    self.arrayShoots = [[CoreDataManager sharedInstance] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:YES];
    
    if (self.arrayShoots.count == 0){
        self.mScrollView.scrollEnabled = YES;
        self.timelineTableView.hidden = YES;
    }else
        [self hiddenViewNotShoots];
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
    
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Follow class] withDelegate:self];
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
    self.arrayShoots = [[CoreDataManager sharedInstance] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:YES];
    
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
    
    if (status && [entityClass isSubclassOfClass:[Follow class]]){
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[User class] withDelegate:self];
    }
    else if (status && [entityClass isSubclassOfClass:[User class]]){
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
    }
    else if (status && [entityClass isSubclassOfClass:[Shot class]]){
        
        self.arrayShoots = [[CoreDataManager sharedInstance] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:NO];
        
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

@end
