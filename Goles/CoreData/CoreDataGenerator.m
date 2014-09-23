
//
//  CoreDataGenerator.m
//  Goles Messenger
//
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "CoreDataGenerator.h"
#import "CoreDataManager.h"

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
                                                        message:@"Shooter Core Data default database has been generated succesfully"
                                                       delegate:self
                                              cancelButtonTitle:NSLocalizedString(@"_ok", @"_ok")
                                              otherButtonTitles:nil, nil];
        [alert show];
    } else {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"_error"
                                                        message:@"There has been some kind of error in the generation process. Launch the app to try again"
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
