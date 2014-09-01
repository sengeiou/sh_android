
//
//  CoreDataGenerator.m
//  Goles Messenger
//
//  Created by Delfin Pereiro on 28/11/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "CoreDataGenerator.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "Event.h"
#import "Tournament.h"

@implementation CoreDataGenerator

#pragma mark - Singleton methods
//------------------------------------------------------------------------------
+ (CoreDataGenerator *)singleton
{
    static CoreDataGenerator *sharedLeague = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedLeague = [[CoreDataGenerator alloc] init];
    });
    return sharedLeague;
    
}

//------------------------------------------------------------------------------
- (id)init {
	self = [super init];
	if (self != nil) {
	}
	return self;
}

//------------------------------------------------------------------------------
- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

#pragma mark - Public methods
//------------------------------------------------------------------------------
-(void)generateDefaultCoreDataBase {
    
    [[CoreDataManager singleton] eraseCoreData];
    NSArray *entitiesToDownload = @[[Team class],[SML class],[Message class],[Tournament class]];
    [entitiesToDownload enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:obj withDelegate:self];
    }];

    [self startSyncControlPopulation];
    [self setEvents];
}

#pragma mark - Private methods
//------------------------------------------------------------------------------
- (void)setEvents {
    
    CoreDataManager *DB = [CoreDataManager singleton];
    NSArray *currentEventsArray = [DB getAllEntities:[Event class]];
    if ([currentEventsArray count] != 21) {
        
        [DB deleteAllEntities:[Event class]];
        NSArray *eventsArray = @[@{kJSON_EVENT_ID:@1,kJSON_NAME:@"Comienza el partido"},
                                 @{kJSON_EVENT_ID:@2,kJSON_NAME:@"Gol de"},
                                 @{kJSON_EVENT_ID:@3,kJSON_NAME:@"Expulsión de"},
                                 @{kJSON_EVENT_ID:@4,kJSON_NAME:@"Final de partido"},
                                 @{kJSON_EVENT_ID:@5,kJSON_NAME:@"Inicio del descanso"},
                                 @{kJSON_EVENT_ID:@6,kJSON_NAME:@"Final del descanso"},
                                 @{kJSON_EVENT_ID:@7,kJSON_NAME:@"Final 90 minutos"},
                                 @{kJSON_EVENT_ID:@8,kJSON_NAME:@"Final de la prórroga"},
                                 @{kJSON_EVENT_ID:@9,kJSON_NAME:@"Penalti señalado"},
                                 @{kJSON_EVENT_ID:@10,kJSON_NAME:@"Penalti y expulsión"},
                                 @{kJSON_EVENT_ID:@11,kJSON_NAME:@"Penalti fallado"},
                                 @{kJSON_EVENT_ID:@12,kJSON_NAME:@"Inicio de prórroga"},
                                 @{kJSON_EVENT_ID:@13,kJSON_NAME:@"Inicio penaltis"},
                                 @{kJSON_EVENT_ID:@14,kJSON_NAME:@"Falta 1h para el partido"},
                                 @{kJSON_EVENT_ID:@15,kJSON_NAME:@"Partido suspendido"},
                                 @{kJSON_EVENT_ID:@16,kJSON_NAME:@"Tarjeta amarilla"},
                                 @{kJSON_EVENT_ID:@17,kJSON_NAME:@"Alineación"},
                                 @{kJSON_EVENT_ID:@18,kJSON_NAME:@"Cambio jugador"},
                                 @{kJSON_EVENT_ID:@19,kJSON_NAME:@"Oferta del partido"},
                                 @{kJSON_EVENT_ID:@20,kJSON_NAME:@"Penalti y amarilla"},
                                 @{kJSON_EVENT_ID:@21,kJSON_NAME:@"Cuotas"}];
        
        [DB insertEntities:[Event class] WithArray:eventsArray];
    }
    
    [self saveAndAlert];
}

#pragma mark - UIAlertViewDelegate methods
//------------------------------------------------------------------------------
-(void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    
    exit(0);
}

//------------------------------------------------------------------------------
- (void)saveAndAlert {
    
    BOOL isSaved = [[CoreDataManager singleton] saveContext];
    
    if ( isSaved ){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"SUCCES!!"
                                                        message:@"Goles Code Data default database has been generated succesfully"
                                                       delegate:self
                                              cancelButtonTitle:NSLocalizedString(@"_ok", @"_ok")
                                              otherButtonTitles:nil, nil];
        [alert show];
    } else {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"_error", @"_error")
                                                        message:@"There has been some kind of error in the generation process. Launch the app to try again"
                                                       delegate:self
                                              cancelButtonTitle:NSLocalizedString(@"_ok", @"_ok")
                                              otherButtonTitles:nil, nil];
        [alert show];
    }
}

#pragma mark - SyncEntity Start Population
//------------------------------------------------------------------------------
- (void)startSyncControlPopulation {
    
    NSNumber *startDate = [NSNumber numberWithDouble:[[NSDate date] timeIntervalSinceReferenceDate]];
    NSNumber *update12hours = @86400;
    NSNumber *update30minutes = @1800;
    NSNumber *update10minutes = @600;
    NSNumber *update1minute = @60;
    
//    NSDictionary *config = @{k_SYNC_NAME_ENTITY:K_CDENTITY_CONFIG,k_SYNC_LASTSERVER_DATE:startDate,
//                             k_SYNC_PRIORITY:update12hours,k_SYNC_DEPENDENCY:[NSNull null],k_SYNC_ITEMS:@100,k_SYNC_ALIAS:K_CDENTITY_CONFIG};
//    
//    NSDictionary *userProfile = @{k_SYNC_NAME_ENTITY:K_CDENTITY_USER_PROFILE,k_SYNC_LASTSERVER_DATE:startDate,
//                                  k_SYNC_PRIORITY:update30minutes,k_SYNC_DEPENDENCY:@"Profile",k_SYNC_ITEMS:@10,k_SYNC_ALIAS:K_CDENTITY_USER_PROFILE};
//    
//    
//    [[CoreDataManager singleton] updateEntities:[SyncControl class] WithArray:@[config,content,contentLanguage,contentTopic,country,countryLanguage,device,image,language,location,mailbox,media,mediaImage,penya,problemLanguage,problemType,template,templateGroup,topic,topicTemplate,topicMailBox,user,userContent,userPenya,userProfile]];
//    
//    
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error {
    
    
}

@end
