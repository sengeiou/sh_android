//
//  FavRestConsumerHelperTest.m
//  Goles
//
//  Created by Christian Cabarrocas on 12/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>

#import "FavRestConsumerHelper.h"

@interface FavRestConsumerHelperTest : XCTestCase

@end

@implementation FavRestConsumerHelperTest

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testCreateRequest {
    
    NSArray *requestorArray = [FavRestConsumerHelper createREQ];
    XCTAssertEqual(requestorArray.count, 5);
    
    XCTAssertEqual([[requestorArray objectAtIndex:0] isKindOfClass:[NSNumber class]], true);
    XCTAssertEqual([[requestorArray objectAtIndex:1] isKindOfClass:[NSNumber class]], true);
    XCTAssertEqual([[requestorArray objectAtIndex:2] isKindOfClass:[NSNumber class]], true);
    XCTAssertEqual([[requestorArray objectAtIndex:3] isKindOfClass:[NSNumber class]], true);
    XCTAssertEqual([[requestorArray objectAtIndex:4] isKindOfClass:[NSNumber class]], true);
}


@end
