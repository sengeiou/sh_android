//
//  TimeLineUtilities.m
//  Shootr
//
//  Created by Christian Cabarrocas on 08/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "TimeLineUtilities.h"
#import "CoreDataParsing.h"
#import "Fav24Colors.h"

@implementation TimeLineUtilities

//------------------------------------------------------------------------------
+(NSString *)getDateShot:(NSNumber *) dateShot{
    
    NSString *timeLeft;
    
    NSDate *today = [NSDate date];
    NSDate *refDate = [NSDate dateWithTimeIntervalSince1970:[dateShot doubleValue]/1000];
    
    NSInteger seconds = [today timeIntervalSinceDate:refDate];
    
//    if (seconds == 0) {
//        NSLog(@"SEGUNDOOOS: %ld", (long)seconds);
//    }
    
    
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

+ (NSMutableAttributedString *)filterLinkWithContent:(NSString *)content {
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:content];
    NSError *error = NULL;
    NSDataDetector *detector =
    [NSDataDetector dataDetectorWithTypes:(NSTextCheckingTypes)NSTextCheckingTypeLink | NSTextCheckingTypePhoneNumber
                                    error:&error];
    
    NSArray *matches = [detector matchesInString:content
                                         options:0
                                           range:NSMakeRange(0, [content length])];
    for (NSTextCheckingResult *match in matches) {
        
        if (([match resultType] == NSTextCheckingTypeLink)) {
            [attributedString addAttributes:@{NSForegroundColorAttributeName:[Fav24Colors iosSevenBlue]} range:match.range];
        }
    }
    return attributedString;
}

static CGFloat kPaddingTop = 38.0f;
static CGFloat kPaddingRight = 96.0f;

#define kFont [UIFont systemFontOfSize:15.0]

+ (CGSize)commentSizeForText:(NSString *)text {
    CGSize textSize = [TimeLineUtilities textSizeForText:text];
    CGFloat height = textSize.height+ kPaddingTop;
    
    
    return CGSizeMake(textSize.width, height);
}

+ (CGSize)textSizeForText:(NSString *)text {
    CGRect screenRect = [[UIScreen mainScreen] bounds];
    CGFloat screenWidth = screenRect.size.width;
    
    
    CGFloat width = screenWidth - kPaddingRight;
    CGSize maxSize = CGSizeMake(width, CGFLOAT_MAX);
    CGSize size = [text sizeWithFont:kFont constrainedToSize:maxSize lineBreakMode:NSLineBreakByWordWrapping];
    
    return size;
}


@end
