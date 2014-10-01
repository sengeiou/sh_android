//
//  NSString+CleanLinks.m
//  Goles
//
//  Created by Christian Cabarrocas on 29/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "NSString+CleanLinks.h"

@implementation NSString (CleanLinks)

- (NSString *)cleanStringfromLinks:(NSString *)string {
    
    if ([string containsString:@"http://"])
        return [string stringByReplacingOccurrencesOfString:@"http://" withString:@""];
    else if ([string containsString:@"https://"])
        return [string stringByReplacingOccurrencesOfString:@"https://" withString:@""];
    
    return string;
}

@end
