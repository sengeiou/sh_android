//
//  Encryption.m
//  Goles
//
//  Created by Maria Teresa Bañuls on 16/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "Encryption.h"
#import <CommonCrypto/CommonHMAC.h>

@implementation Encryption

//------------------------------------------------------------------------------
//Conection singleton instance shared across application
+ (Encryption *)sharedInstance
{
    static Encryption *sharedEncryption = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedEncryption = [[Encryption alloc] init];
    });
    return sharedEncryption;
}

-(NSString *) getPassword:(NSString *) password{
    
    NSString *output = [self sha1:password];
    output = [self md5:output];
    output=[output substringToIndex:20];
    
    return output;
}

-(NSString*) sha1:(NSString*)input
{
    const char *cstr = [input cStringUsingEncoding:NSUTF8StringEncoding];
    NSData *data = [NSData dataWithBytes:cstr length:input.length];
    
    uint8_t digest[CC_SHA1_DIGEST_LENGTH];
    
    CC_SHA1(data.bytes, data.length, digest);
    
    NSMutableString* output = [NSMutableString stringWithCapacity:CC_SHA1_DIGEST_LENGTH * 2];
    
    for(int i = 0; i < CC_SHA1_DIGEST_LENGTH; i++)
        [output appendFormat:@"%02x", digest[i]];
    
    return output;
    
}

- (NSString *) md5:(NSString *) input
{
    const char *cStr = [input UTF8String];
    unsigned char digest[16];
    CC_MD5( cStr, strlen(cStr), digest ); // This is the md5 call
    
    NSMutableString *output = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
    
    for(int i = 0; i < CC_MD5_DIGEST_LENGTH; i++)
        [output appendFormat:@"%02x", digest[i]];
    
    return  output;
    
}

@end
