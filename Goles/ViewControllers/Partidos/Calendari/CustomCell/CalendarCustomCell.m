//
//  CalendarCustomCell.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 20/03/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "CalendarCustomCell.h"
#import "Fav24Colors.h"
#import "NSDate+Utils.h"
#import "Match.h"

@interface CalendarCustomCell ()

@property (nonatomic,weak) IBOutlet UILabel         *labeltournamentName;
@property (nonatomic,weak) IBOutlet UILabel         *labelDate;
@property (nonatomic,weak) IBOutlet UILabel         *labelteamNames;

@property (nonatomic,strong) NSString *identifier;

@end

@implementation CalendarCustomCell


//------------------------------------------------------------------------------
- (void)configCellWithData:(NSDictionary *)matchData forLocalTeam:(BOOL)isLocal {

    self.labelDate.text = nil;

    self.labeltournamentName.text = [NSString stringWithFormat:@"%@ %@",[matchData objectForKey:@"tournamentName"],[matchData objectForKey:@"roundDescription"]];

    NSString *score = [NSString stringWithFormat:@"%@-%@",[matchData objectForKey:@"scoreLocal"],[matchData objectForKey:@"scoreVisitor"]];
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:[[matchData objectForKey:@"dateMatch"] doubleValue]/1000.0];

    //Local Name
    NSString *localName;
    if (isLocal && ![[matchData objectForKey:@"localNameTiny"] isKindOfClass:[NSNull class]])
        localName = [matchData objectForKey:@"localNameTiny"];
    else
        localName = [matchData objectForKey:@"localNameShort"];
    
    //Visitor Name
    NSString *visitorName;
    if (!isLocal && ![[matchData objectForKey:@"visitorNameTiny"] isKindOfClass:[NSNull class]])
        visitorName = [matchData objectForKey:@"visitorNameTiny"];
    else
        visitorName = [matchData objectForKey:@"visitorNameShort"];

    //Team Names
    NSString *teamNames = [NSString stringWithFormat:@"%@ - %@",localName,visitorName];
    
    //To change the color of the rival team
    NSMutableAttributedString *teamNamesAtr = [[NSMutableAttributedString alloc] initWithString:teamNames];
    
    NSRange range;
    
    if (isLocal)
        range = [teamNames rangeOfString:visitorName options:NSCaseInsensitiveSearch];
    else
        range = [teamNames rangeOfString:localName options:NSCaseInsensitiveSearch];
    
    
    [teamNamesAtr addAttribute:NSForegroundColorAttributeName value:[UIColor orangeColor] range:range];

    //Label's logic
    if ([[matchData objectForKey:@"matchState"] integerValue] == kCoreDataMatchStateStarted) {
        self.labelDate.text = [date getFormattedDayTime];
        self.labelteamNames.textColor = [Fav24Colors iosSevenBlue];
        self.labelteamNames.text = [NSString stringWithFormat:@"%@ (%@)",teamNames,score];
        
    }else if ([[matchData objectForKey:@"matchState"] integerValue] == kCoreDataMatchStateFinished){
        self.labelDate.text = [date getFormattedDateFinishedMatchForCalendar];
        self.labelteamNames.textColor = [UIColor lightGrayColor];
        self.labeltournamentName.textColor = [UIColor lightGrayColor];
        self.labelteamNames.text = [NSString stringWithFormat:@"%@ (%@)",teamNames,score];

    }else if ([[matchData objectForKey:@"matchState"] integerValue] == kCoreDataMatchStateNotStarted){
        BOOL matchConfirmed = [[matchData objectForKey:@"dateMatchNotConfirmed"] boolValue];
        if (!matchConfirmed)
            self.labelDate.text = [date getFormattedDateMatchForCalendar:NO];
        else{
            NSDate *startDate = [NSDate dateWithTimeIntervalSince1970:[[matchData objectForKey:@"roundStartDate"] doubleValue]/1000.0];
            NSDate *endDate = [NSDate dateWithTimeIntervalSince1970:[[matchData objectForKey:@"roundEndDate"] doubleValue]/1000.0];
            NSString *roundDate = [NSString stringWithFormat:@"%@ al %@",[startDate getFormattedShortDate],[endDate getFormattedShortDate]];
            self.labelDate.text = roundDate;
            
        }
        self.labelteamNames.attributedText = teamNamesAtr;
    }else {
        self.labelDate.text = NSLocalizedString(@"_roundMatchesListMatchSuspended", nil);
        self.labelteamNames.text = teamNames;
    }

    self.labelDate.textAlignment = NSTextAlignmentRight;
    self.labelteamNames.frame = CGRectMake(self.labelteamNames.frame.origin.x, self.labelteamNames.frame.origin.y,
                                           self.labelDate.frame.origin.x-10, self.labelteamNames.frame.size.height);
    
    [self.layer setShouldRasterize:YES];
    [self.layer setRasterizationScale:[[UIScreen mainScreen] scale]];
}


//------------------------------------------------------------------------------
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        // Initialization code
        self.identifier = reuseIdentifier;
    }
	
    return self;
}

//------------------------------------------------------------------------------
- (NSString *)reuseIdentifier {
    return self.identifier;
    
}


@end
