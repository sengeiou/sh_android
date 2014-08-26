//
//  CustomCellCalendar.m
//  iGoles
//
//  Created by Marçal Albert Castellví on 19/07/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "CustomCellCalendar.h"
#import "Fav24Colors.h"
#import "NSDate+Utils.h"

@interface CustomCellCalendar ()

@property (nonatomic,weak) IBOutlet UILabel *mTVListLabel;
@property (nonatomic,weak) IBOutlet UILabel *labelDate;
@property (nonatomic,weak) IBOutlet UILabel *lScore;
@property (nonatomic,weak) IBOutlet UILabel *labelTeamLocal;
@property (nonatomic,weak) IBOutlet UILabel *labelTeamVisitor;
@property (nonatomic,strong) NSString *identifier;

@end

@implementation CustomCellCalendar

- (BOOL)checkForOpenTV:(NSString *)tvToCheck {
    
    NSString *tv1;
    NSString *tv2;
    
    if ([tvToCheck rangeOfString:@","].location != NSNotFound) {
        NSArray *tempArray = [tvToCheck componentsSeparatedByString:@","];
        tv1 = [[tempArray objectAtIndex:0] stringByReplacingOccurrencesOfString:@" " withString:@""];
        tv2 = [[tempArray objectAtIndex:1] stringByReplacingOccurrencesOfString:@" " withString:@""];
    }else {
        tv1 = tvToCheck;
        tv2 = @"";
    }
    
    NSString *openTVs = @"TVG TVC Canaria TVCanaria Esport Esport3 TPA A3 Teledeporte Tele5 TdP SFC Nitro Marca MarcaTV TV3 laSexta TVE1 TVE La1 Antena ETB Energy Cuatro +QueTele";
    if (([openTVs rangeOfString:tv1].location != NSNotFound) || ([openTVs rangeOfString:tv2].location != NSNotFound)) {
        return YES;
    }
    
    return NO;
}

//-(void) configCellWithMatchData:(DMMatch *)matchData {
//    
//    self.mTVListLabel.text = nil;
//    self.labelDate.text = nil;
//    self.lScore.text = nil;
//    self.labelTeamLocal.text = nil;
//    self.labelTeamVisitor.text = nil;
//    
//    CGFloat localTeamNameLabelMaxWidth = 120.0f;
//    CGFloat visitingTeamNameLabelMaxWidth = 120.0f;
//    
//    // Config Teams name labels
//
//    self.labelTeamLocal.text = [matchData teamLocal];
//    self.labelTeamVisitor.text = [matchData teamVisitor];
//    
//
//    [self.labelTeamLocal sizeToFit];
//    if (self.labelTeamLocal.frame.size.width > localTeamNameLabelMaxWidth)
//        [[self labelTeamLocal] setFrame:CGRectMake(self.labelTeamLocal.frame.origin.x,
//                                                        self.labelTeamLocal.frame.origin.y,
//                                                        localTeamNameLabelMaxWidth,
//                                                        self.labelTeamLocal.frame.size.height)];
//    
//    [self.labelTeamVisitor sizeToFit];
//    if (self.labelTeamVisitor.frame.size.width > visitingTeamNameLabelMaxWidth)
//        self.labelTeamVisitor.frame = CGRectMake(self.labelTeamVisitor.frame.origin.x,
//                                                          self.labelTeamVisitor.frame.origin.y,
//                                                          visitingTeamNameLabelMaxWidth,
//                                                          self.labelTeamVisitor.frame.size.height);
//    
//    // Config TV and Score info Labels
//    if (matchData.matchState == kMatchStateNotStarted)
//    {
//        if (matchData.listTV && ![matchData.listTV isKindOfClass:[NSNull class]] && [matchData.listTV length] > 0) {
//            self.mTVListLabel.textColor = [UIColor whiteColor];
//            self.mTVListLabel.text = [matchData listTV];
//            [self.mTVListLabel resizeWidthToFitContentWithMaxWidth:110 withMargin:15];
//            [self.mTVListLabel alignHorizontallyWithView:self.labelDate from:ALIGN_RIGHT_EDGE];
//            [self.mTVListLabel.layer setCornerRadius:3.5f];
//            
//            UIColor *backgroundColor = [Fav24Colors iosSevenBlue];
//            if ([self checkForOpenTV:matchData.listTV])
//                backgroundColor = [Fav24Colors iosSevenOrange];
//                
//            if ([[matchData.listTV lowercaseString] rangeOfString:NSLocalizedString(@"confirmar",nil)].location != NSNotFound ||
//                [[matchData.listTV lowercaseString] rangeOfString:NSLocalizedString(@"emisión",nil)].location != NSNotFound)
//            {
//                backgroundColor = [Fav24Colors noTVLabel];
//                self.mTVListLabel.textColor = [UIColor whiteColor];
//            }
//            [[self mTVListLabel] setBackgroundColor:backgroundColor];
//            [[self mTVListLabel] setHidden:NO];
//        }
//    }
//    else if (matchData.matchState != kMatchStateSuspended )
//    {
//        self.lScore.text = [matchData score];
//        [self.lScore resizeWidthToFitContentWithMaxWidth:67];
//        [[self mTVListLabel] setHidden:YES];
//    }
//
//    
//    // Config match minute Label
//    
//    self.labelDate.text = [NSString stringWithFormat:NSLocalizedString(@"%@'",nil),matchData.dateMatch];
//    if (matchData.matchState == kMatchStateStarted) {
//        self.labelDate.text = [matchData.dateMatch getFormattedDate];
//        //[self.lScore alignHorizontallyWithView:self.labelDate from:ALIGN_RIGHT_EDGE];
//        }
//    
//    else if (matchData.matchState == kMatchStateSuspended) {
//        [[self labelDate] setText:NSLocalizedString(@"_fixtureMatchesListMatchSuspended", nil)];
//        [[self mTVListLabel] setHidden:YES];
//    }
//    else if (matchData.matchState == kMatchStateFinished) {
//        [[self labelDate] setText:NSLocalizedString(@"_fixtureMatchesListMatchEnded", nil)];
//    } else {
//        [[self labelDate] setText:[matchData.dateMatch getFormattedDateMatch:matchData.dateMatchNotConfirmed]];
//    }
//   
//    if (matchData.matchState == kMatchStateFinished){
//        self.lScore.textColor = [UIColor lightGrayColor];
//    }
//    else{
//        self.lScore.textColor = [Fav24Colors iosSevenBlue];
//    }
//    [self.lScore alignHorizontallyWithView:self.labelDate from:ALIGN_RIGHT_EDGE];
//
//    
//    [[self layer] setShouldRasterize:YES];
//    [[self layer] setRasterizationScale:[[UIScreen mainScreen] scale]];
//}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        // Initialization code
        self.identifier = reuseIdentifier;
    }
	
    return self;
}

- (NSString *)reuseIdentifier {
    return self.identifier;
}

@end
