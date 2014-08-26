#import "Mode.h"
#import "Team.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface Mode ()

-(BOOL)setModeValuesWithDictionary:(NSDictionary *)dict;

@end

@implementation Mode

//------------------------------------------------------------------------------
+(Mode *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Mode *mode = [NSEntityDescription insertNewObjectForEntityForName:@"Mode"
                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [mode setModeValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:mode];
        return nil;
    }
    return mode;
}

//------------------------------------------------------------------------------
+(Mode *)insertWithDictionary:(NSDictionary *)dict andIndex:(NSInteger)index{
    
    Mode *mode = [self insertWithDictionary:dict];
    if ( mode )        [mode setOrderValue:index];
    return mode;
}

//------------------------------------------------------------------------------
+(Mode *)updateWithDictionary:(NSDictionary *)dict {
    return [self updateWithDictionary:dict withIndex:-1];
}

//------------------------------------------------------------------------------
+(Mode *)updateWithDictionary:(NSDictionary *)dict withIndex:(NSInteger)index{
    
    NSNumber *idMode = [dict objectForKey:kJSON_ID_LEAGUE];
    
    if ( idMode ){
        Mode *mode = [[CoreDataManager singleton] getEntity:[Mode class] withId:[idMode integerValue]];
        if ( mode )
            [mode setModeValuesWithDictionary:dict];      // Update entity
        else
            mode = [Mode insertWithDictionary:dict];      // insert new entity
        [mode setOrderValue:index];
        return mode;
    }
    return nil;
}

#pragma mark - public methods

//------------------------------------------------------------------------------
+(Mode *)createTemporaryMode{
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Mode" inManagedObjectContext:[[CoreDataManager singleton] getContext]];
    return [[Mode alloc] initWithEntity:entity insertIntoManagedObjectContext:nil];
}

//------------------------------------------------------------------------------
+(Mode *)createTemporaryModeWithMode:(Mode *)mode {
    
    Mode *tempMode = [self createTemporaryMode];
    [tempMode setIdMode:[mode idMode]];
    [tempMode setName:[mode name]];
    return tempMode;
}

//------------------------------------------------------------------------------
-(NSArray *)getOrderedMatches {

    NSArray *descriptors = [NSArray arrayWithObject:[NSSortDescriptor sortDescriptorWithKey:@"order" ascending:YES]];
    NSArray *sortedTeams = [[self teams] sortedArrayUsingDescriptors:descriptors];
    if ( [sortedTeams count] > 0 )        return sortedTeams;
    return nil;
}

#pragma mark - private methods
//------------------------------------------------------------------------------
- (NSString*)getLocalizedDescriptionFromLanguagesList: (NSArray *)languagesArray {
    NSString *result = nil;
    
    NSString * language = [[[[NSLocale preferredLanguages] objectAtIndex:0] substringToIndex:2] lowercaseString];
    int languageIndex = 0;
    
    if ([language isEqualToString:@"es"]) {
        languageIndex = 0;
    }
    else if ([language isEqualToString:@"en"]) {
        languageIndex = 1;
    }
    
    for (NSDictionary *language in languagesArray) {
        if ([language[@"languageCode"] integerValue] == languageIndex) {
            result = language [@"description"];
            break;
        }
    }
    
    if (!result && [languagesArray count] > 0)
        result = [languagesArray objectAtIndex:0][@"description"];
    
    return result;
}

//------------------------------------------------------------------------------
-(BOOL)setModeValuesWithDictionary:(NSDictionary *)dict {
    
    if (![dict isKindOfClass:[NSDictionary class]] )        return NO;
    
    NSNumber *idMode = [dict objectForKey:kJSON_ID_LEAGUE];
    if ( [idMode isKindOfClass:[NSNumber class]] )
        [self setIdMode:idMode];
    else
        return NO;
    
    //CACTUS: parsing name with OLD data
    NSArray *languages = dict[@"messageList"];
    NSString *name = [self getLocalizedDescriptionFromLanguagesList:languages];
    if ( [name isKindOfClass:[NSString class]] )
        [self setName:name];
    else
        return NO;
    
    NSArray *teams = [dict objectForKey:kJSON_TEAMS];
    if ( [teams isKindOfClass:[NSArray class]] && [teams count] ) {
        [[CoreDataManager singleton] updateEntities:[Team class] WithOrderedArray:teams];
        [[CoreDataManager singleton] unlinkTeams:teams fromMode:idMode];
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


    return YES;
}

@end
