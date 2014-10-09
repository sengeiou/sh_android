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
    
    return [self createTitleViewWithTitle:NSLocalizedString (@"Shooting...", nil)];
}

//------------------------------------------------------------------------------
+ (UIView *)createConectandoTitleView {
    
    return [self createTitleViewWithTitle:NSLocalizedString (@"Connecting...", nil)];
}
//------------------------------------------------------------------------------
+ (UIView *)createActualizandoTitleView {
    
    return [self createTitleViewWithTitle:NSLocalizedString (@"Updating...", nil)];
}

//------------------------------------------------------------------------------
+ (UIView *)createTimelineTitleView {

    return [self createBasicTitleView];
}

//------------------------------------------------------------------------------
+ (CGFloat)heightForShot: (NSString *) text{
    
    NSInteger MAX_HEIGHT = 2000;
    UITextView *textView = [[UITextView alloc] initWithFrame: CGRectMake(0, 26, 320, MAX_HEIGHT)];
    textView.text = text;
    textView.font = [UIFont systemFontOfSize:17];
    [textView sizeToFit];

    return textView.frame.size.height;
    
//    if(textView.frame.size.height <= 36.5)
//        return textView.frame.size.height+47;
//    
//    return textView.frame.size.height+22;
}

//------------------------------------------------------------------------------
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
            timeLeft = NSLocalizedString (@"Now", nil);
    }
    
    
    //timeLeft = [timeLeft stringByReplacingOccurrencesOfString:@"-" withString:@""];
    
    return timeLeft;
}

//------------------------------------------------------------------------------
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

//------------------------------------------------------------------------------
+ (CGPoint) centerTextInImage:(UIImageView *)imageView{
    
    return CGPointMake((imageView.frame.size.width / 2) + 12, (imageView.frame.size.width / 2) - 10);
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
    titlelabel.text = NSLocalizedString (@"Timeline", nil);

    [titleView addSubview:titlelabel];
    
    return titleView;
}

@end
