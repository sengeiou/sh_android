//
//  Encryption.h
//
//  Created by Maria Teresa Bañuls on 16/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Encryption : NSObject

+ (Encryption *)sharedInstance;
-(NSString *) getPassword:(NSString *) password;

@end
