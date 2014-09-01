//
//  CoreDataManager.h
//  Goles Messenger
//
//  Created by Delfín Pereiro on 21/06/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "CoreDataParsing.h"
#import "Match.h"
#import "Player.h"
#import "Round.h"
#import "EventOfMatch.h"
#import "Subscription.h"

@interface CoreDataManager : NSObject
    
//DataAccessLayer singleton instance shared across application
+ (CoreDataManager *)singleton;
+ (CoreDataManager *)sharedInstance;

// Returns the managed object context for the application.
// If the context doesn't already exist, it is created and bound
// to the persistent store coordinator for the application.
- (NSManagedObjectContext *)getContext;

// Get Core Data directory path
+ (NSURL *)applicationPrivateDocumentsDirectory;

//Saves the Data Model onto the DB
// returs YES if context saved corretly, NO otherway
- (BOOL)saveContext;

// Remove all core data entries and files
- (BOOL)eraseCoreData;

- (id) getEntity:(Class)entityClass withId:(NSInteger)entityId;
- (Player *)getCurrentUser;

- (NSArray *) getAllEntities:(Class)entityClass;
- (NSArray *) getAllEntities:(Class)entityClass withPredicate:(NSPredicate *)predicate;
- (NSArray *) getAllEntities:(Class)entityClass orderedByKey:(NSString *)key;
- (NSArray *) getAllEntities:(Class)entityClass orderedByKey:(NSString *)key ascending:(BOOL)ascending;
- (NSArray *) getAllEntities:(Class)entityClass orderedByKey:(NSString *)key ascending:(BOOL)ascending withPredicate:(NSPredicate *)predicate;

- (void)unlinkTeams:(NSArray *)dataArray fromMode:(NSNumber *)mode;
- (void)deleteOldTeamsInClasification:(NSArray *)dataArray;

- (NSArray *)insertEntities:(Class)entityClass WithArray:(NSArray *)dataArray;
- (NSArray *)insertEntities:(Class)entityClass WithOrderedArray:(NSArray *)dataArray;
- (NSArray *)updateEntities:(Class)entityClass WithArray:(NSArray *)dataArray;
- (NSArray *)updateEntities:(Class)entityClass WithOrderedArray:(NSArray *)dataArray;
- (void) deleteObject:(NSManagedObject *)object;
- (void) deleteAllEntities:(Class)entityClass;
- (void) deleteEntitiesIn:(NSArray *)entitiesArray;
- (NSArray *)deleteEntities:(Class)entityClass NotIn:(NSArray *)dataArray;
- (NSArray *)deleteMatchEventsForMatch:(Match *)match notIn:(NSArray *)dataArray;
@end
