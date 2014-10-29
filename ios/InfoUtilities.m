//
//  InfoUtilities.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 29/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "InfoUtilities.h"
#import "Fav24Colors.h"

#define kLabelHorizontalLeftInsets          10.0f
#define kLabelHorizontalRightInsets         10.0f
#define kLabelBottomRightInsets             20.0f

#define kLabelHeight                        30.0f
#define kViewHeight                         40.0f


@implementation InfoUtilities

+(UIView *) createHeaderViewWithFrame:(CGRect) frame andMatch:(Match *)match{
   
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, kViewHeight)];
    
    
    UILabel *lblNameMatch = [[UILabel alloc]initWithFrame:CGRectMake(kLabelHorizontalLeftInsets, kLabelBottomRightInsets, frame.size.width, kLabelHeight)];
    lblNameMatch.font = [UIFont systemFontOfSize:15];
    lblNameMatch.textColor = [Fav24Colors tableFooters];
    
    //NSString *nameMatch = [NSString stringWithFormat:@"%@-%@", match.localTeamName, match.visitorTeamName];
    NSString *nameMatch  =  [@"Real Madrid - Barcelona" uppercaseString];
    lblNameMatch.text = nameMatch;

    CGFloat width =  [lblNameMatch.text sizeWithFont:[UIFont systemFontOfSize:15 ]].width;

    
    UILabel *lblDateMatch = [[UILabel alloc]initWithFrame:CGRectMake(width + kLabelHorizontalLeftInsets, kLabelBottomRightInsets, frame.size.width - width - kLabelHorizontalLeftInsets - kLabelHorizontalRightInsets, kLabelHeight)];
    lblDateMatch.font = [UIFont systemFontOfSize:15];
    
   
    lblDateMatch.text = [InfoUtilities convertDate:@1414605800];  //InfoUtilities convertDate:[match.matchDate intValue]/1000;
    lblDateMatch.textAlignment = NSTextAlignmentRight;
    lblDateMatch.textColor = [Fav24Colors tableFooters];

    [headerView addSubview:lblNameMatch];
    [headerView addSubview:lblDateMatch];
    
    return headerView;
}

+(NSString *)convertDate:(NSNumber *)numberDate{
  
    NSTimeInterval epoch = [numberDate doubleValue]/1000;
    NSDate *epochNSDate = [[NSDate alloc] initWithTimeIntervalSince1970:epoch];
    NSString *dateString = [NSDateFormatter localizedStringFromDate:epochNSDate
                                                          dateStyle:NSDateFormatterShortStyle
                                                          timeStyle:NSDateFormatterShortStyle];

    return dateString;
}


@end
