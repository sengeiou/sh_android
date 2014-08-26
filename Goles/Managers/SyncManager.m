//  Copyright (c) 2014 FCB. All rights reserved.


#import "SyncManager.h"
#import "Constants.h"
#import "FavRestConsumer.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface SyncManager ()

@property (nonatomic) BOOL userSendOK;
@property (nonatomic) BOOL deviceSendOK;
@property (nonatomic) BOOL userPenyaSendOK;
@property (nonatomic) BOOL userContentSendOK;
@property (nonatomic) BOOL topicMailboxSendOK;

@property (nonatomic)              int              contentLanguageCounter;
@property (nonatomic)              int              countryCounter;

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
    NSArray *entitiesToSynchro = @[];
    
    for (id entity in entitiesToSynchro){
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"csys_synchronized != 's'"];
        NSArray *entityArray = [[CoreDataManager singleton] getAllEntities:NSClassFromString(entity) withPredicate:predicate];
        
        for (id updatedEntity in entityArray) {
        
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
