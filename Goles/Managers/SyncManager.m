//  Copyright (c) 2014 FCB. All rights reserved.


#import "SyncManager.h"
#import "Constants.h"
#import "FavRestConsumer.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface SyncManager ()


@end


@implementation SyncManager

#pragma mark - Singleton methods
//------------------------------------------------------------------------------
+ (SyncManager *)singleton {
    
    static SyncManager *sharedUser = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedUser = [[SyncManager alloc] init];
    });
    return sharedUser;
}

//------------------------------------------------------------------------------
+ (SyncManager *)sharedInstance {
    
    return [self singleton];
}

#pragma mark - Singleton overwritten methods
//------------------------------------------------------------------------------
- (id)init {
	self = [super init];
	if (self != nil) {
	}
	return self;
}

//------------------------------------------------------------------------------
- (id)copyWithZone:(NSZone *)zone {
    return self;
}



#pragma mark - Synchronization
//------------------------------------------------------------------------------
- (void)startSyncProcess {
    
    //The main timer for the syncro process
    self.synchroTimer = [NSTimer timerWithTimeInterval:SYNCHRO_TIMER target:self selector:@selector(beginEntitiesProcessing:) userInfo:nil repeats:YES];
    [[NSRunLoop currentRunLoop] addTimer:self.synchroTimer forMode:NSRunLoopCommonModes];
}


//------------------------------------------------------------------------------
- (void)beginEntitiesProcessing:(NSTimer *)timer {

    //First step in syncro, send all entities with csys_syncrhonized != S
    [self sendUpdatesToServerWithDelegate:self];
    
}

#pragma mark - Upload data request
//------------------------------------------------------------------------------
- (void)sendUpdatesToServerWithDelegate:(id)delegate {
    
    NSLog(@"/////////////////////////////////////////////////////");
    NSLog(@"SENDING CONTENT TO SERVER:%@",[NSDate date]);
    NSLog(@"/////////////////////////////////////////////////////");

    
    //Array of all entities that send data to server
    NSArray *entitiesToSynchro = @[K_COREDATA_APPADVICE, K_COREDATA_MATCH_ODD, K_COREDATA_DEVICE,  K_COREDATA_EVENT, K_COREDATA_EVENTOFMATCH, K_COREDATA_MATCH, K_COREDATA_MESSAGE, K_COREDATA_PLAYER, K_COREDATA_ROUND, K_COREDATA_SML, K_COREDATA_SUSCRIPTIONS, K_COREDATA_TEAM, K_COREDATA_TOURNAMENT];
    
    for (id entity in entitiesToSynchro){
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"csys_synchronized != 's'"];
        NSArray *entityArray = [[CoreDataManager singleton] getAllEntities:NSClassFromString(entity) withPredicate:predicate];
        
            for (id updatedEntity in entityArray) {
            
                if  ([updatedEntity isKindOfClass:[K_COREDATA_APPADVICE class]]){
                    
                    AppAdvice *appDevice = (AppAdvice *)updatedEntity;
                    NSTimeInterval birth = [appDevice.csys_birth timeIntervalSince1970]*1000;
                    NSTimeInterval modified = [appDevice.csys_modified timeIntervalSince1970]*1000;
                    NSTimeInterval deleted = [appDevice.csys_deleted timeIntervalSince1970]*1000;
                    
                    
                    NSMutableDictionary *mutDict = [[NSMutableDictionary alloc] initWithDictionary:@{kJSON_ID_APPADVICE:appDevice.idAppAdvice,
                                                                                                     kJSON_ADVICE_PATH: appDevice.path, kJSON_ADVICE_IDMESSAGE:appDevice.idMessage, kJSON_ADVICE_PLATFORM: appDevice.platform, kJSON_ADVICE_STATUS: appDevice.status, kJSON_ADVICE_BUTTON_VISIBLE: appDevice.visibleButton, kJSON_ADVICE_BUTTON_ACTION: appDevice.buttonAction, kJSON_ADVICE_BUTTON_TEXTID: appDevice.buttonTextId, kJSON_ADVICE_BUTTON_DATA: appDevice.buttonData,kJSON_ADVICE_DATESTART:appDevice.startDate,kJSON_ADVICE_DATEEND:appDevice.endDate,kJSON_ADVICE_WEIGHT:appDevice.weight, kJSON_ADVICE_VERSION_START: appDevice.startVersion, kJSON_ADVICE_VERSION_END: appDevice.endVersion,
                                                                                                     kJSON_BIRTH:[NSNumber numberWithLongLong:birth],
                                                                                                     kJSON_MODIFIED:[NSNumber numberWithLongLong:modified]}];

                    if ([appDevice.csys_syncronized isEqualToString:@"d"])
                        [mutDict addEntriesFromDictionary:@{kJSON_DELETED:[NSNumber numberWithLongLong:deleted]}];
                    
                    NSArray *dataArray = @[mutDict];
                    
                    NSDictionary *key = @{kJSON_ID_APPADVICE:appDevice.idAppAdvice};
                   
                    if ([appDevice.csys_syncronized isEqualToString:@"d"])
                        [[FavRestConsumer sharedInstance] deleteEntity:K_COREDATA_APPADVICE withKey:key andData:dataArray andDelegate:delegate];
                    else
                        [[FavRestConsumer sharedInstance] createEntity:K_COREDATA_APPADVICE withData:dataArray andKey:key andDelegate:delegate];

                }
                
                if  ([updatedEntity isKindOfClass:[K_COREDATA_DEVICE class]]){
                    
                    Device *device = (Device *)updatedEntity;
                    NSTimeInterval birth = [device.csys_birth timeIntervalSince1970]*1000;
                    NSTimeInterval modified = [device.csys_modified timeIntervalSince1970]*1000;
                    NSTimeInterval deleted = [device.csys_deleted timeIntervalSince1970]*1000;

                    
                    NSMutableDictionary *mutDict = [[NSMutableDictionary alloc] initWithDictionary:@{kJSON_ID_DEVICE: device.idDevice, kJSON_TOKEN: device.token,
                                                                                                     kJSON_DEVICE_OSVERSION: device.osVer, kJSON_DEVICE_MODEL: device.model,
                                                                                                     kJSON_DEVICE_APPVERSION: device.appVer,kJSON_DEVICE_LOCALE: device.locale,
                                                                                                     kJSON_BIRTH:[NSNumber numberWithLongLong:birth],
                                                                                                     kJSON_MODIFIED:[NSNumber numberWithLongLong:modified]}];
                    
                    if ([device.csys_syncronized isEqualToString:@"d"])
                        [mutDict addEntriesFromDictionary:@{kJSON_DELETED:[NSNumber numberWithLongLong:deleted]}];
                    
                    NSArray *dataArray = @[mutDict];
                    
                    NSDictionary *key = @{kJSON_ID_DEVICE:device.idDevice};
                    
                    if ([device.csys_syncronized isEqualToString:@"d"])
                        [[FavRestConsumer sharedInstance] deleteEntity:K_COREDATA_DEVICE withKey:key andData:dataArray andDelegate:delegate];
                    else
                        [[FavRestConsumer sharedInstance] createEntity:K_COREDATA_DEVICE withData:dataArray andKey:key andDelegate:delegate];
                }
                              
                if  ([updatedEntity isKindOfClass:[K_COREDATA_MATCH class]]){
                    
                    Match *match = (Match *)updatedEntity;
                     
                     NSTimeInterval birth = [match.csys_birth timeIntervalSince1970]*1000;
                     NSTimeInterval modified = [match.csys_modified timeIntervalSince1970]*1000;
                     NSTimeInterval deleted = [match.csys_deleted timeIntervalSince1970]*1000;
                    

                    NSMutableDictionary *mutDict = [[NSMutableDictionary alloc] initWithDictionary:@{kJSON_ID_MATCH: match.idMatch, kJSON_MATCH_STATE: match.matchState,
                                                                                                     kJSON_SCORE_LOCAL: match.localScore, kJSON_SCORE_VISITOR:match.visitorScore,
                                                                                                     kJSON_ID_TEAM_LOCAL: match.idLocalTeam, kJSON_ID_TEAM_VISITOR: match.idVisitorTeam,
                                                                                                     kJSON_DATE_MATCH: match.matchDate, kJSON_ELAPSED_MINUTES: match.elapsedMinutes,
                                                                                                     kJSON_MATCH_TYPE: match.matchType, kJSON_SCORE_TEAMLOCAL_PENALTIES: match.scorePenaltiesLocalTeam,
                                                                                                     kJSON_SCORE_TEAMVISITOR_PENALTIES: match.scorePenaltiesVisitorTeam,
                                                                                                     kJSON_DATE_FINAL: match.endFinal, kJSON_DATE_START: match.startDate,
                                                                                                     kJSON_BIRTH:[NSNumber numberWithLongLong:birth],
                                                                                                     kJSON_MODIFIED:[NSNumber numberWithLongLong:modified]}];
                     
                     if ([match.csys_syncronized isEqualToString:@"d"])
                         [mutDict addEntriesFromDictionary:@{kJSON_DELETED:[NSNumber numberWithLongLong:deleted]}];
                     
                     NSArray *dataArray = @[mutDict];
                     
                     NSDictionary *key = @{kJSON_ID_MATCH:match.idMatch};
                     
                     if ([match.csys_syncronized isEqualToString:@"d"])
                         [[FavRestConsumer sharedInstance] deleteEntity:K_COREDATA_MATCH withKey:key andData:dataArray andDelegate:delegate];
                     else
                         [[FavRestConsumer sharedInstance] createEntity:K_COREDATA_MATCH withData:dataArray andKey:key andDelegate:delegate];
                }

                if  ([updatedEntity isKindOfClass:[K_COREDATA_MESSAGE class]]){
                    
                    Message *message = (Message *)updatedEntity;
                    
                    NSTimeInterval birth = [message.csys_birth timeIntervalSince1970]*1000;
                    NSTimeInterval modified = [message.csys_modified timeIntervalSince1970]*1000;
                    NSTimeInterval deleted = [message.csys_deleted timeIntervalSince1970]*1000;
                    
                    NSMutableDictionary *mutDict = [[NSMutableDictionary alloc] initWithDictionary:@{kJSON_ID_MESSAGE: message.idMessage, kJSON_MESSAGE_MESSAGE: message.message,
                                                                                                     kJSON_MESSAGE_LANGUAGE: message.locale, kJSON_MESSAGE_PLATFORM: message.platform,
                                                                                                     kJSON_BIRTH:[NSNumber numberWithLongLong:birth],
                                                                                                     kJSON_MODIFIED:[NSNumber numberWithLongLong:modified]}];
                    
                    if ([message.csys_syncronized isEqualToString:@"d"])
                        [mutDict addEntriesFromDictionary:@{kJSON_DELETED:[NSNumber numberWithLongLong:deleted]}];
                    
                    NSArray *dataArray = @[mutDict];
                    
                    NSDictionary *key = @{kJSON_ID_MESSAGE:message.idMessage};
                    
                    if ([message.csys_syncronized isEqualToString:@"d"])
                        [[FavRestConsumer sharedInstance] deleteEntity:K_COREDATA_MESSAGE withKey:key andData:dataArray andDelegate:delegate];
                    else
                        [[FavRestConsumer sharedInstance] createEntity:K_COREDATA_MESSAGE withData:dataArray andKey:key andDelegate:delegate];
                }
                
                if  ([updatedEntity isKindOfClass:[K_COREDATA_PLAYER class]]){
                    
                     Player *player = (Player *)updatedEntity;
                    
                     NSTimeInterval birth = [player.csys_birth timeIntervalSince1970]*1000;
                     NSTimeInterval modified = [player.csys_modified timeIntervalSince1970]*1000;
                     NSTimeInterval deleted = [player.csys_deleted timeIntervalSince1970]*1000;
                    
                     NSMutableDictionary *mutDict = [[NSMutableDictionary alloc] initWithDictionary:@{kJSON_PLAYER_ID: player.idPlayer, kJSON_USER_NAME: player.userName,
                                                                                                      kJSON_BIRTH:[NSNumber numberWithLongLong:birth],
                                                                                                      kJSON_MODIFIED:[NSNumber numberWithLongLong:modified]}];
                     
                     if ([player.csys_syncronized isEqualToString:@"d"])
                     [mutDict addEntriesFromDictionary:@{kJSON_DELETED:[NSNumber numberWithLongLong:deleted]}];
                     
                     NSArray *dataArray = @[mutDict];
                     
                     NSDictionary *key = @{kJSON_PLAYER_ID:player.idPlayer};
                     
                     if ([player.csys_syncronized isEqualToString:@"d"])
                         [[FavRestConsumer sharedInstance] deleteEntity:K_COREDATA_PLAYER withKey:key andData:dataArray andDelegate:delegate];
                     else
                         [[FavRestConsumer sharedInstance] createEntity:K_COREDATA_PLAYER withData:dataArray andKey:key andDelegate:delegate];
            }
        }
    }
    [self downloadEntitiesProcessingWithDelegate:delegate];
}

#pragma mark - Other request methods

//------------------------------------------------------------------------------
- (void)downloadEntitiesProcessingWithDelegate:(id)delegate {
    
    NSNumber *lastSync = [self getLastSyncroTime];
    double lastTime = [lastSync doubleValue];
    NSDate* date = [[NSDate dateWithTimeIntervalSince1970:lastTime] dateByAddingTimeInterval:120];
    if ([date compare:[NSDate date]] == NSOrderedAscending) {

        NSLog(@"/////////////////////////////////////////////////////");
        NSLog(@"START SYNC PROCESS:%@",[NSDate date]);
        NSLog(@"/////////////////////////////////////////////////////");
        
        //Array of all entities that needs to be synchronized
        NSArray *entitiesToSynchro = @[];

        for (id entity in entitiesToSynchro) {
            if ([[self entityNeedsToSyncro:entity] integerValue] == 1)
                [self synchroEntity:entity withDelegate:delegate];
        }
    }
}

//------------------------------------------------------------------------------
- (void)synchroEntity:(NSString *)entity withDelegate:(id)delegate{
    
    //Create request for given entity
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:NSClassFromString(entity) withDelegate:self];
}

#pragma mark - SynchControl
//------------------------------------------------------------------------------
- (void)setSyncData:(NSDictionary *)dict withValue:(long long)value{
    
    NSNumber *dateServer = [NSNumber numberWithLongLong:value];
    NSString *className = [self getEntityForOperation:[dict objectForKey:K_WS_OPS]];
    
    //Resolve alias
    NSString *dependencyEntity = [[[SyncManager singleton] getSyncControlForEntity:className] nameEntity];
    NSTimeInterval nowDate = [[NSDate date] timeIntervalSince1970];
    NSNumber *now = [NSNumber numberWithLong:nowDate];

    NSArray *insert = [[CoreDataManager singleton] updateEntities:[SyncControl class] WithArray:@[@{k_SYNC_NAME_ENTITY:dependencyEntity,k_SYNC_LASTSERVER_DATE:dateServer,k_SYNC_LASTCALL:now}]];
    
    if (insert.count > 0)
        [[CoreDataManager singleton] saveContext];
}


#pragma mark - Private Helper methods

//------------------------------------------------------------------------------
- (NSNumber *)getLastSyncroTime {
    
    NSArray *result = [[CoreDataManager singleton] getAllEntities:[SyncControl class] orderedByKey:k_SYNC_LASTCALL ascending:NO withPredicate:nil];

    if (result.count > 0)
        return [[result firstObject] lastCall];
    return @0;
}

//------------------------------------------------------------------------------
- (NSString *)getEntityForOperation:(NSArray *)operation {
    
    NSString *entityName = [[[operation firstObject] objectForKey:K_WS_OPS_METADATA] objectForKey:K_WS_OPS_ENTITY];
    return entityName;
}

//------------------------------------------------------------------------------
- (NSNumber *)entityNeedsToSyncro:(NSString *)entity {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"%K = %@",k_SYNC_NAME_ENTITY,entity];
    NSArray *itemsArray = [[CoreDataManager singleton] getAllEntities:[SyncControl class] withPredicate:predicate];
    if ([itemsArray count] > 0) {
        SyncControl *sync = [itemsArray firstObject];
        long lastUpdate = [sync.lastCall longValue];
        long now = (long)[[NSDate date] timeIntervalSince1970];
        
        if ((now - lastUpdate) > sync.updatePriorityValue)
            return @1;
        else
            NSLog(@"%@ needs no synchro",entity);
    }
    
    return @0;
}
//------------------------------------------------------------------------------
- (SyncControl *)getSyncControlForEntity:(NSString *)entity {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"%K = %@ OR %K = %@",k_SYNC_NAME_ENTITY,entity,k_SYNC_ALIAS,entity];
    NSArray *syncEntityArray = [[CoreDataManager singleton] getAllEntities:[SyncControl class] withPredicate:predicate];
    return [syncEntityArray firstObject];
}

@end
