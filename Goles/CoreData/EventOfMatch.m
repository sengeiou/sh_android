#import "EventOfMatch.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "Event.h"

@interface EventOfMatch ()

// Private interface goes here.

@end


@implementation EventOfMatch

//------------------------------------------------------------------------------
+(EventOfMatch *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    EventOfMatch *eventOfMatch = [NSEntityDescription insertNewObjectForEntityForName:@"EventOfMatch"
                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [eventOfMatch setEventOfMatchValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:eventOfMatch];
        return nil;
    }
    return eventOfMatch;
}

//------------------------------------------------------------------------------
+(EventOfMatch *)updateWithDictionary:(NSDictionary *)dict {
    
    NSNumber *idEventOfMatch = [dict objectForKey:kJSON_ID_EVENT_OF_MATCH];
    
    if ( idEventOfMatch ){
        EventOfMatch *eventOfMatch = [[CoreDataManager singleton] getEntity:[EventOfMatch class] withId:[idEventOfMatch integerValue]];
        if ( eventOfMatch )
            [eventOfMatch setEventOfMatchValuesWithDictionary:dict];      // Update entity
        else
            eventOfMatch = [EventOfMatch insertWithDictionary:dict];      // insert new entity
      
        return eventOfMatch;
    }
    return nil;
}

//------------------------------------------------------------------------------
-(void)twittDone {
    
    [self setTwittGoalDoneValue:YES];
    [[CoreDataManager singleton] saveContext];
    
}

#pragma mark - private methods
//------------------------------------------------------------------------------
-(BOOL)setEventOfMatchValuesWithDictionary:(NSDictionary *)dict {
    
    if (![dict isKindOfClass:[NSDictionary class]] )
        return NO;
    
    NSNumber *idEventOfMatch = [dict objectForKey:kJSON_ID_EVENT_OF_MATCH];
    if ( [idEventOfMatch isKindOfClass:[NSNumber class]] )
        [self setIdEventOfMatch:idEventOfMatch];
    else
        return NO;
    
    NSNumber *idMatch = [dict objectForKey:kJSON_ID_MATCH];
    if ( [idMatch isKindOfClass:[NSNumber class]] )
        [self setIdMatch:idMatch];
    else
        return NO;

    
    NSNumber *idEvent = [dict objectForKey:kJSON_EVENT_ID];
    if ( [idEvent isKindOfClass:[NSNumber class]] )
        [self setIdEvent:idEvent];
    else
        return NO;
    
    NSString *actorTransmiterName = [dict objectForKey:kJSON_EVENT_ACTOR_TRANSMITTER_NAME];
    if ( [actorTransmiterName isKindOfClass:[NSString class]] )
        [self setActorTransmitterName:actorTransmiterName];
    else
        return NO;
    
    NSNumber *minuteOfMatch = [NSNumber numberWithInt:[[dict objectForKey:kJSON_TIME_MATCH] integerValue]];
    if ( [minuteOfMatch isKindOfClass:[NSNumber class]] )
        [self setMatchMinute:minuteOfMatch];
    else
        return NO;
    
    NSNumber *localScore = [dict objectForKey:kJSON_EVENT_LOCAL_SCORE];
    if ( [localScore isKindOfClass:[NSNumber class]] )
        [self setLocalScore:localScore];
    else
        return NO;
    
    NSNumber *visitorScore = [dict objectForKey:kJSON_EVENT_VISITOR_SCORE];
    if ( [visitorScore isKindOfClass:[NSNumber class]] )
        [self setVisitorScore:visitorScore];
    else
        return NO;
    
    NSNumber *timeStamp = [dict objectForKey:kJSON_EVENT_DATE];
    if ( [timeStamp isKindOfClass:[NSNumber class]] ) {
        NSDate *dateEvent = [NSDate dateWithTimeIntervalSince1970:[timeStamp doubleValue]/1000.0];
        [self setDateIn:dateEvent];
    }
    else
        return NO;

    /*NSString *comments = [dict objectForKey:kJSON_EVENT_COMMENTS];
    if ( [comments isKindOfClass:[NSString class]] ) {
        [self setComments:comments];
    }
    else
        return NO;*/
    
    NSNumber *eventId = [dict objectForKey:kJSON_EVENT_ID];
    if ( [eventId isKindOfClass:[NSNumber class]] ) {
        Event *event = [[CoreDataManager singleton] getEntity:[Event class] withId:[eventId integerValue]];
        [self setEvent:event];
    }
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
