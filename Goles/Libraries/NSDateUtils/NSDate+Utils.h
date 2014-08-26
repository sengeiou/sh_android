//
//  NSDate+Utils.h
//  iGoles 2012
//
//  Created by Administrador on 11/07/12.
//  Copyright (c) 2012 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSDate (Utils)

- (NSDate *)toLocalTime;
- (NSDate *)toGlobalTime;

- (NSString *)getNameOfDay;
- (NSString *)getNameOfMonth;
- (NSString *)getMonthNameOfPreviousMonth:(NSInteger)number;
- (NSString *)getFormattedDayTime;
- (NSString *)getFormattedDate;
- (NSString *)getFullFormattedDate;
- (NSString *)getFormattedDateMatch:(BOOL)dateMatchNotConfirmed;
- (NSString *)getFormattedDateFinishedMatchForCalendar;
- (NSString *)getFormattedDateMatchForCalendar:(BOOL)dateMatchNotConfirmed;
- (NSString *)getFormattedShortDate;
- (NSString *)getFormattedDayMonth;
- (NSString *)getFullFormattedShortDate;

- (BOOL)isSameDayAsDate:(NSDate *)comparedDate;

@end
