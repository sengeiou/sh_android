#import "MatchBetType.h"
#import "CoreDataParsing.h"
#import "CoreDataManager.h"
#import "BetType.h"
#import "BetTypeOdd.h"
#import "Provider.h"
#import "Match.h"

@interface MatchBetType ()

@end


@implementation MatchBetType

#pragma mark - Public Methods
//------------------------------------------------------------------------------
+(MatchBetType *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    MatchBetType *matchBetType = [NSEntityDescription insertNewObjectForEntityForName:@"MatchBetType"
                                                     inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [matchBetType setMatchBetTypeValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:matchBetType];
        return nil;
    }
    return matchBetType;
}

//------------------------------------------------------------------------------
+(MatchBetType *)updateWithDictionary:(NSDictionary *)dict {
    
    NSNumber *idMatchBetType = [dict objectForKey:kJSON_ID_MATCHBETTYPE];
    
    if ( idMatchBetType ){
        MatchBetType *matchBetType = [[CoreDataManager singleton] getEntity:[MatchBetType class] withId:[idMatchBetType integerValue]];
        if ( matchBetType )
            [matchBetType setMatchBetTypeValuesWithDictionary:dict];      // Update entity
        else
            matchBetType = [MatchBetType insertWithDictionary:dict];      // insert new entity
        
        return matchBetType;
    }
    return nil;
}

+(MatchBetType *)createTemporaryMatchBetType {
    
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"MatchBetType" inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    MatchBetType *matchBetType = [[MatchBetType alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
    return matchBetType;
}

#pragma mark - Private Methods
//------------------------------------------------------------------------------
-(BOOL)setMatchBetTypeValuesWithDictionary:(NSDictionary *)dict {
    
    //NECESSARY PROPERTIES
    //-------------------------------
    NSNumber *idMatchBetType = [dict objectForKey:kJSON_ID_MATCHBETTYPE];
    if ( [idMatchBetType isKindOfClass:[NSNumber class]] )
        [self setIdMatchBetType:idMatchBetType];
    else
        return NO;
    
    Provider *provider = [dict objectForKey:kJSON_PROVIDER];
    if (provider)
        [self setProvider:provider];
    else
        return NO;
    
    BetType *betType = [dict objectForKey:kJSON_BETTYPE];
    if (betType)
        [self setBetType:betType];
    else
        return NO;
    
    Match *match = [dict objectForKey:K_COREDATA_MATCH];
    if (match)
        [self setMatch:match];
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
