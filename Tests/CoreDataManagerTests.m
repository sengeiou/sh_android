//
//  CoreDataManagerTests.m
//  Shootr
//
//  Created by Christian Cabarrocas on 25/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>
#import "CoreDataManager.h"
#import "TimeLineHelper.h"
#import "ManagerHelper.h"

@interface CoreDataManagerTests : XCTestCase

@end

@implementation CoreDataManagerTests

- (void)setUp {
    [super setUp];

    [TimeLineHelper emptyShotsDataBase];
    [TimeLineHelper populateThreeShots];
    [ManagerHelper emptyUsersDataBase];
    [ManagerHelper populateThreeUsers];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

//Fetch 1 item from insertContext
- (void)testToFetchOneShotFromCoreDataBasedOnID {
    
    id fetchedEntity = [[CoreDataManager singleton] getEntityInInsertContext:[Shot class] withId:1];
    
    XCTAssertNotNil(fetchedEntity,@"We must have an entity");
}

//Fetch 1 item from basicContext
- (void)testToFetchOneUserFromCoreDataBasedOnID {
    
    id fetchedEntity = [[CoreDataManager singleton] getEntity:[User class] withId:1];
    
    XCTAssertNotNil(fetchedEntity,@"We must have an entity");
}

//Fetch All items in basicContext
- (void)testThatAllObjectsAreReturnedOnFetch {
    
    NSArray *fetchResult = [[CoreDataManager singleton] getAllEntities:[User class]];
    XCTAssertEqual(fetchResult.count, 3);
}

//Fetch All items in basicContext
- (void)testThatAllObjectsAreReturnedAndOrderedOnFetch {
    
    //Order by id
    NSArray *fetchResultID = [[CoreDataManager singleton] getAllEntities:[User class] orderedByKey:kJSON_ID_USER];
    XCTAssertEqual(fetchResultID.count, 3);
    XCTAssertEqualObjects([[fetchResultID objectAtIndex:0] idUser], @1);
    XCTAssertEqualObjects([[fetchResultID objectAtIndex:1] idUser], @2);
    XCTAssertEqualObjects([[fetchResultID objectAtIndex:2] idUser], @3);
    
    //Order by name
    NSArray *fetchResultName = [[CoreDataManager singleton] getAllEntities:[User class] orderedByKey:kJSON_NAME];
    XCTAssertEqual(fetchResultName.count, 3);
    XCTAssertEqualObjects([[fetchResultName objectAtIndex:0] name], @"userOne");
    XCTAssertEqualObjects([[fetchResultName objectAtIndex:1] name], @"userThree");
    XCTAssertEqualObjects([[fetchResultName objectAtIndex:2] name], @"userTwo");

}
@end
