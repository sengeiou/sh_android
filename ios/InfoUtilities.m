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
#define kViewHeight                         55.0f


@implementation InfoUtilities

+(UIView *) createHeaderViewWithFrame:(CGRect) frame andMatch:(Match *)match andSection:(NSInteger) section{
   
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, kViewHeight)];
   
    UILabel *lblNameMatch;
    
    if (section == 0)
        lblNameMatch = [[UILabel alloc]initWithFrame:CGRectMake(kLabelHorizontalLeftInsets, kLabelBottomRightInsets+10, frame.size.width, kLabelHeight)];
    else
        lblNameMatch = [[UILabel alloc]initWithFrame:CGRectMake(kLabelHorizontalLeftInsets, kLabelBottomRightInsets, frame.size.width, kLabelHeight)];

    [InfoUtilities setupLabelName:lblNameMatch withMatch:match];

    
    CGFloat width =  [lblNameMatch.text sizeWithFont:[UIFont systemFontOfSize:15 ]].width;

    UILabel *lblDateMatch;
    
    if (section == 0)
        lblDateMatch = [[UILabel alloc]initWithFrame:CGRectMake(width + kLabelHorizontalLeftInsets, kLabelBottomRightInsets+10, frame.size.width - width - kLabelHorizontalLeftInsets - kLabelHorizontalRightInsets, kLabelHeight)];
    else
         lblDateMatch = [[UILabel alloc]initWithFrame:CGRectMake(width + kLabelHorizontalLeftInsets, kLabelBottomRightInsets, frame.size.width - width - kLabelHorizontalLeftInsets - kLabelHorizontalRightInsets, kLabelHeight)];
        
        
    [InfoUtilities setupLabelDate:lblDateMatch withMatch:match];

    [headerView addSubview:lblNameMatch];
    [headerView addSubview:lblDateMatch];
    
    return headerView;
}

+(void)setupLabelName:(UILabel *)labelName withMatch:(Match *)match{
    
    labelName.font = [UIFont systemFontOfSize:15];
    labelName.textColor = [Fav24Colors tableFooters];
    
    //NSString *nameMatch = [NSString stringWithFormat:@"%@-%@", match.localTeamName, match.visitorTeamName];
    NSString *nameMatch  =  [@"Real Madrid - Barcelona" uppercaseString];
    labelName.text = nameMatch;

}

+(void)setupLabelDate:(UILabel *)labelDate withMatch:(Match *)match{
   
    labelDate.font = [UIFont systemFontOfSize:15];
    labelDate.text = [InfoUtilities convertDate:@1414605800];  //InfoUtilities convertDate:[match.matchDate intValue]/1000;
    labelDate.textAlignment = NSTextAlignmentRight;
    labelDate.textColor = [Fav24Colors tableFooters];
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
