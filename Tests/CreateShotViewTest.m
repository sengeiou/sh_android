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


//#define HC_SHORTHAND
//#import <OCHamcrestIOS/OCHamcrestIOS.h>
//#define MOCKITO_SHORTHAND
//#import <OCMockitoIOS/OCMockitoIOS.h>




@interface CreateShotViewTest : XCTestCase

@property (nonatomic,strong) CreateShotView *createSV;
@property (nonatomic,strong) WritingText *writingText;

@end

@implementation CreateShotViewTest

- (void)setUp {
    [super setUp];
//    self.writingText = mock([WritingText class]);
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testWhenSendShotTextFieldIsVisible {
    
    [self.createSV sendShot];
    XCTAssertFalse(self.createSV.viewToDisableTextField.hidden);
}

//- (void)testWhenSendShotChangePropertiesTExtView {
//    
//    self.createSV.writingTextBox = self.writingText;
//    [self.createSV sendShot];
//  //  [verify(self.writingText) setWritingTextViewWhenSendShot];
//}

@end
