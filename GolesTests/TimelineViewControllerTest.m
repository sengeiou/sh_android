//
//  TimelineViewController.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 02/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>
#import "Shot.h"
#import "ShotManager.h"
#import "TimeLineViewController.h"
#import "CoreDataManager.h"
#import "TimeLineHelper.h"
#import "UserManager.h"
#import <math.h>

@interface TimelineViewControllerTest : XCTestCase

@property(nonatomic, strong) TimeLineViewController *tlcv;

@end

@implementation TimelineViewControllerTest

- (void)setUp {
    [super setUp];
 
    self.tlcv = [[TimeLineViewController alloc] init];
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
    
    
    NSString *string1 = @"Texte repetit";
    NSString *string2 = @"Texte no repetit";
    
    XCTAssertTrue([self.tlcv controlRepeatedShot:string1]);
    XCTAssertFalse([self.tlcv controlRepeatedShot:string2]);
}

-(void)testisShotMessageAlreadyInList{
    
    XCTAssertTrue( [self.tlcv isShotMessageAlreadyInList: [TimeLineHelper getThreeShots] withText:@"Texte repetit"]);

}

-(void)testCharactersCounterLeft {
    
    XCTAssertEqual ([self.tlcv countCharacters:2],138);
}



@end
