#import "Team.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface Team ()

-(BOOL)setTeamValuesWithDictionary:(NSDictionary *)dict;

@end


@implementation Team

//------------------------------------------------------------------------------
+(Team *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Team *team = [NSEntityDescription insertNewObjectForEntityForName:@"Team"
                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [team setTeamValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:team];
        return nil;
    }
    return team;
}

//------------------------------------------------------------------------------
+(Team *)updateWithDictionary:(NSDictionary *)dict {
    
    NSNumber *idTeam = [dict objectForKey:kJSON_TEAM_IDTEAM];
    
    if ( idTeam ){
        Team *team = [[CoreDataManager singleton] getEntity:[Team class] withId:[idTeam integerValue]];
        
        if (team && ([[dict objectForKey:K_WS_OPS_DELETE_DATE] isKindOfClass:[NSNull class]] || ![dict objectForKey:K_WS_OPS_DELETE_DATE])) {
            [[CoreDataManager singleton] deleteEntitiesIn:@[self]];
            return nil;
        }else {
            if ( team )
                [team setTeamValuesWithDictionary:dict];      // Update entity
            else
                team = [Team insertWithDictionary:dict];      // insert new entity
        }
        
        return team;
    }
    return nil;
}


#pragma mark - private methods
//------------------------------------------------------------------------------
-(BOOL)setTeamValuesWithDictionary:(NSDictionary *)dict {
    
    if (![dict isKindOfClass:[NSDictionary class]] )
        return NO;
    
    NSNumber *idTeam = [dict objectForKey:kJSON_TEAM_IDTEAM];
    if ( [idTeam isKindOfClass:[NSNumber class]] )
        [self setIdTeam:idTeam];
    else
        return NO;
  
    NSString *nameShort = [dict objectForKey:kJSON_SHORT_NAME];
    if ( [nameShort isKindOfClass:[NSString class]] )
        [self setShortName:nameShort];
    
    NSString *clubName = [dict objectForKey:kJSON_CLUB_NAME];
    if ( [clubName isKindOfClass:[NSString class]] )
            [self setClubName:clubName];
    
    NSString *officialName = [dict objectForKey:kJSON_OFICIAL_NAME];
    if ( [officialName isKindOfClass:[NSString class]] )
        [self setOfficialName:officialName];
    
    NSString *tlaName = [dict objectForKey:kJSON_TLA_NAME];
    if ( [tlaName isKindOfClass:[NSString class]] )
        [self setTlaName:tlaName];

    

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
    
    return YES;
}


@end
