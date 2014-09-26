
//
//  CoreDataGenerator.m
//  Goles Messenger
//
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "CoreDataGenerator.h"
#import "CoreDataManager.h"
#import "SyncControl.h"

@implementation CoreDataGenerator

#pragma mark - Singleton methods
//------------------------------------------------------------------------------
+ (CoreDataGenerator *)singleton
{
    static CoreDataGenerator *sharedClass = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedClass = [[CoreDataGenerator alloc] init];
    });
    return sharedClass;
    
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
    [self createSynchroTableData];
//    [self createTestingData];
}

#pragma mark - For testing purposes
//------------------------------------------------------------------------------
- (void)createSynchroTableData {
    
    NSNumber *referenceDate = @0;
    NSNumber *startDate = @0;
    NSNumber *update30minutes = @1800;
    NSNumber *update1minute = @10;
    
    NSDictionary *user = @{k_SYNC_NAME_ENTITY:K_COREDATA_USER,k_SYNC_LASTSERVER_DATE:referenceDate,k_SYNC_LASTCALL:startDate,k_SYNC_PRIORITY:update30minutes,
                           k_SYNC_ALIAS:K_COREDATA_USER};
    
    NSDictionary *follow = @{k_SYNC_NAME_ENTITY:K_COREDATA_FOLLOW,k_SYNC_LASTSERVER_DATE:referenceDate,k_SYNC_LASTCALL:startDate,k_SYNC_PRIORITY:update30minutes,
                           k_SYNC_ALIAS:K_COREDATA_FOLLOW};

    NSDictionary *shots = @{k_SYNC_NAME_ENTITY:K_COREDATA_SHOT,k_SYNC_LASTSERVER_DATE:referenceDate,k_SYNC_LASTCALL:startDate,k_SYNC_PRIORITY:update1minute,
                             k_SYNC_ALIAS:K_COREDATA_SHOT};
    
    NSArray *inserted = [[CoreDataManager singleton] updateEntities:[SyncControl class] WithArray:@[user,follow,shots]];
    if (inserted.count > 0)
        [self saveAndAlert];
}

#pragma mark - For testing purposes
//------------------------------------------------------------------------------
-(void)createTestingData {

    //Download needed entities
    NSArray *entitiesToDownload = @[[Team class],[Match class]];
    [entitiesToDownload enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:obj withDelegate:self];
    }];
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
                                                        message:@"Shootr Core Data default database has been generated succesfully."
                                                       delegate:self
                                              cancelButtonTitle:NSLocalizedString(@"_ok", @"_ok")
                                              otherButtonTitles:nil, nil];
        [alert show];
    } else {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"_error"
                                                        message:@"There has been some kind of error in the generation process. Launch the app to try again."
                                                       delegate:self
                                              cancelButtonTitle:NSLocalizedString(@"_ok", @"_ok")
                                              otherButtonTitles:nil, nil];
        [alert show];
    }
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error {
    
    if (status)
        [self saveAndAlert];
}

@end
