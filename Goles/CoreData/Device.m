#import "Device.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#include <sys/types.h>
#include <sys/sysctl.h>
#import "UserManager.h"

@interface Device ()

@end


@implementation Device

#pragma mark - PUBLIC METHODS

// Custom logic goes here.
//------------------------------------------------------------------------------
+(Device *)insertWithDictionary:(NSDictionary *)dict{
    
    NSManagedObjectContext *context = [[CoreDataManager singleton] getContext];
    Device *device = [NSEntityDescription insertNewObjectForEntityForName:@"Device"
                                                     inManagedObjectContext:context];
    
    BOOL correctlyInserted = [device setDeviceValuesWithDictionary:dict];
    if ( !correctlyInserted ){
        [[CoreDataManager singleton] deleteObject:device];
        return nil;
    }
    
    return device;
}

//------------------------------------------------------------------------------
+(Device *)updateWithDictionary:(NSDictionary *)dict {
    
    NSArray *devices = [[CoreDataManager singleton] getAllEntities:[Device class]];
    Device *device;
    if ([devices count] == 0){
        device = [Device insertWithDictionary:dict];     // insert new entity
    }
    else {
        device = [devices objectAtIndex:0];
        [device setDeviceValuesWithDictionary:dict];    // Update entity
    }
    return device;
}

//------------------------------------------------------------------------------
- (void)resetToken {
    [self setValue:nil forKey:kJSON_TOKEN];
}

#pragma marck - Private Methods
//------------------------------------------------------------------------------
- (NSString *)getDeviceLanguage {
    
    NSString *currentLanguage = [[NSLocale preferredLanguages] objectAtIndex:0];
    NSString *countryCode = [[NSLocale currentLocale] objectForKey: NSLocaleCountryCode];
    return [NSString stringWithFormat:@"%@_%@", currentLanguage, countryCode];
}

//------------------------------------------------------------------------------
- (NSString *)platformString {
    size_t size;
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *machine = malloc(size);
    sysctlbyname("hw.machine", machine, &size, NULL, 0);
    NSString *platform = [NSString stringWithUTF8String:machine];
    free(machine);
    
    if ([platform isEqualToString:@"iPhone3,1"])    return @"iPhone 4";
    if ([platform isEqualToString:@"iPhone3,3"])    return @"Verizon iPhone 4";
    if ([platform isEqualToString:@"iPhone4,1"])    return @"iPhone 4S";
    if ([platform isEqualToString:@"iPhone2,1"])    return @"iPhone 3GS";
    if ([platform isEqualToString:@"iPhone5,1"])    return @"iPhone 5 (GSM)";
    if ([platform isEqualToString:@"iPhone5,2"])    return @"iPhone 5 (GSM+CDMA)";
    if ([platform isEqualToString:@"iPhone5,3"])    return @"iPhone 5c (GSM)";
    if ([platform isEqualToString:@"iPhone5,4"])    return @"iPhone 5c (Global)";
    if ([platform isEqualToString:@"iPhone6,1"])    return @"iPhone 5s (GSM)";
    if ([platform isEqualToString:@"iPhone6,2"])    return @"iPhone 5s (Global)";
    if ([platform isEqualToString:@"iPhone7,1"])    return @"iPhone 6+";
    if ([platform isEqualToString:@"iPod3,1"])      return @"iPod Touch 3G";
    if ([platform isEqualToString:@"iPod4,1"])      return @"iPod Touch 4G";
    if ([platform isEqualToString:@"iPod5,1"])      return @"iPod Touch 5G";
    if ([platform isEqualToString:@"iPad2,1"])      return @"iPad 2 (WiFi)";
    if ([platform isEqualToString:@"iPad2,2"])      return @"iPad 2 (GSM)";
    if ([platform isEqualToString:@"iPad2,3"])      return @"iPad 2 (CDMA)";
    if ([platform isEqualToString:@"iPad2,4"])      return @"iPad 2 (WiFi)";
    if ([platform isEqualToString:@"iPad2,5"])      return @"iPad Mini (WiFi)";
    if ([platform isEqualToString:@"iPad2,6"])      return @"iPad Mini (GSM)";
    if ([platform isEqualToString:@"iPad2,7"])      return @"iPad Mini (GSM+CDMA)";
    if ([platform isEqualToString:@"iPad3,1"])      return @"iPad 3 (WiFi)";
    if ([platform isEqualToString:@"iPad3,2"])      return @"iPad 3 (GSM+CDMA)";
    if ([platform isEqualToString:@"iPad3,3"])      return @"iPad 3 (GSM)";
    if ([platform isEqualToString:@"iPad3,4"])      return @"iPad 4 (WiFi)";
    if ([platform isEqualToString:@"iPad3,5"])      return @"iPad 4 (GSM)";
    if ([platform isEqualToString:@"iPad3,6"])      return @"iPad 4 (GSM+CDMA)";
    if ([platform isEqualToString:@"iPad4,1"])      return @"iPad Air (WiFi)";
    if ([platform isEqualToString:@"iPad4,2"])      return @"iPad Air (GSM)";
    if ([platform isEqualToString:@"iPad4,4"])      return @"iPad Mini Retina (WiFi)";
    if ([platform isEqualToString:@"iPad4,5"])      return @"iPad Mini Retina (GSM)";
    if ([platform isEqualToString:@"i386"])         return @"Simulator";
    if ([platform isEqualToString:@"x86_64"])       return @"Simulator";
    return platform;
}

//------------------------------------------------------------------------------
-(BOOL)setDeviceValuesWithDictionary:(NSDictionary *)dict {
    
    self.platform = @1;
    self.appVer = [[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString *)kCFBundleVersionKey];
    self.osVer = [NSString stringWithFormat:@"iOS %@",[[UIDevice currentDevice] systemVersion]];
    self.language = [self getDeviceLanguage];
    self.model = [self platformString];

    User *user = [[UserManager singleton] getActiveUser];
    if (user)
        [self setUser:user];
    
    if ( dict ){
        
        NSNumber *idDevice = [dict objectForKey:kJSON_ID_DEVICE];
        if ([idDevice isKindOfClass:[NSNumber class]])
            [self setIdDevice:idDevice];

        NSString *token = [dict objectForKey:kJSON_TOKEN];
        if ([token isKindOfClass:[NSString class]])
            [self setToken:token];


        //SYNCRO  PROPERTIES
        
        NSString *syncro = [dict objectForKey:kJSON_SYNCRONIZED];
        if ( [syncro isKindOfClass:[NSString class]] )
            [self setCsys_syncronized:syncro];
        else
            [self setCsys_syncronized:kJSON_SYNCRO_SYNCRONIZED];
        
        NSNumber *revision = [dict objectForKey:kJSON_REVISION];
        if ( [revision isKindOfClass:[NSNumber class]] )
            [self setCsys_revision:revision];
        
        NSNumber *birth = [dict objectForKey:kJSON_BIRTH];
        if ([birth isKindOfClass:[NSNumber class]]) {
            [self setCsys_birth:birth];
        }
        
        NSNumber *modified = [dict objectForKey:kJSON_MODIFIED];
        if ([modified isKindOfClass:[NSNumber class]]) {
            [self setCsys_modified:modified];
        }
        
        NSNumber *deleted = [dict objectForKey:K_OP_DELETE];
        if ([deleted isKindOfClass:[NSNumber class]]) {
            [self setCsys_deleted:deleted];
        }
    }
    
    return YES;
}

@end
