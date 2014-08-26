#import "LineUp.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface LineUp ()


@end


@implementation LineUp

//------------------------------------------------------------------------------
+(LineUp *)insertWithDictionary:(NSDictionary *)dict {
 
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    LineUp *lineup = [NSEntityDescription insertNewObjectForEntityForName:@"LineUp"
                                                 inManagedObjectContext:context];
    BOOL correctlyInserted = [lineup setMatchValuesWithDictionary:dict];
    if ( !correctlyInserted ){
        [[CoreDataManager singleton] deleteObject:lineup];
        return nil;
    }
    
    return lineup;
}

//------------------------------------------------------------------------------
+(LineUp *)updateWithDictionary:(NSDictionary *)dict{
    
    NSNumber *idLineup = [dict objectForKey:kJSON_LINEUP_ID];
    
    if ( idLineup ){
        LineUp *lineup = [[CoreDataManager singleton] getEntity:[LineUp class] withId:[idLineup integerValue]];
        if ( lineup )
            [lineup setMatchValuesWithDictionary:dict];      // Update entity
        else
            lineup = [self insertWithDictionary:dict];       // insert new entity
        
        return lineup;
    }
    return nil;
}

//------------------------------------------------------------------------------
-(BOOL)setMatchValuesWithDictionary:(NSDictionary *)dict{
    
    NSNumber *idlineup = [dict objectForKey:@"idLineUp"];
    
    if (idlineup) {

        NSString *goalkeeper = [dict objectForKey:@"line0"];
        NSString *defenders = [dict objectForKey:@"line1"];
        NSString *midfielder01 = [dict objectForKey:@"line2"];
        NSString *midfielder02 = [dict objectForKey:@"line3"];
        NSString *striker = [dict objectForKey:@"line4"];
        NSString *reserve = [dict objectForKey:@"extraData"];
        
        if ([idlineup isKindOfClass:[NSNumber class]])
            self.idLineUp = idlineup;

        if ([goalkeeper isKindOfClass:[NSString class]])
            self.goalkeeper = goalkeeper;
        else
            self.goalkeeper = nil;
        
        if ([defenders isKindOfClass:[NSString class]])
            self.defenders = defenders;
        else
            self.defenders = nil;
        
        if ( [midfielder01 isKindOfClass:[NSString class]] )
            self.midfielder01 = midfielder01;
        else
            self.midfielder01 = nil;

        if ( [midfielder02 isKindOfClass:[NSString class]] )
            self.midfielder02 = midfielder02;
        else
            self.midfielder02 = nil;

        if ( [striker isKindOfClass:[NSString class]] )
            self.striker = striker;
        else
            self.striker = nil;

        if ( [reserve isKindOfClass:[NSString class]] )
            self.reserve = reserve;
        else
            self.reserve = nil;

        
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
    return NO;
}
@end
