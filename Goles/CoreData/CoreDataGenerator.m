
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
//    NSArray *entitiesToDownload = @[[Team class],[SML class],[Message class]];
//    [entitiesToDownload enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
//        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:obj withDelegate:self];
//    }];
//
    [self startSyncControlPopulation];
}

#pragma mark - Private methods

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
