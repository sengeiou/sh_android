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
+ (UIImage *)imageFromText:(NSString *)text font:(UIFont *)font {
    
    UILabel * addCustomLabel =
    [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 62, 25)];
    addCustomLabel.text = text;
    addCustomLabel.textColor = [UIColor whiteColor];
    addCustomLabel.font = font; //[UIFont boldSystemFontOfSize:10];
    addCustomLabel.numberOfLines = 2;
    addCustomLabel.backgroundColor = [UIColor clearColor];
    addCustomLabel.textAlignment = NSTextAlignmentCenter;
    addCustomLabel.shadowColor = [UIColor colorWithRed:66/255.0 green:89/255.0 blue:147/255.0 alpha:1.0];
    //[UIColor blackColor];

    addCustomLabel.shadowOffset = CGSizeMake(0,-1);
    
    CGSize size = addCustomLabel.bounds.size;
    
    static CGFloat scale = -1.0;
    
    UIScreen *screen = [UIScreen mainScreen];
    scale = [screen scale];
    
    if (scale > 0.0) {
        UIGraphicsBeginImageContextWithOptions(size, NO, scale);
    } else {
        UIGraphicsBeginImageContext(size);
    }
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    [addCustomLabel.layer renderInContext: context];
    UIImage *img = UIGraphicsGetImageFromCurrentImageContext();
    //CGContextRelease(context);
    
    return img;
}

+ (UIImage *)imageFromText:(NSString *)text {
    return [self imageFromText:text font:[UIFont boldSystemFontOfSize:12]];
}

+ (UIImage *) imageFromColor:(UIColor *)color {
    CGRect rect = CGRectMake(0, 0, 1, 1);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [color CGColor]);
    //  [[UIColor colorWithRed:222./255 green:227./255 blue: 229./255 alpha:1] CGColor]) ;
    CGContextFillRect(context, rect);
    UIImage *img = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return img;
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

//Get difference hours between dates
+(NSString *)getDateShot:(NSNumber *) dateShot{
    
    NSString *timeLeft;
    
    NSDate *today = [NSDate date];
    NSDate *refDate = [NSDate dateWithTimeIntervalSince1970:[dateShot doubleValue]/1000];
    
    NSInteger seconds = [today timeIntervalSinceDate:refDate];
    
    if (seconds == 0) {
        NSLog(@"SEGUNDOOOS: %ld", (long)seconds);
    }

    
    NSInteger days = (int) (floor(seconds / (3600 * 24)));
    if(days) seconds -= days * 3600 * 24;
    
    NSInteger hours = (int) (floor(seconds / 3600));
    if(hours) seconds -= hours * 3600;
    
    NSInteger minutes = (int) (floor(seconds / 60));
    if(minutes) seconds -= minutes * 60;
    
    if(days)
        timeLeft = [NSString stringWithFormat:@"%ldd", (long)days];
    else if(hours)
        timeLeft = [NSString stringWithFormat: @"%ldh", (long)hours];
    else if(minutes)
        timeLeft = [NSString stringWithFormat: @"%ldm", (long)minutes];
    else if(seconds)
        timeLeft = [NSString stringWithFormat: @"%lds", (long)seconds];
    
    if (days == 0 && hours == 0 && minutes == 0) {
        if (seconds < 0 || seconds == 0 )
            timeLeft = @"Now";
    }
    

    //timeLeft = [timeLeft stringByReplacingOccurrencesOfString:@"-" withString:@""];
    
    return timeLeft;
}

//Calculate size for height Cell
+ (CGFloat)heightForShot: (NSString *) text{
    
    NSInteger MAX_HEIGHT = 2000;
    UITextView * textView = [[UITextView alloc] initWithFrame: CGRectMake(0, 26, 320, MAX_HEIGHT)];
    textView.text = text;
    textView.font = [UIFont systemFontOfSize:17];
    [textView sizeToFit];
    
    //if(textView.frame.size.height <= 36.5)
        return textView.frame.size.height+35;
    
    //return textView.frame.size.height+22;
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

+(UIImage*) drawText:(NSString*) text
             inImage:(UIImage*)  image
             atPoint:(CGPoint)   point andSizeFont:(CGFloat) sizeFont;
{ 
     UIFont *font = [UIFont systemFontOfSize:sizeFont];
    
    //UIGraphicsBeginImageContext(image.size);
    UIGraphicsBeginImageContext(CGSizeMake(210, 210));
    //[image drawInRect:CGRectMake(0,0,image.size.width,image.size.height)];
    [image drawInRect:CGRectMake(0,0,210,210)];

    UITextView *myText = [[UITextView alloc] init];
    myText.text = text;
    myText.backgroundColor = [UIColor clearColor];
    
    CGSize maximumLabelSize = CGSizeMake(210,210);
    
    CGRect expectedLabelSize =[myText.text boundingRectWithSize:maximumLabelSize options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:font} context:nil];
    
    myText.frame = CGRectMake((210 / 2) - (expectedLabelSize.size.width / 2),
                              (210 / 2) - (expectedLabelSize.size.height / 2),
                              210,
                              210);

    [myText.text drawInRect:myText.frame withAttributes:@{NSFontAttributeName:font, NSForegroundColorAttributeName:[UIColor whiteColor]}];
    
    
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return newImage;
}

+(CGPoint) centerTextInImage:(UIImageView *)imageView{

    return CGPointMake((imageView.frame.size.width / 2) + 12, (imageView.frame.size.width / 2) - 10);
}


+(NSMutableAttributedString *) formatTitle:(NSString *)text{

    NSMutableAttributedString *attrString = [[NSMutableAttributedString alloc] initWithString:text];
    [attrString addAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:18], NSForegroundColorAttributeName:[Fav24Colors iosSevenBlue]} range:NSMakeRange(0, 1)];
    [attrString addAttributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:13], NSForegroundColorAttributeName:[Fav24Colors iosSevenBlue]} range:NSMakeRange(1, text.length-1)];

    return attrString;
}

@end
