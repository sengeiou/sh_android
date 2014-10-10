//
//  PeopleLineUtilities.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 10/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "PeopleLineUtilities.h"

@implementation PeopleLineUtilities


+ (UISearchBar *)createSearchNavBar{

    CGRect screenRect = [[UIScreen mainScreen] bounds];
    
    UISearchBar *searchBar  = [[UISearchBar alloc] initWithFrame:CGRectMake(screenRect.origin.x+3, 0, screenRect.size.width, 30)];
    searchBar.showsCancelButton = YES;
    [searchBar sizeToFit];
    [searchBar setShowsCancelButton:YES animated:YES];
    searchBar.autoresizingMask = UIViewAutoresizingNone;
    
    return searchBar;

    

//    UISearchBar *searchBar = [UISearchBar new];
//    searchBar.showsCancelButton = YES;
//    [searchBar sizeToFit];
//    UIView *barWrapper = [[UIView alloc]initWithFrame:searchBar.bounds];
//    [barWrapper addSubview:searchBar];
//    self.navigationItem.titleView = barWrapper;
}

@end
