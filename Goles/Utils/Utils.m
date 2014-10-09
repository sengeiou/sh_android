//
//  Utils.m
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2012 Fav24. All rights reserved.
//

#import "Utils.h"
#import "FavRestConsumerHelper.h"
#import <QuartzCore/QuartzCore.h>
#import "Fav24Fonts.h"
#import "Fav24Colors.h"
#import "Constants.h"
#include <sys/xattr.h>

@implementation Utils


//------------------------------------------------------------------------------
+(id)getValueFromUserDefaultsFromKey:(NSString *)key {
    
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    id result =[prefs stringForKey: key];
    return result;
}

//------------------------------------------------------------------------------
+(void)setValueToUserDefaults:(id)value ToKey:(NSString *)key {
    
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setValue:value forKey:key];
    [prefs synchronize];
}

+ (UIView *)setNavigationBarTitle:(NSString *)title andSubtitle:(NSString *)subtitle forMaximumLenght:(NSNumber *)lenghtView {
    return [Utils setNavigationBarTitle:title andSubtitle:subtitle forLoadingMode:NO forMaximumLenght:lenghtView];
}


+ (UIView *)setNavigationBarTitle:(NSString *)title andSubtitle:(NSString *)subtitle{
    return [Utils setNavigationBarTitle:title andSubtitle:subtitle forLoadingMode:NO forMaximumLenght:nil];
}

+ (UIView *)setNavigationBarTitle:(NSString *)title andSubtitle:(NSString *)subtitle forLoadingMode:(BOOL)loadingMode forMaximumLenght:(NSNumber *)lenghtView{
    
    NSInteger lengthOfString = [title length] ;
    
    if (lenghtView > 0)
        lengthOfString = [lenghtView integerValue];
    else
        lengthOfString = 158;
    
    
    CGRect titleRect = CGRectMake(0.0f, 0.0f, lengthOfString, 44.0f);
    UIView *_headerTitleSubtitleView = [[UILabel alloc] initWithFrame:titleRect];
    _headerTitleSubtitleView.backgroundColor = [UIColor clearColor];
    
    UILabel *titleView = [[UILabel alloc] initWithFrame:CGRectZero];
    if (subtitle)   titleView.frame = CGRectMake(titleRect.origin.x, 2.0f, titleRect.size.width, 24.0f);
    else            titleView.frame = CGRectMake(titleRect.origin.x, 0.0f, titleRect.size.width, 44.0f);

    titleView.backgroundColor = [UIColor clearColor];
    titleView.font = [UIFont boldSystemFontOfSize:17];
    titleView.textAlignment = NSTextAlignmentCenter;
    titleView.text = title;

    if (subtitle) {

        UILabel *subtitleView = [[UILabel alloc] initWithFrame:CGRectMake(titleRect.origin.x, 24, titleRect.size.width, 44-24)];
        subtitleView.backgroundColor = [UIColor clearColor];
        subtitleView.font = [UIFont systemFontOfSize:13];
        subtitleView.textColor = [UIColor grayColor];
        subtitleView.textAlignment = NSTextAlignmentCenter;
        subtitleView.text = subtitle;
        [_headerTitleSubtitleView addSubview:subtitleView];
    }
    
    if (loadingMode) {
        titleView.frame = CGRectMake(titleRect.origin.x, 2, titleRect.size.width, 44);
        titleView.font = [UIFont italicSystemFontOfSize:14];
        titleView.textAlignment = NSTextAlignmentCenter;
        titleView.text = title;

    }
    [_headerTitleSubtitleView addSubview:titleView];
    return _headerTitleSubtitleView;
}

+ (UIView *)createTableFooterWithText:(NSString *)footerText {
    
    UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(14, 0, 295, 70)];

    UILabel *footerLabel = [[UILabel alloc] initWithFrame:CGRectMake(14, 0, 295, 70)];
    footerLabel.text = footerText;
    footerLabel.textColor = [UIColor colorWithRed:110.0f/255.0f green:110.0f/255.0f blue:110.0f/255.0f alpha:1];
    footerLabel.textAlignment = NSTextAlignmentLeft;
    footerLabel.numberOfLines = 0;
    footerLabel.lineBreakMode = NSLineBreakByWordWrapping;
    footerLabel.font = [Fav24Fonts footerTableFont];
    
    [footerView addSubview:footerLabel];
    
    return footerView;
}

+ (UIView *)createTableHeaderWithText:(NSString *)headerText andRightDetail:(NSString *)detailText withPenalties:(BOOL)penalties{
    
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(14, 0, 300, 55)];
    
    UILabel *headerLabel = [[UILabel alloc] initWithFrame:CGRectMake(14, 12, 300, 55)];
    headerLabel.text = headerText;
    headerLabel.textColor = [UIColor colorWithRed:110.0f/255.0f green:110.0f/255.0f blue:110.0f/255.0f alpha:1];
    headerLabel.textAlignment = NSTextAlignmentLeft;
    headerLabel.font = [Fav24Fonts headerTableFont];
    [headerView addSubview:headerLabel];

    UILabel *detailLabel = [[UILabel alloc] initWithFrame:CGRectMake(160, 12, 145, 55)];

    if (penalties)
        detailText = [detailText stringByAppendingString:@" (PEN)"];
    
    detailLabel.text = detailText;
    detailLabel.textAlignment = NSTextAlignmentRight;
    detailLabel.font = [UIFont systemFontOfSize:14];
    detailLabel.textColor = [UIColor lightGrayColor];
    [headerView addSubview:detailLabel];
    
    return headerView;
}


//------------------------------------------------------------------------------
+(NSNumber *)getDeviceCurrentLanguageCode {
    
    NSString *language = [[[[NSLocale preferredLanguages] objectAtIndex:0] substringToIndex:2] lowercaseString];
    NSNumber *languageCode = @1;    // English (Default)
    if ([language isEqualToString:@"es"] || [language isEqualToString:@"ca"])
        languageCode = @0;          // Spanish
    
    return languageCode;
}

//------------------------------------------------------------------------------
+ (UIView *)createTableHeaderView:(NSString *)headerText withLink:(NSString *)headerLink andMethod:(SEL)onClickMethod withDelegate:(id)delegate{

    return [self createTableHeaderView:headerText withLink:headerLink inBottomPosition:YES andMethod:onClickMethod withDelegate:delegate];
}

//------------------------------------------------------------------------------
+ (UIView *)createTableHeaderView:(NSString *)headerText
                         withLink:(NSString *)headerLink
                 inBottomPosition:(BOOL)isInbottomPosition
                        andMethod:(SEL)onClickMethod
                     withDelegate:(id)delegate
{
    //Constants
    int topMargin = 30;
    int leftMargin = 14;
    
    CGRect screenSize = [[UIScreen mainScreen] bounds];
    
    //Create label for the standard text
    UILabel *headerTextLabel = [[UILabel alloc] initWithFrame:CGRectMake(leftMargin, 0.0, screenSize.size.width - (2*leftMargin), MAXFLOAT)];
    headerTextLabel.text = headerText;
    headerTextLabel.textColor = [Fav24Colors tableFooters];
    headerTextLabel.font = [UIFont systemFontOfSize:14];
    headerTextLabel.numberOfLines = 0;
    [headerTextLabel sizeToFit];
    //[headerTextLabel setBackgroundColor:[UIColor greenColor]];
    
    //Create the button
    UIButton *linkButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    linkButton.frame = CGRectMake(leftMargin,
                                  0.0,
                                  headerTextLabel.frame.size.width,
                                  MAXFLOAT);

    [linkButton setTitle:headerLink forState:UIControlStateNormal];
    [linkButton setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
    [linkButton addTarget:delegate action:onClickMethod forControlEvents:UIControlEventTouchUpInside];
    linkButton.titleLabel.font = [UIFont systemFontOfSize:14];
    [linkButton sizeToFit];
    //[linkButton setBackgroundColor:[UIColor orangeColor]];

    if ( isInbottomPosition){
        [headerTextLabel setFrame:CGRectOffset([headerTextLabel frame], 0.0, topMargin)];
        [linkButton setFrame:CGRectOffset([linkButton frame], 0.0, headerTextLabel.frame.origin.y+headerTextLabel.frame.size.height)];
    }else {
        [linkButton setFrame:CGRectOffset([linkButton frame], 0.0, topMargin)];
        [headerTextLabel setFrame:CGRectOffset([headerTextLabel frame], 0.0, linkButton.frame.origin.y+linkButton.frame.size.height)];
    }
    
    //Create general header view
    CGRect frame = CGRectMake(0, 0, 320, headerTextLabel.frame.size.height+linkButton.frame.size.height+topMargin);
    UIView *header = [[UIView alloc] initWithFrame:frame];
    [header addSubview:headerTextLabel];
    [header addSubview:linkButton];
    //[header setBackgroundColor:[UIColor redColor]];
    
    
    return header;
}

//------------------------------------------------------------------------------
+ (UIView *)createTableFooterView:(NSString *)headerText
                         withFirstLink:(NSString *)linkOne
                    andSecondLink:(NSString *)linkTwo
                 inBottomPosition:(BOOL)isInbottomPosition
                        andFirstMethod:(SEL)methodOne
                        andSecondMethod:(SEL)methodTwo
                     withDelegate:(id)delegate
{
    //Constants
    int topMargin = 20;
    int leftMargin = 14;
    
    CGRect screenSize = [[UIScreen mainScreen] bounds];
    
    //Create label for the standard text
    UILabel *headerTextLabel = [[UILabel alloc] initWithFrame:CGRectMake(leftMargin, 0.0, screenSize.size.width - (2*leftMargin), MAXFLOAT)];
    headerTextLabel.text = headerText;
    headerTextLabel.textColor = [UIColor darkGrayColor];
    headerTextLabel.font = [UIFont systemFontOfSize:14];
    headerTextLabel.numberOfLines = 0;
    [headerTextLabel sizeToFit];
    //    [headerTextLabel setBackgroundColor:[UIColor greenColor]];
    
    //Create the first button
    UIButton *firstLinkButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    firstLinkButton.frame = CGRectMake(leftMargin,
                                       0.0,
                                       headerTextLabel.frame.size.width,
                                       MAXFLOAT);
    
    [firstLinkButton setTitle:linkOne forState:UIControlStateNormal];
    [firstLinkButton setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
    [firstLinkButton addTarget:delegate action:methodOne forControlEvents:UIControlEventTouchUpInside];
    firstLinkButton.titleLabel.font = [UIFont systemFontOfSize:14];
    [firstLinkButton sizeToFit];
    //[firstLinkButton setBackgroundColor:[UIColor orangeColor]];
    
    //Create the second button
    UIButton *secondLinkButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    secondLinkButton.frame = CGRectMake(leftMargin,
                                        0.0,
                                        headerTextLabel.frame.size.width,
                                        MAXFLOAT);
    
    [secondLinkButton setTitle:linkTwo forState:UIControlStateNormal];
    [secondLinkButton setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
    [secondLinkButton addTarget:delegate action:methodTwo forControlEvents:UIControlEventTouchUpInside];
    secondLinkButton.titleLabel.font = [UIFont systemFontOfSize:14];
    [secondLinkButton sizeToFit];
    //[secondLinkButton setBackgroundColor:[UIColor greenColor]];
    
    if ( isInbottomPosition ){
        [headerTextLabel setFrame:CGRectOffset([headerTextLabel frame], 0.0, topMargin+2)];
        //headerTextLabel.backgroundColor = [UIColor redColor];
        [firstLinkButton setFrame:CGRectOffset([firstLinkButton frame], 0.0, headerTextLabel.frame.origin.y+headerTextLabel.frame.size.height+10)];
        //firstLinkButton.backgroundColor = [UIColor greenColor];
        [secondLinkButton setFrame:CGRectOffset([secondLinkButton frame], 0.0, headerTextLabel.frame.origin.y+headerTextLabel.frame.size.height+firstLinkButton.frame.size.height+15)];
        //secondLinkButton.backgroundColor = [UIColor blueColor];
    }else {
        [firstLinkButton setFrame:CGRectOffset([firstLinkButton frame], 0.0, topMargin)];
        [secondLinkButton setFrame:CGRectOffset([secondLinkButton frame], 0.0, topMargin)];
        [headerTextLabel setFrame:CGRectOffset([headerTextLabel frame], 0.0, secondLinkButton.frame.origin.y+secondLinkButton.frame.size.height)];
    }
    
    //Create general header view
    CGRect frame = CGRectMake(0, 50, 320, headerTextLabel.frame.size.height+firstLinkButton.frame.size.height+secondLinkButton.frame.size.height+topMargin+20);
    UIView *header = [[UIView alloc] initWithFrame:frame];
    [header addSubview:headerTextLabel];
    [header addSubview:firstLinkButton];
    [header addSubview:secondLinkButton];
    //[header setBackgroundColor:[UIColor lightGrayColor]];
    
    
    return header;
}

//------------------------------------------------------------------------------
+ (UIView *)createPickerFooterView:(NSString *)headerText
{
    //Constants
    int topMargin = 8;
    int leftMargin = 14;
    
    CGRect screenSize = [[UIScreen mainScreen] bounds];
    
    //Create label for the standard text
    UILabel *headerTextLabel = [[UILabel alloc] initWithFrame:CGRectMake(leftMargin, topMargin, screenSize.size.width - (2*leftMargin), 30)];
    headerTextLabel.text = headerText;
    headerTextLabel.textColor = [Fav24Colors tableFooters];
    headerTextLabel.font = [UIFont systemFontOfSize:14];
    headerTextLabel.numberOfLines = 0;
    [headerTextLabel sizeToFit];
    
    //Create general header view
    CGRect frame = CGRectMake(0, 0, 320, headerTextLabel.frame.size.height+6);
    UIView *header = [[UIView alloc] initWithFrame:frame];
    [header addSubview:headerTextLabel];
    
    return header;
}

//------------------------------------------------------------------------------
+ (UIView *)createTableHeaderView:(NSString *)headerText
                    withFirstLink:(NSString *)linkOne
                    andSecondLink:(NSString *)linkTwo
                 inBottomPosition:(BOOL)isInbottomPosition
                   andFirstMethod:(SEL)methodOne
                  andSecondMethod:(SEL)methodTwo
                     withDelegate:(id)delegate
{
    //Constants
    int topMargin = -40;
    int leftMargin = 14;
    
    CGRect screenSize = [[UIScreen mainScreen] bounds];
    
    //Create label for the standard text
    UILabel *headerTextLabel = [[UILabel alloc] initWithFrame:CGRectMake(leftMargin, 0.0, screenSize.size.width - (2*leftMargin), MAXFLOAT)];
    headerTextLabel.text = headerText;
    headerTextLabel.textColor = [UIColor darkGrayColor];
    headerTextLabel.font = [UIFont systemFontOfSize:14];
    headerTextLabel.numberOfLines = 0;
    [headerTextLabel sizeToFit];
    //    [headerTextLabel setBackgroundColor:[UIColor greenColor]];
    
    //Create the first button
    UIButton *firstLinkButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    firstLinkButton.frame = CGRectMake(leftMargin,
                                  0.0,
                                  headerTextLabel.frame.size.width,
                                  MAXFLOAT);
    
    [firstLinkButton setTitle:linkOne forState:UIControlStateNormal];
    [firstLinkButton setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
    [firstLinkButton addTarget:delegate action:methodOne forControlEvents:UIControlEventTouchUpInside];
    firstLinkButton.titleLabel.font = [UIFont systemFontOfSize:14];
    [firstLinkButton sizeToFit];
    //[firstLinkButton setBackgroundColor:[UIColor orangeColor]];
    
    //Create the second button
    UIButton *secondLinkButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    secondLinkButton.frame = CGRectMake(leftMargin,
                                  0.0,
                                  headerTextLabel.frame.size.width,
                                  MAXFLOAT);
    
    [secondLinkButton setTitle:linkTwo forState:UIControlStateNormal];
    [secondLinkButton setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
    [secondLinkButton addTarget:delegate action:methodTwo forControlEvents:UIControlEventTouchUpInside];
    secondLinkButton.titleLabel.font = [UIFont systemFontOfSize:14];
    [secondLinkButton sizeToFit];
    //[secondLinkButton setBackgroundColor:[UIColor greenColor]];
    
    if ( isInbottomPosition ){
        [headerTextLabel setFrame:CGRectOffset([headerTextLabel frame], 0.0, topMargin+2)];
        //headerTextLabel.backgroundColor = [UIColor redColor];
        [firstLinkButton setFrame:CGRectOffset([firstLinkButton frame], 0.0, headerTextLabel.frame.origin.y+headerTextLabel.frame.size.height+10)];
        //firstLinkButton.backgroundColor = [UIColor greenColor];
        [secondLinkButton setFrame:CGRectOffset([secondLinkButton frame], 0.0, headerTextLabel.frame.origin.y+headerTextLabel.frame.size.height+firstLinkButton.frame.size.height+15)];
        //secondLinkButton.backgroundColor = [UIColor blueColor];
    }else {
        [firstLinkButton setFrame:CGRectOffset([firstLinkButton frame], 0.0, topMargin)];
        [secondLinkButton setFrame:CGRectOffset([secondLinkButton frame], 0.0, topMargin)];
        [headerTextLabel setFrame:CGRectOffset([headerTextLabel frame], 0.0, secondLinkButton.frame.origin.y+secondLinkButton.frame.size.height)];
    }
    
    //Create general header view
    CGRect frame = CGRectMake(0, 50, 320, headerTextLabel.frame.size.height+firstLinkButton.frame.size.height+secondLinkButton.frame.size.height+topMargin+20);
    UIView *header = [[UIView alloc] initWithFrame:frame];
    [header addSubview:headerTextLabel];
    [header addSubview:firstLinkButton];
    [header addSubview:secondLinkButton];
    //[header setBackgroundColor:[UIColor lightGrayColor]];
    
    
    return header;
}


//Comprove if a format mail is valid
+ (BOOL) NSStringIsValidEmail:(NSString *)checkString
{
    BOOL stricterFilter = NO;
    NSString *stricterFilterString = @"[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}";
    NSString *laxString = @".+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2}[A-Za-z]*";
    NSString *emailRegex = stricterFilter ? stricterFilterString : laxString;
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
    return [emailTest evaluateWithObject:checkString];
}

+ (int)getIphone: (CGFloat) height{
    
    if (height == 736)
       return 7;
    else if (height == 667)
        return 6;
    else if (height == 568)
        return 5;
    else if (height == 480)
       return 4;
    

    return 0;
}


+(NSMutableAttributedString *) formatTitle:(NSString *)text{

    NSMutableAttributedString *attrString = [[NSMutableAttributedString alloc] initWithString:text];
    [attrString addAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:18], NSForegroundColorAttributeName:[Fav24Colors iosSevenBlue]} range:NSMakeRange(0, 1)];
    [attrString addAttributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:13], NSForegroundColorAttributeName:[Fav24Colors iosSevenBlue]} range:NSMakeRange(1, text.length-1)];

    return attrString;
}

//------------------------------------------------------------------------------
+ (BOOL)addSkipBackupAttributeToItemAtURL:(NSURL *)URL
{
    const char* filePath = [[URL path] fileSystemRepresentation];
    const char* attrName = "com.apple.MobileBackup";
    if (&NSURLIsExcludedFromBackupKey == nil) {
        // iOS 5.0.1 and lower
        u_int8_t attrValue = 1;
        int result = setxattr(filePath, attrName, &attrValue, sizeof(attrValue), 0, 0);
        return result == 0;
    } else {
        // First try and remove the extended attribute if it is present. iOS 5.1 and later
        int result = (int) getxattr(filePath, attrName, NULL, sizeof(u_int8_t), 0, 0);
        if (result != -1) {
            // The attribute exists, we need to remove it
            int removeResult = removexattr(filePath, attrName, 0);
            if (removeResult == 0) {
                NSLog(@"Removed extended attribute on file %@", URL);
            }
        }
        
        // Set the new key
        return [URL setResourceValue:[NSNumber numberWithBool:YES] forKey:NSURLIsExcludedFromBackupKey error:nil];
    }
}

+ (BOOL)addSkipBackupAttributeToItemAtPath:(NSString *)path
{
    const char* filePath = [path fileSystemRepresentation];
    const char* attrName = "com.apple.MobileBackup";
    u_int8_t attrValue = 1;
    
    int result = setxattr(filePath, attrName, &attrValue, sizeof(attrValue), 0, 0);
    return result == 0;
}

@end
