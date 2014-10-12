//  Copyright (c) 2014 FCB. All rights reserved.


#import "SyncManager.h"
#import "Constants.h"
#import "FavRestConsumer.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "CleanManager.h"
#import "Follow.h"
#import "User.h"
#import "Shot.h"

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
    [self performSelectorInBackground:@selector(start) withObject:nil];
}

-(void)start{
    [self sendUpdatesToServerWithDelegate:self];

}

#pragma mark - Upload data request
//------------------------------------------------------------------------------
- (void)sendUpdatesToServerWithDelegate:(id)delegate {
    
    NSLog(@"/////////////////////////////////////////////////////");
    NSLog(@"SENDING CONTENT TO SERVER:%@",[NSDate date]);

#warning Move all process in this method to a block
    
    //Array of all entities that send data to server
    NSArray *entitiesToSynchro = @[K_COREDATA_USER];
    
    for (id entity in entitiesToSynchro){
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"%K != 's'",kJSON_SYNCRONIZED];
        NSArray *entityArray = [[CoreDataManager singleton] getAllEntities:NSClassFromString(entity) withPredicate:predicate];
        
        for (id updatedEntity in entityArray) {
            
            if  ([updatedEntity isKindOfClass:[K_COREDATA_USER class]]){
                
                 User *user = (User *)updatedEntity;
                
                 NSNumber *birth = user.csys_birth;
                 NSNumber *modified = user.csys_modified;
                 NSNumber *deleted = user.csys_deleted;
                      
                 NSMutableDictionary *mutDict = [[NSMutableDictionary alloc] initWithDictionary:@{kJSON_ID_USER: user.idUser, kJSON_USERNAME: user.userName,kJSON_BIO:user.bio, kJSON_WEBSITE: user.website, kJSON_POINTS: user.points,kJSON_NUMFOLLOWING: user.numFollowing, kJSON_NUMFOLLOWERS:user.numFollowers,kJSON_RANK:user.rank,kJSON_BIRTH:birth,kJSON_MODIFIED:modified}];
                 
                 if ([user.csys_syncronized isEqualToString:@"d"])
                     [mutDict addEntriesFromDictionary:@{kJSON_DELETED:deleted}];
                 
                 NSArray *dataArray = @[mutDict];
                 
                 NSDictionary *key = @{kJSON_ID_USER:user.idUser};
                 
                 if ([user.csys_syncronized isEqualToString:@"d"])
                     [[FavRestConsumer sharedInstance] deleteEntity:K_COREDATA_USER withKey:key andData:dataArray andDelegate:delegate];
                 else
                     [[FavRestConsumer sharedInstance] createEntity:K_COREDATA_USER withData:dataArray andKey:key andDelegate:delegate];
            }
            
            if  ([updatedEntity isKindOfClass:[K_COREDATA_USER class]]){
                
                Follow *follow = (Follow *)updatedEntity;
                NSArray *dataArray = @[[Follow createDictFromEntity:follow]];
                NSDictionary *key = @{kJSON_ID_USER:follow.idUser,kJSON_FOLLOW_IDUSERFOLLOWED:follow.idUserFollowed};
                [[FavRestConsumer sharedInstance] createEntity:K_COREDATA_FOLLOW withData:dataArray andKey:key andDelegate:delegate];
            }
        }
    }
    [self downloadEntitiesProcessingWithDelegate:delegate];
}

#pragma mark - Other request methods

//------------------------------------------------------------------------------
- (void)downloadEntitiesProcessingWithDelegate:(id)delegate {
    
    NSLog(@"/////////////////////////////////////////////////////");
    NSLog(@"START SYNC PROCESS:%@",[NSDate date]);
    
    if ([self.synchroTimer isValid])
        [self.synchroTimer invalidate];
    
    [self synchroEntityWithCompletion:^(BOOL status,NSError *error){
        
        if (!error && status){

            NSLog(@"SYNC ENDED");
            
            if (![self.synchroTimer isValid])
                [self performSelectorOnMainThread:@selector(startSyncProcess) withObject:nil waitUntilDone:NO];

            
//            [[CleanManager singleton] beginCleanProcessOnCompletion:^(BOOL success, NSError *error) {
//                if (success) {
//                    if (![self.synchroTimer isValid])
//                        [self performSelectorOnMainThread:@selector(startSyncProcess) withObject:nil waitUntilDone:NO];
//                }else{
//                    if (error) NSLog(@"%s %@",__PRETTY_FUNCTION__,error);
//                   
//                }
//            }];
        }
        else
            if (error) NSLog(@"Sync error: %s %@",__PRETTY_FUNCTION__,error);
    }];

}

//------------------------------------------------------------------------------
- (void)synchroEntityWithCompletion:(void (^)(BOOL status,NSError *error))completionBlock{
    
    //Array of all entities that needs to be synchronized
    NSArray *entitiesToSynchro = @[K_COREDATA_FOLLOW,K_COREDATA_SHOT];
    
    for (id entity in entitiesToSynchro) {
        if ([[self entityNeedsToSyncro:entity] integerValue] == 1)
            [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:NSClassFromString(entity) withDelegate:self];
    }
    completionBlock(YES,nil);
}

//------------------------------------------------------------------------------
- (NSNumber *)getFilterDateForEntity:(NSString *)entity {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"%K = %@",k_SYNC_NAME_ENTITY,entity];
    NSArray *itemsArray = [[CoreDataManager singleton] getAllEntities:[SyncControl class] withPredicate:predicate];
    NSNumber *filterDate;
    if ([itemsArray count] > 0) {
        filterDate = [[itemsArray firstObject] lastServerDate];
    }
    return filterDate;
}

#pragma mark - SynchControl
//------------------------------------------------------------------------------
- (void)setSyncData:(NSDictionary *)dict withValue:(NSNumber *)value{
    
    NSString *className = [self getEntityForOperation:[dict objectForKey:K_WS_OPS]];
    
    if (![className isEqualToString:kJSON_LOGIN]) {
    
        //Resolve alias
        NSString *dependencyEntity = [[[SyncManager singleton] getSyncControlForEntity:className] nameEntity];
        NSTimeInterval nowDate = [[NSDate date] timeIntervalSince1970];
        NSNumber *now = [NSNumber numberWithLong:nowDate];

        NSArray *insert = [[CoreDataManager singleton] updateEntities:[SyncControl class] WithArray:@[@{k_SYNC_NAME_ENTITY:dependencyEntity,k_SYNC_LASTSERVER_DATE:value,k_SYNC_LASTCALL:now}]];
        
        if (insert.count > 0)
            [[CoreDataManager singleton] saveContext];
    }
}


#pragma mark - Private Helper methods

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
        
        if ((now - lastUpdate) > sync.updatePriorityValue){
            NSLog(@"Synchornizing %@",entity);
            return @1;
        }
    }
    
    return @0;
}
//------------------------------------------------------------------------------
- (SyncControl *)getSyncControlForEntity:(NSString *)entity {
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"%K = %@ OR %K = %@",k_SYNC_NAME_ENTITY,entity,k_SYNC_ALIAS,entity];
    NSArray *syncEntityArray = [[CoreDataManager singleton] getAllEntities:[SyncControl class] withPredicate:predicate];
    return [syncEntityArray firstObject];
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && [entityClass isSubclassOfClass:[Follow class]]){
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[User class] withDelegate:self];
    }

    else if (status && [entityClass isSubclassOfClass:[Shot class]]){
        NSNotificationCenter *notificationCenter = [NSNotificationCenter defaultCenter];
        [notificationCenter postNotificationName:K_NOTIF_SHOT_END object:nil userInfo:nil];
    }
}

@end
