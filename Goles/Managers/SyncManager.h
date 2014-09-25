//  Copyright (c) 2014 FCB. All rights reserved.


#import <Foundation/Foundation.h>
#import "SyncControl.h"

@interface SyncManager : NSObject

@property (nonatomic,strong) NSTimer *synchroTimer;

+ (SyncManager *)singleton;
+ (SyncManager *)sharedInstance;


//------------------------------------------------------------------------------
//BASIC SYNCRO PROCESS STRUCTURE
//------------------------------------------------------------------------------

//01    - (void)startSyncProcess                                            Is called from appdelegate. Has a timer constant and on/off bolean in constants.h
//02    - (void)beginEntitiesProcessing:(NSTimer *)timer                    Is called from the timer method. Starts the upload and download of data.
//03    - (void)sendUpdatesToServerWithDelegate:(id)delegate                Uploads modified, deleted or new entries in local database to server.
//04    - (void)downloadEntitiesProcessingWithDelegate:(id)delegate         Downloads data from server based on cycle times by class set in SyncControl
//      - (NSNumber *)entityNeedsToSyncro:(NSString *)entity                Used to check if entity needs syncro ( based in time cycle)
//05    - (void)synchroEntity:(NSString *)entity withDelegate:(id)delegate  All the entities that needs syncro are used to generate request methods.


//------------------------------------------------------------------------------
/**
 @brief Starts the process of synchronization.
 @discussion    Uses a timer with a time interval set in Constants.h to call the main method of syncro process.
 */
- (void)startSyncProcess;




- (void)setSyncData:(NSDictionary *)dict withValue:(NSNumber *)value;
- (void)synchroEntity:(NSString *)entity withDelegate:(id)delegate;
- (NSNumber *)getFilterDateForEntity:(NSString *)entity;

//------------------------------------------------------------------------------
/**
 @brief Send data to server
 
 @discussion Creates the necessary requests to send data to server.
 The data to be send is based in NSArray of all entities that sends data.
 For every entity in array, this method search all the rows that have "csys_synchronized" no equal to "S".

 */
- (void)sendUpdatesToServerWithDelegate:(id)delegate;

- (SyncControl *)getSyncControlForEntity:(NSString *)entity;


@end
