//
//  Conection.h
//  Goles
//
//  Created by Maria Teresa Bañuls on 16/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Conection : NSObject <UIAlertViewDelegate>

@property (nonatomic) int timeToCheck;
@property (nonatomic) int   retryCounter;
@property (nonatomic,strong) NSDate *requestDate;
@property (nonatomic, assign) BOOL isConection;

- (void)getServerTime;

+ (Conection *)sharedInstance;

@end
