//
//  InfoUtilities.h
//  Shootr
//
//  Created by Maria Teresa Bañuls on 29/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Match.h"

@interface InfoUtilities : NSObject

+(UIView *) createHeaderViewWithFrame:(CGRect) frame andMatch:(Match *) match;

@end
