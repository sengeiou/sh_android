#import "Provider.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface Provider ()

-(BOOL)setProviderValuesWithDictionary:(NSDictionary *)dict;

@end



@implementation Provider
//------------------------------------------------------------------------------
+(Provider *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Provider *provider = [NSEntityDescription insertNewObjectForEntityForName:@"Provider"
                                                       inManagedObjectContext:context];
    
    BOOL correctlyInserted = [provider setProviderValuesWithDictionary:dict];
    if ( !correctlyInserted ){
        [[CoreDataManager singleton] deleteObject:provider];
        return nil;
    }
    return provider;
}

//------------------------------------------------------------------------------
+(Provider *)createTemporaryProvider {
    
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Provider" inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    Provider *provider = [[Provider alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
    return provider;
}

//------------------------------------------------------------------------------
+(Provider *)updateWithDictionary:(NSDictionary *)dict {
    
    NSNumber *idProvider = [dict objectForKey:kJSON_ID_PROVIDER];
    
    if ( idProvider ){
        Provider *provider = [[CoreDataManager singleton] getEntity:[Provider class] withId:[idProvider integerValue]];
        
        if ( provider )
            [provider setProviderValuesWithDictionary:dict];    // Update entity
        else
            provider = [Provider insertWithDictionary:dict];    // insert new entity
        
        return provider;
    }
    return nil;
}

#pragma marck - Private Methods
//------------------------------------------------------------------------------
-(BOOL)setProviderValuesWithDictionary:(NSDictionary *)dict{
        
    NSNumber *idProvider = [dict objectForKey:kJSON_ID_PROVIDER];
    if ( [idProvider isKindOfClass:[NSNumber class]] )
        [self setIdProvider:idProvider];
    else
        return NO;

    NSString *uniqueKey = [dict objectForKey:kJSON_UNIQUE_KEY];
    if ([uniqueKey isKindOfClass:[NSString class]])
        [self setUniqueKey:uniqueKey];
    else
        return NO;
    
    NSString *name = [dict objectForKey:kJSON_NAME];
    if ([name isKindOfClass:[NSString class]])
        [self setName:name];
    
    NSNumber *activePorra = [dict objectForKey:kJSON_SWEEPSTAKE_ACTIVE];
    if ([activePorra isKindOfClass:[NSNumber class]])
        [self setActivePorraValue:[activePorra boolValue]];
    
    NSNumber *visible = [dict objectForKey:kJSON_VISIBLE];
    if ([visible isKindOfClass:[NSNumber class]])
        [self setVisibleIOSValue:[visible boolValue]];
    
    NSNumber *weight = [dict objectForKey:kJSON_WEIGHT];
    if ([weight isKindOfClass:[NSNumber class]])
        [self setWeight:weight];
    
    NSString *disclaimer = [dict objectForKey:kJSON_DISCLAIMER];
    if ([disclaimer isKindOfClass:[NSString class]])
        [self setDisclaimer:disclaimer];

    NSString *registryURL = [dict objectForKey:kJSON_REGISTRYURL];
    if ([registryURL isKindOfClass:[NSString class]])
        [self setRegistryURL:registryURL];

    NSString *comment = [dict objectForKey:kJSON_COMMENT];
    if ([comment isKindOfClass:[NSString class]])
        [self setComment:comment];
    
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
