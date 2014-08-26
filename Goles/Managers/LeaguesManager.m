//
//  LeaguesManager.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 14/10/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "LeaguesManager.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "Team.h"
#import "Tournament.h"
#import "Services.pch"
#import "UserManager.h"
#import "Device.h"

@interface LeaguesManager ()

@end

@implementation LeaguesManager

//------------------------------------------------------------------------------
//DataAccessLayer singleton instance shared across application
+ (LeaguesManager *)singleton
{
    static LeaguesManager *sharedLeague = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedLeague = [[LeaguesManager alloc] init];
    });
    return sharedLeague;
    
}

//------------------------------------------------------------------------------
+ (LeaguesManager *)sharedInstance
{
    return [self singleton];
}

#pragma mark - Public Methods
//------------------------------------------------------------------------------
- (NSArray *)getSubscribedTeams {
 
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"!(subscription = nil)"];
    NSArray *favourites = [[CoreDataManager singleton] getAllEntities:[Team class]
                                                         orderedByKey:@"order"
                                                            ascending:YES
                                                        withPredicate:predicate];
    return favourites;
}

//------------------------------------------------------------------------------
- (NSArray *)getModes {
    
   
    //[[RestConsumer sharedInstance] getLeaguesWithDelegate:self];
    
    NSArray *modesArray =  [[CoreDataManager singleton] getAllEntities:[Mode class] orderedByKey:@"idMode"];
    
    //Temporally sort
    NSMutableArray *orderedArray = [[NSMutableArray alloc] initWithCapacity:5];
    [orderedArray addObject:[modesArray objectAtIndex:0]];
    [orderedArray addObject:[modesArray objectAtIndex:1]];
    [orderedArray addObject:[modesArray objectAtIndex:3]];
    [orderedArray addObject:[modesArray objectAtIndex:2]];
    [orderedArray addObject:[modesArray objectAtIndex:4]];
    return orderedArray;
}

//------------------------------------------------------------------------------
- (Mode *)getSelectedMode{
    return [self mSelectedMode];
}

//------------------------------------------------------------------------------
- (Mode *)getModeWithId:(NSUInteger)idMode {
    
    NSArray *modes = [self getModes];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"%K=%d", kJSON_ID_MODE, idMode];
    NSArray *result = [modes filteredArrayUsingPredicate:predicate];
    
    if ( [result count]>0 )
        return [result firstObject];

    return nil;
}

//------------------------------------------------------------------------------
- (NSArray *)getCurrentRounds {
    
    NSUInteger maxNextRound = 3;
    
    NSMutableArray *result = [[NSMutableArray alloc] init];
    NSTimeInterval currentDate = [[NSDate date] timeIntervalSince1970] * 1000;
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(tournament.status = 1) AND (ANY tournament.modes.idMode = %d) AND (matchesList.@count > 0) ", [[self mSelectedMode] idModeValue] ];
    NSPredicate *prev = [NSPredicate predicateWithFormat:@"%f > endDate", currentDate];
    NSPredicate *previousRoundPred = [NSCompoundPredicate andPredicateWithSubpredicates:@[predicate, prev]];
    
    //Order by DateEnd descending to get the most recently finished round if more than 1 arrive in from DB
    NSArray *rounds = [[CoreDataManager singleton] getAllEntities:[Round class]
                                                       orderedByKey:@"endDate"
                                                          ascending:NO
                                                      withPredicate:previousRoundPred];
    Round *previousRound;
    if ( [rounds count]>0 ){
        previousRound = rounds[0];
        [result addObject:previousRound];
    }
    //Order by DateStart ascending to order the next rounds by start date. Now fixed to 3 rounds maximum
    NSPredicate *next = [NSPredicate predicateWithFormat:@"%f <= endDate", currentDate];
    NSPredicate *nextRoundsPred = [NSCompoundPredicate andPredicateWithSubpredicates:@[predicate, next]];
    NSArray *nextRounds = [[CoreDataManager singleton] getAllEntities:[Round class]
                                                           orderedByKey:@"startDate"
                                                              ascending:YES
                                                          withPredicate:nextRoundsPred];
    
    NSInteger roundsToInsert = [nextRounds count] > maxNextRound ? maxNextRound : [nextRounds count];
    [result addObjectsFromArray:[nextRounds objectsAtIndexes:[NSIndexSet indexSetWithIndexesInRange:NSMakeRange(0, roundsToInsert)]]];
    
    return result;
}


//------------------------------------------------------------------------------
- (NSArray *)getLeaguesFromCurrentMode {

    return [self getLeaguesFromMode:[[self mSelectedMode] idMode]];
}

//------------------------------------------------------------------------------
- (NSArray *)getLeaguesFromMode:(NSNumber *)idMode{
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(isLeague = YES) AND (ANY modes.idMode = %@)",idMode];

    NSArray *leagues = [[CoreDataManager singleton] getAllEntities:[Tournament class] withPredicate:predicate];
    if ( [leagues count]>0 )
        return leagues;
    return nil;
}

//------------------------------------------------------------------------------
- (BOOL)changeMode:(Mode *)newMode {
    
    if (newMode.idModeValue != self.mSelectedMode.idModeValue ) {
        [self setMSelectedMode:newMode];
        [[NSNotificationCenter defaultCenter] postNotificationName:kCHANGE_LEAGUE_NOTIFICATION object:nil];
        return YES;
    }
    return NO;
}

#pragma mark - WSClient Leagues delegate
//------------------------------------------------------------------------------
- (void)getLeaguesDidResponse {
    
    //save current Date to user Defaults
    NSUserDefaults *localStorage = [NSUserDefaults standardUserDefaults];
    [localStorage setObject:[NSDate date] forKey:@"ModesListLastUpdateDate"];
    [localStorage synchronize];
    
    // Update Manager selected Mode
    NSArray *leagues = [self getModes];
    
    if ([self mSelectedMode]) {
        BOOL selectedLeagueExist = NO;
        for (Mode *mode in leagues) {
            if (mode.idMode == self.mSelectedMode.idMode) {
                [self setMSelectedMode:mode];
                selectedLeagueExist = YES;
                break;
            }
        }
        if (!selectedLeagueExist)
            [self changeMode:[leagues objectAtIndex:0]];
    }else {
        [self changeMode:[leagues objectAtIndex:0]];
    }
}

#pragma mark - Singleton overwritten methods
//------------------------------------------------------------------------------
- (id)init {
	self = [super init];
	if (self != nil) {
        // Get Mode "Primera" by default
        [self setMSelectedMode:[[CoreDataManager singleton] getEntity:[Mode class] withId:1]];

        //Refresh Modes from server
        //[[RestConsumer sharedInstance] getLeaguesWithDelegate:self];
	}
	return self;
}

//------------------------------------------------------------------------------
- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

@end
