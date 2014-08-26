#import "BetTypeOdd.h"
#import "CoreDataParsing.h"
#import "CoreDataManager.h"
#import "MatchBetType.h"

@interface BetTypeOdd ()

@end


@implementation BetTypeOdd

#pragma mark - Public Methods
//------------------------------------------------------------------------------
+(BetTypeOdd *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    BetTypeOdd *betTypeOdd = [NSEntityDescription insertNewObjectForEntityForName:@"BetTypeOdd"
                                                     inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [betTypeOdd setBetTypeOddValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:betTypeOdd];
        return nil;
    }
    return betTypeOdd;
}

//------------------------------------------------------------------------------
+(BetTypeOdd *)updateWithDictionary:(NSDictionary *)dict {
    
    NSNumber *idBetTypeOdd = [dict objectForKey:kJSON_ID_BETTYPEODD];
    
    if ( idBetTypeOdd && [idBetTypeOdd isKindOfClass:[NSNumber class]]){
        BetTypeOdd *betTypeOdd = [[CoreDataManager singleton] getEntity:[BetTypeOdd class] withId:[idBetTypeOdd integerValue]];
        if ( betTypeOdd )
            [betTypeOdd setBetTypeOddValuesWithDictionary:dict];      // Update entity
        else
            betTypeOdd = [BetTypeOdd insertWithDictionary:dict];      // insert new entity        return betTypeOdd;

        return betTypeOdd;
    }
    return nil;
}

#pragma mark - Private Methods
//------------------------------------------------------------------------------
-(BOOL)setBetTypeOddValuesWithDictionary:(NSDictionary *)dict {

    //NECESSARY PROPERTIES
    //-------------------------------
    NSNumber *idBetTypeOdd = [dict objectForKey:kJSON_ID_BETTYPEODD];
    if ( [idBetTypeOdd isKindOfClass:[NSNumber class]] )
        [self setIdBetTypeOdd:idBetTypeOdd];
    else
        return NO;
    
    NSNumber *value = [NSNumber numberWithFloat:[[dict objectForKey:kJSON_VALUE] floatValue]];
    if ( [value isKindOfClass:[NSNumber class]] )
        [self setValue:value];
    
    NSString *url = [dict objectForKey:kJSON_URL];
    if ( [url isKindOfClass:[NSString class]] )
        [self setUrl:url];
    else
        return NO;

    
    //OPTIONAL PROPERTIES
    //-------------------------------
//    NSNumber *idMatchBetType = [dict objectForKey:kJSON_ID_MATCHBETTYPE];
//    MatchBetType *matchBetType = [[CoreDataManager singleton] getEntity:[MatchBetType class] withId:[idMatchBetType integerValue]];
//    if (matchBetType)
//        [self setMatchBetType:matchBetType];
    
    NSString *name = [dict objectForKey:kJSON_NAME];
    if ( [name isKindOfClass:[NSString class]] )
        [self setName:name];
    else
        return NO;
    
    NSString *comment = [dict objectForKey:kJSON_COMMENT];
    if ( [comment isKindOfClass:[NSString class]] )
        [self setComment:comment];
    
    [self setReadyToDeleteValue:NO];
    
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

    return YES;

}

@end
