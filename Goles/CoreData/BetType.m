#import "BetType.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface BetType ()

@end


@implementation BetType

#pragma mark - Public Methods
//------------------------------------------------------------------------------
+(BetType *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    BetType *betType = [NSEntityDescription insertNewObjectForEntityForName:@"BetType"
                                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [betType setBetTypeValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:betType];
        return nil;
    }
    return betType;
}

//------------------------------------------------------------------------------
+(BetType *)updateWithDictionary:(NSDictionary *)dict {
    
    NSNumber *idBetType = [dict objectForKey:kJSON_ID_BETTYPE];
    
    if ( idBetType ){
        BetType *betType = [[CoreDataManager singleton] getEntity:[BetType class] withId:[idBetType integerValue]];
        if ( betType )
            [betType setBetTypeValuesWithDictionary:dict];      // Update entity
        else
            betType = [BetType insertWithDictionary:dict];      // insert new entity
        return betType;
    }
    return nil;
}

+(BetType *)createTemporaryBetType {
    
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"BetType" inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    BetType *betType = [[BetType alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
    return betType;
}

#pragma mark - Private Methods
//------------------------------------------------------------------------------
-(BOOL)setBetTypeValuesWithDictionary:(NSDictionary *)dict {
    
    //NECESSARY PROPERTIES
    //-------------------------------
    NSNumber *idBetType = [dict objectForKey:kJSON_ID_BETTYPE];
    if ( [idBetType isKindOfClass:[NSNumber class]] )
        [self setIdBetType:idBetType];
    else
        return NO;
    
    NSNumber *alwaysVisible = [dict objectForKey:kJSON_ALWAYS_VISIBLE];
    if ( [alwaysVisible isKindOfClass:[NSNumber class]] )
        [self setAlwaysVisible:alwaysVisible];
    else
        return NO;
    
    NSNumber *weight = [dict objectForKey:kJSON_WEIGHT];
    if ( [weight isKindOfClass:[NSNumber class]] )
        [self setWeight:weight];
    else
        return NO;
    
    NSString *uniqueKey = [dict objectForKey:kJSON_UNIQUE_KEY];
    if ( [uniqueKey isKindOfClass:[NSString class]] )
        [self setUniqueKey:uniqueKey];
    else
        return NO;
    
    NSString *name = [dict objectForKey:kJSON_NAME];
    if ( [name isKindOfClass:[NSString class]] )
        [self setName:name];
    else
        return NO;
    
    //OPTIONAL PROPERTIES
    //-------------------------------
    NSString *comment = [dict objectForKey:kJSON_COMMENT];
    if ( [comment isKindOfClass:[NSString class]] )
        [self setComment:comment];

    NSString *title = [dict objectForKey:kJSON_TITLE];
    if ( [title isKindOfClass:[NSString class]] )
        [self setTitle:title];
    
    
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
