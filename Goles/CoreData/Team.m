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
+(Team *)insertWithDictionary:(NSDictionary *)dict andIndex:(NSInteger)index{
    
    Team *team = [self insertWithDictionary:dict];
    if ( team )        [team setOrderValue:index];
    return team;
}

//------------------------------------------------------------------------------
+(Team *)updateWithDictionary:(NSDictionary *)dict {
    return [self updateWithDictionary:dict withIndex:-1];
}

//------------------------------------------------------------------------------
+(Team *)updateWithDictionary:(NSDictionary *)dict withIndex:(NSInteger)index{
    
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
        
//        if ( index > -1 )       [team setOrderValue:index];

        return team;
    }
    return nil;
}

#pragma mark - public methods

//------------------------------------------------------------------------------
+(Team *)createTemporaryTeam {
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Team" inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    Team *team = [[Team alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
    return team;
}

//------------------------------------------------------------------------------
+(Team *)createTemporaryTeamWithTeam:(Team *)team {
    
    Team *newTeam = [self createTemporaryTeam];
    
    [newTeam setIdTeam:[team idTeam]];
    [newTeam setIsNationalTeam:[team isNationalTeam]];
    [newTeam setName:[team name]];
    [newTeam setNameShort:[team nameShort]];
    [newTeam setOrder:[team order]];
    [newTeam setUrlImage:[team urlImage]];

    return newTeam;
}

#pragma mark - private methods
//------------------------------------------------------------------------------
-(BOOL)setTeamValuesWithDictionary:(NSDictionary *)dict {
    
    if (![dict isKindOfClass:[NSDictionary class]] )        return NO;
    
    NSNumber *idTeam = [dict objectForKey:kJSON_TEAM_IDTEAM];
    if ( [idTeam isKindOfClass:[NSNumber class]] )
        [self setIdTeam:idTeam];
    else
        return NO;
  
    NSString *nameShort = [dict objectForKey:kJSON_TEAM_NAMESHORT];
    NSString *name = [dict objectForKey:K_CD_NAME];
    
    if ( [nameShort isKindOfClass:[NSString class]] ){
        [self setNameShort:nameShort];
        if ( [name isKindOfClass:[NSString class]] )
            [self setName:name];
        else if ( ![self name] )
            [self setName:nameShort];
    }

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
    
    return YES;
}


@end
