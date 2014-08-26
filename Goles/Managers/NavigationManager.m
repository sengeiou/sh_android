//
//  NavigationManager.m
//  Goles Messenger
//
//  Created by Delfin Pereiro on 02/12/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "NavigationManager.h"

@interface NavigationManager()

//@property (nonatomic, weak) DetailMatchFormViewController *mDetailViewController;

@end

@implementation NavigationManager
//------------------------------------------------------------------------------
//DataAccessLayer singleton instance shared across application
+ (NavigationManager *)singleton
{
    static NavigationManager *sharedLeague = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedLeague = [[NavigationManager alloc] init];
    });
    return sharedLeague;
    
}

//------------------------------------------------------------------------------
+ (NavigationManager *)sharedInstance
{
    return [self singleton];
}

#pragma mark - Public methods
//------------------------------------------------------------------------------
/**
 Pushes a match detail view controller in a given navigation controller stack. If a match detail view
 controller was already presented in the navigation controller, this method pops all views until the match
 detail view becomes visible
 
 @params    navigationController    The navigation controller where pushing the match detail view controller in
 @returns   DetailMatchFormViewController   The presented match detail view controller
 */
//------------------------------------------------------------------------------
/*-(DetailMatchFormViewController *)pushMatchDetailViewInNavigation:(UINavigationController *)navigationController {
    
    [self dissmissAnyModalView];
    
    DetailMatchFormViewController *detail = [self mDetailViewController];
    
    if ( !detail ){
        detail = [[DetailMatchFormViewController alloc] initWithNibName:@"DetailMatchFormViewController" bundle:nil];
        [self setMDetailViewController:detail];
        detail.hidesBottomBarWhenPushed = YES;
        [navigationController pushViewController:detail animated:YES];
    } else {
        [[detail navigationController] popToViewController:detail animated:NO];
    }
    
    return [self mDetailViewController];
}*/

#pragma mark - Private methods
//------------------------------------------------------------------------------
/**
 Dismisses any view controller modally presented until tabbar view controller is visible
 */
//------------------------------------------------------------------------------
-(void)dissmissAnyModalView {
    
    id viewController = [(UITabBarController *)[[[[UIApplication sharedApplication] windows] firstObject] rootViewController] presentedViewController];
    
    if ( [viewController isKindOfClass:[UINavigationController class]] ){
        [[viewController visibleViewController] dismissViewControllerAnimated:NO completion:nil];
        [self dissmissAnyModalView];
    } else if ( [viewController isKindOfClass:[UIViewController class]] ){
        [viewController dismissViewControllerAnimated:NO completion:nil];
        [self dissmissAnyModalView];        
    }
}

#pragma mark - Singleton overwritten methods
//------------------------------------------------------------------------------
- (id)init {
	self = [super init];
	if (self != nil) {
	}
	return self;
}

//------------------------------------------------------------------------------
- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

@end
