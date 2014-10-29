//
//  PerformanceTests.m
//
//  Created by Christian Cabarrocas on 12/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>
#import "UserManager.h"
#import "ShotManager.h"

@interface PerformanceTests : XCTestCase

@end

@implementation PerformanceTests

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testGetActiveUsersIDs {

    [self measureBlock:^{
        [[UserManager singleton] getActiveUsersIDs];
    }];
}

- (void)testGetShotsForTimeLine {
    
    [self measureBlock:^{
        [[ShotManager singleton] getShotsForTimeLine];
    }];
}

@end
