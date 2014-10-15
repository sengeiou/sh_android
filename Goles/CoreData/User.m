#import "User.h"
#import "CoreDataManager.h"
#import "UserManager.h"

@interface User ()

// Private interface goes here.

@end


@implementation User

//------------------------------------------------------------------------------
+(User *)insertWithDictionary:(NSDictionary *)dict {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    User *user = [NSEntityDescription insertNewObjectForEntityForName:@"User"
                                               inManagedObjectContext:context];
    
    BOOL insertedCorrectly = [user setUserValuesWithDictionary:dict];
    if ( !insertedCorrectly ){
        [[CoreDataManager singleton] deleteObject:user];
        return nil;
    }
    return user;
}

//------------------------------------------------------------------------------
+(instancetype)updateWithDictionary:(NSDictionary *)dict {
    
    NSString *identifier = [NSString stringWithFormat:@"id%@",[[self class] description]];
    NSNumber *idNumber = [dict objectForKey:identifier];
    
    if ( idNumber ){
        id objectInstance = [[CoreDataManager singleton] getEntity:[self class] withId:[idNumber integerValue]];
        
        if (objectInstance && (![[dict objectForKey:K_WS_OPS_DELETE_DATE] isKindOfClass:[NSNull class]] || ![dict objectForKey:K_WS_OPS_DELETE_DATE])) {
            [[CoreDataManager singleton] deleteObject:objectInstance];
            return nil;
        }
        else if ((![[dict objectForKey:K_WS_OPS_DELETE_DATE] isKindOfClass:[NSNull class]] || ![dict objectForKey:K_WS_OPS_DELETE_DATE]))
            return nil;
        else if ( objectInstance )
            [objectInstance setUserValuesWithDictionary:dict];      // Update entity
        else
            objectInstance = [self insertWithDictionary:dict];      // insert new entity
        return objectInstance;
    }
    return nil;
}

//------------------------------------------------------------------------------
+(User *)createUserWithUser:(User *)user {
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    User *newUser = [NSEntityDescription insertNewObjectForEntityForName:K_COREDATA_USER inManagedObjectContext:context];
    
    [newUser setIdUser:user.idUser];
    [newUser setUserName:user.userName];
    [newUser setIdFavoriteTeam:user.idFavoriteTeam];
    [newUser setSessionToken:user.sessionToken];
    [newUser setEMail:user.eMail];
    [newUser setName:user.name];
    [newUser setPhoto:user.photo];
    [newUser setBio:user.bio];
    [newUser setWebsite:user.website];
    [newUser setPoints:user.points];
    [newUser setNumFollowing:user.numFollowing];
    [newUser setNumFollowers:user.numFollowers];
    [newUser setRank:user.rank];
    [newUser setFavoriteTeamName:user.favoriteTeamName];
    
    //SYNCRO  PROPERTIES
    [newUser setCsys_syncronized:kJSON_SYNCRO_SYNCRONIZED];
   
    NSTimeInterval epochTime = [[NSDate date] timeIntervalSince1970]*1000;

    [newUser setCsys_modified:[NSNumber numberWithLongLong:epochTime]];
    [newUser setCsys_revision:0];
    [newUser setCsys_birth:[NSNumber numberWithLongLong:epochTime]];
    
    
    return newUser;
}
//------------------------------------------------------------------------------
-(BOOL)setUserValuesWithDictionary:(NSDictionary *)dict {
    
    BOOL result = YES;
    
    NSNumber *idUser = [dict objectForKey:kJSON_ID_USER];
    if ( [idUser isKindOfClass:[NSNumber class]] )
        [self setIdUser:idUser];
    else
        result = NO;
    
    NSString *userName = [dict objectForKey:kJSON_USERNAME ];
    if ([userName isKindOfClass:[NSString class]])
        [self setUserName:userName];
    else
        result = NO;
    
    NSNumber *idFavoriteTeam = [dict objectForKey:kJSON_ID_FAVORITE_TEAM ];
    if ([idFavoriteTeam isKindOfClass:[NSNumber class]])
        [self setIdFavoriteTeam:idFavoriteTeam];
    
    NSString *sessionToken = [dict objectForKey:kJSON_SESSIONTOKEN ];
    if ([sessionToken isKindOfClass:[NSString class]])
        [self setSessionToken:sessionToken];

    
    NSString *email = [dict objectForKey:kJSON_EMAIL ];
    if ([email isKindOfClass:[NSString class]])
        [self setEMail:email];
    
    NSString *name = [dict objectForKey:kJSON_NAME ];
    if ([name isKindOfClass:[NSString class]])
        [self setName:name];
    
    NSString *photo = [dict objectForKey:kJSON_PHOTO ];
    if ([photo isKindOfClass:[NSString class]])
        [self setPhoto:photo];
    
    NSString *bio = [dict objectForKey:kJSON_BIO ];
    if ([bio isKindOfClass:[NSString class]])
        [self setBio:bio];
    
    NSString *website = [dict objectForKey:kJSON_WEBSITE ];
    if ([website isKindOfClass:[NSString class]])
        [self setWebsite:website];
    
    NSNumber *points = [dict objectForKey:kJSON_POINTS ];
    if ([points isKindOfClass:[NSNumber class]])
        [self setPoints:points];
    
    NSNumber *numFollowing = [dict objectForKey:kJSON_NUMFOLLOWING ];
    if ([numFollowing isKindOfClass:[NSNumber class]])
        [self setNumFollowing:numFollowing];
    
    NSNumber *numFollowers = [dict objectForKey:kJSON_NUMFOLLOWERS ];
    if ([numFollowers isKindOfClass:[NSNumber class]])
        [self setNumFollowers:numFollowers];
    
    NSNumber *rank = [dict objectForKey:kJSON_RANK ];
    if ([rank isKindOfClass:[NSNumber class]])
        [self setRank:rank];

    NSString *favoriteTeamName = [dict objectForKey:kJSON_FAVORITE_TEAM_NAME];
    if ([favoriteTeamName isKindOfClass:[NSString class]])
        [self setFavoriteTeamName:favoriteTeamName];
    
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

    return result;
}

@end
