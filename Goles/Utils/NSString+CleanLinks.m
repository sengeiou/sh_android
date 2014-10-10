//
//  NSString+CleanLinks.m
//
//  Created by Christian Cabarrocas on 29/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "NSString+CleanLinks.h"

@implementation NSString (CleanLinks)

- (NSString *)cleanStringfromLinks:(NSString *)string {
    
    
    if ([string rangeOfString:@"http://"].location != NSNotFound)
        return [string stringByReplacingOccurrencesOfString:@"http://" withString:@""];
    else if ( [string rangeOfString:@"https://"].location != NSNotFound)
        return [string stringByReplacingOccurrencesOfString:@"https://" withString:@""];
    
    return string;
}

@end
