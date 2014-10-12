#import "Follow.h"
#import "CoreDataManager.h"


@implementation Follow

//------------------------------------------------------------------------------
+(Follow *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Follow *follow = [NSEntityDescription insertNewObjectForEntityForName:@"Follow"
                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [follow setFollowValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:follow];
        return nil;
    }
    return follow;
}

//------------------------------------------------------------------------------
+(instancetype)updateWithDictionary:(NSDictionary *)dict {

    NSNumber *idUser = [dict objectForKey:kJSON_ID_USER];
    NSNumber *idUserFollowed = [dict objectForKey:kJSON_FOLLOW_IDUSERFOLLOWED];

    if ( idUser && idUserFollowed ){
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idUser = %@ && idUserFollowed = %@",idUser,idUserFollowed];
        NSArray *objectsArray = [[CoreDataManager singleton] getAllEntities:[Follow class] withPredicate:predicate];
        Follow *follow = [objectsArray firstObject];
        
        if (follow && (![[dict objectForKey:K_WS_OPS_DELETE_DATE] isKindOfClass:[NSNull class]] || ![dict objectForKey:K_WS_OPS_DELETE_DATE])) {
            [[CoreDataManager singleton] deleteObject:follow];
            return nil;
        }
        else if ((![[dict objectForKey:K_WS_OPS_DELETE_DATE] isKindOfClass:[NSNull class]] || ![dict objectForKey:K_WS_OPS_DELETE_DATE]))
            return nil;
        else if (follow)
            [follow setFollowValuesWithDictionary:dict];      // Update entity
        else
            follow = [self insertWithDictionary:dict];      // insert new entity
        return follow;
    }
    return nil;
}

//------------------------------------------------------------------------------
-(BOOL)setFollowValuesWithDictionary:(NSDictionary *)dict {
    
    BOOL result = YES;
    
    NSNumber *idUser = [dict objectForKey:kJSON_ID_USER];
    if ( [idUser isKindOfClass:[NSNumber class]])
        [self setIdUser:idUser];
    else
        result = NO;
    
    NSNumber *idUserFollowed = [dict objectForKey:kJSON_FOLLOW_IDUSERFOLLOWED];
    if ([idUserFollowed isKindOfClass:[NSNumber class]])
        [self setIdUserFollowed:idUserFollowed];
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

//------------------------------------------------------------------------------
+ (NSDictionary *)createDictFromEntity:(Follow *)follow {

    NSDictionary *followDict = @{kJSON_ID_USER:follow.idUser,kJSON_FOLLOW_IDUSERFOLLOWED:follow.idUserFollowed,K_WS_OPS_BIRTH_DATE:follow.csys_birth,K_WS_OPS_UPDATE_DATE:follow.csys_modified,K_WS_OPS_REVISION:follow.csys_revision};
    return followDict;
}


@end
