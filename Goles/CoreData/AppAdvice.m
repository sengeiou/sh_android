#import "AppAdvice.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "Message.h"

@interface AppAdvice ()

@end


@implementation AppAdvice

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
    
    NSString *identifier = [NSString stringWithFormat:@"id%@",[[self class] description]];
    NSNumber *idNumber = [dict objectForKey:identifier];
    
    if ( idNumber ){
        id objectInstance = [[CoreDataManager singleton] getEntity:[self class] withId:[idNumber integerValue]];
        if ( objectInstance )
            [objectInstance setObjectValuesWithDictionary:dict];      // Update entity
        else
            objectInstance = [self insertWithDictionary:dict];      // insert new entity
        return objectInstance;
    }
    return nil;
}

//------------------------------------------------------------------------------
+(NSArray *)getAppAdviceForPath:(NSString *)path {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"path = %@ && status = 1 && platform = 1",path];
    NSArray *appAdvices = [[CoreDataManager singleton] getAllEntities:[AppAdvice class] withPredicate:predicate];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:kJSON_ADVICE_WEIGHT ascending:NO];
    NSArray *arrayTemp = [NSArray arrayWithObject:sortDescriptor];
    NSArray *sortedArray = [appAdvices sortedArrayUsingDescriptors:arrayTemp];
    
    AppAdvice *advice = [sortedArray firstObject];
    if (advice){
        NSString *locale = [[NSLocale currentLocale]localeIdentifier];
        NSPredicate *predicateM = [NSPredicate predicateWithFormat:@"idMessage = %@ && platform = 1 && locale = %@",advice.message.idMessage,locale];
        NSArray *appMessages = [[CoreDataManager singleton] getAllEntities:[Message class] withPredicate:predicateM];
        if (appMessages.count > 0)
            return @[advice,appMessages.firstObject];
    }
    
    return nil;
}

#pragma mark - Private Methods
//------------------------------------------------------------------------------
-(BOOL)setObjectValuesWithDictionary:(NSDictionary *)dict {

    //NECESSARY PROPERTIES
    //-------------------------------
    NSNumber *idAppAdvice = [dict objectForKey:kJSON_ID_APPADVICE];
    if ( [idAppAdvice isKindOfClass:[NSNumber class]] )
        [self setIdAppAdvice:idAppAdvice];
    else
        return NO;

    NSNumber *idMessage = [dict objectForKey:kJSON_ID_MESSAGE];
    if ( [idMessage isKindOfClass:[NSNumber class]]){
        NSString *locale = [[NSLocale currentLocale] localeIdentifier];
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"idMessage = %@ && locale = %@ && platform = 1",idMessage, locale];
        NSArray *messages = [[CoreDataManager singleton] getAllEntities:[Message class] withPredicate:predicate];
        if ([messages count] > 0) {
            Message *message = [messages firstObject];
            [self setMessage:message];
        }
        else {
            NSPredicate *predicateTwo = [NSPredicate predicateWithFormat:@"idMessage = %@ && locale = %@ && platform = 1",idMessage,@"es_ES"];
            NSArray *messagesTwo = [[CoreDataManager singleton] getAllEntities:[Message class] withPredicate:predicateTwo];
            Message *messageTwo = [messagesTwo firstObject];
            if (messageTwo)
                [self setMessage:messageTwo];
            else
                return NO;
        }
    }
    
    //OPTIONAL PROPERTIES
    //-------------------------------
    
    NSString *path = [dict objectForKey:kJSON_ADVICE_PATH];
    if ( [path isKindOfClass:[NSString class]])
        [self setPath:path];
    
    NSNumber *status = [dict objectForKey:kJSON_ADVICE_STATUS];
    if ( [status isKindOfClass:[NSNumber class]])
        [self setStatus:status];
    
    NSNumber *platform = [dict objectForKey:kJSON_ADVICE_PLATFORM];
    if ( [platform isKindOfClass:[NSNumber class]])
        [self setPlatform:platform];
    
    NSNumber *button = [dict objectForKey:kJSON_ADVICE_BUTTON_VISIBLE];
    if ( [button isKindOfClass:[NSNumber class]])
        [self setVisibleButton:button];

    NSString *buttonAction = [dict objectForKey:kJSON_ADVICE_BUTTON_ACTION];
    if ( [buttonAction isKindOfClass:[NSString class]])
        [self setButtonAction:buttonAction];

    NSString *buttonData = [dict objectForKey:kJSON_ADVICE_BUTTON_DATA];
    if ( [buttonData isKindOfClass:[NSString class]])
        [self setButtonData:buttonData];

    NSNumber *versionStart = [dict objectForKey:kJSON_ADVICE_VERSION_START];
    if ( [versionStart isKindOfClass:[NSNumber class]])
        [self setStartVersion:versionStart];
    
    NSNumber *buttonTextID = [dict objectForKey:kJSON_ADVICE_BUTTON_TEXTID];
    if ( [buttonTextID isKindOfClass:[NSNumber class]])
        [self setButtonTextId:buttonTextID];

    
    NSNumber *versionEnd = [dict objectForKey:kJSON_ADVICE_VERSION_END];
    if ( [versionEnd isKindOfClass:[NSNumber class]])
        [self setEndVersion:versionEnd];
    
    NSNumber *weight = [dict objectForKey:kJSON_ADVICE_WEIGHT];
    if ( [weight isKindOfClass:[NSNumber class]])
        [self setWeight:weight];
    
    NSNumber *dateStart = [dict objectForKey:kJSON_ADVICE_DATESTART];
    if ([dateStart isKindOfClass:[NSNumber class]]) {
        NSTimeInterval epoch = [dateStart doubleValue]/1000;
        NSDate *dateStartDate = [NSDate dateWithTimeIntervalSince1970:epoch];
        if ([dateStartDate isKindOfClass:[NSDate class]])
            [self setStartDate:dateStartDate];
    }
    
    NSNumber *dateEnd = [dict objectForKey:kJSON_ADVICE_DATEEND];
    if ([dateEnd isKindOfClass:[NSNumber class]]) {
        NSTimeInterval epoch = [dateEnd doubleValue]/1000;
        NSDate *dateEndDate = [NSDate dateWithTimeIntervalSince1970:epoch];
        if ([dateEndDate isKindOfClass:[NSDate class]])
            [self setEndDate:dateEndDate];
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
