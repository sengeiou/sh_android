//
//  GolesTests.m
//  GolesTests
//
//  Created by Christian Cabarrocas on 11/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "Match.h"
#import "SyncManager.h"
#import "Services.pch"
#import "CoreDataManager.h"

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

- (SyncManager *)createUniqueInstance {
    
    return [[SyncManager alloc] init];
}

- (SyncManager *)getSharedInstance {
    
    return [SyncManager sharedInstance];
}

#pragma mark - tests


#pragma mark - Conection

- (void)testConection {
    
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:SERVERTIME]];
    
    NSURLResponse *response = nil;
    
    NSError *err = nil;
    
    NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&err];
    
    XCTAssertNotNil(response, @"Deberíamos tener respuesta");
    
    XCTAssertNil(err, @"No deberíamos tener error");
    
    XCTAssertTrue([data length] > 0, @"Deberíamos tener datos");
}

#pragma mark - SingletonForSyncmanager

- (void)testSingletonSharedInstanceCreated {
    
    XCTAssertNotNil([self getSharedInstance]);
}

- (void)testSingletonUniqueInstanceCreated {
    
    XCTAssertNotNil([self createUniqueInstance]);
    
}

- (void)testSingletonReturnsSameSharedInstanceTwice {
    
    SyncManager *s1 = [self getSharedInstance];
    XCTAssertEqual(s1, [self getSharedInstance]);
    
}

- (void)testSingletonSharedInstanceSeparateFromUniqueInstance {
    
    SyncManager *s1 = [self getSharedInstance];
    XCTAssertNotEqual(s1, [self createUniqueInstance]);
}

- (void)testSingletonReturnsSeparateUniqueInstances {
    
    SyncManager *s1 = [self createUniqueInstance];
    XCTAssertNotEqual(s1, [self createUniqueInstance]);
}

#pragma mark - Get Entities for Syncronization

-(void) testGetSyncForEntity{
    
    NSArray *entitiesToSynchro = @[K_COREDATA_APPADVICE, K_COREDATA_DEVICE, K_COREDATA_MATCH, K_COREDATA_MESSAGE, K_COREDATA_PLAYER, K_COREDATA_SML, K_COREDATA_TEAM];
    
    NSPredicate *predicate;
    
    
    for (int i = 0; i < entitiesToSynchro.count; i++) {
        predicate = [NSPredicate predicateWithFormat:@"%K = %@ OR %K = %@",k_SYNC_NAME_ENTITY,entitiesToSynchro[i],k_SYNC_ALIAS,entitiesToSynchro[i]];
    }
    
    NSArray *syncEntityArray = [[CoreDataManager singleton] getAllEntities:[SyncControl class] withPredicate:predicate];
   
    XCTAssertNotNil(syncEntityArray);

}


@end
