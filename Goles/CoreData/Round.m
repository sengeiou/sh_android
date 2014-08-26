#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface Round()

-(BOOL)setRoundValuesWithDictionary:(NSDictionary *)dict;

@end

@implementation Round

#pragma marck - Private methods
//------------------------------------------------------------------------------
+(Round *)insertWithDictionary:(NSDictionary *)dict
{
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Round *round = [NSEntityDescription insertNewObjectForEntityForName:@"Round" inManagedObjectContext:context];
    
    BOOL correctlyInserted = [round setFixtureValuesWithDictionary:dict];
    if ( !correctlyInserted ){
        [[CoreDataManager singleton] deleteObject:round];
        return nil;
    }
    
    return round;
}

//------------------------------------------------------------------------------
+(Round *)updateWithDictionary:(NSDictionary *)dict {
    
    NSNumber *idRound = [dict objectForKey:kJSON_ID_ROUND];
    
    if ( idRound ){
        Round *round = [[CoreDataManager singleton] getEntity:[Round class] withId:[idRound integerValue]];
        if ( round )
            [round setFixtureValuesWithDictionary:dict];      // Update entity
        else
            round = [Round insertWithDictionary:dict];      // insert new entity
        return round;
    }
    return nil;
}


#pragma mark - Private methods
//------------------------------------------------------------------------------
-(NSArray *)getSortedMatchesByDate {
    
    NSArray *sortedMatches = nil;

    if ([[self matchesList] count] > 0 ){
        
        NSArray *descriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"matchDate" ascending:YES],
                                 [NSSortDescriptor sortDescriptorWithKey:@"idMatch" ascending:YES]];
        sortedMatches = [[self matchesList]sortedArrayUsingDescriptors:descriptors];
    }
    return sortedMatches;
}

//------------------------------------------------------------------------------
-(void)unlinkMatchesNotInArray:(NSArray *)matchIdsArray {
    
    if ( matchIdsArray ){
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"NOT(idMatch IN %@)", matchIdsArray];
        NSSet *unlinkedMatches = [[self matchesList] filteredSetUsingPredicate:predicate];
        for ( Match *match in unlinkedMatches)
            [match setRound:nil];
    }
}

#pragma mark - Private methods
//------------------------------------------------------------------------------
-(BOOL)setFixtureValuesWithDictionary:(NSDictionary *)dict {
    BOOL result = YES;
    
    [self setIdRound:[dict objectForKey:kJSON_ID_ROUND]];
    [self setStartDate:[dict objectForKey:kJSON_ROUND_START_DATE ]];
    [self setEndDate:[dict objectForKey:kJSON_ROUND_END_DATE]];
    [self setName:[dict objectForKey:kJSON_ROUND_NAME]];
    [self setCampaign:[dict objectForKey:@"campaign"]];
    [self setIdCampaign:[dict objectForKey:@"campaignID"]];
    
    // Set fixture matches
    NSArray *matchesArray = [dict objectForKey:kJSON_MATCHES];
    if ( [matchesArray isKindOfClass:[NSArray class]] && [matchesArray count] > 0 )
    {
        for ( NSInteger index = 0; index<[matchesArray count]; ++index ){
            NSDictionary *matchDict = [matchesArray objectAtIndex:index];
            Match *match = [Match updateWithDictionary:matchDict withIndex:index];
            [match setRound:self];
            [[self matchesListSet] addObject:match];
        }
    }else
        result = NO;
    
    //Set Fixture Type
    // 0 = Partido ida
    // 1 = Partido vuelta
    // 2 = indeterminado

    NSNumber *fixtureType = [dict objectForKey:kJSON_ROUND_TYPE];
    if ( [fixtureType isKindOfClass:[NSNumber class]] )
        [self setRoundType:fixtureType];
    else {
        Match *match = [[self matchesList] anyObject];
        if ( [match idWinnerTeam] )
            [self setRoundType:@1];       // 1 -> Fixture type "vuelta"
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
