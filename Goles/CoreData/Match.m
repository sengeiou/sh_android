#import "Match.h"
#import "Team.h"
#import "Subscription.h"
#import "Event.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface Match ()

-(BOOL)setMatchValuesWithDictionary:(NSDictionary *)dict;

@end


@implementation Match

//------------------------------------------------------------------------------
+(Match *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];    
    Match *match = [NSEntityDescription insertNewObjectForEntityForName:@"Match"
                                                 inManagedObjectContext:context];
    BOOL correctlyInserted = [match setMatchValuesWithDictionary:dict];
    if ( !correctlyInserted ){
        [[CoreDataManager singleton] deleteObject:match];
        return nil;
    }
    
    return match;
}

//------------------------------------------------------------------------------
+(Match *)createTemporaryMatch {
    
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Match" inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    Match *match = [[Match alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
    return match;
}

//------------------------------------------------------------------------------
+(Match *)createTemporaryMatchFromMatch:(Match *)match {
    
    Match *newMatch = [self createTemporaryMatch];
    [newMatch copyFromMatch:match];
    
    if ( [match subscription] ){
        Subscription *newSubscription = [Subscription createTemporarySubscriptionWithSubscription:[match subscription]];
        [newSubscription setMatch:newMatch];
        [newMatch setSubscription:newSubscription];
    }

    return newMatch;
}

//------------------------------------------------------------------------------
+(Match *)updateWithDictionary:(NSDictionary *)dict {
    return [self updateWithDictionary:dict withIndex:-1];
}

//------------------------------------------------------------------------------
+(Match *)updateWithDictionary:(NSDictionary *)dict withIndex:(NSInteger)index{
    
    NSNumber *idMatch = [dict objectForKey:kJSON_ID_MATCH];
    
    if ( idMatch ){
        Match *match = [[CoreDataManager singleton] getEntity:[Match class] withId:[idMatch integerValue]];
        if ( match )    [match setMatchValuesWithDictionary:dict];      // Update entity
        else            match = [self insertWithDictionary:dict];       // insert new entity    
        [match setOrderValue:index];
        return match;
    }
    return nil;
}

//------------------------------------------------------------------------------
-(void)copyFromMatch:(Match *)match{
    
    [self setIdMatch:[match idMatch]];
    
    [self setLocalScore:[match localScore]];
    [self setVisitorScore:[match visitorScore]];
    
    [self setMatchDate:[match matchDate]];
    [self setElapsedMinutes:[match elapsedMinutes]];
    [self setMatchState:[match matchState]];
    [self setOrder:[match matchState]];
    
    [self setTeamLocal:[Team createTemporaryTeamWithTeam:match.teamLocal]];
    [self setTeamVisitor:[Team createTemporaryTeamWithTeam:match.teamVisitor]];
}

#pragma marck - Public Methods
//------------------------------------------------------------------------------
+ (NSArray *)changeMatchesFormatToParse:(NSArray *)matchesToChange {
  
    NSMutableArray *finalMacthes = [[NSMutableArray alloc] init];
    
    for (NSDictionary *match in matchesToChange) {
        NSMutableDictionary *mutMatch = [match mutableCopy];
        
        NSNumber *localScore = [match objectForKey:kJSON_LOCAL_MATCH_SCORE];
        if ( [localScore isKindOfClass:[NSNumber class]] )
            [mutMatch setObject:localScore forKey:kJSON_SCORE_LOCAL];

        NSNumber *visitorScore = [match objectForKey:kJSON_VISITOR_MATCH_SCORE];
        if ( [visitorScore isKindOfClass:[NSNumber class]] )
            [mutMatch setObject:visitorScore forKey:kJSON_SCORE_VISITOR];
        
        NSDictionary *localTeamDict = @{kJSON_ID_TEAM:[match objectForKey:@"idTeamLocal"],
                                        kJSON_NAME_SHORT:[match objectForKey:@"localName"]};
        
        NSDictionary *visitorTeamDict = @{kJSON_ID_TEAM:[match objectForKey:@"idTeamVisitor"],
                                          kJSON_NAME_SHORT:[match objectForKey:@"visitorName"]};
        
        [mutMatch addEntriesFromDictionary:@{@"matchDate":[match objectForKey:@"dateMatch"],
                                             @"localTeamData":localTeamDict,
                                             @"visitorTeamData":visitorTeamDict}];
        
        if (mutMatch)            [finalMacthes addObject:mutMatch];
    }
    return finalMacthes;
}


//------------------------------------------------------------------------------
-(NSArray *)getOrderedEvents{

    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"dateIn" ascending:NO];
    return [self.eventsOfMatch sortedArrayUsingDescriptors:@[descriptor]];

}

//------------------------------------------------------------------------------
-(BOOL)checkForPenaltiesRoundInMatch {
    
    for (EventOfMatch *eventOM in self.eventsOfMatch) {
        if (eventOM.event.idEventValue == 8)
            return YES;
    }
        
    return NO;
}

//------------------------------------------------------------------------------
-(NSArray *)getChronicleOrderedEvents{
    
    NSMutableArray *firstHalfEvents = [[NSMutableArray alloc] init];
    NSMutableArray *secondHalfEvents = [[NSMutableArray alloc] init];
    BOOL restReached = NO;
    
    NSSortDescriptor *firstDescriptor = [[NSSortDescriptor alloc] initWithKey:@"dateIn" ascending:YES];
    NSArray *sortedEvents = [self.eventsOfMatch sortedArrayUsingDescriptors:@[firstDescriptor]];
    
    for (EventOfMatch *event in sortedEvents) {
        if (event.event.idEventValue == 6)
            restReached = YES;
        
        if (!restReached) {
            [firstHalfEvents addObject:event];
        }else
            [secondHalfEvents addObject:event];
    }
    
    NSSortDescriptor *descriptorMinute = [[NSSortDescriptor alloc] initWithKey:@"minuteOfMatch" ascending:NO];
    NSSortDescriptor *descriptorEventID = [[NSSortDescriptor alloc] initWithKey:@"dateIn" ascending:NO];
    
    NSArray *first = [firstHalfEvents sortedArrayUsingDescriptors:@[descriptorMinute,descriptorEventID]];
    NSArray *second = [secondHalfEvents sortedArrayUsingDescriptors:@[descriptorMinute,descriptorEventID]];
    
    NSMutableArray *secondMutable = [second mutableCopy];
    
    for (EventOfMatch *newEvent in first)
        [secondMutable addObject:newEvent];
    
    return [secondMutable copy];
    
}

//------------------------------------------------------------------------------
-(void)updateMatchElapsedMinute:(int)newElapsedMinute {
    
    [self setElapsedMinutes:[NSNumber numberWithInt:newElapsedMinute]];
    [[CoreDataManager singleton] saveContext];
}


#pragma mark - Private Methods
//------------------------------------------------------------------------------
-(BOOL)setMatchValuesWithDictionary:(NSDictionary *)dict{
    
    BOOL result = YES;

    NSLog(@"%@", dict);
    
    NSNumber *idMatch = [dict objectForKey:kJSON_ID_MATCH];
    NSNumber *matchState = [dict objectForKey:kJSON_MATCH_STATE];
    NSNumber *idLocal = [dict objectForKey:kJSON_ID_TEAM_LOCAL];
    NSNumber *idVisitor = [dict objectForKey:kJSON_ID_TEAM_VISITOR];
    
    if ( [idMatch isKindOfClass:[NSNumber class]] &&
        [matchState isKindOfClass:[NSNumber class]] &&
        [matchState intValue]>=kCoreDataMatchStateNotStarted && [matchState intValue]<=kCoreDataMatchStateFinished &&
        [idLocal isKindOfClass:[NSNumber class]] &&
        [idVisitor isKindOfClass:[NSNumber class]])
    {
        
        Team *localTeam = [[CoreDataManager sharedInstance] getEntity:[Team class] withId:[[dict objectForKey:kJSON_ID_TEAM_LOCAL] integerValue]];
        Team *visitorTeam = [[CoreDataManager sharedInstance] getEntity:[Team class] withId:[[dict objectForKey:kJSON_ID_TEAM_VISITOR] integerValue]];

        if (localTeam && visitorTeam) {
            [self setTeamLocal:localTeam];
            [self setTeamVisitor:visitorTeam];
        }
        
        [self setIdMatch:idMatch];
        [self setMatchState:matchState];
        
        NSNumber *localScore = [dict objectForKey:kJSON_SCORE_LOCAL];
        if ( [localScore isKindOfClass:[NSNumber class]] )
            [self setLocalScore:localScore];
        
        NSNumber *visitorScore = [dict objectForKey:kJSON_SCORE_VISITOR];
        if ( [visitorScore isKindOfClass:[NSNumber class]] )
            [self setVisitorScore:visitorScore];
        
        NSString *elapsedMin = [dict objectForKey:kJSON_ELAPSED_MINUTES];
        if ( [elapsedMin isKindOfClass:[NSString class]] )
            [self setElapsedMinutes:@([elapsedMin integerValue])];
        
        NSNumber *matchDate = [dict objectForKey:kJSON_DATE_MATCH];
        if ( [matchDate isKindOfClass:[NSNumber class]] )
            [self setMatchDate:matchDate];
        
        NSNumber *matchType = [dict objectForKey:kJSON_MATCH_TYPE];
        if ( [matchDate isKindOfClass:[NSNumber class]] )
            [self setMatchType:matchType];
        
        NSNumber *matchSubstate = [dict objectForKey:@"matchSubstate"];
        if ([matchSubstate isKindOfClass:[NSNumber class]])                     [self setMatchSubstate:matchSubstate];
        
        NSString *matchTVList = [dict objectForKey:@"listTV"];
        if ([matchTVList isKindOfClass:[NSString class]])                       [self setListTV:matchTVList];
        
        NSString *matchPreviousScore = [dict objectForKey:@"previousMatchScore"];
        if ([matchPreviousScore isKindOfClass:[NSString class]])                [self setPreviousMatchScore:matchPreviousScore];
        
        NSNumber *matchIdTeamWinner = [dict objectForKey:@"idTeamWinner"];
        if ([matchIdTeamWinner isKindOfClass:[NSNumber class]])                 [self setIdWinnerTeam:matchIdTeamWinner];
        
        NSNumber *dateMatchNotConfirmed = [dict objectForKey:@"dateMatchNotConfirmed"];
        if ([dateMatchNotConfirmed isKindOfClass:[NSNumber class]])             [self setNotConfirmedMatchDate:dateMatchNotConfirmed];
        
        //SYNCRO  PROPERTIES
        
        NSString *syncro = [dict objectForKey:kJSON_SYNCRONIZED];
        if ( [syncro isKindOfClass:[NSString class]] )
            [self setCsys_syncronized:syncro];
        else
            [self setCsys_syncronized:kJSON_SYNCRO_SYNCRONIZED];
        
        NSNumber *revision = [dict objectForKey:K_WS_OPS_REVISION];
        if ( [revision isKindOfClass:[NSNumber class]] )
            [self setCsys_revision:revision];
        
        NSNumber *birth = [dict objectForKey:K_WS_OPS_BIRTH_DATE];
        if ([birth isKindOfClass:[NSNumber class]]) {
            NSTimeInterval epochBirth = [birth doubleValue]/1000;
            NSDate *birthDate = [NSDate dateWithTimeIntervalSince1970:epochBirth];
            if ([birthDate isKindOfClass:[NSDate class]])
                [self setCsys_birth:birthDate];
        }
        
        NSNumber *modified = [dict objectForKey:K_WS_OPS_UPDATE_DATE];
        if ([modified isKindOfClass:[NSNumber class]]) {
            NSTimeInterval epochModified = [modified doubleValue]/1000;
            NSDate *modifiedDate = [NSDate dateWithTimeIntervalSince1970:epochModified];
            if ([modifiedDate isKindOfClass:[NSDate class]])
                [self setCsys_modified:modifiedDate];
        }
        
        NSNumber *deleted = [dict objectForKey:K_WS_OPS_DELETE_DATE];
        if ([deleted isKindOfClass:[NSNumber class]]) {
            NSTimeInterval epochDeleted = [deleted doubleValue]/1000;
            NSDate *deletedDate = [NSDate dateWithTimeIntervalSince1970:epochDeleted];
            if ([deletedDate isKindOfClass:[NSDate class]])
                [self setCsys_deleted:deletedDate];
        }
        
    }
    else
        result = NO;
    
    return result;
}


@end
