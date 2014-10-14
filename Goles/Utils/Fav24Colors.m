//
//  Fav24Colors.m
//
//  Created by Christian Cabarrocas on 10/09/14.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "Fav24Colors.h"

@implementation Fav24Colors

+(UIColor *)tableViewHeaderTextColor {
    return [UIColor colorWithRed:0.43137254901961f green:0.43137254901961f blue:0.43137254901961f alpha:1.0];   //RGB(110,110,110)
}

+(UIColor *)tableViewBackgroundColor {
    return [UIColor colorWithRed:239/255.0f green:239/255.0f blue:244/255.0f alpha:1.0];                      //RGB(236,236,236)
}

+(UIColor *)iosSevenBlue {
    return [UIColor colorWithRed:0.0f green:122/255.0f blue:255/255.0f alpha:1.0];
}

+(UIColor *)iosSevenOrange{
    return [UIColor colorWithRed:1.0 green:0.5843137254902 blue:0.0 alpha:1.0];                 //RGB(255,149,0)
}

+(UIColor *)iosSevenRed {
    return [UIColor colorWithRed:1.0 green:0.23137254901961 blue:0.18823529411765 alpha:1.0];   //RGB(255,59,48)
}

+(UIColor *)noTVLabel {
    return [UIColor lightGrayColor];

}

+(UIColor *)cellLabelGray {
    return [UIColor colorWithRed:143.0f/255.0f green:143.0f/255.0f blue:143.0f/255.0f alpha:1];
}

+(UIColor *)teamsNotClassified {
    return [UIColor colorWithRed:180.0f/255.0f green:180.0f/255.0f blue:180.0f/255.0f alpha:1.0];
}

+(UIColor *)tableFooters {
    return [UIColor colorWithRed:109.0f/255.0f green:109.0f/255.0f blue:114.0f/255.0f alpha:1.0];
}

+(UIColor *)iosSevenGray {
    return [UIColor colorWithRed:196.0f/255.0f green:196.0f/255.0f blue:196.0f/255.0f alpha:1.0];
}

+(UIColor *)iosNavigationBar{ 
    return [UIColor colorWithRed:247.0f/255.0f green:247.0f/255.0f blue:247.0f/255.0f alpha:1.0];
}

+(UIColor *)backgroundTextViewSendShot{
    return [UIColor colorWithRed:230.0/255.0 green:230.0/255.0 blue:230.0/255.0 alpha:1];
}

+(UIColor *)textTextViewSendShot{
    return [UIColor colorWithRed:137.0/255.0 green:137.0/255.0 blue:137.0/255.0 alpha:1];
}

+(UIColor *)textWhatsUpViewSendShot{
    return [UIColor colorWithRed:190.0/255.0 green:190.0/255.0 blue:192.0/255.0 alpha:1];
}

@end
