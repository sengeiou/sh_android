//
//  GolesTests.m
//  GolesTests
//
//  Created by Christian Cabarrocas on 11/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "Match.h"
#import "FavRestConsumer.h"

@interface GolesTests : XCTestCase

@end

@implementation GolesTests

- (void)setUp
{
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown
{
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

#pragma mark - helper methods

- (FavRestConsumer *)createUniqueInstance {
    
    return [[FavRestConsumer alloc] init];
}

- (FavRestConsumer *)getSharedInstance {
    
    return [FavRestConsumer sharedInstance];
}

#pragma mark - tests

- (void)testSingletonSharedInstanceCreated {
    
    XCTAssertNotNil([self getSharedInstance]);
}

- (void)testSingletonUniqueInstanceCreated {
    
    XCTAssertNotNil([self createUniqueInstance]);
    
}

- (void)testSingletonReturnsSameSharedInstanceTwice {
    
    FavRestConsumer *s1 = [self getSharedInstance];
    XCTAssertEqual(s1, [self getSharedInstance]);
    
}

- (void)testSingletonSharedInstanceSeparateFromUniqueInstance {
    
    FavRestConsumer *s1 = [self getSharedInstance];
    XCTAssertNotEqual(s1, [self createUniqueInstance]);
}

- (void)testSingletonReturnsSeparateUniqueInstances {
    
    FavRestConsumer *s1 = [self createUniqueInstance];
    XCTAssertNotEqual(s1, [self createUniqueInstance]);
}


@end
