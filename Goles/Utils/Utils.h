//
//  Utils.h
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2012 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AppAdvice.h"

@interface Utils : NSObject

+(id)getValueFromUserDefaultsFromKey:(NSString *)key;
+(void)setValueToUserDefaults:(id)value ToKey:(NSString *)key;

+ (UIView *)setNavigationBarTitle:(NSString *)title andSubtitle:(NSString *)subtitle forMaximumLenght:(NSNumber *)lenghtView;
+ (UIView *)createTableFooterWithText:(NSString *)footerText;
+ (UIView *)createTableHeaderWithText:(NSString *)headerText andRightDetail:(NSString *)detailText withPenalties:(BOOL)penalties;
+ (NSNumber *)getDeviceCurrentLanguageCode;

+ (UIView *)createTableHeaderView:(NSString *)headerText
                         withLink:(NSString *)headerLink
                        andMethod:(SEL)onClickMethod
                     withDelegate:(id)delegate;

+ (UIView *)createTableHeaderView:(NSString *)headerText
                         withLink:(NSString *)headerLink
                 inBottomPosition:(BOOL)isInbottomPosition
                        andMethod:(SEL)onClickMethod
                     withDelegate:(id)delegate;


+ (UIView *)createPickerFooterView:(NSString *)headerText;


+ (UIView *)createTableHeaderView:(NSString *)headerText
                    withFirstLink:(NSString *)linkOne
                    andSecondLink:(NSString *)linkTwo
                 inBottomPosition:(BOOL)isInbottomPosition
                   andFirstMethod:(SEL)methodOne
                  andSecondMethod:(SEL)methodTwo
                     withDelegate:(id)delegate;

+ (UIView *)createTableFooterView:(NSString *)headerText
                    withFirstLink:(NSString *)linkOne
                    andSecondLink:(NSString *)linkTwo
                 inBottomPosition:(BOOL)isInbottomPosition
                   andFirstMethod:(SEL)methodOne
                  andSecondMethod:(SEL)methodTwo
                     withDelegate:(id)delegate;


+ (BOOL) NSStringIsValidEmail:(NSString *)checkString;

+ (int)getIphone: (CGFloat) height;

+(NSMutableAttributedString *) formatTitle:(NSString *)text;

+ (BOOL)addSkipBackupAttributeToItemAtURL:(NSURL *)URL;

@end
