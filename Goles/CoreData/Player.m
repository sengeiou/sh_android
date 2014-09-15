#import "Player.h"
//#import "Prediction.h"
#import "Constants.h"
#import "CoreDataManager.h"
#import "Device.h"
#import "CoreDataParsing.h"


@interface Player ()


@end

@implementation Player

//------------------------------------------------------------------------------
+(Player *)insertWithDictionary:(NSDictionary *)dict{
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Player *player = [NSEntityDescription insertNewObjectForEntityForName:@"Player"
                                                   inManagedObjectContext:context];
    
    BOOL correctlyInserted = [player setPlayerValuesWithDictionary:dict];
    if ( !correctlyInserted ){
        [[CoreDataManager singleton] deleteObject:player];
        return nil;
    }
    
    return player;
}

//------------------------------------------------------------------------------
+(Player *)updateWithDictionary:(NSDictionary *)dict withIndex:(NSInteger)index{
    
    NSNumber *idPlayer = [dict objectForKey:kJSON_ID_USER];
    
    if ( idPlayer ){
        Player *player = [[CoreDataManager singleton] getEntity:[Player class] withId:[idPlayer integerValue]];
        
        if ( player )
            [player setPlayerValuesWithDictionary:dict];    // Update entity
        else
            player = [Player insertWithDictionary:dict];    // insert new entity
        
        return player;
    }
    return nil;
}


#pragma mark - Public Methods
//------------------------------------------------------------------------------
-(BOOL)setPlayerValuesWithDictionary:(NSDictionary *)dict {
    
    BOOL result = YES;
    
    NSNumber *idUser = [dict objectForKey:kJSON_ID_USER];
    if ( [idUser isKindOfClass:[NSNumber class]] )
        [self setIdUser:idUser];
    else
        result = NO;
    
    NSString *nick = [dict objectForKey:kJSON_NICK ];
    if ([nick isKindOfClass:[NSNull class]]) {
        [self setNick:nick];
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
