#import "Match.h"
#import "Team.h"
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
+(Match *)updateWithDictionary:(NSDictionary *)dict{
    
    NSNumber *idMatch = [dict objectForKey:kJSON_ID_MATCH];
    
    if ( idMatch ){
        Match *match = [[CoreDataManager singleton] getEntity:[Match class] withId:[idMatch integerValue]];
        if ( match )    [match setMatchValuesWithDictionary:dict];      // Update entity
        else            match = [self insertWithDictionary:dict];       // insert new entity    
        return match;
    }
    return nil;
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
            [self setCsys_birth:birth];
        }
        
        NSNumber *modified = [dict objectForKey:K_WS_OPS_UPDATE_DATE];
        if ([modified isKindOfClass:[NSNumber class]]) {
            [self setCsys_modified:modified];
        }
        
        NSNumber *deleted = [dict objectForKey:K_WS_OPS_DELETE_DATE];
        if ([deleted isKindOfClass:[NSNumber class]]) {
            [self setCsys_deleted:deleted];
        }
        
    }
    else
        result = NO;
    
    return result;
}


@end
