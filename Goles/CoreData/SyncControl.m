#import "SyncControl.h"
#import "CoreDataManager.h"

@implementation SyncControl

#pragma mark - Public Methods
//------------------------------------------------------------------------------
+(instancetype)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    id objectInstance = [NSEntityDescription insertNewObjectForEntityForName:[[self class] description] inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [objectInstance setObjectValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:objectInstance];
        return nil;
    }
    return objectInstance;
}

//------------------------------------------------------------------------------
+(instancetype)updateWithDictionary:(NSDictionary *)dict {
    
    NSString *identifier = [dict objectForKey:k_SYNC_NAME_ENTITY];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"nameEntity = %@",identifier];
    NSArray *objectInstance = [[CoreDataManager singleton] getAllEntities:[SyncControl class] withPredicate:predicate];
    SyncControl *syncObject = [objectInstance firstObject];
    
    if (syncObject)
        [syncObject setObjectValuesWithDictionary:dict];      // Update entity
    else
        syncObject = [self insertWithDictionary:dict];      // insert new entity
    
    return syncObject;
}

//------------------------------------------------------------------------------
+(BOOL)deleteWithDictionary:(NSDictionary *)dict{
    
    NSString *identifier = [dict objectForKey:k_SYNC_NAME_ENTITY];
    
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"nameEntity = %@",identifier];
    NSArray *objectInstance = [[CoreDataManager singleton] getAllEntities:[SyncControl class] withPredicate:predicate];
    SyncControl *syncObject = [objectInstance firstObject];
    if (syncObject) {
        [syncObject.managedObjectContext deleteObject:syncObject];
        [[CoreDataManager singleton] saveContext];
        return YES;
    }
    return NO;
}

#pragma mark - Private Methods
//------------------------------------------------------------------------------
-(BOOL)setObjectValuesWithDictionary:(NSDictionary *)dict {
    
    NSString *name = [dict objectForKey:k_SYNC_NAME_ENTITY];
    if ( [name isKindOfClass:[NSString class]] )
        [self setNameEntity:name];
    else
        return NO;
    
    NSNumber *lastServerDate = [dict objectForKey:k_SYNC_LASTSERVER_DATE];
    if ([lastServerDate isKindOfClass:[NSNumber class]])
        [self setLastServerDate:lastServerDate];
    else
        return NO;
    
    NSNumber *lastCall = [dict objectForKey:k_SYNC_LASTCALL];
    if ([lastCall isKindOfClass:[NSNumber class]])
        [self setLastCall:lastCall];
    
    NSNumber *priority = [dict objectForKey:k_SYNC_PRIORITY];
    if ([priority isKindOfClass:[NSNumber class]])
        [self setUpdatePriority:priority];

    NSString *alias = [dict objectForKey:k_SYNC_ALIAS];
    if ( [alias isKindOfClass:[NSString class]] )
        [self setAliasView:alias];
    
    return YES;
}

@end
