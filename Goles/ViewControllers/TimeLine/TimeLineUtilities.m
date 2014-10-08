//
//  TimeLineUtilities.m
//  Shootr
//
//  Created by Christian Cabarrocas on 08/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "TimeLineUtilities.h"

@implementation TimeLineUtilities

//------------------------------------------------------------------------------
+ (UIView *)createEnviandoTitleView {
    
    UIView *titleView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 250, 50)];
    UILabel *title = [[UILabel alloc] initWithFrame:CGRectMake(75, 10, 100, 30)];
    title.font = [UIFont boldSystemFontOfSize:17];
    title.text = @"Enviando...";
    
    UIActivityIndicatorView *activity = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    activity.frame = CGRectMake(40, 10, 30, 30);
    [activity startAnimating];
    
    [titleView addSubview:activity];
    [titleView addSubview:title];
    
    return titleView;
}

//------------------------------------------------------------------------------
+ (UIView *)createTimelineTitleView {
    
    UIView *titleView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 250, 50)];
    UILabel *title = [[UILabel alloc] initWithFrame:CGRectMake(75, 10, 100, 30)];
    title.font = [UIFont boldSystemFontOfSize:17];
    title.text = @"Timeline";

    [titleView addSubview:title];
    
    return titleView;
}
@end
