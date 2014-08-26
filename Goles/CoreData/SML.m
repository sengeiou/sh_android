#import "SML.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "Utils.h"

@interface SML ()

-(BOOL)setSMLValuesWithDictionary:(NSDictionary *)dict;
-(void)setDefaultSMLValues;

@end


#define SMLMessages         @{@"0":NSLocalizedString(@"Goool!",nil), @"1":NSLocalizedString(@"Gooooool!!!",nil), @"2":NSLocalizedString(@"Gol",nil)}
#define SMLSoundMessages    @{@"1":NSLocalizedString(@"Sistema",nil), @"2":NSLocalizedString(@"Gooooool!!!",nil), @"3":NSLocalizedString(@"_TeamDetailNeutralGoalSoundNameLabel",nil),@"4":NSLocalizedString(@"Silbidos",nil)}

@implementation SML

//------------------------------------------------------------------------------
+(SML *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    SML *sml = [NSEntityDescription insertNewObjectForEntityForName:@"SML"
                                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [sml setSMLValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:sml];
        return nil;
    }
    return sml;
}

//------------------------------------------------------------------------------
+(SML *)updateWithDictionary:(NSDictionary *)dict {
    
    NSNumber *idSML = [dict objectForKey:kJSON_ID_SML];
    
    if ( idSML ){
        SML *sml = [[CoreDataManager singleton] getEntity:[SML class] withId:[idSML integerValue]];
        if ( sml )
            [sml setSMLValuesWithDictionary:dict];              // Update entity
        else
            sml = [SML insertWithDictionary:dict];     // insert new entity
        return sml;
    }
    return nil;
}

#pragma mark - public methods
//------------------------------------------------------------------------------
+(SML *)createDefaultSML {

    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    SML *sml = [NSEntityDescription insertNewObjectForEntityForName:@"SML"
                                             inManagedObjectContext:context];
    [sml setDefaultSMLValues];
    return sml;
}

//------------------------------------------------------------------------------
+(SML *)createTemporarySML {
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"SML" inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    SML *sml = [[SML alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
    [sml setDefaultSMLValues];
    return sml;
}

//------------------------------------------------------------------------------
+(SML *)createTemporarySMLWithSML:(SML *)sml {
    
    SML *newSML = [self createTemporarySML];
    
    if ( sml ) {
        [newSML setIdSML:[sml idSML]];
        [newSML setSound:[sml sound]];
        [newSML setMessage:[sml message]];
        [newSML setLanguage:[sml language]];
    }
    
    return newSML;
}

//------------------------------------------------------------------------------
-(BOOL)updateValuesWithDictionary:(NSDictionary *)dict {
    return [self setSMLValuesWithDictionary:dict];
}

//------------------------------------------------------------------------------
-(NSString *)getMessageString {
    return [SMLMessages objectForKey:[[self message] stringValue]];
}

//------------------------------------------------------------------------------
-(NSString *)getSoundMessageString {
    return [SMLSoundMessages objectForKey:[[self sound] stringValue]];
}

#pragma mark - private methods
//------------------------------------------------------------------------------
-(void)setDefaultSMLValues {
    
    [self setSound:@1];
    [self setMessage:@0];
    [self setLanguage:[Utils getDeviceCurrentLanguageCode]];
}

//------------------------------------------------------------------------------
-(BOOL)setSMLValuesWithDictionary:(NSDictionary *)dict {
    
    if (![dict isKindOfClass:[NSDictionary class]] )        return NO;

    NSNumber *idSML = [dict objectForKey:kJSON_ID_SML];
    if ( [idSML isKindOfClass:[NSNumber class]] )
        [self setIdSML:idSML];
    else
        return NO;
    
    NSNumber *sound = [dict objectForKey:kJSON_SOUND];
    if ( [sound isKindOfClass:[NSNumber class]] )
        [self setSound:sound];
    else
        return NO;
    
    NSNumber *message = [dict objectForKey:kJSON_MESSAGE];
    if ( [message isKindOfClass:[NSNumber class]] )
        [self setMessage:message];
    else
        return NO;

    NSNumber *language = [dict objectForKey:kJSON_LANGUAGE];
    if ( [language isKindOfClass:[NSNumber class]] )
        [self setLanguage:language];
    else
        return NO;
    
    
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
