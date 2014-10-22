//
//  FavRestConsumerHelperTest.m
//
//  Created by Christian Cabarrocas on 12/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>

#import "FavRestConsumerHelper.h"

@interface FavRestConsumerHelperTest : XCTestCase

@property (nonatomic,strong) NSArray *requestorArray;

@end

@implementation FavRestConsumerHelperTest

- (void)setUp {
    [super setUp];
    self.requestorArray = [FavRestConsumerHelper createREQ];
}

- (void)tearDown {
    [super tearDown];
}

- (void)testCreateRequestisComplete {
    XCTAssertEqual(self.requestorArray.count, 5,@"Should be 5 items in requestor array");
}

- (void)testCreateRequestisValid {
    
    XCTAssertEqual([[self.requestorArray objectAtIndex:0] isKindOfClass:[NSNumber class]], true,@"Device must be a NSNUmber");
    XCTAssertEqual([[self.requestorArray objectAtIndex:1] isKindOfClass:[NSNumber class]], true,@"Player must be a NSNUmber");
    XCTAssertEqual([[self.requestorArray objectAtIndex:2] isKindOfClass:[NSNumber class]], true,@"Platform must be a NSNUmber");
    XCTAssertEqual([[self.requestorArray objectAtIndex:3] isKindOfClass:[NSNumber class]], true,@"AppVersion must be a NSNUmber");
    XCTAssertEqual([[self.requestorArray objectAtIndex:4] isKindOfClass:[NSNumber class]], true,@"Epoch must be a NSNUmber");

}

//Prepared when backend accepts sessionTokken
- (void)DISABLED_testCreateRequestisComplete {
    XCTAssertEqual(self.requestorArray.count, 6,@"Should be 6 items in requestor array");
}

- (void)DISABLED_testCreateRequestisValid {
    
    XCTAssertEqual([[self.requestorArray objectAtIndex:0] isKindOfClass:[NSNumber class]], true,@"Device must be a NSNUmber");
    XCTAssertEqual([[self.requestorArray objectAtIndex:1] isKindOfClass:[NSNumber class]], true,@"Player must be a NSNUmber");
    XCTAssertEqual([[self.requestorArray objectAtIndex:2] isKindOfClass:[NSNumber class]], true,@"Platform must be a NSNUmber");
    XCTAssertEqual([[self.requestorArray objectAtIndex:3] isKindOfClass:[NSNumber class]], true,@"AppVersion must be a NSNUmber");
    XCTAssertEqual([[self.requestorArray objectAtIndex:4] isKindOfClass:[NSNumber class]], true,@"Epoch must be a NSNUmber");
    XCTAssertEqual([[self.requestorArray objectAtIndex:5] isKindOfClass:[NSString class]], true,@"Session token must be a NSString");
    
}

@end
