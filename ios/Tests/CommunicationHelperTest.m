//
//  CommunicationHelperTest.m
//  Shootr
//
//  Created by Christian Cabarrocas on 28/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>
#import "CommunicationHelper.h"

@interface CommunicationHelperTest : XCTestCase

@property (nonatomic, strong) CommunicationHelper *communicationHelper;

@end

@implementation CommunicationHelperTest

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
    
    self.communicationHelper = [CommunicationHelper singleton];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testPostIncorrectData {
    
    XCTestExpectation *serverResponding = [self expectationWithDescription:@"Waiting for server response"];
    
    [self.communicationHelper postRequest:@{} onCompletion:^(NSDictionary *response, NSError *error) {
        XCTAssertNotEqual([[response objectForKey:@"status"] objectForKey:@"message"], @"OK");
        [serverResponding fulfill];
    }];
    
    [self waitForExpectationsWithTimeout:10 handler:^(NSError *error) {
       if (error)
           XCTFail(@"Timeout from server");
    }];
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
