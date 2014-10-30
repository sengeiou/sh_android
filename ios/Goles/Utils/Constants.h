//
//  Constants.h
//
//
//  Created by Christian Cabarrocas on 23/09/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#ifndef Goles_Messenger_Constants_h
#define Goles_Messenger_Constants_h

#import "Services.pch"

#define K_DEBUG_MODE    NO
#define K_DEBUG_SERVICE NO

#define IS_DEVELOPING   YES  // Pointing to PRE or PRO

#define IS_GENERATING_DEFAULT_DATABASE      NO


//Synchronization switch
#define SYNCHRO_ACTIVATED      YES


//Syncro time process in seconds
#define SYNCHRO_TIMER   10

//Syncro time process in seconds
#define CHARACTERS_SHOT   140

//Profile selection
#define FOLLOWING_SELECTED   @1
#define FOLLOWERS_SELECTED   @2

#endif  

#ifdef DEBUG
#define DLog(...) NSLog(@"%s %@", __PRETTY_FUNCTION__, [NSString stringWithFormat:__VA_ARGS__])
#else
#define DLog(...)

#ifndef NS_BLOCK_ASSERTIONS
#define NS_BLOCK_ASSERTIONS
#endif
#endif

#define kDefaultGrupedTableHeaderHeight 55.0f
#define kHeightNavBarWithSegmentedControl 118.0f