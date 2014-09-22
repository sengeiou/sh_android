#import "User.h"
#import "CoreDataManager.h"

@interface User ()

// Private interface goes here.

@end


@implementation User

//------------------------------------------------------------------------------
+(User *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    User *user = [NSEntityDescription insertNewObjectForEntityForName:@"User"
                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [user setUserValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:user];
        return nil;
    }
    return user;
}

//------------------------------------------------------------------------------
+(instancetype)updateWithDictionary:(NSDictionary *)dict {
    
    NSString *identifier = [NSString stringWithFormat:@"id%@",[[self class] description]];
    NSNumber *idNumber = [dict objectForKey:identifier];
    
    if ( idNumber ){
        id objectInstance = [[CoreDataManager singleton] getEntity:[self class] withId:[idNumber integerValue]];
        if ( objectInstance )
            [objectInstance setUserValuesWithDictionary:dict];      // Update entity
        else
            objectInstance = [self insertWithDictionary:dict];      // insert new entity
        return objectInstance;
    }
    return nil;
}

//------------------------------------------------------------------------------
-(BOOL)setUserValuesWithDictionary:(NSDictionary *)dict {
    
    BOOL result = YES;
    
    NSNumber *idUser = [dict objectForKey:kJSON_ID_USER];
    if ( [idUser isKindOfClass:[NSNumber class]] )
        [self setIdUser:idUser];
    else
        result = NO;
    
    NSString *userName = [dict objectForKey:kJSON_USERNAME ];
    if ([userName isKindOfClass:[NSString class]])
        [self setUserName:userName];
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
