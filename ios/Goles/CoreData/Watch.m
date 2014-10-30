#import "Watch.h"
#import "CoreDataManager.h"

@interface Watch ()

// Private interface goes here.

@end


@implementation Watch

// Custom logic goes here.
//------------------------------------------------------------------------------
+(Watch *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Watch *watch = [NSEntityDescription insertNewObjectForEntityForName:@"Watch"
                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [watch setWatchValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:watch];
        return nil;
    }
    return watch;
}

//------------------------------------------------------------------------------
+(instancetype)updateWithDictionary:(NSDictionary *)dict {
    
    NSString *identifier = [NSString stringWithFormat:@"id%@",[[self class] description]];
    NSNumber *idNumber = [dict objectForKey:identifier];
    
    if ( idNumber ){
        id objectInstance = [[CoreDataManager singleton] getEntity:[self class] withId:[idNumber integerValue]];
        
        if (objectInstance && (![[dict objectForKey:K_WS_OPS_DELETE_DATE] isKindOfClass:[NSNull class]] || ![dict objectForKey:K_WS_OPS_DELETE_DATE])) {
            [[CoreDataManager singleton] deleteObject:objectInstance];
            return nil;
        }
        else if ((![[dict objectForKey:K_WS_OPS_DELETE_DATE] isKindOfClass:[NSNull class]] || ![dict objectForKey:K_WS_OPS_DELETE_DATE]))
            return nil;
        else if ( objectInstance )
            [objectInstance setWatchValuesWithDictionary:dict];      // Update entity
        else
            objectInstance = [self insertWithDictionary:dict];      // insert new entity
        return objectInstance;
    }
    return nil;
}

//------------------------------------------------------------------------------
-(BOOL)setWatchValuesWithDictionary:(NSDictionary *)dict {
    
    BOOL result = YES;
    
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
