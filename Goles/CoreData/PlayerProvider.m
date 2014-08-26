#import "PlayerProvider.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "UserManager.h"

@interface PlayerProvider ()


@end


@implementation PlayerProvider

//------------------------------------------------------------------------------
+(PlayerProvider *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    PlayerProvider *playerProvider = [NSEntityDescription insertNewObjectForEntityForName:@"PlayerProvider"
                                                 inManagedObjectContext:context];
    BOOL correctlyInserted = [playerProvider setPlayerProviderValuesWithDictionary:dict];
    if ( !correctlyInserted ){
        [[CoreDataManager singleton] deleteObject:playerProvider];
        return nil;
    }
    
    return playerProvider;
}

//------------------------------------------------------------------------------
+(PlayerProvider *)updateWithDictionary:(NSDictionary *)dict {
    
    NSNumber *idPlayerProvider = [dict objectForKey:kJSON_ID_BETTYPE];
    
    if ( idPlayerProvider ){
        PlayerProvider *playerProvider = [[CoreDataManager singleton] getEntity:[PlayerProvider class] withId:[idPlayerProvider integerValue]];
        if ( playerProvider )
            [playerProvider setPlayerProviderValuesWithDictionary:dict];      // Update entity
        else
            playerProvider = [PlayerProvider insertWithDictionary:dict];      // insert new entity
        return playerProvider;
    }
    return nil;
}

#pragma mark - Private Methods
//------------------------------------------------------------------------------
-(BOOL)setPlayerProviderValuesWithDictionary:(NSDictionary *)dict{
    
    //NECESSARY PROPERTIES
    //-------------------------------
    NSNumber *idPlayerProvider = [dict objectForKey:kJSON_ID_BETTYPE];
    if ( [idPlayerProvider isKindOfClass:[NSNumber class]] )
        [self setIdPlayerProvider:idPlayerProvider];
    else
        return NO;

    //OPTIONAL PROPERTIES
    //-------------------------------
    NSNumber *visible = [dict objectForKey:kJSON_ALWAYS_VISIBLE];
    if ( [visible isKindOfClass:[NSNumber class]] )
        [self setVisible:visible];

    NSNumber *weight = [dict objectForKey:kJSON_WEIGHT];
    if ( [weight isKindOfClass:[NSNumber class]] )
        [self setWeight:weight];

    NSNumber *status = [dict objectForKey:kJSON_WEIGHT];
    if ( [status isKindOfClass:[NSNumber class]] )
        [self setStatus:status];
    
    
    //RELATIONSHIPS
    Player *player = [[UserManager singleton] mUser];
    [self setPlayer:player];
    
    
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
