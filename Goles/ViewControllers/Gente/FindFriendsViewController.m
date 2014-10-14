//
//  FindFriendsViewController.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 14/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "FindFriendsViewController.h"
#import "PeopleLineUtilities.h"

@interface FindFriendsViewController ()<UISearchBarDelegate>

@property (nonatomic,strong)                UISearchBar     *mySearchBar;

@end

@implementation FindFriendsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

-(void)addSearchNavBar{
    self.mySearchBar = [PeopleLineUtilities createSearchNavBar];
    self.mySearchBar.delegate = self;
    [self.mySearchBar becomeFirstResponder];
    [self.navigationController.navigationBar addSubview:self.mySearchBar];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
