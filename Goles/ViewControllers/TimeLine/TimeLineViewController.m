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
#import "Shot.h"

@interface TimeLineViewController ()

@property (nonatomic,strong) IBOutlet UITableView    *timelineTableView;

@end

@implementation TimeLineViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Follow class] withDelegate:self];
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:self];
}

//------------------------------------------------------------------------------
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return 1;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
     
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"shootCell" forIndexPath:indexPath];
 
    return cell;
 }
 

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error {
    
    if (status){
        NSLog(@"Response OK");
    }
}

@end
