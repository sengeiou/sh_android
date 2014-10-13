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
    
    UISearchBar *searchBar  = [[UISearchBar alloc] init];
    if (screenRect.size.width > screenRect.size.height)
        searchBar.frame = CGRectMake(screenRect.origin.x+12, -5, screenRect.size.width-15, 30);
    else
        searchBar.frame = CGRectMake(screenRect.origin.x+12, 0, screenRect.size.width-15, 30);
    searchBar.showsCancelButton = YES;
    [searchBar sizeToFit];
    [searchBar setShowsCancelButton:YES animated:YES];
    searchBar.autoresizingMask = UIViewAutoresizingNone;
    
    return searchBar;

}

@end
