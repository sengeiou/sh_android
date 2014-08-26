#import "_SML.h"

@interface SML : _SML {}

+(SML *)insertWithDictionary:(NSDictionary *)dict;
+(SML *)updateWithDictionary:(NSDictionary *)dict;
+(SML *)createDefaultSML;
+(SML *)createTemporarySML;
+(SML *)createTemporarySMLWithSML:(SML *)sml;
-(BOOL)updateValuesWithDictionary:(NSDictionary *)dict;
-(NSString *)getMessageString;
-(NSString *)getSoundMessageString;

@end
