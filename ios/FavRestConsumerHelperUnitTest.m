//
//  FavRestConsumerHelperUnitTest.m
//  Shootr
//
//  Created by Christian Cabarrocas on 28/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>
#import "FavRestConsumerHelper.h"
#import "UserManager.h"

#define HC_SHORTHAND
#import <OCHamcrestIOS/OCHamcrestIOS.h>
#define MOCKITO_SHORTHAND
#import <OCMockitoIOS/OCMockitoIOS.h>


@interface FavRestConsumerHelperUnitTest : XCTestCase

@property (nonatomic,strong) FavRestConsumerHelper *favRestConsumer;
@property (nonatomic,strong) UserManager *userManager;

@end

@implementation FavRestConsumerHelperUnitTest

- (void)setUp {
    [super setUp];
    self.favRestConsumer = [[FavRestConsumerHelper alloc]init];

    self.userManager = mock([UserManager class]);

    self.favRestConsumer.userManager = self.userManager;
    
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testWhenIdPlayerIsANumberItIsInserted {
    // This is an example of a functional test case.
    
    [given([self.userManager getUserId]) willReturn:@1];
    
    NSArray *requestorArray = [self.favRestConsumer createREQ];
    
    XCTAssertEqual(requestorArray.count, 5,@"Should be 5 items in requestor array");

    
    XCTAssertEqual([[requestorArray objectAtIndex:0] isKindOfClass:[NSNumber class]], true,@"Device must be a NSNUmber");
    XCTAssertEqual([[requestorArray objectAtIndex:1] isKindOfClass:[NSNumber class]], true,@"Player must be a NSNUmber");
    XCTAssertEqual([[requestorArray objectAtIndex:2] isKindOfClass:[NSNumber class]], true,@"Platform must be a NSNUmber");
    XCTAssertEqual([[requestorArray objectAtIndex:3] isKindOfClass:[NSNumber class]], true,@"AppVersion must be a NSNUmber");
    XCTAssertEqual([[requestorArray objectAtIndex:4] isKindOfClass:[NSNumber class]], true,@"Epoch must be a NSNUmber");
}


- (void)testWhenIdPlayerIsANullItIsInserted {
    // This is an example of a functional test case.
    
    [given([self.userManager getUserId]) willReturn:nil];
    
    NSArray *requestorArray = [self.favRestConsumer createREQ];
    
    XCTAssertEqual(requestorArray.count, 5,@"Should be 5 items in requestor array");
    
    
    XCTAssertEqual([[requestorArray objectAtIndex:0] isKindOfClass:[NSNumber class]], true,@"Device must be a NSNUmber");
    XCTAssertEqual([[requestorArray objectAtIndex:1] isKindOfClass:[NSNull class]], true,@"Player must be a NSNull");
    XCTAssertEqual([[requestorArray objectAtIndex:2] isKindOfClass:[NSNumber class]], true,@"Platform must be a NSNUmber");
    XCTAssertEqual([[requestorArray objectAtIndex:3] isKindOfClass:[NSNumber class]], true,@"AppVersion must be a NSNUmber");
    XCTAssertEqual([[requestorArray objectAtIndex:4] isKindOfClass:[NSNumber class]], true,@"Epoch must be a NSNUmber");
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
