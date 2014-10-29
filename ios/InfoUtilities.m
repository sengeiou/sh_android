//
//  InfoUtilities.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 29/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "InfoUtilities.h"

@implementation InfoUtilities

+(UIView *) createHeaderViewWithFrame:(CGRect) frame andMatch:(Match *)match{
   
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
    
    
    UILabel *lblNameMatch = [[UILabel alloc]initWithFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
    [lblNameMatch setLineBreakMode:NSLineBreakByTruncatingTail];
    lblNameMatch.font = [UIFont systemFontOfSize:15];
    [lblNameMatch sizeToFit];
    
    UILabel *lblDateMatch = [[UILabel alloc]initWithFrame:CGRectMake(0, frame.size.width - lblNameMatch.frame.size.width, frame.size.width - lblNameMatch.frame.size.width, frame.size.height)];
    [lblDateMatch setLineBreakMode:NSLineBreakByTruncatingTail];
    lblDateMatch.font = [UIFont systemFontOfSize:15];
    
    
    NSTimeInterval epoch = [match.matchDate intValue]/1000;
    NSDate *dateStartDate = [NSDate dateWithTimeIntervalSince1970:epoch];
    NSString *dateString = [NSDateFormatter localizedStringFromDate:dateStartDate
                                                          dateStyle:NSDateFormatterShortStyle
                                                          timeStyle:NSDateFormatterFullStyle];
    lblDateMatch.text = dateString;
    [lblDateMatch sizeToFit];
    
    
    [headerView addSubview:lblDateMatch];
    [headerView addSubview:lblNameMatch];
    
    return headerView;
}



@end
