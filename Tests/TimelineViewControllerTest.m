//
//  TimelineViewController.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 02/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>
#import "TimeLineHelper.h"
#import "CreateShotView.h"
#import "ManagerHelper.h"

@interface TimelineViewControllerTest : XCTestCase

@property(nonatomic, strong) CreateShotView *tlcv;

@end

@implementation TimelineViewControllerTest

- (void)setUp {
    [super setUp];
 
    self.tlcv = [[CreateShotView alloc] init];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

-(void)testWhenNoShotsAvailableNewShotIsNotRepeated{
    
    [TimeLineHelper emptyShotsDataBase];
    
    XCTAssertEqual([self.tlcv controlRepeatedShot:@"Hola k ase??????"], NO);
}


-(void)testWhenShotsAvailableNewShotIsNotRepeated{
    
    [TimeLineHelper emptyShotsDataBase];
    [TimeLineHelper populateThreeShots];
    [ManagerHelper emptyUsersDataBase];
    [ManagerHelper populateThreeUsers];
    
    
    NSString *string1 = @"Texte repetit";
    NSString *string2 = @"Texte no repetit";
    
    //XCTAssertTrue([self.tlcv controlRepeatedShot:string1]);
    XCTAssertFalse([self.tlcv controlRepeatedShot:string2]);
}

-(void)testisShotMessageAlreadyInList{
    
    XCTAssertTrue( [self.tlcv isShotMessageAlreadyInList: [TimeLineHelper getThreeShots] withText:@"Texte repetit"]);

}

-(void)testCharactersCounterLeft {

    XCTAssertEqualObjects([self.tlcv countCharacters:2],@"138");
    XCTAssertEqualObjects([self.tlcv countCharacters:0],@"140");
    XCTAssertEqualObjects([self.tlcv countCharacters:140],@"0");
    XCTAssertEqualObjects([self.tlcv countCharacters:150],@"0");
}



@end
