#import "Tournament.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface Tournament ()

// Private interface goes here.
-(BOOL)setTournamentValuesWithDictionary:(NSDictionary *)dict;

@end


@implementation Tournament

#pragma marck - Private methods
//------------------------------------------------------------------------------
+(Tournament *)insertWithDictionary:(NSDictionary *)dict
{
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Tournament *tournament = [NSEntityDescription insertNewObjectForEntityForName:@"Tournament" inManagedObjectContext:context];
    
    BOOL correctlyInserted = [tournament setTournamentValuesWithDictionary:dict];
    if ( !correctlyInserted ){
        [[CoreDataManager singleton] deleteObject:tournament];
        return nil;
    }
    
    return tournament;
}

//------------------------------------------------------------------------------
+(Tournament *)updateWithDictionary:(NSDictionary *)dict
{    
    NSNumber *idTournament = [dict objectForKey:kJSON_TOURNAMENT_ID_TOURNAMENT];
    
    if ( idTournament ){
        Tournament *tournament = [[CoreDataManager singleton] getEntity:[Tournament class] withId:[idTournament integerValue]];
        if ( tournament )
            [tournament setTournamentValuesWithDictionary:dict];      // Update entity
        else
            tournament = [Tournament insertWithDictionary:dict];      // insert new entity

        return tournament;
    }
    return nil;
}

#pragma marck - Private methods
//------------------------------------------------------------------------------
-(BOOL)setTournamentValuesWithDictionary:(NSDictionary *)dict {
    BOOL result = YES;
    
    NSNumber *idTournament = [dict objectForKey:kJSON_TOURNAMENT_ID_TOURNAMENT];
    if ( [idTournament isKindOfClass:[NSNumber class]] )
        [self setIdTournament:idTournament];
    else
        return NO;
    
    NSString *name = [dict objectForKey:kJSON_NAME];
    if ( [name isKindOfClass:[NSString class]] )
        [self setName:name];
    else
        return NO;
    
    NSNumber *yearTournamet = [dict objectForKey:kJSON_TOURNAMENT_YEAR];
    if ( [yearTournamet isKindOfClass:[NSNumber class]] )
        [self setYear:yearTournamet];
    
    NSString *imageName = [dict objectForKey:kJSON_TOURNAMENT_IMAGE_NAME];
    if ( [imageName isKindOfClass:[NSString class]] )
        [self setImageName:imageName];
    
    NSDate *dateStart = [dict objectForKey:kJSON_TOURNAMENT_DATE_START];
    if ( [dateStart isKindOfClass:[NSDate class]] )
        [self setStartDate:dateStart];

    NSDate *dateEnd = [dict objectForKey:kJSON_TOURNAMENT_DATE_END];
    if ( [dateEnd isKindOfClass:[NSDate class]] )
        [self setEndDate:dateEnd];
    
    NSNumber *isLeague = [dict objectForKey:kJSON_TOURNAMENT_IS_LEAGUE];
    if ( [isLeague isKindOfClass:[NSNumber class]] )
        [self setIsLeague:isLeague];
    
    NSNumber *orderBy = [dict objectForKey:kJSON_TOURNAMENT_ORDERBY];
    if ( [orderBy isKindOfClass:[NSNumber class]] )
        [self setOrderBy:orderBy];
    
    NSNumber *visible = [dict objectForKey:kJSON_TOURNAMENT_VISIBLE];
    if ( [visible isKindOfClass:[NSNumber class]] )
        [self setVisibleApp:visible];
    
    
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


    return result;
}

@end
