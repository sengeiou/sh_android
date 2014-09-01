
#import "Subscription.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "UserManager.h"
#import "Team.h"
#import "SML.h"
#import "Utils.h"

@interface Subscription ()

@end


@implementation Subscription

//------------------------------------------------------------------------------
+(Subscription *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Subscription *subscription = [NSEntityDescription insertNewObjectForEntityForName:@"Subscription"
                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [subscription setSubscriptionValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:subscription];
        return nil;
    }
    return subscription;
}

//------------------------------------------------------------------------------
+(Subscription *)updateWithDictionary:(NSDictionary *)dict {
    
    NSNumber *idSubscription = [dict objectForKey:kJSON_ID_SUBSCRIPTION];
    
    if ( idSubscription ){
        Subscription *subscription = [[CoreDataManager singleton] getEntity:[Subscription class] withId:[idSubscription integerValue]];
        if ( subscription )
            [subscription setSubscriptionValuesWithDictionary:dict];      // Update entity
        else
            subscription = [Subscription insertWithDictionary:dict];      // insert new entity
        return subscription;
    }
    return nil;
}

#pragma mark - public method
//------------------------------------------------------------------------------
+(Subscription *)createTemporarySubscription {
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Subscription" inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    Subscription *subscription = [[Subscription alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
    return subscription;
}

//------------------------------------------------------------------------------
+(Subscription *)createTemporarySubscriptionWithSubscription:(Subscription *)subscription {
    
    Subscription *newSubscription = [self createTemporarySubscription];
    
    if ( subscription ){
        [newSubscription setIdAllEvents:[subscription idAllEvents]];
        [newSubscription setIdDevice:[subscription idDevice]];
        [newSubscription setIdSubscription:[subscription idSubscription]];
    }
    
    SML *newSML = [SML createTemporarySMLWithSML:[subscription sml] ];
    [newSubscription setSml:newSML];
    
    return newSubscription;
}

//------------------------------------------------------------------------------
-(NSUInteger)getSubscriptionsNumber{
    
    NSUInteger value = self.idAllEventsValue;
    NSUInteger securityIDAllEvents = 0;
    NSUInteger subscriptionsCounter = 0;
    
    if ( value & goles ){
        ++subscriptionsCounter;
        securityIDAllEvents = securityIDAllEvents + goles;
    }
    if  (((value & inicio) == inicio) &&
         ((value & fin) == fin) &&
         ((value & suspendido) == suspendido) &&
         ((value & endmin90) == endmin90) &&
         ((value & inicioProrroga) == inicioProrroga) &&
         ((value & finalProrroga) == finalProrroga) &&
         ((value & inicioPenalties) == inicioPenalties)){
        securityIDAllEvents = securityIDAllEvents + inicio+fin+suspendido+endmin90+inicioProrroga+finalProrroga+inicioPenalties;
        ++subscriptionsCounter;
    }

    if ( value & expulsiones ) {
        securityIDAllEvents = securityIDAllEvents + expulsiones;
        ++subscriptionsCounter;
    }
    
    if ( value & recordatoriounahoraantes ){
        securityIDAllEvents = securityIDAllEvents + recordatoriounahoraantes;
        ++subscriptionsCounter;
    }
    if (((value & descanso) == descanso) && ((value & finDescanso) == finDescanso)){
        securityIDAllEvents = securityIDAllEvents + descanso+finDescanso;
        ++subscriptionsCounter;
    }
    if ( value & alineacion ){
        securityIDAllEvents = securityIDAllEvents + alineacion;
        ++subscriptionsCounter;
    }
    if (((value & penaltis) == penaltis) &&
        ((value & penaltiRoja) == penaltiRoja) &&
        ((value & penaltiFallado) == penaltiFallado) &&
        ((value & penaltiAmarilla) == penaltiAmarilla)){
        securityIDAllEvents = securityIDAllEvents + penaltis+penaltiRoja+penaltiFallado+penaltiAmarilla;
        ++subscriptionsCounter;
    }
    
    if ( value & amarilla ){
        securityIDAllEvents = securityIDAllEvents + amarilla;
        ++subscriptionsCounter;
    }
    if ( value & cambios ) {
        securityIDAllEvents = securityIDAllEvents + cambios;
        ++subscriptionsCounter;
    }

    
    self.idAllEvents = [NSNumber numberWithInt:securityIDAllEvents];
    [[CoreDataManager singleton] saveContext];
    
    return subscriptionsCounter;
}

//------------------------------------------------------------------------------
-(BOOL)isOnSubscription:(kSubscriptionNum)subscription {
    
    return (self.idAllEventsValue & subscription) == subscription;
}

//------------------------------------------------------------------------------
-(BOOL)isInicioBlockOn:(Subscription *)subscription {
    
    if  (((self.idAllEventsValue & inicio) == inicio) &&
        ((self.idAllEventsValue & fin) == fin) &&
        ((self.idAllEventsValue & suspendido) == suspendido) &&
        ((self.idAllEventsValue & endmin90) == endmin90) &&
        ((self.idAllEventsValue & inicioProrroga) == inicioProrroga) &&
        ((self.idAllEventsValue & finalProrroga) == finalProrroga) &&
        ((self.idAllEventsValue & inicioPenalties) == inicioPenalties)) {
        return YES;
    }

    return NO;
}

//------------------------------------------------------------------------------
-(BOOL)isAdicionalBlockOn:(Subscription *)subscription {
    
    return  ((((self.idAllEventsValue & recordatoriounahoraantes) == recordatoriounahoraantes) &&
              ((self.idAllEventsValue & descanso) == descanso) &&
              ((self.idAllEventsValue & alineacion) == alineacion) &&
              ((self.idAllEventsValue & penaltis) == penaltis) &&
              ((self.idAllEventsValue & penaltiRoja) == penaltiRoja) &&
              ((self.idAllEventsValue & penaltiFallado) == penaltiFallado) &&
              ((self.idAllEventsValue & penaltiAmarilla) == penaltiAmarilla) &&
              ((self.idAllEventsValue & amarilla) == amarilla) &&
              ((self.idAllEventsValue & cambios) == cambios) &&
              ((self.idAllEventsValue & cuotas) == cuotas)));
}

#pragma mark - private methods
//------------------------------------------------------------------------------
-(BOOL)setSubscriptionValuesWithDictionary:(NSDictionary *)dict {
    
    if (![dict isKindOfClass:[NSDictionary class]] )        return NO;
    
    NSNumber *idSubscription = [dict objectForKey:kJSON_ID_SUBSCRIPTION];
    if ( [idSubscription isKindOfClass:[NSNumber class]] )
        [self setIdSubscription:idSubscription];

    NSNumber *idDevice = [[UserManager sharedInstance] getIdDevice];
    [self setIdDevice:idDevice];


    NSNumber *idAllTeams = [dict objectForKey:kJSON_ID_ALL_EVENTS];
    if ( [idDevice isKindOfClass:[NSNumber class]] )
        [self setIdAllEvents:idAllTeams];
    else
        return NO;
    
    NSNumber *idTeam = [dict objectForKey:kJSON_ID_TEAM];
    NSNumber *idMatch = [dict objectForKey:kJSON_ID_MATCH];
    if ( [idTeam isKindOfClass:[NSNumber class]] ) {
        Team *team = [[CoreDataManager singleton] getEntity:[Team class] withId:[idTeam integerValue]];
        if ( team )
            [self setTeam:team];
        else
            return NO;
        
    } else if ( [idMatch isKindOfClass:[NSNumber class]] ) {
        Match *match = [[CoreDataManager singleton] getEntity:[Match class] withId:[idMatch integerValue]];
        if ( match )
            [self setMatch:match];
        else
            return NO;
        
    } else {
        return NO;
    }

    NSNumber *idSML = [dict objectForKey:kJSON_ID_SML];
    SML *sml = [[CoreDataManager sharedInstance] getEntity:[SML class] withId:[idSML integerValue]];
    if (sml)
        [self setSml:sml];
    else
        return NO;

    NSNumber *negation = [dict objectForKey:kJSON_NEGATION];
    if ( [negation isKindOfClass:[NSNumber class]] )
        [self setNegation:negation];
    

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
