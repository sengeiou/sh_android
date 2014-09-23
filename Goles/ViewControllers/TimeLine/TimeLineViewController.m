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

@interface TimeLineViewController ()

@property (nonatomic,strong) IBOutlet UITableView    *timelineTableView;
@property (weak, nonatomic) IBOutlet UIButton *btnWatching;
@property (weak, nonatomic) IBOutlet UIButton *btnSearch;
@property (weak, nonatomic) IBOutlet UIButton *btnInfo;
@property (weak, nonatomic) IBOutlet UITextField *txtField;
@property (weak, nonatomic) IBOutlet UIButton *btnShoot;
@property (weak, nonatomic) IBOutlet UIView *viewNotShoots;
@property (weak, nonatomic) NSArray *arrayShoots;

@end

@implementation TimeLineViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Follow class] withDelegate:self];
    
    if (self.arrayShoots.count == 0)
        self.timelineTableView.hidden = YES;
    else
        self.viewNotShoots.hidden = YES;
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
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
     
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"shootCell" forIndexPath:indexPath];
 
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
       self.arrayShoots = [[CoreDataManager sharedInstance] getAllEntities:[Shot class] orderedByKey:kJSON_BIRTH ascending:YES];
        [self.timelineTableView reloadData];
    }
}

@end
