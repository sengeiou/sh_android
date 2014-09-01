
//
//  CoreDataManager.m
//  Goles Messenger
//
//  Created by Delfín Pereiro on 21/06/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "Constants.h"
//#import "Flurry.h"
#import "Event.h"
#import "Team.h"

@interface CoreDataManager()
{
    NSManagedObjectModel            *managedObjectModel;
    NSPersistentStoreCoordinator    *persistentStoreCoordinator;
    NSManagedObjectContext          *managedObjectContext;
}

@property (nonatomic, retain, readonly) NSManagedObjectModel            *managedObjectModel;
@property (nonatomic, retain, readonly) NSPersistentStoreCoordinator    *persistentStoreCoordinator;
@property (nonatomic, retain, readonly) NSManagedObjectContext          *managedObjectContext;

-(NSFetchRequest *)createFetchRequestForEntityNamed:(NSString *)entityName
                                       orderedByKey:(NSString *)key
                                          ascending:(BOOL)ascending;

@end


@implementation CoreDataManager

//------------------------------------------------------------------------------
//DataAccessLayer singleton instance shared across application
+ (CoreDataManager *)singleton
{
    static CoreDataManager *sharedCoreData = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedCoreData = [[CoreDataManager alloc] init];
    });
    return sharedCoreData;
}

//------------------------------------------------------------------------------
+ (CoreDataManager *)sharedInstance
{
    return [CoreDataManager singleton];
}

#pragma mark - Public methods
//------------------------------------------------------------------------------
- (BOOL)eraseCoreData{
    
    BOOL result = YES;
    
    NSError *error = nil;
    NSArray *stores = [persistentStoreCoordinator persistentStores];
    for(NSPersistentStore *store in stores) {
        [persistentStoreCoordinator removePersistentStore:store error:&error];
        if ( ![[NSFileManager defaultManager] removeItemAtPath:store.URL.path error:&error] )
        {
            result = NO;
            if (K_DEBUG_MODE) DLog(@"Error: %@", error);
        }

    }
    managedObjectContext = nil;
    persistentStoreCoordinator = nil;
    managedObjectModel = nil;
    
    [self managedObjectContext];
    
    return result;
}


//------------------------------------------------------------------------------
- (NSManagedObjectContext *)getContext {
    
    return managedObjectContext;
}

//------------------------------------------------------------------------------
- (BOOL)saveContext {
    NSError *error;
    if (![managedObjectContext save:&error]) {
        DLog(@"Whoops, couldn't save: %@", [error localizedDescription]);
//        [Flurry logError:K_COREDATA_ERROR_SAVECONTEXT message:@"Core Data Save Context Error" error:error];
        return NO;
    }
    return YES;
}
//------------------------------------------------------------------------------
- (id)getEntity:(Class)entityClass withId:(NSInteger)entityId{
    
    NSAssert( (entityClass != [NSNull class] && entityClass),@"[GOLES MESSENGER ERROR]: Error trying to find entity of class '%@'",NSStringFromClass(entityClass));
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:[NSString stringWithFormat:@"id%@ = %i",NSStringFromClass(entityClass),entityId]];
    NSArray *result = [self getAllEntities:entityClass withPredicate:predicate];
    
    if ( [result count]>0 )     return [result objectAtIndex:0];
    else                        return nil;
    
    
    
}

//------------------------------------------------------------------------------
- (Player *)getCurrentUser {
    
    Player *result = nil;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"!(profile = nil) AND (profile.active = YES)"];
    NSArray *playersWithProfile = [self getAllEntities:[Player class] withPredicate:predicate];
    
    NSInteger count = [playersWithProfile count];
    if ( count > 0 ){
        if ( count > 1 )
            DLog(@"[GOLES MESSENGER ERROR]: Existe más de un player en CoreData con Profile activo!!!");

        result = [playersWithProfile objectAtIndex:0];
    }
    return result;
}

//------------------------------------------------------------------------------
- (NSArray *) getAllEntities:(Class)entityClass
{    
    return [self getAllEntities:entityClass orderedByKey:nil];
}

//------------------------------------------------------------------------------
- (NSArray *) getAllEntities:(Class)entityClass withPredicate:(NSPredicate *)predicate
{
    NSFetchRequest *request = [self createFetchRequestForEntityNamed:NSStringFromClass(entityClass) orderedByKey:nil ascending:YES];
    [request setPredicate:predicate];
    
    NSError * error = nil;
    NSArray *result = nil;
    if ( [request entity] ){
        result = [managedObjectContext executeFetchRequest:request error:&error];
    }
    return result;
}

//------------------------------------------------------------------------------
- (NSArray *) getAllEntities:(Class)entityClass orderedByKey:(NSString *)key
{
    return [self getAllEntities:entityClass orderedByKey:key ascending:YES];
}

//------------------------------------------------------------------------------
- (NSArray *) getAllEntities:(Class)entityClass orderedByKey:(NSString *)key ascending:(BOOL)ascending
{    
    return [self getAllEntities:entityClass orderedByKey:key ascending:ascending withPredicate:nil];
}

//------------------------------------------------------------------------------
- (NSArray *) getAllEntities:(Class)entityClass orderedByKey:(NSString *)key ascending:(BOOL)ascending withPredicate:(NSPredicate *)predicate
{
    NSFetchRequest *request = [self createFetchRequestForEntityNamed:NSStringFromClass(entityClass) orderedByKey:key ascending:ascending];
    [request setPredicate:predicate];
    NSError * error = nil;
    NSArray *result = nil;
    if ( [request entity] ){
        result = [managedObjectContext executeFetchRequest:request error:&error];
    }
    
    return result;
}


//------------------------------------------------------------------------------
- (void) deleteObject:(NSManagedObject *)object{
    if (object) [managedObjectContext deleteObject:object];
}

//------------------------------------------------------------------------------
- (void) deleteAllEntities:(Class) entityClass {
    
    NSArray *items = [self getAllEntities:entityClass];
    [self deleteEntitiesIn:items];
}

//------------------------------------------------------------------------------
- (void) deleteEntitiesIn:(NSArray *) entitiesArray  {
    
    for (NSManagedObject *managedObject in entitiesArray)
    	[managedObjectContext deleteObject:managedObject];
}

//------------------------------------------------------------------------------
- (NSArray *)deleteEntities:(Class)entityClass NotIn:(NSArray *)dataArray {
    
    NSArray *toDelete = nil;
    
    if ( [dataArray isKindOfClass:[NSArray class]] ){
        NSString *entityId = [NSString stringWithFormat:@"id%@",NSStringFromClass(entityClass)];
        
        // Get all objects ids
        NSArray *idsArray = [dataArray valueForKey:entityId];
        
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"NOT (%K IN %@)",entityId, idsArray];
        toDelete = [[CoreDataManager singleton] getAllEntities:entityClass withPredicate:predicate];
        [[CoreDataManager singleton] deleteEntitiesIn:toDelete];
    }
    
    return toDelete;
}


//------------------------------------------------------------------------------
- (void)unlinkTeams:(NSArray *)dataArray fromMode:(NSNumber *)mode{
    
    if ( [dataArray isKindOfClass:[NSArray class]] ){
        NSString *entityId = [NSString stringWithFormat:@"idTeam"];
        
        // Get all objects ids
        NSArray *idsArray = [dataArray valueForKey:entityId];
        
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"NOT (%K IN %@) && mode.idMode = %@",entityId, idsArray,mode];
        NSArray *toChange = [[CoreDataManager singleton] getAllEntities:[Team class] withPredicate:predicate];
        for (Team *team in toChange)
            [team setMode:nil];
    }
    
}

//------------------------------------------------------------------------------
/**
 Every time we call EventsOfMatch WS we delete all events of match that we have in CoreData and don't arrive in the WS response.
 This is necessary to delete events deleted in server. We can't delete all the match events every time because we want
 to have previous data when entering in detailMatch.
 */
//------------------------------------------------------------------------------
- (NSArray *)deleteMatchEventsForMatch:(Match *)match notIn:(NSArray *)dataArray{
    
    NSArray *toDelete = nil;
    
    if (match && [dataArray isKindOfClass:[NSArray class]]) {
        NSArray *idsArray = [dataArray valueForKey:kJSON_ID_EVENT_OF_MATCH];
        NSPredicate *predicate1 = [NSPredicate predicateWithFormat:@"match.idMatch = %i",match.idMatchValue];
        NSPredicate *predicate2 = [NSPredicate predicateWithFormat:@"NOT (%K IN %@)",kJSON_ID_EVENT_OF_MATCH, idsArray];
        NSPredicate *eventsPredicate = [NSCompoundPredicate andPredicateWithSubpredicates:@[predicate1, predicate2]];
        toDelete = [[CoreDataManager singleton] getAllEntities:[EventOfMatch class] withPredicate:eventsPredicate];
        [[CoreDataManager singleton] deleteEntitiesIn:toDelete];
    }
    
    return toDelete;
}


//------------------------------------------------------------------------------
- (NSArray *)insertEntities:(Class)entityClass WithArray:(NSArray *)dataArray {
    
    NSMutableArray *result = [[NSMutableArray alloc] init];
    
    if ( [entityClass respondsToSelector:@selector(insertWithDictionary:)] )
    {
        if ( [dataArray isKindOfClass:[NSArray class]] && [dataArray count]>0 ){
            for (NSDictionary *entityDict in dataArray  ){
                id entity = [entityClass insertWithDictionary:entityDict];
                if ( entity )
                    [result addObject:entity];
            }
        }
    } else {
        DLog(@"[GOLES MESSENGER ERROR]: Se esta intentando actualizar la entidad de Core Data '%@' que no responde al evento insertEntities", entityClass);
    }
    
    return result;
}

//------------------------------------------------------------------------------
- (NSArray *)insertEntities:(Class)entityClass WithOrderedArray:(NSArray *)dataArray {
    
    NSMutableArray *result = [[NSMutableArray alloc] init];
    
    if ( [entityClass respondsToSelector:@selector(insertWithDictionary:andIndex:)] )
    {
        if ( [dataArray isKindOfClass:[NSArray class]] && [dataArray count]>0 ){
            for (NSInteger index = 0; index < [dataArray count]; ++index){
                NSDictionary *entityDict = [dataArray objectAtIndex:index];
                id entity = [entityClass insertWithDictionary:entityDict andIndex:index];
                if ( entity )
                    [result addObject:entity];
            }            
        }
    } else {
        DLog(@"[GOLES MESSENGER ERROR]: Se esta intentando actualizar la entidad de Core Data '%@' que no responde al evento insertEntities:withIndex:", entityClass);
    }
    
    return result;
}

//------------------------------------------------------------------------------
- (NSArray *)updateEntities:(Class)entityClass WithOrderedArray:(NSArray *)dataArray {
    
    NSMutableArray *result = [[NSMutableArray alloc] init];
    
    if ( [dataArray isKindOfClass:[NSArray class]] &&
        [entityClass respondsToSelector:@selector(updateWithDictionary:withIndex:)])
    {
        for (NSInteger index = 0; index < [dataArray count]; ++index){
            NSDictionary *entityDict = [dataArray objectAtIndex:index];
            id entity = [entityClass updateWithDictionary:entityDict withIndex:index];
            if ( entity )
                [result addObject:entity];
        }
    } else {
        DLog(@"[GOLES MESSENGER ERROR]: Se esta intentando actualizar la entidad de Core Data '%@' que no responde al evento updateWithDictionary:withIndex:", entityClass);
    }
    
    return result;
}

//------------------------------------------------------------------------------
- (NSArray *)updateEntities:(Class)entityClass WithArray:(NSArray *)dataArray {
    
    NSMutableArray *resultInserted = [[NSMutableArray alloc] init];
    
    for (NSDictionary *entityDict in dataArray) {

        id entity = [entityClass updateWithDictionary:entityDict];
            if ( entity )
                [resultInserted addObject:entity];
        }

    return resultInserted;
}

#pragma mark - Private methods

//------------------------------------------------------------------------------
-(NSFetchRequest *)createFetchRequestForEntityNamed:(NSString *)entityName
                                       orderedByKey:(NSString *)key
                                          ascending:(BOOL)ascending{
    
    NSFetchRequest *request= [[NSFetchRequest alloc] init];
    [request setEntity: [NSEntityDescription entityForName:entityName inManagedObjectContext:managedObjectContext]];
    [request setIncludesPropertyValues:YES]; //only fetch the managedObjectID
    
    if ( key ){
        NSSortDescriptor *sort = [[NSSortDescriptor alloc] initWithKey:key ascending:ascending];
        [request setSortDescriptors:[NSArray arrayWithObject:sort]];
    }
    
    return request;
}

#pragma mark - Core Data methods
//------------------------------------------------------------------------------
// Returns the managed object context for the application.
// If the context doesn't already exist, it is created and bound to the persistent store coordinator for the application.
- (NSManagedObjectContext *)managedObjectContext
{
    if (managedObjectContext != nil)
        return managedObjectContext;
    
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if ( coordinator != nil) {
        managedObjectContext = [[NSManagedObjectContext alloc] init];
        [managedObjectContext setPersistentStoreCoordinator:coordinator];
    }
    return managedObjectContext;
}

//------------------------------------------------------------------------------
- (BOOL)copyDefaultDataToSQLiteDataBase {
    
    NSURL *storeURL = [[CoreDataManager applicationPrivateDocumentsDirectory] URLByAppendingPathComponent:@"golesDataBase.sqlite"];
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"defaultDataBase" ofType:@"sqlite"];
    NSString *toPath = [storeURL relativePath];
    NSError *error;
    return [[NSFileManager new] copyItemAtPath:filePath toPath:toPath error:&error];

}

//------------------------------------------------------------------------------
// Returns the persistent store coordinator for the application.
// If the coordinator doesn't already exist, it is created and the application's store added to it.
- (NSPersistentStoreCoordinator *)persistentStoreCoordinator
{
    if (persistentStoreCoordinator != nil)
        return persistentStoreCoordinator;
    
    NSURL *storeURL = [[CoreDataManager applicationPrivateDocumentsDirectory] URLByAppendingPathComponent:@"golesDataBase.sqlite"];
    

    if (!IS_GENERATING_DEFAULT_DATABASE && ![[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithContentsOfURL:storeURL encoding:NSUTF8StringEncoding error:nil] isDirectory:NO])
        [self copyDefaultDataToSQLiteDataBase];
    
    NSError *error = nil;
    persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];
    if (![persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:nil error:&error]) {
        /*
         Replace this implementation with code to handle the error appropriately.
         
         abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
         
         Typical reasons for an error here include:
         * The persistent store is not accessible;
         * The schema for the persistent store is incompatible with current managed object model.
         Check the error message to determine what the actual problem was.
         
         
         If the persistent store is not accessible, there is typically something wrong with the file path. Often, a file URL is pointing into the application's resources directory instead of a writeable directory.
         
         If you encounter schema incompatibility errors during development, you can reduce their frequency by:
         * Simply deleting the existing store:
         [[NSFileManager defaultManager] removeItemAtURL:storeURL error:nil]
         
         * Performing automatic lightweight migration by passing the following dictionary as the options parameter:
         @{NSMigratePersistentStoresAutomaticallyOption:@YES, NSInferMappingModelAutomaticallyOption:@YES}
         
         Lightweight migration will only work for a limited set of schema changes; consult "Core Data Model Versioning and Data Migration Programming Guide" for details.
         
         */
        
        DLog(@"Unresolved error %@, %@", error, [error userInfo]);
        abort();
    }
    
    return persistentStoreCoordinator;
}

//------------------------------------------------------------------------------
// Returns the managed object model for the application.
// If the model doesn't already exist, it is created from the application's model.
- (NSManagedObjectModel *)managedObjectModel
{
    if (managedObjectModel != nil)
        return managedObjectModel;
    
    NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"golesDataBase" withExtension:@"momd"];
    managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
    
    return managedObjectModel;
}

#pragma mark - Application's Documents directory
//------------------------------------------------------------------------------
// Returns the URL to the application's Private Documents directory.
+ (NSURL *)applicationPrivateDocumentsDirectory
{
//    NSURL *url =[[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
        NSString *libraryPath = [NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES) lastObject];
        NSString *path = [libraryPath stringByAppendingPathComponent:@"Private Documents"];
    
        BOOL isDirectory;
        if (![[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:&isDirectory]) {
            NSError *error = nil;
            if (![[NSFileManager defaultManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:&error]) {
                DLog(@"Can't create directory %@ [%@]", path, error);
                abort(); // replace with proper error handling
            }
        }
        else if (!isDirectory) {
            DLog(@"Path %@ exists but is no directory", path);
            abort(); // replace with error handling
        }
    
    NSURL *url = [NSURL fileURLWithPath:path isDirectory:isDirectory];
    return url;
}



#pragma mark - Singleton overwritten methods
//------------------------------------------------------------------------------
- (id)init {
	self = [super init];
	if (self != nil) {
        // Setting Core Data context
        [self managedObjectContext];
	}
	return self;
}

//------------------------------------------------------------------------------
- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

@end
