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
    
    NSNumber *idMatch = [dict objectForKey:kJSON_ID_MATCH];
    if ( [idMatch isKindOfClass:[NSNumber class]] )
        [self setIdMatch:idMatch];
    else
        result = NO;
    
    NSNumber *idLocal = [dict objectForKey:kJSON_ID_TEAM_LOCAL];
    if ( [idLocal isKindOfClass:[NSNumber class]] )
        [self setIdLocalTeam:idLocal];
    else
        result = NO;
    
    NSNumber *idVisitor = [dict objectForKey:kJSON_ID_TEAM_VISITOR];
    if ( [idVisitor isKindOfClass:[NSNumber class]] )
        [self setIdVisitorTeam:idVisitor];
    else
        result = NO;
    
    NSString *localTeaName = [dict objectForKey:kJSON_LOCAL_TEAM_NAME];
    if ( [localTeaName isKindOfClass:[NSString class]] )
        [self setLocalTeamName:localTeaName];

    NSString *visitorTeaName = [dict objectForKey:kJSON_VISITOR_TEAM_NAME];
    if ( [visitorTeaName isKindOfClass:[NSString class]] )
        [self setVisitorTeamName:visitorTeaName];
    
    NSNumber *matchDate = [dict objectForKey:kJSON_DATE_MATCH];
    if ( [matchDate isKindOfClass:[NSNumber class]] )
        [self setMatchDate:matchDate];
    else
        result = NO;
    
    
    NSNumber *status = [dict objectForKey:K_WS_STATUS];
    if ( [status isKindOfClass:[NSNumber class]] )
        [self setStatus:status];
    else
        result = NO;
    
    //SYNCRO  PROPERTIES
    NSString *syncro = [dict objectForKey:kJSON_SYNCRONIZED];
    if ( [syncro isKindOfClass:[NSString class]] )
        [self setCsys_syncronized:syncro];
    else
        [self setCsys_syncronized:kJSON_SYNCRO_SYNCRONIZED];
    
    NSNumber *revision = [dict objectForKey:K_WS_OPS_REVISION];
    if ( [revision isKindOfClass:[NSNumber class]] )
        [self setCsys_revision:revision];
    else
        [self setCsys_revision:@0];
    
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
    
   return result;
}


@end
