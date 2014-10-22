//
//  CreateShotViewTest.m
//  Shootr
//
//  Created by Christian Cabarrocas on 22/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>
#import "CreateShotView.h"
#import "WritingText.h"
#import "TimeLineViewController.h"

#define HC_SHORTHAND
#import <OCHamcrestIOS/OCHamcrestIOS.h>
#define MOCKITO_SHORTHAND
#import <OCMockitoIOS/OCMockitoIOS.h>


@interface CreateShotViewTest : XCTestCase

@property (nonatomic,strong) CreateShotView *createSV;
@property (nonatomic,strong) WritingText *writingText;

@end

@implementation CreateShotViewTest

- (void)setUp {
    [super setUp];
    self.writingText = mock([WritingText class]);
    self.createSV = [[CreateShotView alloc] init];
    
//    self.createSV.viewToDisableTextField = [[UIView alloc] init];
}

- (void)tearDown {

    [super tearDown];
}

//- (void)testWhenSendShotTextFieldIsVisible {
//    
//    [self.createSV sendShot];
//    XCTAssertNotNil(self.createSV.viewToDisableTextField);
////    XCTAssertFalse(self.createSV.viewToDisableTextField.hidden);
//}

- (void)testWhenSendShotChangePropertiesTExtView {
    
    self.createSV.writingTextBox = self.writingText;
    [self.createSV sendShot];
    [verify(self.writingText) setWritingTextViewWhenSendShot];
}

//- (void)testWhenSendShotCharactersLEftIsInvisibleIsVisible {
//    
//    [self.createSV sendShot];
//    XCTAssertNotNil(self.createSV.charactersLeft);
////    XCTAssertTrue(self.createSV.charactersLeft.hidden);
//}

@end
