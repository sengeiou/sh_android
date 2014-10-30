//
//  ViewNotShots.m
//  Shootr
//
//  Created by bamate on 20/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ViewNotShots.h"

@interface ViewNotShots ()


@property (weak, nonatomic) IBOutlet UILabel *lblNoShots;
@property (weak, nonatomic) IBOutlet UILabel *lblShare;

@end


@implementation ViewNotShots



#pragma mark - INIT
//------------------------------------------------------------------------------
- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    if ((self = [super initWithCoder:aDecoder])) {
        [self textLocalizableLabels];
    }
    return self;
}

#pragma mark - Localizable Strings
//------------------------------------------------------------------------------
-(void)textLocalizableLabels{
    
    [self.startShootingFirstTime setTitle:NSLocalizedString(@"Start Shooting", nil) forState:UIControlStateNormal];
    self.lblNoShots.text =  NSLocalizedString(@"No Shots", nil);
    self.lblShare.text = NSLocalizedString (@"Share with friends about football.", nil);
}

//------------------------------------------------------------------------------
- (void)addTargetSendShot:(id)target action:(SEL)action {
    
    [self.startShootingFirstTime addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
}

#pragma mark - PUBLIC METHODS
//------------------------------------------------------------------------------
- (void)setNoShotsViewInvisible {
    self.hidden = YES;
}

//------------------------------------------------------------------------------
- (void)setNoShotsViewVisible {
    self.hidden = NO;
}

@end
