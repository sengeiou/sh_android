//
//  TimeLineHelper.h
//  Shootr
//
//  Created by Maria Teresa Bañuls on 02/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Shot.h"

@interface TimeLineHelper : NSObject

+(void)populateThreeShots;
+(NSArray *) getThreeShots;
+(void) emptyShotsDataBase;



@end
