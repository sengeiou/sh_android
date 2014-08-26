#import "Classification.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

#import "Tournament.h"

@interface Classification ()

@end


@implementation Classification

//------------------------------------------------------------------------------
+(Classification *)insertWithDictionary:(NSDictionary *)dict {
    
    return [self insertWithDictionary:dict forTournament:nil];
}

//------------------------------------------------------------------------------
+(Classification *)insertWithDictionary:(NSDictionary *)dict forTournament:(Tournament *) tournament{
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Classification *classification = [NSEntityDescription insertNewObjectForEntityForName:@"Classification"
                                                                   inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [classification setClassificationValuesWithDictionary:dict forTournament:tournament];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:classification];
        return nil;
    }
    return classification;
}

//------------------------------------------------------------------------------
+(Classification *)updateWithDictionary:(NSDictionary *)dict {
    return [self updateWithDictionary:dict forTournament:nil];
}

//------------------------------------------------------------------------------
+(Classification *)updateWithDictionary:(NSDictionary *)dict forTournament:(Tournament *) tournament{
    
    NSNumber *idClassification = [self createClassificationID:dict forTournament:tournament];
    
    if ( idClassification ){
        Classification *classification = [[CoreDataManager singleton] getEntity:[Classification class] withId:[idClassification integerValue]];
        if ( classification )
            [classification setClassificationValuesWithDictionary:dict forTournament:tournament];      // Update entity
        else
            classification = [Classification insertWithDictionary:dict forTournament:tournament];      // insert new entity
        
        return classification;
    }
    return nil;
}

#pragma mark - private methods
//------------------------------------------------------------------------------
+(NSNumber *)createClassificationID:(NSDictionary *)dict forTournament:(Tournament *)tournament {
        
    NSString *idTournament = [[tournament idTournament] stringValue];
    NSString *idTeam = [[dict objectForKey:kJSON_ID_TEAM] stringValue];
    NSMutableString *idClassificationString = [NSMutableString new];
    
    if ( idTournament )
        [idClassificationString appendString:idTournament];
    
    if ( idTeam )
        [idClassificationString appendString:idTeam];
    
    if ( [idClassificationString length]>0 ){
        NSNumber *idClassification = [NSNumber numberWithInteger:[idClassificationString integerValue]];
        return idClassification;
    }
    
    return nil;
}

//------------------------------------------------------------------------------
-(BOOL)setClassificationValuesWithDictionary:(NSDictionary *)dict forTournament:(Tournament *)tournament{
    
    if (![dict isKindOfClass:[NSDictionary class]] )        return NO;
    
    NSNumber *idClassification = [Classification createClassificationID:dict forTournament:tournament];
    if ( idClassification )
        [self setIdClassification:idClassification];
    else
        return NO;
    
    NSNumber *gal = [dict objectForKey:kJSON_CLASSIFICATION_GAL];
    if ( [gal isKindOfClass:[NSNumber class]] )
        [self setGal:gal];
    else
        return NO;

    NSNumber *gav = [dict objectForKey:kJSON_CLASSIFICATION_GAV];
    if ( [gav isKindOfClass:[NSNumber class]] )
        [self setGav:gav];
    else
        return NO;

    NSNumber *gfl = [dict objectForKey:kJSON_CLASSIFICATION_GFL];
    if ( [gfl isKindOfClass:[NSNumber class]] )
        [self setGfl:gfl];
    else
        return NO;

    NSNumber *gfv = [dict objectForKey:kJSON_CLASSIFICATION_GFV];
    if ( [gfv isKindOfClass:[NSNumber class]] )
        [self setGfv:gfv];
    else
        return NO;

    NSNumber *dl = [dict objectForKey:kJSON_CLASSIFICATION_DL];
    if ( [dl isKindOfClass:[NSNumber class]] )
        [self setDl:dl];
    else
        return NO;

    NSNumber *dv = [dict objectForKey:kJSON_CLASSIFICATION_DV];
    if ( [dv isKindOfClass:[NSNumber class]] )
        [self setDv:dv];
    else
        return NO;

    NSNumber *wl = [dict objectForKey:kJSON_CLASSIFICATION_WL];
    if ( [wl isKindOfClass:[NSNumber class]] )
        [self setWl:wl];
    else
        return NO;
    
    NSNumber *wv = [dict objectForKey:kJSON_CLASSIFICATION_WV];
    if ( [wv isKindOfClass:[NSNumber class]] )
        [self setWv:wv];
    else
        return NO;

    NSNumber *pl = [dict objectForKey:kJSON_CLASSIFICATION_PL];
    if ( [pl isKindOfClass:[NSNumber class]] )
        [self setPl:pl];
    else
        return NO;
    
    NSNumber *pv = [dict objectForKey:kJSON_CLASSIFICATION_PV];
    if ( [pv isKindOfClass:[NSNumber class]] )
        [self setPv:pv];
    else
        return NO;

    NSNumber *ll = [dict objectForKey:kJSON_CLASSIFICATION_LL];
    if ( [ll isKindOfClass:[NSNumber class]] )
        [self setLl:ll];
    else
        return NO;

    NSNumber *lv = [dict objectForKey:kJSON_CLASSIFICATION_LV];
    if ( [lv isKindOfClass:[NSNumber class]] )
        [self setLv:lv];
    else
        return NO;

    NSNumber *points = [dict objectForKey:kJSON_CLASSIFICATION_POINTS];
    if ( [points isKindOfClass:[NSNumber class]] )
        [self setPoints:points];
    else
        return NO;

    NSNumber *weight = [dict objectForKey:kJSON_CLASSIFICATION_WEIGHT];
    if ( [weight isKindOfClass:[NSNumber class]] )
        [self setWeight:weight];
    else
        return NO;
    
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
