#import "Message.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface Message ()

@end


@implementation Message

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
    
    NSNumber *idMessage = [dict objectForKey:kJSON_ID_MESSAGE];
    NSString *locale = [dict objectForKey:kJSON_MESSAGE_LANGUAGE];
    NSString *text = [dict objectForKey:kJSON_MESSAGE_MESSAGE];
    
    if (idMessage && locale && text) {
        
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idMessage = %@ && locale = %@",idMessage,locale];

        NSArray *messages = [[CoreDataManager singleton] getAllEntities:[Message class] withPredicate:predicate];
        id message = [messages firstObject];
        if (message)
            [message setObjectValuesWithDictionary:dict];      // Update entity
        else
            [self insertWithDictionary:dict];      // insert new entity
        
        return message;
    }
    
    return nil;
}

#pragma mark - Private Methods
//------------------------------------------------------------------------------
-(BOOL)setObjectValuesWithDictionary:(NSDictionary *)dict {
    
    //NECESSARY PROPERTIES
    //-------------------------------
    NSNumber *idMessage = [dict objectForKey:kJSON_ID_MESSAGE];
    if ( [idMessage isKindOfClass:[NSNumber class]] )
        [self setIdMessage:idMessage];
    else
        return NO;
    
    NSString *language = [dict objectForKey:kJSON_MESSAGE_LANGUAGE];
    if ( [language isKindOfClass:[NSString class]])
        [self setLocale:language];
    else
        return NO;
    
    NSString *messageText = [dict objectForKey:kJSON_MESSAGE_MESSAGE];
    if ( [messageText isKindOfClass:[NSString class]])
        [self setMessage:messageText];
    else
        return NO;

    //OPTIONAL PROPERTIES
    //-------------------------------
    
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
    
    return YES;}

@end
