#import "Shot.h"
#import "CoreDataManager.h"
#import "User.h"

@implementation Shot

//------------------------------------------------------------------------------
+(Shot *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Shot *shot = [NSEntityDescription insertNewObjectForEntityForName:@"Shot"
                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [shot setShotValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:shot];
        return nil;
    }
    return shot;
}

//------------------------------------------------------------------------------
+(instancetype)updateWithDictionary:(NSDictionary *)dict {
    
    NSString *identifier = [NSString stringWithFormat:@"id%@",[[self class] description]];
    NSNumber *idNumber = [dict objectForKey:identifier];
    
    if ( idNumber ){
        id objectInstance = [[CoreDataManager singleton] getEntity:[self class] withId:[idNumber integerValue]];
        
        if (objectInstance && ([[dict objectForKey:K_WS_OPS_DELETE_DATE] isKindOfClass:[NSNull class]] || ![dict objectForKey:K_WS_OPS_DELETE_DATE])) {
            [[CoreDataManager singleton] deleteObject:objectInstance];
            return nil;
        }
        else if ( objectInstance )
            [objectInstance setShotValuesWithDictionary:dict];      // Update entity
        else
            objectInstance = [self insertWithDictionary:dict];      // insert new entity
        
        return objectInstance;
    }
    return nil;
}

//------------------------------------------------------------------------------
-(BOOL)setShotValuesWithDictionary:(NSDictionary *)dict {
    
    BOOL result = YES;
    
    NSNumber *idUser = [dict objectForKey:kJSON_ID_USER];
    if ( [idUser isKindOfClass:[NSNumber class]] ){
        User *currentUser = [[CoreDataManager singleton] getEntity:[User class] withId:[idUser integerValue]];
        if (currentUser)
            [self setUser:currentUser];
        else
            result = NO;
    }
    else
        result = NO;
    
    NSNumber *idShot = [dict objectForKey:kJSON_SHOT_IDSHOT ];
    if ([idShot isKindOfClass:[NSNumber class]])
        [self setIdShot:idShot];
    else
        result = NO;
    
    NSString *comment = [dict objectForKey:kJSON_SHOT_COMMENT];
    if ([comment isKindOfClass:[NSString class]])
        [self setComment:comment];
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
