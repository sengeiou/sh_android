//
//  NSDate+Utils.m
//  iGoles 2012
//
//  Created by Administrador on 11/07/12.
//  Copyright (c) 2012 Fav24. All rights reserved.
//

#import "NSDate+Utils.h"

@implementation NSDate (Utils)

- (NSDate *)toLocalTime {
    NSTimeZone *tz = [NSTimeZone defaultTimeZone];
    NSInteger seconds = [tz secondsFromGMTForDate: self];
    return [NSDate dateWithTimeInterval: seconds sinceDate: self];
}
- (NSDate *)toGlobalTime {
    NSTimeZone *tz = [NSTimeZone defaultTimeZone];
    NSInteger seconds = -[tz secondsFromGMTForDate: self];
    return [NSDate dateWithTimeInterval: seconds sinceDate: self];
}

- (NSString *)getNameOfDay {
    NSDateFormatter *df_in = [[NSDateFormatter alloc] init];
    [df_in setDateFormat:@"cccc"];
    NSString *d = [df_in stringFromDate:self];
    return d;
}


- (NSString *)getNameOfMonth {
    NSDateFormatter *df_in = [[NSDateFormatter alloc] init];
    [df_in setDateFormat:@"MMMM"];
    NSString *d = [df_in stringFromDate:self];
    return d;
}

-(NSString *)getMonthNameOfPreviousMonth:(NSInteger)number {
    
    NSDateComponents *components = [[NSCalendar currentCalendar] components:NSMonthCalendarUnit fromDate:self];
    NSInteger monthNumber = [components month] + number;
    
    if ( monthNumber > 12)              monthNumber -= 12;
    else if ( monthNumber <= 0)         monthNumber += 12;
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    return [[formatter monthSymbols] objectAtIndex:monthNumber-1];
}

- (NSString *)getFormattedDayTime {
    NSDateFormatter *df_in = [[NSDateFormatter alloc] init];
    [df_in setDateFormat:NSLocalizedString(@"ccc HH:mm",nil)];
    NSString *d = [[df_in stringFromDate:self] lowercaseString];
    return d;
}
- (NSString *)getFormattedDate {
    NSDateFormatter *df_in = [[NSDateFormatter alloc] init];
    [df_in setDateFormat:NSLocalizedString(@"dd/MM/YY",nil)];
    NSString *d = [df_in stringFromDate:self];
    return d;
}

- (NSString *)getFormattedShortDate {
    NSDateFormatter *df_in = [[NSDateFormatter alloc] init];
    [df_in setDateFormat:NSLocalizedString(@"dd/MM",nil)];
    NSString *d = [df_in stringFromDate:self];
    return d;
}

- (NSString *)getFullFormattedDate {
    NSDateFormatter *df_in = [[NSDateFormatter alloc] init];
    [df_in setDateFormat:NSLocalizedString(@"dd/MM/YY HH:mm",nil)];
    NSString *d = [df_in stringFromDate:self];
    return d;
}

- (NSString *)getFullFormattedShortDate {
    NSDateFormatter *df_in = [[NSDateFormatter alloc] init];
    [df_in setDateFormat:NSLocalizedString(@"dd/MM HH:mm",nil)];
    NSString *d = [df_in stringFromDate:self];
    return d;
}

- (NSString *)getFormattedHour {
    NSDateFormatter *df_in = [[NSDateFormatter alloc] init];
    [df_in setDateFormat:NSLocalizedString(@"HH:mm",nil)];
    NSString *d = [df_in stringFromDate:self];
    return d;
}

- (NSString *)getFormattedDayMonth {
    NSDateFormatter *df_in = [[NSDateFormatter alloc] init];
    [df_in setDateFormat:NSLocalizedString(@"ccc dd/MM",nil)];
    NSString *d = [[df_in stringFromDate:self] lowercaseString];
    return d;
}

- (NSInteger)daysWithinEraFromDate:(NSDate *) startDate toDate:(NSDate *) endDate {
    NSCalendar *gregorian = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    [gregorian setTimeZone:[NSTimeZone localTimeZone]];
    NSDate *newDate1 = [startDate dateByAddingTimeInterval:[[NSTimeZone localTimeZone] secondsFromGMT]];
    NSDate *newDate2 = [endDate dateByAddingTimeInterval:[[NSTimeZone localTimeZone] secondsFromGMT]];
    
    NSInteger startDay=[gregorian ordinalityOfUnit:NSDayCalendarUnit
                                            inUnit: NSEraCalendarUnit forDate:newDate1];
    NSInteger endDay=[gregorian ordinalityOfUnit:NSDayCalendarUnit
                                          inUnit: NSEraCalendarUnit forDate:newDate2];
    return endDay-startDay;
}

//- (NSString *)getFormattedDateMatch:(BOOL)dateMatchNotConfirmed {
//    if (dateMatchNotConfirmed) return NSLocalizedString(@"Por confirmar", nil);
//    
//    int days = [self daysWithinEraFromDate:[NSDate date] toDate:self];
//
//    if (days == 0)                  return [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"_MatchDetailDateHOY", nil), [self getFormattedHour]];
//    else if (days == 1)             return [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"_MatchDetailDateMañana", nil), [self getFormattedHour]];
//    else if (days < 7 && days > 1)  return [self getFormattedDayTime];
//    else                            return [self getFullFormattedDate];
//}

- (NSString *)getFormattedDateMatch:(BOOL)dateMatchNotConfirmed {
    if (dateMatchNotConfirmed) return NSLocalizedString(@"Por confirmar", nil);
    
    NSString *dateToReturn;
    int days = [self daysWithinEraFromDate:[NSDate date] toDate:self];
    
    if (days == 0)
        dateToReturn = [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"_MatchDetailDateHOY", nil), [self getFormattedHour]];

    else if (days == 1){
        dateToReturn =  [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"_MatchDetailDateMañana", nil), [self getFormattedHour]];
        
        NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit fromDate:[NSDate date]];
        long hour = [components hour];
        long tst = 7;
        if (hour > tst) {
        
            NSString *trimmedString = [dateToReturn substringFromIndex:MAX((int)[dateToReturn length]-5, 0)];
            NSString *newHour;
            if ([trimmedString isEqualToString:@"00:00"]){
                newHour = @"24:00";
                NSString *newDate = [NSString stringWithFormat:@"%@ %@",NSLocalizedString(@"_MatchDetailDateHOY", nil),newHour];
                return newDate;
            }else if ([trimmedString isEqualToString:@"00:15"] ||
                      [trimmedString isEqualToString:@"00:30"] || [trimmedString isEqualToString:@"00:45"]){
                NSString *newDate = [NSString stringWithFormat:@"%@ %@",NSLocalizedString(@"_MatchDetailDateMadrugada", nil),trimmedString];
                return newDate;
                
            }else if ([trimmedString isEqualToString:@"01:00"] || [trimmedString isEqualToString:@"02:00"] ||
                      [trimmedString isEqualToString:@"03:00"] || [trimmedString isEqualToString:@"04:00"]){
                NSString *newDate = [NSString stringWithFormat:@"%@ %@",NSLocalizedString(@"_MatchDetailDateMadrugada", nil),trimmedString];
                return newDate;
            }
        }
    }
    
    else if (days < 7 && days > 1)
        dateToReturn = [self getFormattedDayTime];
    else
        dateToReturn = [self getFullFormattedDate];
    
    NSString *trimmedString=[dateToReturn substringFromIndex:MAX((int)[dateToReturn length]-4, 0)];
    NSString *newHour;
    if ([trimmedString isEqualToString:@"00:00"]) {
        newHour = @"24:00";
    }
    return dateToReturn;
}

- (NSString *)getFormattedDateFinishedMatchForCalendar {
    
    NSDateComponents *matchComponents = [[NSCalendar currentCalendar] components:NSCalendarUnitYear fromDate:self];
    NSInteger matchYear = [matchComponents year];
    
    NSDateComponents *actualComponents = [[NSCalendar currentCalendar] components:NSCalendarUnitYear fromDate:[NSDate date]];
    NSInteger actualYear = [actualComponents year];
    
    if (matchYear < actualYear)
        return [self getFormattedDate];
    else
        return [self getFormattedDayMonth];

}

- (NSString *)getFormattedDateMatchForCalendar:(BOOL)dateMatchNotConfirmed {
    if (dateMatchNotConfirmed) return @"Por confirmar";

    int days = [self daysWithinEraFromDate:[NSDate date] toDate:self];
    
    if (days == 0)                  return [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"_MatchDetailDateHOY", nil), [self getFormattedHour]];
    else if (days == 1)             return [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"_MatchDetailDateMañana", nil), [self getFormattedHour]];
    else if (days < 7 && days > 1)  return [self getFormattedDayTime];
    else                            return [self getFullFormattedShortDate];
}


- (BOOL)isSameDayAsDate:(NSDate *)comparedDate {
    
    if (comparedDate) {
    
        NSCalendar* calendar = [NSCalendar currentCalendar];
        unsigned unitFlags = NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit;
        NSDateComponents* comp1 = [calendar components:unitFlags fromDate:self ];
        NSDateComponents* comp2 = [calendar components:unitFlags fromDate:comparedDate ];
        
        return [comp1 day] == [comp2 day] && [comp1 month] == [comp2 month] && [comp1 year] == [comp2 year];
    }
    return NO;
}



@end
