//
//  TimeLineUtilities.m
//  Shootr
//
//  Created by Christian Cabarrocas on 08/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "TimeLineUtilities.h"
#import "CoreDataParsing.h"

@implementation TimeLineUtilities

#pragma mark - PUBLIC METHODS OF TITLE VIEW CREATION
//------------------------------------------------------------------------------
+ (UIView *)createEnviandoTitleView {
    
    return [self createTitleViewWithTitle:@"Enviando..."];
}

//------------------------------------------------------------------------------
+ (UIView *)createConectandoTitleView {
    
    return [self createTitleViewWithTitle:@"Conectando..."];
}

//------------------------------------------------------------------------------
+ (UIView *)createTimelineTitleView {

    return [self createBasicTitleView];
}



#pragma mark - PRIVATE HELPER METHODS
//------------------------------------------------------------------------------
+ (UIView *)createTitleViewWithTitle:(NSString *)title {
    
    CGRect screenRect = [[UIScreen mainScreen] bounds];
    UIView *titleView = [[UIView alloc] initWithFrame:CGRectMake(screenRect.origin.x, screenRect.origin.y, screenRect.size.width, 50)];
    UILabel *titlelabel = [[UILabel alloc] initWithFrame:CGRectMake((screenRect.size.width/2)-90, 10, 100, 30)];
    titlelabel.font = [UIFont boldSystemFontOfSize:17];
    titlelabel.text = title;
    
    UIActivityIndicatorView *activity = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    activity.frame = CGRectMake((screenRect.size.width/2)-125, 10, 30, 30);
    [activity startAnimating];
    
    [titleView addSubview:activity];
    [titleView addSubview:titlelabel];
    
    return titleView;
}

//------------------------------------------------------------------------------
+ (UIView *)createBasicTitleView {
    
    CGRect screenRect = [[UIScreen mainScreen] bounds];
    UIView *titleView = [[UIView alloc] initWithFrame:CGRectMake(screenRect.origin.x, screenRect.origin.y, screenRect.size.width, 50)];
    UILabel *titlelabel = [[UILabel alloc] initWithFrame:CGRectMake((screenRect.size.width/2)-85, 10, 100, 30)];
    titlelabel.font = [UIFont boldSystemFontOfSize:17];
    titlelabel.text = @"Timeline";

    [titleView addSubview:titlelabel];
    
    return titleView;
}

@end
