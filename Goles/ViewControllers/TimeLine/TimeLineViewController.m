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

@end

@implementation TimeLineViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.arrayShoots = [[NSArray alloc]init];
    self.btnShoot.enabled = NO;
    self.txtField.delegate = self;
    
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Follow class] withDelegate:self];
    
    if (self.arrayShoots.count == 0){
        self.mScrollView.scrollEnabled = YES;
        self.timelineTableView.hidden = YES;
    }else
        [self hiddenViewNotShoots];
}

-(void)hiddenViewNotShoots{
    self.timelineTableView.hidden = NO;
    self.viewNotShoots.hidden = YES;
    self.timelineTableView.delegate = self;
    self.timelineTableView.dataSource = self;
    self.mScrollView.scrollEnabled = NO;
}

-(void)viewWillAppear:(BOOL)animated{
    self.navigationController.navigationBarHidden = YES;
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return self.arrayShoots.count;
}

//------------------------------------------------------------------------------
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 90;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    Shot *shot = self.arrayShoots[indexPath.row];
    
    ShotTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"shootCell" forIndexPath:indexPath];
    
    cell.txvText.text = shot.comment;
    cell.lblName.text = shot.user.name;
    [cell.imgPhoto fadeInFromURL:[NSURL URLWithString:shot.user.photo] withOuterMatte:YES andInnerBorder:NO];
    cell.lblDate.text = [self getDateShot:shot.csys_birth];

    return cell;
 }

-(NSString *)getDateShot:(NSDate *) dateShot{
    
    NSString *timeLeft;
    
    NSDate *today10am =[NSDate date];
    
    NSInteger seconds = [today10am timeIntervalSinceDate:dateShot];
    
    NSInteger days = (int) (floor(seconds / (3600 * 24)));
    if(days) seconds -= days * 3600 * 24;
    
    NSInteger hours = (int) (floor(seconds / 3600));
    if(hours) seconds -= hours * 3600;
    
    NSInteger minutes = (int) (floor(seconds / 60));
    if(minutes) seconds -= minutes * 60;
    
    if(days)
        timeLeft = [NSString stringWithFormat:@"%ldd", (long)days*-1];
    else if(hours)
        timeLeft = [NSString stringWithFormat: @"%ldh", (long)hours*-1];
    else if(minutes)
        timeLeft = [NSString stringWithFormat: @"%ldm", (long)minutes*-1];
    else if(seconds)
        timeLeft = [NSString stringWithFormat: @"%lds", (long)seconds*-1];
    
    timeLeft = [timeLeft stringByReplacingOccurrencesOfString:@"-" withString:@""];
    
    return timeLeft;
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
